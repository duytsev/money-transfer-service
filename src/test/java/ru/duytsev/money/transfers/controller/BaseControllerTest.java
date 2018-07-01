package ru.duytsev.money.transfers.controller;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.duytsev.money.transfers.Application;

public abstract class BaseControllerTest {

    private static final int PORT = 8085;
    protected static final String BASE_URL = "http://localhost:8085/api/v1";

    @BeforeClass
    public static void setup() throws Exception {
        Application.start(PORT);
    }

    @AfterClass
    public static void teardown() throws Exception {
        Application.stop();
    }
}

