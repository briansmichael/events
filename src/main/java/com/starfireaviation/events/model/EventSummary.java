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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * EventSummary.
 */
@Data
public class EventSummary implements Serializable {

    /**
     * Default SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Event ID.
     */
    private Long id;

    /**
     * Title.
     */
    private String title;

    /**
     * LocalDateTime - startTime.
     */
    private LocalDateTime startTime;

    /**
     * Private (no public notices, only private ones).
     */
    private boolean privateEvent = false;

    /**
     * Lessons.
     */
    private List<String> lessons;

    /**
     * ReferenceMaterials.
     */
    private List<ReferenceMaterial> referenceMaterials;

    /**
     * Address.
     */
    private Address address;

    /**
     * Event participants.
     */
    private int participantCount;

    /**
     * Event lead (or primary instructor).
     */
    private String lead;

}
