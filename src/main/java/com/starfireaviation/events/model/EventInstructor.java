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

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * EventInstructor.
 */
@Data
@Entity
@Table(name = "EVENT_INSTRUCTOR")
public class EventInstructor extends BaseEntity {

    /**
     * Event ID.
     */
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    /**
     * User ID.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Confirmed.
     */
    @Column(name = "confirmed", nullable = true)
    private Boolean confirmed;

    /**
     * Declined.
     */
    @Column(name = "declined", nullable = true)
    private Boolean declined;

    /**
     * LocalDateTime - confirmation time.
     */
    @Column(name = "confirmation_time", nullable = true)
    private LocalDateTime confirmationTime;

    /**
     * Checked in.
     */
    @Column(name = "checked_in", nullable = true)
    private Boolean checkedIn;

    /**
     * LocalDateTime - checkin time.
     */
    @Column(name = "checkin_time", nullable = true)
    private LocalDateTime checkinTime;

}
