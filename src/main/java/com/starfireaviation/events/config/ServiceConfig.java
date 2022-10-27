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
import com.starfireaviation.groundschool.jobs.UpdateCourses;
import com.starfireaviation.groundschool.model.AddressRepository;
import com.starfireaviation.groundschool.model.AnswerRepository;
import com.starfireaviation.groundschool.model.CommentRepository;
import com.starfireaviation.groundschool.model.EventParticipantRepository;
import com.starfireaviation.groundschool.model.EventRepository;
import com.starfireaviation.groundschool.model.QuestionReferenceMaterialRepository;
import com.starfireaviation.groundschool.model.QuestionRepository;
import com.starfireaviation.groundschool.model.QuizQuestionRepository;
import com.starfireaviation.groundschool.model.QuizRepository;
import com.starfireaviation.groundschool.model.ReferenceMaterialRepository;
import com.starfireaviation.groundschool.model.UserRepository;
import com.starfireaviation.groundschool.service.AddressService;
import com.starfireaviation.groundschool.service.AnswerService;
import com.starfireaviation.groundschool.service.CommentService;
import com.starfireaviation.groundschool.service.EventService;
import com.starfireaviation.groundschool.service.NotificationService;
import com.starfireaviation.groundschool.service.QuestionService;
import com.starfireaviation.groundschool.service.QuizService;
import com.starfireaviation.groundschool.service.ReferenceMaterialService;
import com.starfireaviation.groundschool.service.UserService;
import com.starfireaviation.groundschool.util.GSDecryptor;
import com.starfireaviation.groundschool.validation.CommentValidator;
import com.starfireaviation.groundschool.validation.EventValidator;
import com.starfireaviation.groundschool.validation.QuizValidator;
import com.starfireaviation.groundschool.validation.ReferenceMaterialValidator;
import com.starfireaviation.groundschool.validation.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
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
@EnableConfigurationProperties({
        ApplicationProperties.class,
        CalendarProperties.class,
        TimeoutProperties.class })
public class ServiceConfig {

    /**
     * QuestionService.
     *
     * @param qRepository   QuestionRepository
     * @param qrmRepository QuestionReferenceMaterialRepository
     * @param aService      AnswerService
     * @param rmService     ReferenceMaterialService
     * @return QuestionService
     */
    @Bean
    public QuestionService questionService(final QuestionRepository qRepository,
            final QuestionReferenceMaterialRepository qrmRepository,
            final AnswerService aService,
            final ReferenceMaterialService rmService) {
        return new QuestionService(qRepository, qrmRepository, aService, rmService);
    }

    /**
     * QuizService.
     *
     * @param qRepository  QuizRepository
     * @param qService     QuestionService
     * @param eService     EventService
     * @param qqRepository QuizQuestionRepository
     * @return QuizService
     */
    @Bean
    public QuizService quizService(final QuizRepository qRepository,
            final QuestionService qService,
            final EventService eService,
            final QuizQuestionRepository qqRepository) {
        return new QuizService(qRepository, qService, eService, qqRepository);
    }

    /**
     * AnswerService.
     *
     * @param answerRepository AnswerRepository
     * @return AnswerService
     */
    @Bean
    public AnswerService answerService(final AnswerRepository answerRepository) {
        return new AnswerService(answerRepository);
    }

    /**
     * ReferenceMaterialService.
     *
     * @param rmRepository  ReferenceMaterialRepository
     * @param qrmRepository QuestionReferenceMaterialRepository
     * @return ReferenceMaterialService
     */
    @Bean
    public ReferenceMaterialService referenceMaterialService(final ReferenceMaterialRepository rmRepository,
            final QuestionReferenceMaterialRepository qrmRepository) {
        return new ReferenceMaterialService(rmRepository, qrmRepository);
    }

    /**
     * ReportService.
     *
     * @param qService QuizService
     * @param uService UserService
     * @return ReportService
     */
    @Bean
    public ReportService reportService(final QuizService qService,
            final UserService uService) {
        return new ReportService(qService, uService);
    }

    /**
     * UserService.
     *
     * @param uRepository UserRepository
     * @param template    RestTemplate
     * @return UserService
     */
    @Bean
    public UserService userService(final UserRepository uRepository,
            final RestTemplate template) {
        return new UserService(uRepository, template);
    }

    /**
     * AddressService.
     *
     * @param addressRepository AddressRepository
     * @return AddressService
     */
    @Bean
    public AddressService addressService(final AddressRepository addressRepository) {
        return new AddressService(addressRepository);
    }

    /**
     * CommentService.
     *
     * @param cRepository CommentRepository
     * @return CommentService
     */
    @Bean
    public CommentService commentService(final CommentRepository cRepository) {
        return new CommentService(cRepository);
    }

