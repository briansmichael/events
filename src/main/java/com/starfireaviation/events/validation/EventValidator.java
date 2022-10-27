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

import com.starfireaviation.events.exception.ConflictException;
import com.starfireaviation.events.exception.InvalidPayloadException;
import com.starfireaviation.events.model.Event;
import com.starfireaviation.events.model.EventRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * EventValidator.
 */
@Slf4j
public class EventValidator extends BaseValidator {

    /**
     * MINUTE_RANGE.
     */
    public static final long MINUTE_RANGE = 30;

    /**
     * EventRepository.
     */
    private final EventRepository eventRepository;

    /**
     * EventValidator.
     *
     * @param eRepository EventRepository
     * @param uService    UserService
     */
    public EventValidator(final EventRepository eRepository,
                          final UserService uService) {
        super(uService);
        eventRepository = eRepository;
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
