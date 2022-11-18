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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.starfireaviation.common.CommonConstants;
import com.starfireaviation.events.model.EventParticipantRepository;
import com.starfireaviation.events.model.EventRepository;
import com.starfireaviation.events.model.VoteRepository;
import com.starfireaviation.events.service.DataService;
import com.starfireaviation.events.service.EventService;
import com.starfireaviation.events.validation.EventValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * ServiceConfig.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({ ApplicationProperties.class })
public class ServiceConfig {

    /**
     * EventService.
     *
     * @param eRepository EventRepository
     * @param vRepository VoteRepository
     * @param epRepository EventParticipantRepository
     * @param dService DataService
     * @return EventService
     */
    @Bean
    public EventService eventService(final EventRepository eRepository,
                                     final VoteRepository vRepository,
                                     final EventParticipantRepository epRepository,
                                     final DataService dService) {
        return new EventService(eRepository, vRepository, epRepository, dService);
    }


    /**
     * HttpClient.
     *
     * @return HttpClient
     */
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    /**
     * ObjectMapper.
     *
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * Creates a rest template with default timeout settings. The bean definition
     * will be updated to accept timeout
     * parameters once those are part of the Customer settings.
     *
     * @param restTemplateBuilder RestTemplateBuilder
     * @param props   ApplicationProperties
     *
     * @return Rest Template with request, read, and connection timeouts set
     */
    @Bean
    public RestTemplate restTemplate(
            final RestTemplateBuilder restTemplateBuilder,
            final ApplicationProperties props) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(props.getConnectTimeout()))
                .setReadTimeout(Duration.ofMillis(props.getReadTimeout()))
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    /**
     * EventValidator.
     *
     * @param dService DataService
     * @return EventValidator
     */
    @Bean
    public EventValidator eventValidator(final DataService dService) {
        return new EventValidator(dService);
    }

    /**
     * Hazelcast Events Instance.
     *
     * @return HazelcastInstance
     */
    @Bean("events")
    public HazelcastInstance hazelcastQuestionsInstance() {
        return Hazelcast.newHazelcastInstance(
                new Config().addMapConfig(
                        new MapConfig("events")
                                .setTimeToLiveSeconds(CommonConstants.THREE_HUNDRED)
                                .setMaxIdleSeconds(CommonConstants.THREE_HUNDRED)));
    }

}
