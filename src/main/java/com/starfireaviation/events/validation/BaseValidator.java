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

import com.starfireaviation.events.exception.AccessDeniedException;
import com.starfireaviation.model.Role;
import com.starfireaviation.model.User;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;

/**
 * BaseValidator.
 */
@Slf4j
public class BaseValidator {

    /**
     * UserService.
     */
    private final UserService userService;

    /**
     * BaseValidator.
     *
     * @param uService UserService
     */
    public BaseValidator(final UserService uService) {
        userService = uService;
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
        final User loggedInUser = userService.findByUsername(principal.getName());
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
        final User loggedInUser = userService.findByUsername(principal.getName());
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
        final User loggedInUser = userService.findByUsername(principal.getName());
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
        final User loggedInUser = userService.findByUsername(principal.getName());
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
            final User loggedInUser = userService.findByUsername(principal.getName());
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
}
