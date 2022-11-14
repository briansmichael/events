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
 * EventRepository.
 */
public interface EventRepository extends Repository<EventEntity, Long> {

    /**
     * Deletes an event.
     *
     * @param user Event
     */
    void delete(EventEntity user);

    /**
     * Gets all events.
     *
     * @return list of Events
     */
    Optional<List<EventEntity>> findAll();

    /**
     * Gets an event.
     *
     * @param id Long
     * @return Event
     */
    Optional<EventEntity> findById(long id);

    /**
     * Saves an event.
     *
     * @param user Event
     * @return Event
     */
    EventEntity save(EventEntity user);
}
