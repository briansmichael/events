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

import com.starfireaviation.events.exception.AccessDeniedException;
import com.starfireaviation.events.exception.ConflictException;
import com.starfireaviation.events.exception.InvalidPayloadException;
import com.starfireaviation.events.exception.ResourceNotFoundException;
import com.starfireaviation.events.model.Event;
import com.starfireaviation.events.service.EventService;
import com.starfireaviation.events.validation.EventValidator;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EventController.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping({
        "/events"
})
public class EventController {

    /**
     * MAX_UPCOMING_COUNT.
     */
    public static final int MAX_UPCOMING_COUNT = 10;

    /**
     * UPCOMING_DAYS.
     */
    public static final int UPCOMING_DAYS = 5;

    /**
     * RSVP_DAYS.
     */
    public static final int RSVP_DAYS = 1;

    /**
     * EventService.
     */
    private final EventService eventService;

    /**
     * EventValidator.
     */
    private final EventValidator eventValidator;

    /**
     * EventController.
     *
     * @param eService   EventService
     * @param eValidator EventValidator
     */
    public EventController(final EventService eService,
                           final EventValidator eValidator) {
        eventService = eService;
        eventValidator = eValidator;
    }

    /**
     * Creates a event.
     *
     * @param event     Event
     * @param principal Principal
     * @return Event
     * @throws ResourceNotFoundException when no event is found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     * @throws InvalidPayloadException   when invalid data is provided
     * @throws ConflictException         when another event occurs within 30 minutes
     *                                   of the provided event
     */
    @PostMapping
    public Event post(@RequestBody final Event event, final Principal principal) throws ResourceNotFoundException,
            AccessDeniedException, InvalidPayloadException, ConflictException {
        eventValidator.validate(event);
        eventValidator.accessAdminOrInstructor(principal);
        if (event == null) {
            return event;
        }
        return eventService.store(event);
    }