    /**
     * EventService.
     *
     * @param eRepository  EventRepository
     * @param epRepository EventParticipantRepository
     * @param uService     UserService
     * @param aService     AddressService
     * @param lpService    LessonPlanService
     * @return EventService
     */
    @Bean
    public EventService eventService(final EventRepository eRepository,
            final EventParticipantRepository epRepository,
            final UserService uService,
            final AddressService aService,
            final LessonPlanService lpService) {
        return new EventService(eRepository, epRepository, uService, aService, lpService);
    }

    /**
     * LessonPlanService.
     *
     * @param lpRepository LessonPlanRepository
     * @param aRepostory   ActivityRepository
     * @return LessonPlanService
     */
    @Bean
    public LessonPlanService lessonPlanService(final LessonPlanRepository lpRepository,
            final ActivityRepository aRepostory) {
        return new LessonPlanService(lpRepository, aRepostory);
    }

    /**
     * LessonService.
     *
     * @param lRepository LessonRepository
     * @return LessonService
     */
    @Bean
    public LessonService lessonService(final LessonRepository lRepository) {
        return new LessonService(lRepository);
    }

    /**
     * NotificationService.
     *
     * @param httpClient HttpClient
     * @param objectMapper ObjectMapper
     * @return NotificationService
     */
    @Bean
    public NotificationService notificationService(final HttpClient httpClient,
                                                   final ObjectMapper objectMapper) {
        return new NotificationService(httpClient, objectMapper);
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
     * GroundSchool data decryptor.
     *
     * @param props GroundSchoolProperties
     * @return GSDecryptor
     */
    @Bean
    public GSDecryptor gsDecryptor(final ApplicationProperties props) {
        log.info("Secret Key: {}; Init Vector: {}", props.getSecretKey(), props.getInitVector());
        return new GSDecryptor(props.isGsDecryptorEnabled(), props.getSecretKey(), props.getInitVector());
    }

    /**
     * Creates a rest template with default timeout settings. The bean definition
     * will be updated to accept timeout
     * parameters once those are part of the Customer settings.
     *
     * @param restTemplateBuilder RestTemplateBuilder
     * @param timeoutProperties   TimeoutProperties
     *
     * @return Rest Template with request, read, and connection timeouts set
     */
    @Bean
    public RestTemplate restTemplate(
            final RestTemplateBuilder restTemplateBuilder,
            final TimeoutProperties timeoutProperties) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(timeoutProperties.getConnect()))
                .setReadTimeout(Duration.ofMillis(timeoutProperties.getRead()))
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    /**
     * CommentValidator.
     *
     * @param uService UserService
     * @return CommentValidator
     */
    @Bean
    public CommentValidator commentValidator(final UserService uService) {
        return new CommentValidator(uService);
    }

    /**
     * EventValidator.
     *
     * @param eRepository EventRepository
     * @param uService UserService
     * @return EventValidator
     */
    @Bean
    public EventValidator eventValidator(final EventRepository eRepository,
                                         final UserService uService) {
        return new EventValidator(eRepository, uService);
    }

    /**
     * LessonValidator.
     *
     * @param uService UserService
     * @return LessonValidator
     */
    @Bean
    public LessonValidator lessonValidator(final UserService uService) {
        return new LessonValidator(uService);
    }

    /**
     * UserValidator.
     *
     * @param uService UserService
     * @param uRepository UserRepository
     * @return UserValidator
     */
    @Bean
    public UserValidator userValidator(final UserService uService,
                                       final UserRepository uRepository) {
        return new UserValidator(uService, uRepository);
    }

    /**
     * QuizValidator.
     *
     * @param uService UserService
     * @return QuizValidator
     */
    @Bean
    public QuizValidator quizValidator(final UserService uService) {
        return new QuizValidator(uService);
    }

    /**
     * ReferenceMaterialValidator.
     *
     * @param uService UserService
     * @return ReferenceMaterialValidator
     */
    @Bean
    public ReferenceMaterialValidator referenceMaterialValidator(final UserService uService) {
        return new ReferenceMaterialValidator(uService);
    }

    /**
     * ReportValidator.
     *
     * @param uService UserService
     * @return ReportValidator
     */
    @Bean
    public ReportValidator reportValidator(final UserService uService) {
        return new ReportValidator(uService);
    }

    /**
     * UpdateCourses Quartz Job.
     *
     * @return JobDetail
     */
    @Bean
    public JobDetail updateCourses() {
        return JobBuilder
                .newJob(UpdateCourses.class)
                .withIdentity("updateCourses")
                .storeDurably()
                .build();
    }

    /**
     * UpdateCourses Quartz Trigger.
     *
     * @param updateCoursesJobDetail JobDetail
     * @param props ApplicationProperties
     * @return Trigger
     */
    @Bean
    public Trigger updateCoursesTrigger(final JobDetail updateCoursesJobDetail,
                                        final ApplicationProperties props) {
        return TriggerBuilder
                .newTrigger()
                .forJob(updateCoursesJobDetail)
                .withIdentity("updateCoursesTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule(props.getUpdateCoursesCron()))
                .build();
    }
}
