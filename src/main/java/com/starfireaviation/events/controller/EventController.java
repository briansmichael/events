/*
 *  Copyright (C) 2022 Starfire Aviation, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.starfireaviation.events.controller;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.starfireaviation.common.exception.AccessDeniedException;
import com.starfireaviation.common.exception.ConflictException;
import com.starfireaviation.common.exception.InvalidPayloadException;
import com.starfireaviation.common.exception.ResourceNotFoundException;
import com.starfireaviation.common.model.Event;
import com.starfireaviation.common.model.EventType;
import com.starfireaviation.events.model.EventEntity;
import com.starfireaviation.events.service.EventService;
import com.starfireaviation.events.validation.EventValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EventController.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping({ "/api/events" })
public class EventController {

    /**
     * MAX_UPCOMING_COUNT.
     */
    public static final int MAX_UPCOMING_COUNT = 10;

    /**
     * EventService.
     */
    private final EventService eventService;

    /**
     * EventValidator.
     */
    private final EventValidator eventValidator;

    /**
     * Events cache.
     */
    private final IMap<Long, Event> cache;

    /**
     * Upcoming Events cache.
     */
    private final IMap<String, List<Event>> upcomingCache;

    /**
     * EventController.
     *
     * @param eService   EventService
     * @param eValidator EventValidator
     * @param hazelcastInstance HazelcastInstance
     */
    public EventController(final EventService eService,
                           final EventValidator eValidator,
                           @Qualifier("events") final HazelcastInstance hazelcastInstance) {
        eventService = eService;
        eventValidator = eValidator;
        cache = hazelcastInstance.getMap("events");
        upcomingCache = hazelcastInstance.getMap("upcomingEvents");
    }

    /**
     * Creates an event.
     *
     * @param event     Event
     * @param principal Principal
     * @return Event
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     * @throws InvalidPayloadException   when invalid data is provided
     * @throws ConflictException         when another event occurs within 30 minutes
     *                                   of the provided event
     */
    @PostMapping
    public Event post(@RequestBody final Event event, final Principal principal)
            throws AccessDeniedException, InvalidPayloadException, ConflictException {
        eventValidator.validate(event);
        eventValidator.accessAdminOrInstructor(principal);
        return map(eventService.store(map(event)));
    }

    /**
     * Gets an event.
     *
     * @param eventId   Long
     * @param principal Principal
     * @return Event
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = { "/{eventId}" })
    public Event get(@PathVariable("eventId") final Long eventId, final Principal principal)
            throws AccessDeniedException {
        eventValidator.accessAnyAuthenticated(principal);
        if (cache.containsKey(eventId)) {
            return cache.get(eventId);
        }
        final Event event = map(eventService.get(eventId));
        cache.put(eventId, event);
        return event;
    }

    /**
     * Updates an event.
     *
     * @param event     Event
     * @param principal Principal
     * @return Event
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @PutMapping
    public Event put(@RequestBody final Event event, final Principal principal)
            throws AccessDeniedException, ConflictException, InvalidPayloadException {
        eventValidator.accessAdminOrInstructor(principal);
        eventValidator.validate(event);
        return map(eventService.store(map(event)));
    }

    /**
     * Deletes an event.
     *
     * @param eventId   Long
     * @param principal Principal
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @DeleteMapping(path = { "/{eventId}" })
    public void delete(@PathVariable("eventId") final Long eventId, final Principal principal)
            throws AccessDeniedException {
        eventValidator.accessAdminOrInstructor(principal);
        eventService.delete(eventId);
        if (cache.containsKey(eventId)) {
            cache.delete(eventId);
        }
    }

    /**
     * Get all events.
     *
     * @param principal Principal
     *
     * @return list of Event
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping
    public List<Event> list(final Principal principal) throws AccessDeniedException {
        eventValidator.accessAdminOrInstructor(principal);
        return eventService.getAll().stream().map(this::map).collect(Collectors.toList());
    }

    /**
     * Gets the list of supporting instructors for the given event.
     *
     * @param eventId   Event ID
     * @param principal Principal
     * @return list of User IDs
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = { "/{eventId}/instructors" })
    public List<Long> supportingInstructors(@PathVariable("eventId") final long eventId, final Principal principal)
            throws AccessDeniedException {
        eventValidator.accessAnyAuthenticated(principal);
        return eventService.getAllSupportingInstructors(eventId);
    }

    /**
     * Get X upcoming events.
     *
     * @param type      EventType
     * @param count     number of events to be returned
     * @return list of Event
     */
    @GetMapping(path = { "/upcoming/{type}/{count}" })
    public List<Event> upcoming(
            @PathVariable("type") final EventType type,
            @PathVariable("count") final int count) {
        int actualCount = count;
        if (actualCount > MAX_UPCOMING_COUNT) {
            actualCount = MAX_UPCOMING_COUNT;
        }
        final String key = getKey(type, count);
        if (upcomingCache.containsKey(key)) {
            return upcomingCache.get(key);
        }
        final List<Event> upcoming = eventService
                .getAll()
                .stream()
                .filter(
                        event -> event.getStartTime().isAfter(LocalDateTime.now())
                                && !event.isPrivateEvent()
                                && event.getEventType() == type)
                .sorted(Comparator.comparing(EventEntity::getStartTime))
                .limit(actualCount)
                .map(this::map)
                .collect(Collectors.toList());
        upcomingCache.put(key, upcoming);
        return upcoming;
    }

    /**
     * Registers a user for an event.
     *
     * @param eventId   event ID
     * @param userId    user ID
     * @param principal Principal
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @PostMapping(path = { "/{eventId}/register/{userId}" })
    public void register(
            @PathVariable("eventId") final long eventId,
            @PathVariable("userId") final long userId,
            final Principal principal) throws AccessDeniedException {
        eventValidator.accessAdminInstructorOrSpecificUser(userId, principal);
        final EventEntity event = eventService.get(eventId);
        if ((!event.isPrivateEvent()
                || (event.isPrivateEvent()
                        && eventValidator.isAdminOrInstructor(principal)))
                && !eventService.isRegistered(eventId, userId)) {
            eventService.register(eventId, userId);
        }
    }

    /**
     * Unregisters a user from an event.
     *
     * @param eventId   event ID
     * @param userId    user ID
     * @param principal Principal
     * @throws ResourceNotFoundException when event nor user is found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @PostMapping(path = { "/{eventId}/unregister/{userId}" })
    public void unregister(
            @PathVariable("eventId") final long eventId,
            @PathVariable("userId") final long userId,
            final Principal principal) throws ResourceNotFoundException, AccessDeniedException {
        eventValidator.accessAdminInstructorOrSpecificUser(userId, principal);
        eventService.unregister(eventId, userId);
    }

    /**
     * Gets an event's checkin code, if assigned.
     *
     * @param eventId   Long
     * @param principal Principal
     * @return Event's checkin code
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = {
            "/{eventId}/checkincode"
    })
    public String getCheckinCode(@PathVariable("eventId") final long eventId, final Principal principal)
            throws AccessDeniedException {
        eventValidator.accessAnyAuthenticated(principal);
        String code = null;
        final EventEntity event = eventService.get(eventId);
        if (event != null) {
            code = event.getCheckinCode();
        }
        return code;
    }

    /**
     * Starts an event.
     *
     * @param eventId   Long
     * @param principal Principal
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @PostMapping(path = { "/{eventId}/start" })
    public void start(@PathVariable("eventId") final long eventId, final Principal principal)
            throws AccessDeniedException {
        eventValidator.accessAdminOrInstructor(principal);
        final EventEntity event = eventService.get(eventId);
        if (event != null && !event.isStarted()) {
            event.setStarted(true);
            event.setStartTime(LocalDateTime.now(ZoneOffset.UTC));
            //event.setCheckinCode(CodeGenerator.generateCode(CommonConstants.FOUR));
            eventService.store(event);
        }
    }

    /**
     * Complete's an event.
     *
     * @param eventId   Long
     * @param principal Principal
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @PostMapping(path = { "/{eventId}/complete" })
    public void complete(@PathVariable("eventId") final long eventId, final Principal principal)
            throws AccessDeniedException {
        eventValidator.accessAdminOrInstructor(principal);
        final EventEntity event = eventService.get(eventId);
        if (event != null) {
            if (!event.isStarted()) {
                event.setStarted(true);
                event.setStartTime(LocalDateTime.now());
            }
            event.setCompleted(true);
            event.setCompletedTime(LocalDateTime.now());
            event.setCheckinCode(null);
            eventService.store(event);
        }
    }

    /**
     * Generates a key from the provided values.
     *
     * @param type EventType
     * @param count count
     * @return key
     */
    private String getKey(final EventType type, final int count) {
        return String.format("type=%s;count=%s", type, count);
    }

    /**
     * Maps an EventEntity to an Event.
     *
     * @param eventEntity EventEntity
     * @return Event
     */
    private Event map(final EventEntity eventEntity) {
        final Event event = new Event();
        event.setId(eventEntity.getId());
        event.setEventType(eventEntity.getEventType());
        event.setPrivateEvent(eventEntity.isPrivateEvent());
        event.setCheckinCode(eventEntity.getCheckinCode());
        event.setCompleted(eventEntity.isCompleted());
        event.setCalendarUrl(eventEntity.getCalendarUrl());
        event.setCheckinCodeRequired(eventEntity.isCheckinCodeRequired());
        event.setCompletedTime(eventEntity.getCompletedTime());
        event.setCompleted(eventEntity.isCompleted());
        event.setStarted(eventEntity.isStarted());
        event.setStartTime(eventEntity.getStartTime());
        event.setLead(eventEntity.getLead());
        event.setLessonPlanId(eventEntity.getLessonPlanId());
        event.setParticipantIds(eventService.getParticipants(eventEntity.getId()));
        event.setTitle(eventEntity.getTitle());
        return event;
    }

    /**
     * Maps an Event to and EventEntity.
     *
     * @param event Event
     * @return EventEntity
     */
    private EventEntity map(final Event event) {
        final EventEntity eventEntity = new EventEntity();
        eventEntity.setEventType(event.getEventType());
        eventEntity.setId(event.getId());
        eventEntity.setCompleted(event.isCompleted());
        eventEntity.setPrivateEvent(event.isPrivateEvent());
        eventEntity.setLead(event.getLead());
        eventEntity.setStarted(eventEntity.isStarted());
        eventEntity.setCalendarUrl(eventEntity.getCalendarUrl());
        eventEntity.setTitle(event.getTitle());
        eventEntity.setCompleted(event.isCompleted());
        eventEntity.setCompletedTime(event.getCompletedTime());
        eventEntity.setStartTime(event.getStartTime());
        eventEntity.setCheckinCode(eventEntity.getCheckinCode());
        eventEntity.setCheckinCodeRequired(eventEntity.isCheckinCodeRequired());
        eventEntity.setLessonPlanId(eventEntity.getLessonPlanId());
        return eventEntity;
    }

}
