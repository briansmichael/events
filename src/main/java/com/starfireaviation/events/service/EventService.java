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

import com.starfireaviation.common.model.Role;
import com.starfireaviation.events.model.EventEntity;
import com.starfireaviation.events.model.EventParticipant;
import com.starfireaviation.events.model.EventParticipantRepository;
import com.starfireaviation.events.model.EventRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * EventService.
 */
public class EventService {

    /**
     * EventRepository.
     */
    private final EventRepository eventRepository;

    /**
     * EventRepository.
     */
    private final EventParticipantRepository eventParticipantRepository;

    /**
     * DataService.
     */
    private final DataService dataService;

    /**
     * EventService.
     *
     * @param eRepository  EventRepository
     * @param epRepository EventParticipantRepository
     * @param dService     DataService
     */
    public EventService(final EventRepository eRepository,
                        final EventParticipantRepository epRepository,
                        final DataService dService) {
        eventRepository = eRepository;
        eventParticipantRepository = epRepository;
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
     */
    public void delete(final long id) {
        final EventEntity event = get(id);
        if (event != null) {
            eventRepository.delete(event);
        }
    }

    /**
     * Gets all events.
     *
     * @return list of Event
     */
    public List<EventEntity> getAll() {
        return eventRepository.findAll().orElse(new ArrayList<>());
    }

    /**
     * Gets a event.
     *
     * @param id Long
     * @return Event
     */
    public EventEntity get(final long id) {
        return eventRepository.findById(id).orElseThrow();
    }

    /**
     * Register a user for an event.
     *
     * @param eventId Event ID
     * @param userId  User ID
     */
    public void register(final Long eventId, final Long userId) {
        final Optional<EventParticipant> eventParticipantOpt = eventParticipantRepository.findByUserId(userId)
                .orElse(new ArrayList<>())
                .stream()
                .filter(eventParticipant -> eventParticipant.getEventId() == eventId)
                .findFirst();
        if (eventParticipantOpt.isEmpty()) {
            final EventParticipant eventParticipant = new EventParticipant();
            eventParticipant.setEventId(eventId);
            eventParticipant.setUserId(userId);
            eventParticipant.setCreatedAt(new Date());
            eventParticipant.setUpdatedAt(new Date());
            eventParticipantRepository.save(eventParticipant);
        }
    }

    /**
     * Unregister a user from an event.
     *
     * @param eventId Event ID
     * @param userId  User ID
     */
    public void unregister(final Long eventId, final Long userId) {
        eventParticipantRepository.findByUserId(userId)
                .orElse(new ArrayList<>())
                .stream()
                .filter(eventParticipant -> eventParticipant.getEventId() == eventId)
                .forEach(eventParticipantRepository::delete);
    }

    /**
     * Is the user registered for an event?
     *
     * @param eventId Event ID
     * @param userId  User ID
     * @return whether or not user is registered
     */
    public boolean isRegistered(final Long eventId, final Long userId) {
        return eventParticipantRepository.findByUserId(userId)
                .orElse(new ArrayList<>())
                .stream()
                .anyMatch(eventParticipant -> eventParticipant.getEventId() == eventId);
    }

    /**
     * Gets all supporting instructors for the given event.
     *
     * @param eventId Event ID
     * @return list of User IDs
     */
    public List<Long> getAllSupportingInstructors(final Long eventId) {
        final Long eventLead = get(eventId).getLead();
        return getParticipants(eventId)
                .stream()
                .distinct()
                .filter(userId -> !Objects.equals(userId, eventLead)
                        && dataService.getUser(userId).getRole() == Role.INSTRUCTOR)
                .collect(Collectors.toList());
    }

    /**
     * Get participant list for an event.
     *
     * @param eventId Event ID
     * @return list of user IDs
     */
    public List<Long> getParticipants(final Long eventId) {
        return eventParticipantRepository.findByEventId(eventId)
                .orElse(new ArrayList<>())
                .stream()
                .map(EventParticipant::getUserId)
                .collect(Collectors.toList());
    }
}
