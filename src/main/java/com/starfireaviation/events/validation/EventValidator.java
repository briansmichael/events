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

package com.starfireaviation.events.validation;

import com.starfireaviation.common.exception.AccessDeniedException;
import com.starfireaviation.common.exception.ConflictException;
import com.starfireaviation.common.exception.InvalidPayloadException;
import com.starfireaviation.common.exception.ResourceNotFoundException;
import com.starfireaviation.common.model.Event;
import com.starfireaviation.common.model.Role;
import com.starfireaviation.common.model.User;
import com.starfireaviation.events.model.EventRepository;
import com.starfireaviation.events.service.DataService;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.time.LocalDateTime;

/**
 * EventValidator.
 */
@Slf4j
public class EventValidator {

    /**
     * MINUTE_RANGE.
     */
    public static final long MINUTE_RANGE = 30;

    /**
     * EventRepository.
     */
    private final EventRepository eventRepository;

    /**
     * DataService.
     */
    private final DataService dataService;

    /**
     * EventValidator.
     *
     * @param eRepository EventRepository
     * @param dService    DataService
     */
    public EventValidator(final EventRepository eRepository,
                          final DataService dService) {
        eventRepository = eRepository;
        dataService = dService;
    }

    /**
     * Validates access by an admin or instructor.
     *
     * @param principal Principal
     * @return Logged in user's ID
     * @throws AccessDeniedException when principal user is not permitted to access
     *                               user info
     */
    public Long accessAdminOrInstructor(final Principal principal) throws AccessDeniedException {
        empty(principal);
        final User loggedInUser = dataService.getUser(principal.getName());
        final Role role = loggedInUser.getRole();
        if (role != Role.ADMIN && role != Role.INSTRUCTOR) {
            log.warn(
                    String.format(
                            "%s throwing AccessDeniedException because role is [%s]",
                            "accessAdminOrInstructor()",
                            role));
            throw new AccessDeniedException("Current user is not authorized");
        }
        return loggedInUser.getId();
    }

    /**
     * Validates access by an admin.
     *
     * @param principal Principal
     * @return Logged in user's ID
     * @throws ResourceNotFoundException when principal user is not found
     * @throws AccessDeniedException     when principal user is not permitted to
     *                                   access user info
     */
    public Long accessAdmin(final Principal principal) throws ResourceNotFoundException,
            AccessDeniedException {
        empty(principal);
        final User loggedInUser = dataService.getUser(principal.getName());
        final Role role = loggedInUser.getRole();
        if (role != Role.ADMIN) {
            log.warn(
                    String.format(
                            "%s throwing AccessDeniedException because role is [%s]",
                            "accessAdmin()",
                            role));
            throw new AccessDeniedException("Current user is not authorized");
        }
        return loggedInUser.getId();
    }

    /**
     * Validates access by any authenticated user.
     *
     * @param principal Principal
     * @return Logged in user's ID
     * @throws AccessDeniedException when principal user is not permitted to access
     *                               user info
     */
    public Long accessAnyAuthenticated(final Principal principal) throws AccessDeniedException {
        empty(principal);
        final User loggedInUser = dataService.getUser(principal.getName());
        final Role role = loggedInUser.getRole();
        if (role != Role.ADMIN && role != Role.INSTRUCTOR && role != Role.STUDENT) {
            log.warn(
                    String.format(
                            "%s throwing AccessDeniedException because role is [%s]",
                            "accessAnyAuthenticated()",
                            role));
            throw new AccessDeniedException("Current user is not authorized");
        }
        return loggedInUser.getId();
    }

