package ru.duytsev.money.transfers.controller;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.duytsev.money.transfers.Application;
import ru.duytsev.money.transfers.dto.ModelWrapper;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class AccountControllerTest {

    private static final int PORT = 8085;
    private static final String BASE_URL = "http://localhost:8085/api/v1";
    private static final String ACCOUNTS = "/accounts";

    @BeforeClass
    public static void setup() throws Exception {
        Application.start(PORT);
    }

    @AfterClass
    public static void teardown() throws Exception {
        Application.stop();
    }

    @Test
    public void testAccountCreate() {
        createAccount(100.123)
            .then()
                .contentType(JSON)
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("data.balance", equalTo(100.123f));
    }

    @Test
    public void testAccountCreate_negativeBalance() {
        createAccount(-300.1)
            .then()
                .contentType(JSON)
                .statusCode(HttpStatus.SC_CONFLICT)
                .body("success", equalTo(false))
                .body("data.errorCode", equalTo(3))
                .body("data.errorMessage", equalTo("Failed to create account with negative balance: -300.1"));

    }

    @Test
    public void testGetById() {
        ModelWrapper created = createAccount(1234)
                .thenReturn()
                .as(ModelWrapper.class);
        Map data = (Map) created.getData();

        get(BASE_URL + ACCOUNTS + "/" + data.get("id"))
            .then()
                .contentType(JSON)
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("data.balance", equalTo(1234f));;

    }

    @Test
    public void testGetById_resourceNotFound() {
        get(BASE_URL + ACCOUNTS + "/123")
            .then()
                .contentType(JSON)
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("success", equalTo(false))
                .body("data.errorCode", equalTo(2))
                .body("data.errorMessage", equalTo("Resource with id 123 was not found"));
    }

    @Test
    public void testList() {
        createAccount(623.099);
        createAccount(1231.0103);

        get(BASE_URL + ACCOUNTS)
            .then()
                .contentType(JSON)
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("data.size()", greaterThan(1));
    }

    private Response createAccount(double balance) {
        Map<String, Object> map = new HashMap<>();
        map.put("balance", balance);
        return
            given()
                .contentType(JSON)
                .body(map)
            .when()
                .post(BASE_URL + ACCOUNTS);
    }
}

