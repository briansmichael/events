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

package com.starfireaviation.events.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * EventUserRepository.
 */
public interface EventParticipantRepository extends Repository<EventParticipant, Long> {

    /**
     * Deletes an event user.
     *
     * @param eventUser EventUser
     */
    void delete(EventParticipant eventUser);

    /**
     * Gets all eventUsers.
     *
     * @return list of EventUser
     */
    List<EventParticipant> findAll();

    /**
     * Gets an eventUser.
     *
     * @param id Long
     * @return EventUser
     */
    EventParticipant findById(long id);

    /**
     * Finds all EventUser by eventId.
     *
     * @param eventId event ID
     * @return list of EventUser
     */
    List<EventParticipant> findByEventId(long eventId);

    /**
     * Finds all EventUser by userId.
     *
     * @param userId user ID
     * @return list of EventUser
     */
    List<EventParticipant> findByUserId(long userId);

    /**
     * Finds all EventUser by eventId and userId.
     *
     * @param eventId Event ID
     * @param userId user ID
     * @return list of EventUser
     */
    @Query(value = "SELECT * FROM event_participant WHERE event_id = ?1 AND user_id = ?2 LIMIT 1", nativeQuery = true)
    EventParticipant findByEventAndUserId(long eventId, long userId);

    /**
     * Saves an eventUser.
     *
     * @param eventUser EventUser
     * @return EventUser
     */
    EventParticipant save(EventParticipant eventUser);
}
