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

import java.util.HashMap;
import java.util.Map;

@Service
public class DataService {

    /**
     * Gets a User by username.
     *
     * @param name user name
     * @return User
     */
    public User getUser(final String name) {
        return null;
    }

    /**
     * Gets a User by ID.
     *
     * @param userId user ID
     * @return User
     */
    public User getUser(final Long userId) {
        return null;
    }

    /**
     * Gets a map of the number of times each lesson plan has been presented previously.
     *
     * @return map of lesson plan presentation counts
     */
    public Map<Long, Long> getPastLessonPlanPresentationCounts() {
        return new HashMap<>();
    }

    /**
     * Checks if a LessonPlan exists for the provided ID.
     *
     * @param lessonPlanId LessonPlan ID
     * @return if LessonPlan exists
     */
    public boolean existsLessonPlan(final Long lessonPlanId) {
        return Boolean.FALSE;
    }
}
