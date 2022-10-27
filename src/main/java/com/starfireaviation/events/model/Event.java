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

import com.starfireaviation.model.CommonConstants;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event.
 */
@Data
@Entity
@Table(name = "EVENT")
public class Event extends BaseEntity {

    /**
     * Default SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Title.
     */
    @Column(name = "title", nullable = false, length = CommonConstants.TWO_HUNDRED_FIFTY_FIVE)
    private String title;

    /**
     * Event started?
     */
    @Column(name = "started", nullable = false)
    private boolean started = false;

    /**
     * LocalDateTime - startTime.
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * Event completed?
     */
    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    /**
     * LocalDateTime - completedTime.
     */
    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    /**
     * Google calendar URL.
     */
    @Column(name = "calendar_url", nullable = true, length = CommonConstants.TWO_HUNDRED_FIFTY_FIVE)
    private String calendarUrl;

    /**
     * Checkin code.
     */
    @Column(name = "checkin_code", nullable = true, length = CommonConstants.FOUR)
    private String checkinCode;

    /**
     * Checkin code required.
     */
    @Column(name = "checkin_code_required", nullable = false)
    private boolean checkinCodeRequired;

    /**
     * Private (no public notices, only private ones).
     */
    @Column(name = "private", nullable = false)
    private boolean privateEvent = false;

    /**
     * EventType.
     */
    @Column(name = "type", length = CommonConstants.ONE_HUNDRED)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    /**
     * Address.
     */
    @Transient
    private Address address;

    /**
     * LessonPlan ID.
     */
    @Column(name = "lesson_plan_id", nullable = false)
    private Long lessonPlanId;

    /**
     * Event participants.
     */
    @Transient
    private List<User> participants;

    /**
     * Event lead (or primary instructor).
     */
    @Column(name = "lead", nullable = false)
    private Long lead;

}