    /**
     * Validates access by an admin, instructor, or the authenticated user.
     *
     * @param userId    User ID
     * @param principal Principal
     * @return Logged in user's ID
     * @throws AccessDeniedException when principal user is not permitted to access
     *                               user info
     */
    public Long accessAdminInstructorOrSpecificUser(final Long userId, final Principal principal)
            throws AccessDeniedException {
        empty(principal);
        final User loggedInUser = dataService.getUser(principal.getName());
        final Role role = loggedInUser.getRole();
        if (role != Role.ADMIN && role != Role.INSTRUCTOR && userId.longValue() != loggedInUser.getId().longValue()) {
            log.warn(
                    String.format(
                            "%s throwing AccessDeniedException because role is [%s] and userId "
                                    + "is [%s] and loggedInUser ID is [%s]",
                            "accessAdminInstructorOrSpecificUser()",
                            role,
                            userId,
                            loggedInUser.getId()));
            throw new AccessDeniedException("Current user is not authorized");
        }
        return loggedInUser.getId();
    }

    /**
     * Determines if logged in user is an admin.
     *
     * @param principal Principal
     * @return admin user?
     */
    public boolean isAdmin(final Principal principal) {
        boolean admin = false;
        try {
            accessAdmin(principal);
            admin = true;
        } catch (AccessDeniedException | ResourceNotFoundException e) {
            admin = false;
        }
        return admin;
    }

    /**
     * Determines if logged in user is an authenticated user.
     *
     * @param userId    User ID
     * @param principal Principal
     * @return authenticated user?
     */
    public boolean isAuthenticatedUser(final Long userId, final Principal principal) {
        boolean authenticatedUser = false;
        try {
            empty(principal);
            final User loggedInUser = dataService.getUser(principal.getName());
            if (userId == loggedInUser.getId()) {
                authenticatedUser = true;
            }
        } catch (AccessDeniedException ee) {
            authenticatedUser = false;
        }
        return authenticatedUser;
    }

    /**
     * Determines if logged in user is an admin or instructor.
     *
     * @param principal Principal
     * @return admin or instructor user
     */
    public boolean isAdminOrInstructor(final Principal principal) {
        boolean adminOrInstructor = false;
        if (principal == null) {
            return adminOrInstructor;
        }
        try {
            accessAdminOrInstructor(principal);
            adminOrInstructor = true;
        } catch (AccessDeniedException ade) {
            adminOrInstructor = false;
        }
        return adminOrInstructor;
    }

    /**
     * Ensures principal is not null.
     *
     * @param principal Principal
     * @throws AccessDeniedException when principal is null
     */
    private static void empty(final Principal principal) throws AccessDeniedException {
        if (principal == null) {
            log.warn(
                    String.format("%s throwing AccessDeniedException because principal is %s", "empty()", principal));
            throw new AccessDeniedException("No authorization provided");
        }
    }

    /**
     * Event Validation.
     *
     * @param event Event
     * @throws ConflictException       when another event occurs within 30 minutes
     *                                 of the provided event
     * @throws InvalidPayloadException when event information is invalid
     */
    public void validate(final Event event) throws ConflictException, InvalidPayloadException {
        empty(event);
        conflict(event);
    }

    /**
     * Ensures event object is not null.
     *
     * @param event Event
     * @throws InvalidPayloadException when event is null
     */
    private static void empty(final Event event) throws InvalidPayloadException {
        if (event == null) {
            String msg = "No event information was provided";
            log.warn(msg);
            throw new InvalidPayloadException(msg);
        }
    }

    /**
     * Ensures new event does not take place within 30 minutes of another event.
     *
     * @param event Event
     * @throws ConflictException when another event occurs within 30 minutes of the
     *                           provided event
     */
    private void conflict(final Event event) throws ConflictException {
        final LocalDateTime prior = event.getStartTime().minusMinutes(MINUTE_RANGE);
        final LocalDateTime after = event.getStartTime().plusMinutes(MINUTE_RANGE);
        final boolean conflict = eventRepository
                .findAll()
                .stream()
                .filter(eventEntity -> eventEntity.getStartTime().isAfter(LocalDateTime.now()))
                .anyMatch(eventEntity -> {
                    final LocalDateTime entityStartTime = eventEntity.getStartTime();
                    return entityStartTime.isAfter(prior) && entityStartTime.isBefore(after);
                });
        if (conflict) {
            throw new ConflictException(
                    String.format("Another event is scheduled within %s minutes of this event", MINUTE_RANGE));
        }
    }
}
