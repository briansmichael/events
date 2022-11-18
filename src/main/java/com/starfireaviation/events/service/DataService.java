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

import com.starfireaviation.common.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataService {

    /**
     * Gets a User by username.
     *
     * @param name user name
     * @return User
     */
    public User getUser(final String name) {
        // TODO call GET https://users.starfireaviation.com/api/users?username={name}
        final Long userId = null;
        if (userId != null) {
            return getUser(userId);
        }
        return null;
    }

    /**
     * Gets a User by ID.
     *
     * @param userId user ID
     * @return User
     */
    public User getUser(final Long userId) {
        // TODO call GET https://users.starfireaviation.com/api/users/{userId}
        return null;
    }

    /**
     * Checks if a LessonPlan exists for the provided ID.
     *
     * @param lessonPlanId LessonPlan ID
     * @return if LessonPlan exists
     */
    public boolean existsLessonPlan(final Long lessonPlanId) {
        // TODO call GET https://lessons.starfireaviation.com/api/lessonplans/{lessonPlanId}
        return Boolean.FALSE;
    }

    /**
     * Gets all presentable LessonPlans.
     *
     * @return list of LessonPlan IDs
     */
    public List<Long> getAllPresentableLessonPlans() {
        // TODO call GET https://lessons.starfireaviation.com/api/lessonplans?group=PVT?presentable=true
        return new ArrayList<>();
    }
}
