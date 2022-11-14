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

import com.starfireaviation.common.CommonConstants;
import com.starfireaviation.common.model.EventType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Event.
 */
@Data
@Entity
@Table(name = "EVENT")
public class EventEntity implements Serializable {

    /**
     * Default SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Created At.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Date createdAt = new Date();

    /**
     * Updated At.
     */
    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Date updatedAt = new Date();

    /**
     * Title.
     */
    @Column(name = "title", nullable = false)
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
    @Column(name = "calendar_url")
    private String calendarUrl;

    /**
     * Checkin code.
     */
    @Column(name = "checkin_code", length = CommonConstants.FOUR)
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
     * LessonPlan ID.
     */
    @Column(name = "lesson_plan_id", nullable = false)
    private Long lessonPlanId;

    /**
     * Event lead (or primary instructor).
     */
    @Column(name = "lead", nullable = false)
    private Long lead;

}
