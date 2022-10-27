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

import com.starfireaviation.events.model.EventParticipantRepository;
import com.starfireaviation.events.model.EventRepository;
import com.starfireaviation.model.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
     * EventParticipantRepository.
     */
    private final EventParticipantRepository eventUserRepository;

    /**
     * UserService.
     */
    private final UserService userService;

    /**
     * AddressService.
     */
    private final AddressService addressService;

    /**
     * LessonPlanService.
     */
    private final LessonPlanService lessonPlanService;

    /**
     * EventService.
     *
     * @param eRepository  EventRepository
     * @param epRepository EventParticipantRepository
     * @param uService     UserService
     * @param aService     AddressService
     * @param lpService    LessonPlanService
     */
    public EventService(final EventRepository eRepository,
                        final EventParticipantRepository epRepository,
                        final UserService uService,
                        final AddressService aService,
                        final LessonPlanService lpService) {
        eventRepository = eRepository;
        eventUserRepository = epRepository;
        userService = uService;
        addressService = aService;
        lessonPlanService = lpService;
    }

    /**
     * Creates an event.
     *
     * @param event Event
     * @return User
     */
    public Event store(final Event event) {
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
     * @throws ResourceNotFoundException when the event is not found
     */
    public Event delete(final long id) throws ResourceNotFoundException {
        final Event event = get(id);
        if (event != null) {
            eventRepository.delete(event);
        }
        return event;
    }

    /**
     * Gets all events.
     *
     * @return list of Event
     * @throws ResourceNotFoundException when the event is not found
     */
    public List<Event> getAll() throws ResourceNotFoundException {
        List<Event> events = new ArrayList<>();
        List<Event> eventEntities = eventRepository.findAll();
        for (Event eventEntity : eventEntities) {
            events.add(get(eventEntity.getId()));
        }
        return events;
    }

    /**
     * Gets a event.
     *
     * @param id Long
     * @return Event
     * @throws ResourceNotFoundException when the event is not found
     */
    public Event get(final long id) throws ResourceNotFoundException {
        final Event event = eventRepository.findById(id);
        final List<EventParticipant> eventParticipantEntities = eventUserRepository.findByEventId(id);
        final List<User> participants = new ArrayList<>();
        for (final EventParticipant eventParticipant : eventParticipantEntities) {
            if (!eventParticipant.getDeclined()) {
                participants.add(userService.get(eventParticipant.getUserId()));
            }
        }
        event.setAddress(addressService.get(1));
        event.setParticipants(participants);
        return event;
    }

    /**
     * RSVP's a user for an event.
     *
     * @param eventId Event ID
     * @param userId  User ID
     * @param confirm confirm or decline
     * @throws ResourceNotFoundException when the event or user is not found
     */
    public void rsvp(final Long eventId, final Long userId, final boolean confirm) throws ResourceNotFoundException {
        if (!isRegistered(eventId, userId)) {
            register(eventId, userId);
        }
        final EventParticipant eventUserEntity = eventUserRepository.findByEventAndUserId(eventId, userId);
        if (userId.equals(eventUserEntity.getUserId())) {
            if (confirm) {
                eventUserEntity.setConfirmed(true);
                eventUserEntity.setDeclined(false);
            } else {
                eventUserEntity.setConfirmed(false);
                eventUserEntity.setDeclined(true);
            }
            eventUserEntity.setConfirmationTime(LocalDateTime.now());
            eventUserRepository.save(eventUserEntity);
        }
    }

    /**
     * Register a user for an event.
     *
     * @param eventId Event ID
     * @param userId  User ID
     * @throws ResourceNotFoundException when the event or user is not found
     */
    public void register(final Long eventId, final Long userId) throws ResourceNotFoundException {
        final Event event = get(eventId);
        if (event != null && userService.get(userId) != null) {
            final EventParticipant eventUserEntity = new EventParticipant();
            eventUserEntity.setEventId(eventId);
            eventUserEntity.setUserId(userId);
            // If we're within 24 hours of the start time, treat registrations as an RSVP
            if (event.getStartTime() != null && LocalDateTime.now().isAfter(event.getStartTime().minusDays(1))) {
                eventUserEntity.setConfirmed(true);
                eventUserEntity.setDeclined(false);
                eventUserEntity.setConfirmationTime(LocalDateTime.now());
            }
            eventUserRepository.save(eventUserEntity);
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
        final Event event = get(eventId);
        if (event != null && userService.get(userId) != null) {
            boolean unregistered = false;
            final List<EventParticipant> eventUserEntities = eventUserRepository.findByEventId(eventId);
            for (final EventParticipant eventUserEntity : eventUserEntities) {
                if (userId.equals(eventUserEntity.getUserId())) {
                    eventUserEntity.setConfirmed(false);
                    eventUserEntity.setDeclined(true);
                    eventUserEntity.setConfirmationTime(LocalDateTime.now());
                    eventUserRepository.save(eventUserEntity);
                    unregistered = true;
                    break;
                }
            }
            if (!unregistered) {
                final EventParticipant eventUserEntity = new EventParticipant();
                eventUserEntity.setEventId(eventId);
                eventUserEntity.setUserId(userId);
                eventUserEntity.setConfirmed(false);
                eventUserEntity.setDeclined(true);
                eventUserEntity.setConfirmationTime(LocalDateTime.now());
                eventUserRepository.save(eventUserEntity);
            }
        }
    }

    /**
     * Checks in a user for an event.
     *
     * @param eventId Event ID
     * @param userId  User ID
     * @param code    checkin code
     * @return checkin success
     * @throws ResourceNotFoundException when the event or user is not found
     */
    public boolean checkin(final Long eventId, final Long userId, final String code) throws ResourceNotFoundException {
        boolean success = false;
        final Event event = get(eventId);
        final EventParticipant eventParticipantEntity = eventUserRepository.findByEventAndUserId(eventId, userId);
        if (event != null
                && userService.get(userId) != null
                && eventParticipantEntity != null
                && (eventParticipantEntity.getCheckedIn() == null
                        || !eventParticipantEntity.getCheckedIn())
                && (!event.isCheckinCodeRequired()
                        || (event.isCheckinCodeRequired()
                                && event.getCheckinCode() != null
                                && event.getCheckinCode().equalsIgnoreCase(code)))) {
            eventParticipantEntity.setCheckedIn(true);
            eventParticipantEntity.setCheckinTime(LocalDateTime.now());
            eventUserRepository.save(eventParticipantEntity);
            success = true;
        }
        return success;
    }

    /**
     * Did the user check in to an event?
     *
     * @param eventId Event ID
     * @param userId  User ID
     * @return checked in?
     */
    public boolean didCheckIn(final Long eventId, final Long userId) {
        if (eventId != null && userId != null) {
            final EventParticipant eventUserEntity = eventUserRepository.findByEventAndUserId(eventId, userId);
            if (eventUserEntity != null && eventUserEntity.getCheckedIn() != null) {
                return eventUserEntity.getCheckedIn();
            }
        }
        return false;
    }

    /**
     * Did the user RSVP to an event?
     *
     * @param eventId Event ID
     * @param userId  User ID
     * @return RSVP?
     */
    public boolean didRSVP(final Long eventId, final Long userId) {
        if (eventId != null && userId != null) {
            final EventParticipant eventUserEntity = eventUserRepository.findByEventAndUserId(eventId, userId);
            if (eventUserEntity != null && eventUserEntity.getCheckedIn() != null) {
                return eventUserEntity.getConfirmed() || eventUserEntity.getDeclined();
            }
        }
        return false;
    }

    /**
     * Retrieves all users for an event.
     *
     * @param eventId Event ID
     * @return list of User ID
     */
    public List<Long> getAllEventUsers(final Long eventId) {
        final List<Long> userIds = new ArrayList<>();
        final List<EventParticipant> eventUserEntities = eventUserRepository.findByEventId(eventId);
        if (eventUserEntities != null) {
            for (final EventParticipant eventUserEntity : eventUserEntities) {
                if (!eventUserEntity.getDeclined()) {
                    userIds.add(eventUserEntity.getUserId());
                }
            }
        }
        return userIds;
    }

    /**
     * Retrieves all users for an event who have checked in.
     *
     * @param eventId Event ID
     * @return list of User ID
     */
    public List<Long> getAllEventCheckedInUsers(final Long eventId) {
        final List<Long> userIds = new ArrayList<>();
        for (final Long userId : getAllEventUsers(eventId)) {
            if (didCheckIn(eventId, userId)) {
                userIds.add(userId);
            }
        }
        return userIds;
    }

    /**
     * Retrieves all users for an event who have RSVP'ed.
     *
     * @param eventId Event ID
     * @return list of User ID
     */
    public List<Long> getAllEventRSVPedUsers(final Long eventId) {
        final List<Long> userIds = new ArrayList<>();
        for (final Long userId : getAllEventUsers(eventId)) {
            if (didRSVP(eventId, userId)) {
                userIds.add(userId);
            }
        }
        return userIds;
    }

    /**
     * Is the user checked in to the current event?
     *
     * @param userId User ID
     * @return Event ID for the event in which user is checked in
     * @throws ResourceNotFoundException when the user is not found
     */
    public Long isCheckedIn(final Long userId) throws ResourceNotFoundException {
        final Long currentEventId = getCurrentEvent();
        Long eventId = null;
        if (didCheckIn(currentEventId, userId)) {
            eventId = currentEventId;
        }
        return eventId;
    }

    /**
     * Returns the current (started but not completed) event. If no event is found,
     * null is returned.
     *
     * @return Event ID
     * @throws ResourceNotFoundException when event is not found
     */
    public Long getCurrentEvent() throws ResourceNotFoundException {
        Long eventId = null;
        final List<Event> events = getAll();
        for (final Event event : events) {
            if (event.isStarted() && !event.isCompleted()) {
                eventId = event.getId();
            }
        }
        return eventId;
    }

    /**
     * Is the user registered for an event?
     *
     * @param eventId Event ID
     * @param userId  User ID
     * @return whether or not user is registered
     */
    public boolean isRegistered(final Long eventId, final Long userId) {
        boolean registered = false;
        if (eventId == null || userId == null) {
            return registered;
        }
        final EventParticipant eventParticipant = eventUserRepository.findByEventAndUserId(eventId, userId);
        if (eventParticipant != null && !eventParticipant.getDeclined()) {
            registered = true;
        }
        return registered;
    }

    /**
     * Gets all supporting instructors for the given event.
     *
     * @param eventId Event ID
     * @return list of User IDs
     * @throws ResourceNotFoundException when event ID is not found
     */
    public List<Long> getAllSupportingInstructors(final Long eventId) throws ResourceNotFoundException {
        final List<Long> supportingInstructors = new ArrayList<>();
        final Long eventLead = get(eventId).getLead();
        getAllEventUsers(eventId)
                .stream()
                .distinct()
                .forEach(userId -> {
                    try {
                        if (!Objects.equals(userId, eventLead)
                                && userService.get(userId).getRole() == Role.INSTRUCTOR) {
                            supportingInstructors.add(userId);
                        }
                    } catch (ResourceNotFoundException rnfe) {
                        // Do nothing
                    }
                });
        return supportingInstructors;
    }

    /**
     * Returns a summary of the event specified.
     *
     * @param eventId Event ID
     * @return EvenrSummary
     * @throws ResourceNotFoundException when event is not found
     */
    public EventSummary getEventSummary(final Long eventId) throws ResourceNotFoundException {
        final EventSummary eventSummary = new EventSummary();
        final Event event = get(eventId);
        eventSummary.setId(eventId);
        eventSummary.setTitle(event.getTitle());
        eventSummary.setStartTime(event.getStartTime());
        eventSummary.setPrivateEvent(event.isPrivateEvent());
        eventSummary.setAddress(event.getAddress());
        eventSummary.setParticipantCount(event.getParticipants().size());
        final LessonPlan lessonPlan = lessonPlanService.get(event.getLessonPlanId());
        if (lessonPlan != null && lessonPlan.getActivities() != null) {
            eventSummary.setLessons(
                    lessonPlan
                            .getActivities()
                            .stream()
                            .filter(activity -> activity.getActivityType() == ActivityType.LESSON)
                            .map(activity -> activity.getTitle())
                            .collect(Collectors.toList()));
        }
        final User lead = userService.get(event.getLead());
        eventSummary.setLead(lead.getFirstName() + " " + lead.getLastName());
        // TODO add reference materials
        eventSummary.setReferenceMaterials(new ArrayList<>());
        return eventSummary;
    }

    /**
     * Is the user registered for an event?
     *
     * @param eventId Event ID
     * @param userId  User ID
     * @param member  member
     * @return whether or not user is registered
     * @throws ResourceNotFoundException when the event or user is not found
     */
    public boolean setMembership(final Long eventId, final Long userId, final boolean member)
            throws ResourceNotFoundException {
        final EventParticipant eventParticipantEntity = eventUserRepository.findByEventAndUserId(eventId, userId);
        if (eventParticipantEntity != null) {
            eventParticipantEntity.setMember(member);
            eventUserRepository.save(eventParticipantEntity);
        } else {
            throw new ResourceNotFoundException();
        }
        return true;
    }

    /**
     * Gets whether or not a user is a member for an event.
     *
     * @param eventId Event ID
     * @param userId  User ID
     * @return member
     * @throws ResourceNotFoundException when the event is not found
     */
    public boolean isMember(final long eventId, final long userId) throws ResourceNotFoundException {
        boolean member = false;
        final EventParticipant eventParticipantEntity = eventUserRepository.findByEventAndUserId(eventId, userId);
        if (eventParticipantEntity != null) {
            member = eventParticipantEntity.getMember() != null && eventParticipantEntity.getMember();
        } else {
            throw new ResourceNotFoundException();
        }
        return member;
    }

}
