package com.coms309.duality;

import com.coms309.duality.model.*;
import com.coms309.duality.repository.ActivityRepository;
import com.coms309.duality.repository.CheckinRepository;
import com.coms309.duality.repository.PersonRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.h2.engine.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class TaylorBauerTest {
    @LocalServerPort
    int port;

    @Autowired
    private CheckinRepository checkinRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ActivityRepository activityRepository;

    private Person newUser;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        // make a user
        newUser = new Person();
        newUser.setFirstName("Elphaba");
        newUser.setLastName("The Witch");
        newUser.setUsername("elphytt");
        newUser.setPassword("wicked12!");
        newUser.setEmail("elphy@gmail.com");
        newUser.setType(UserType.PREMIUM);
        newUser = personRepository.save(newUser);
    }

    @Test
    public void testCreateCheckin() {
        // fake data lol
        Checkin c = new Checkin();
        c.setMentalHealthRating(5);
        c.setPhysicalHealthRating(4);
        c.setWaterAmount(2000);
        c.setNotes("He has risen!");
        c.setSteps(8000);
        c.setSleep(8);

        // create checkin
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(c)
                .when()
                .post("/checkin/create/4");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Checkin created"));
    }
    @Test
    public void testRetrieveCheckin() {
        // get tha checkin
        Response getResponse = RestAssured.given() .when()
                .get("/checkin/" + LocalDate.now() + "/4");

        assertEquals(200, getResponse.getStatusCode());
        List<Checkin> retrievedCheckins = getResponse.getBody().jsonPath().getList(".", Checkin.class);
        assertFalse(retrievedCheckins.isEmpty());

        // make sure data right
        Checkin checkins = retrievedCheckins.get(0);
        assertEquals(5, checkins.getMentalHealthRating());
        assertEquals(4, checkins.getPhysicalHealthRating());
        assertEquals(2000, checkins.getWaterAmount());
        assertEquals("He has risen!", checkins.getNotes());
        assertEquals(8, checkins.getSleep());
        assertEquals(LocalDate.now(), checkins.getDate());

    }

    @Test
    public void testPreviousCheckin() {
        Response previousCheckins = RestAssured.given()
                .when()
                .get("/checkin/sevenDays/19");

        assertEquals(200, previousCheckins.getStatusCode());
        System.out.println(previousCheckins.getBody().asString());

        Response streakResponse = RestAssured.given()
                .when()
                .get("/checkin/streak/19");

        assertEquals(200, streakResponse.getStatusCode());
        System.out.println(streakResponse.getBody().asString());

    }

    // testing completition of activity
    @Test
    public void testCompleteActivity() {
        Response response = RestAssured.given()
                .when()
                .post("/activities/complete/19/4");
        assertEquals(200, response.getStatusCode());
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("Activity completed!") ||
                responseBody.equals("Error"));
    }


    @Test
    public void testCreateActivity() {
        // fake data
        Activity a = new Activity();
        a.setActivityName("Golfing");
        a.setCategory("MOVEMENT");
        a.setDescription("Lets golf yo!");

        // create activity
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(a)
                .when()
                .post("/activities/admin/create-activity/3");
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testGetActivityByName() {
        Response response = RestAssured.given()
                .param("activityName", "Sound Map")
                .param("userId", "4")
                .when()
                .get("/activities/name");

        assertEquals(200, response.getStatusCode());
        List<Activity> activities = response.getBody().jsonPath().getList(".", Activity.class);
        assertFalse(activities.isEmpty());
    }

    @Test
    public void testSubscriptionBuyAndCancel() {
        Payment paymentInfo = new Payment();
        paymentInfo.setCardNumber("4111111111111111");
        paymentInfo.setExpirationDate("12/25");
        paymentInfo.setCvv("123");

        // buy subscription
        Response buyResponse = RestAssured.given()
                .contentType("application/json")
                .body(paymentInfo)
                .when()
                .post("/subscription/buy-subscription/4");
        assertEquals(200, buyResponse.getStatusCode());
        assertTrue(buyResponse.getBody().asString().contains("subscription has been purchased"));
        Person p = personRepository.findById(4);
        assertEquals(p.getType(), UserType.PREMIUM);

    }
    @Test
    public void testCancelSubscription() {
        // cancel subscription
        Response cancelResponse = RestAssured.given()
                .when()
                .put("/subscription/cancel-subscription/4");
        assertEquals(200, cancelResponse.getStatusCode());
        assertTrue(cancelResponse.getBody().asString().contains("subscription has been cancelled"));
        Person p = personRepository.findById(4);
        assertEquals(p.getType(), UserType.DEFAULT);
    }

//    @Test
//    public void testCreateUpdateInfoAccount() {
//        Person newPerson = new Person();
//        newPerson.setFirstName("Girl");
//        newPerson.setLastName("Girl");
//        newPerson.setUsername("justagirl");
//        newPerson.setPassword("sillygirlh!");
//        newPerson.setEmail("girl@gmail.com");
//
//        Response createResponse = RestAssured.given()
//                .contentType("application/json")
//                .body(newPerson)
//                .when()
//                .post("/user/create-account");
//
//        assertEquals(200, createResponse.getStatusCode());
//        assertTrue(createResponse.getBody().asString().contains("account has been created"));
//    }

}
