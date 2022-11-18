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
import com.starfireaviation.events.model.VoteEntity;
import com.starfireaviation.events.model.VoteRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
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
     * VoteRepository.
     */
    private final VoteRepository voteRepository;

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
     * @param eRepository EventRepository
     * @param vRepository VoteRepository
     * @param epRepository EventParticipantRepository
     * @param dService DataService
     */
    public EventService(final EventRepository eRepository,
                        final VoteRepository vRepository,
                        final EventParticipantRepository epRepository,
                        final DataService dService) {
        eventRepository = eRepository;
        voteRepository = vRepository;
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
        final Long eventLead = get(eventId).getLeader();
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

    /**
     * Votes for a lesson to be presented at an event.
     * Note: Only 1 vote can be cast per user per event.
     *
     * @param eventId Event ID
     * @param lessonPlanId Lesson Plan ID - lesson to be presented
     * @param userId User ID - user casting vote
     */
    public void vote(final Long eventId, final Long userId, final Long lessonPlanId) {
        final VoteEntity vote = voteRepository.findByEventIdAndUserId(eventId, userId).orElse(new VoteEntity());
        if (!Objects.equals(lessonPlanId, vote.getLessonPlanId()) && dataService.existsLessonPlan(lessonPlanId)) {
            vote.setEventId(eventId);
            vote.setUserId(userId);
            vote.setLessonPlanId(lessonPlanId);
            voteRepository.save(vote);
        }
    }

    /**
     * Withdraws vote for a lesson to be presented at an event.
     * Note: If no vote has been cast, then no action is performed.
     *
     * @param eventId Event ID
     * @param userId User ID - user casting vote
     */
    public void withdrawVote(final Long eventId, final Long userId) {
        voteRepository.findByEventIdAndUserId(eventId, userId).ifPresent(voteRepository::delete);
    }

    /**
     * Assigns a lesson plan to events based upon votes received or, if no votes received, by least previous
     * presentations.
     */
    public void assign() {
        eventRepository.findAll().orElse(new ArrayList<>()).forEach(event -> {
            final Map<Long, Long> tally = new HashMap<>();
            voteRepository
                    .findByEventId(event.getId())
                    .orElse(new ArrayList<>())
                    .forEach(vote -> tally.put(vote.getLessonPlanId(),
                            tally.getOrDefault(vote.getLessonPlanId(), 0L) + 1));
            Long winningLessonPlanId = Long.MIN_VALUE;
            Long highestCount = Long.MIN_VALUE;
            for (Map.Entry<Long, Long> entry : tally.entrySet()) {
                if (entry.getValue() > highestCount) {
                    winningLessonPlanId = entry.getKey();
                    highestCount = entry.getValue();
                }
            }
            if (winningLessonPlanId > 0) {
                event.setLessonPlanId(winningLessonPlanId);
                eventRepository.save(event);
            } else {
                final Map<Long, Long> previousPresentationMap = getPastLessonPlanPresentationCounts(event);
                // TreeMap Key = Count; Value = LessonPlan ID
                final TreeMap<Long, Long> map = new TreeMap<>();
                dataService
                        .getAllPresentableLessonPlans()
                        .forEach(lpId -> map.put(previousPresentationMap.getOrDefault(lpId, 0L), lpId));
                event.setLessonPlanId(map.get(map.firstKey()));
                eventRepository.save(event);
            }
        });
    }

    /**
     * Gets a map of the number of times each lesson plan has been presented previously.
     * Key = LessonPlan ID; Value = Count
     *
     * @param event Event
     * @return map of lesson plan presentation counts
     */
    private Map<Long, Long> getPastLessonPlanPresentationCounts(final EventEntity event) {
        final Map<Long, Long> map = new HashMap<>();
        eventRepository
                .findAll()
                .orElse(new ArrayList<>())
                .stream()
                .filter(e -> e.getStartTime().isBefore(event.getStartTime()))
                .forEach(e -> map.put(e.getLessonPlanId(), map.getOrDefault(e.getLessonPlanId(), 0L) + 1));
        return map;
    }

}
