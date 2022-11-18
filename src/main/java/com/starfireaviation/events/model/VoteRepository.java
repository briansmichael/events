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
 * VoteRepository.
 */
public interface VoteRepository extends Repository<VoteEntity, Long> {

    /**
     * Deletes a vote.
     *
     * @param vote Vote
     */
    void delete(VoteEntity vote);

    /**
     * Gets all votes by event.
     *
     * @param eventId Long
     * @return list of Votes
     */
    Optional<List<VoteEntity>> findByEventId(Long eventId);

    /**
     * Gets vote by event and user.
     *
     * @param eventId Long
     * @param userId Long
     * @return list of Votes
     */
    Optional<VoteEntity> findByEventIdAndUserId(Long eventId, Long userId);

    /**
     * Saves an event.
     *
     * @param vote Vote
     * @return Vote
     */
    VoteEntity save(VoteEntity vote);
}