    /**
     * Gets an event.
     *
     * @param eventId   Long
     * @param principal Principal
     * @return Event
     * @throws ResourceNotFoundException when address is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = {
            "/{eventId}"
    })
    public Event get(@PathVariable("eventId") final long eventId, final Principal principal)
            throws ResourceNotFoundException,
            AccessDeniedException {
        eventValidator.accessAnyAuthenticated(principal);
        return eventService.get(eventId);
    }

    /**
     * Updates an event.
     *
     * @param event     Event
     * @param principal Principal
     * @return Event
     * @throws ResourceNotFoundException when no event is found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @PutMapping
    public Event put(@RequestBody final Event event, final Principal principal) throws ResourceNotFoundException,
            AccessDeniedException {
        eventValidator.accessAdminOrInstructor(principal);
        if (event == null) {
            return event;
        }
        return eventService.store(event);
    }

    /**
     * Deletes an event.
     *
     * @param eventId   Long
     * @param principal Principal
     * @return Event
     * @throws ResourceNotFoundException when event is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @DeleteMapping(path = {
            "/{eventId}"
    })
    public Event delete(@PathVariable("eventId") final long eventId, final Principal principal)
            throws ResourceNotFoundException,
            AccessDeniedException {
        eventValidator.accessAdminOrInstructor(principal);
        return eventService.delete(eventId);
    }

    /**
     * Get all events.
     *
     * @param principal Principal
     *
     * @return list of Event
     * @throws ResourceNotFoundException when address is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping
    public List<Event> list(final Principal principal) throws ResourceNotFoundException, AccessDeniedException {
        eventValidator.accessAdminOrInstructor(principal);
        return eventService.getAll();
    }

    /**
     * Gets the list of supporting instructors for the given event.
     *
     * @param eventId   Event ID
     * @param principal Principal
     * @return list of User IDs
     * @throws ResourceNotFoundException when event is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = {
            "/{eventId}/instructors"
    })
    public List<Long> supportingInstructors(@PathVariable("eventId") final long eventId, final Principal principal)
            throws ResourceNotFoundException, AccessDeniedException {
        eventValidator.accessAnyAuthenticated(principal);
        return eventService.getAllSupportingInstructors(eventId);
    }

    /**
     * Gets whether or not a user is a member for an event.
     *
     * @param eventId   Event ID
     * @param userId    User ID
     * @param principal Principal
     * @return member
     * @throws ResourceNotFoundException when event or user is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = {
            "/{eventId}/{userId}/member"
    })
    public boolean isMember(
            @PathVariable("eventId") final long eventId,
            @PathVariable("userId") final long userId,
            final Principal principal)
            throws ResourceNotFoundException, AccessDeniedException {
        eventValidator.accessAdminInstructorOrSpecificUser(userId, principal);
        return eventService.isMember(eventId, userId);
    }

    /**
     * Gets the list of checked in participants for the given event.
     *
     * @param eventId   Event ID
     * @param principal Principal
     * @return list of User IDs
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = {
            "/{eventId}/participants/checkedin"
    })
    public List<Long> getAllCheckedInParticipants(@PathVariable("eventId") final long eventId,
            final Principal principal)
            throws AccessDeniedException {
        eventValidator.accessAdminOrInstructor(principal);
        return eventService.getAllEventCheckedInUsers(eventId);
    }

    /**
     * Get X upcoming events.
     *
     * @param type      EventType
     * @param count     number of events to be returned
     * @param principal Principal
     * @return list of Event
     * @throws ResourceNotFoundException when address is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = {
            "/upcoming/{type}/{count}"
    })
    public List<EventSummary> upcoming(
            @PathVariable("type") final EventType type,
            @PathVariable("count") final int count,
            final Principal principal)
            throws ResourceNotFoundException, AccessDeniedException {
        int actualCount = count;
        if (actualCount > MAX_UPCOMING_COUNT) {
            actualCount = MAX_UPCOMING_COUNT;
        }
        List<Event> upcomingEvents = eventService
                .getAll()
                .stream()
                .filter(
                        event -> event.getStartTime().isAfter(LocalDateTime.now())
                                && !event.isPrivateEvent()
                                && event.getEventType() == type)
                .sorted(Comparator.comparing(Event::getStartTime))
                .limit(actualCount)
                .collect(Collectors.toList());
        List<EventSummary> eventSummaries = new ArrayList<>();
        for (Event event : upcomingEvents) {
            eventSummaries.add(eventService.getEventSummary(event.getId()));
        }

        return eventSummaries;
    }

    /**
     * RSVP's a user for an event.
     *
     * @param eventId   event ID
     * @param userId    user ID
     * @param confirm   confirm or decline
     * @param type      NotificationType
     * @param principal Principal
     * @throws ResourceNotFoundException when event or user is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @PostMapping(path = {
            "/{eventId}/rsvp/{userId}/{confirm}/{type}"
    })
    public void rsvp(
            @PathVariable("eventId") final long eventId,
            @PathVariable("userId") final long userId,
            @PathVariable("confirm") final boolean confirm,
            @PathVariable("type") final NotificationType type,
            final Principal principal) throws ResourceNotFoundException, AccessDeniedException {
        eventValidator.accessAdminInstructorOrSpecificUser(userId, principal);
        eventService.rsvp(eventId, userId, confirm);
    }

    /**
     * Registers a user for an event.
     *
     * @param eventId   event ID
     * @param userId    user ID
     * @param principal Principal
     * @throws ResourceNotFoundException when event nor user is found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @PostMapping(path = {
            "/{eventId}/register/{userId}"
    })
    public void register(
            @PathVariable("eventId") final long eventId,
            @PathVariable("userId") final long userId,
            final Principal principal) throws ResourceNotFoundException, AccessDeniedException {
        eventValidator.accessAdminInstructorOrSpecificUser(userId, principal);
        final Event event = eventService.get(eventId);
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
    @PostMapping(path = {
            "/{eventId}/unregister/{userId}"
    })
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
     * @throws ResourceNotFoundException when event is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @GetMapping(path = {
            "/{eventId}/checkincode"
    })
    public String getCheckinCode(@PathVariable("eventId") final long eventId, final Principal principal)
            throws ResourceNotFoundException, AccessDeniedException {
        eventValidator.accessAnyAuthenticated(principal);
        String code = null;
        final Event event = eventService.get(eventId);
        if (event != null) {
            code = event.getCheckinCode();
        }
        return code;
    }

    /**
     * Checkin.
     *
     * @param userId    User ID
     * @param eventId   Event ID
     * @param code      checkin code
     * @param principal Principal
     * @return checkin success
     * @throws ResourceNotFoundException when event or user is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @PostMapping(path = {
            "/{eventId}/checkin/{userId}/{code}"
    })
    public boolean checkin(
            @PathVariable("eventId") final long eventId,
            @PathVariable("userId") final long userId,
            @PathVariable("code") final String code,
            final Principal principal) throws ResourceNotFoundException, AccessDeniedException {
        eventValidator.accessAdminInstructorOrSpecificUser(userId, principal);
        return eventService.checkin(eventId, userId, code);
    }

    /**
     * Starts an event.
     *
     * @param eventId   Long
     * @param principal Principal
     * @return started event
     * @throws ResourceNotFoundException when event is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @PostMapping(path = {
            "/{eventId}/start"
    })
    public Event start(@PathVariable("eventId") final long eventId, final Principal principal)
            throws ResourceNotFoundException,
            AccessDeniedException {
        eventValidator.accessAdminOrInstructor(principal);
        Event event = eventService.get(eventId);
        if (event != null && !event.isStarted()) {
            event.setStarted(true);
            event.setStartTime(LocalDateTime.now(ZoneOffset.UTC));
            event.setCheckinCode(CodeGenerator.generateCode(CommonConstants.FOUR));
            event = eventService.store(event);
        }
        return event;
    }

    /**
     * Complete's an event.
     *
     * @param eventId   Long
     * @param principal Principal
     * @return completed event
     * @throws ResourceNotFoundException when event is not found
     * @throws AccessDeniedException     when user doesn't have permission to
     *                                   perform operation
     */
    @PostMapping(path = {
            "/{eventId}/complete"
    })
    public Event complete(@PathVariable("eventId") final long eventId, final Principal principal)
            throws ResourceNotFoundException,
            AccessDeniedException {
        eventValidator.accessAdminOrInstructor(principal);
        Event event = eventService.get(eventId);
        if (event != null) {
            if (!event.isStarted()) {
                event.setStarted(true);
                event.setStartTime(LocalDateTime.now());
            }
            event.setCompleted(true);
            event.setCompletedTime(LocalDateTime.now());
            event.setCheckinCode(null);
            event = eventService.store(event);
        }
        return event;
    }

}
