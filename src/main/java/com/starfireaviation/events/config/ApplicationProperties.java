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

package com.starfireaviation.events.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * GroundSchool Properties.
 */
@Getter
@Setter
@ConfigurationProperties("groundschool")
public class ApplicationProperties {

    /**
     * GSDecryptor enabled flag.
     */
    private boolean gsDecryptorEnabled;

    /**
     * SecretKey.
     */
    private String secretKey;

    /**
     * Init Vector.
     */
    private String initVector;

    /**
     * Database location.
     */
    private String dbLocation;

    /**
     * Content source location.
     */
    private String contentSourceLocation;

    /**
     * Update Courses Cron Expression.
     */
    private String updateCoursesCron;

    /**
     * Course Update Map.
     *
     * Note: key = course; value = cron
     */
    private Map<String, String> courseUpdateJobs;

    /**
     * UI Host.
     */
    private String uiHost;
}
