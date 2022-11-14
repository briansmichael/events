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

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * EventParticipantRepository.
 */
public interface EventParticipantRepository extends Repository<EventParticipant, Long> {

    /**
     * Deletes an entry.
     *
     * @param eventParticipant EventParticipant
     */
    void delete(EventParticipant eventParticipant);

    /**
     * Gets an entry.
     *
     * @param id Long
     * @return EventParticipant
     */
    Optional<EventParticipant> findById(Long id);

    /**
     * Gets all EventParticipants for a given event.
     *
     * @param eventId Event ID
     * @return list of EventParticipant
     */
    Optional<List<EventParticipant>> findByEventId(Long eventId);

    /**
     * Gets all EventParticipants for a given user.
     *
     * @param userId User ID
     * @return list of EventParticipant
     */
    Optional<List<EventParticipant>> findByUserId(Long userId);

    /**
     * Saves an entry.
     *
     * @param eventParticipant EventParticipant
     * @return EventParticipant
     */
    EventParticipant save(EventParticipant eventParticipant);
}
