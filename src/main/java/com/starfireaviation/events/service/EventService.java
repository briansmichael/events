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

package com.starfireaviation.events.service;

import com.starfireaviation.common.exception.ResourceNotFoundException;
import com.starfireaviation.common.model.Role;
import com.starfireaviation.events.model.EventEntity;
import com.starfireaviation.events.model.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * EventService.
 */
public class EventService {

    /**
     * EventRepository.
     */
    private final EventRepository eventRepository;

    /**
     * DataService.
     */
    private final DataService dataService;

    /**
     * EventService.
     *
     * @param eRepository  EventRepository
     * @param dService     DataService
     */
    public EventService(final EventRepository eRepository,
                        final DataService dService) {
        eventRepository = eRepository;
        dataService = dService;
    }

    /**
     * Creates an event.
     *
     * @param event Event
     * @return User
     */
    public EventEntity store(final EventEntity event) {
        if (event == null) {
            return null;
        }
        return eventRepository.save(event);
    }

    /**
     * Deletes a event.
     *
     * @param id Long
     * @return Event
     */
    public EventEntity delete(final long id) {
        final EventEntity event = get(id);
        if (event != null) {
            eventRepository.delete(event);
        }
        return event;
    }

    /**
     * Gets all events.
     *
     * @return list of Event
     */
    public List<EventEntity> getAll() {
        List<EventEntity> events = new ArrayList<>();
        List<EventEntity> eventEntities = eventRepository.findAll();
        for (EventEntity eventEntity : eventEntities) {
            events.add(get(eventEntity.getId()));
        }
        return events;
    }

    /**
     * Gets a event.
     *
     * @param id Long
     * @return Event
     */
    public EventEntity get(final long id) {
        final EventEntity event = eventRepository.findById(id);
        return event;
    }

    /**
     * Register a user for an event.
     *
     * @param eventId Event ID
     * @param userId  User ID
     * @throws ResourceNotFoundException when the event or user is not found
     */
    public void register(final Long eventId, final Long userId) throws ResourceNotFoundException {
        final EventEntity event = get(eventId);
        if (!event.getParticipants().contains(userId)) {
            event.getParticipants().add(userId);
            eventRepository.save(event);
        }
    }

    /**
     * Unregister a user from an event.
     *
     * @param eventId Event ID
     * @param userId  User ID
     * @throws ResourceNotFoundException when the event or user is not found
     */
    public void unregister(final Long eventId, final Long userId) throws ResourceNotFoundException {
        final EventEntity event = get(eventId);
        if (event.getParticipants().contains(userId)) {
            event.getParticipants().remove(userId);
            eventRepository.save(event);
        }
    }

    /**
     * Is the user registered for an event?
     *
     * @param eventId Event ID
     * @param userId  User ID
     * @return whether or not user is registered
     */
    public boolean isRegistered(final Long eventId, final Long userId) {
        final EventEntity event = get(eventId);
        if (event.getParticipants().contains(userId)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * Gets all supporting instructors for the given event.
     *
     * @param eventId Event ID
     * @return list of User IDs
     */
    public List<Long> getAllSupportingInstructors(final Long eventId) {
        final List<Long> supportingInstructors = new ArrayList<>();
        final Long eventLead = get(eventId).getLead();
        final EventEntity event = get(eventId);
        event.getParticipants()
                .stream()
                .distinct()
                .forEach(userId -> {
                    if (!Objects.equals(userId, eventLead)
                            && dataService.getUser(userId).getRole() == Role.INSTRUCTOR) {
                        supportingInstructors.add(userId);
                    }
                });
        return supportingInstructors;
    }

}
