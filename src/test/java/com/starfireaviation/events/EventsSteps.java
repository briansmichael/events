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

package com.starfireaviation.events;

import com.starfireaviation.common.CommonConstants;
import com.starfireaviation.common.model.Event;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
public class EventsSteps extends BaseSteps {

    @Before
    public void init() {
        testContext.reset();
    }

    @Given("^I have an event")
    public void iHaveAnEvent() throws Throwable {
        testContext.setEvent(new Event());
    }

    @And("^The event has a title with (.*) characters$")
    public void theEventHasATitleWithXCharacters(final int characterCount) throws Throwable {
        // TODO
    }

    @And("^An event exists$")
    public void anEventExists() throws Throwable {
        // TODO
    }

    @When("^I submit the event$")
    public void iAddTheEvent() throws Throwable {
        log.info("I submit the event");
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (testContext.getOrganization() != null) {
            headers.add(CommonConstants.ORGANIZATION_HEADER_KEY, testContext.getOrganization());
        }
        if (testContext.getCorrelationId() != null) {
            headers.add(CommonConstants.CORRELATION_ID_HEADER_KEY, testContext.getCorrelationId());
        }
        //final HttpEntity<Question> httpEntity = new HttpEntity<>(testContext.getQuestion(), headers);
        //testContext.setResponse(restTemplate.postForEntity(URL, httpEntity, Void.class));
    }

    @When("^I submit the event for update$")
    public void iSubmitTheEventForUpdate() {
        // TODO
    }

    @When("^I get the event$")
    public void iGetTheEvent() throws Throwable {
        // TODO
    }

    @When("^I delete the event$")
    public void iDeleteTheEvent() throws Throwable {
        // TODO
    }

    @When("^I get all events$")
    public void iGetAllEvents() throws Throwable {
        // TODO
    }

    @When("^I get the list of supporting instructors$")
    public void iGetTheListOfSupportingInstructors() {
        // TODO
    }

    @When("^I get whether or not a user is a member of an event$")
    public void iGetWhetherOrNotAUserIsAMemberOfAnEvent() throws Throwable {
        // TODO
    }

    @When("^I get the list of checked in participants for an event$")
    public void iGetTheListOfCheckedInParticipantsForAnEvent() throws Throwable {
        // TODO
    }

    @When("^I RSVP for an event$")
    public void iRSVPForAnEvent() {
        // TODO
    }

    @When("^I get the list of upcoming events$")
    public void iGetTheListOfUpcomingEvents() throws Throwable {
        // TODO
    }

    @When("^I register for an event$")
    public void iRegisterForAnEvent() throws Throwable {
        // TODO
    }

    @When("^I unregister for an event$")
    public void iUnregisterForAnEvent() throws Throwable {
        // TODO
    }

    @When("^I get an event's checkin code$")
    public void iGetAnEventCheckinCode() throws Throwable {
        // TODO
    }

    @When("^I checkin a user to an event$")
    public void iCheckinAUserToAnEvent() throws Throwable {
        // TODO
    }

    @When("^I start an event$")
    public void iStartAnEvent() throws Throwable {
        // TODO
    }

    @When("^I complete an event$")
    public void iCompleteAnEvent() throws Throwable {
        // TODO
    }

    @Then("^An event should be received$")
    public void anEventShouldBeReceived() throws Throwable {
        // TODO
    }

    @Then("^The event should be removed$")
    public void theEventShouldBeRemoved() throws Throwable {
        // TODO
    }

}
