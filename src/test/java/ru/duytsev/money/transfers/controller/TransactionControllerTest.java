package ru.duytsev.money.transfers.controller;

import org.apache.http.HttpStatus;
import org.junit.Test;
import ru.duytsev.money.transfers.model.Account;
import ru.duytsev.money.transfers.model.Transaction;
import ru.duytsev.money.transfers.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class TransactionControllerTest extends BaseControllerTest {

    private static final String DEPOSITS = "/deposits";
    private static final String WITHDRAWALS = "/withdrawals";
    private static final String TRANSFERS = "/transfers";

    private AccountRepository accountRepository = AccountRepository.getInstance();

    @Test
    public void testDeposit() {
        Account created = createAccount(BigDecimal.ZERO);
        Map<String, Object> map = new HashMap<>();
        map.put("accountId", created.getId());
        map.put("amount", 300);
        given()
            .contentType(JSON)
            .body(map)
        .when()
            .post(BASE_URL + DEPOSITS)
        .then()
            .contentType(JSON)
            .statusCode(HttpStatus.SC_OK)
            .body("success", equalTo(true))
            .body("data.fromAccountId", equalTo(null))
            .body("data.toAccountId", equalTo(created.getId()))
            .body("data.amount", equalTo(300));

        assertThat(accountRepository.getById(created.getId()).getBalance(), equalTo(BigDecimal.valueOf(300)));
    }

    @Test
    public void testDeposit_zero() {
        Account created = createAccount(BigDecimal.ZERO);
        Map<String, Object> map = new HashMap<>();
        map.put("accountId", created.getId());
        map.put("amount", 0);
        given()
            .contentType(JSON)
            .body(map)
        .when()
            .post(BASE_URL + DEPOSITS)
        .then()
            .contentType(JSON)
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("success", equalTo(false))
            .body("data.errorCode", equalTo(1))
            .body("data.errorMessage", equalTo("Amount has to be positive number, amount: 0"));
    }

    @Test
    public void testWithdrawal() {
        Account created = createAccount(BigDecimal.valueOf(1000));
        Map<String, Object> map = new HashMap<>();
        map.put("accountId", created.getId());
        map.put("amount", 300.100);
        given()
                .contentType(JSON)
                .body(map)
                .when()
                .post(BASE_URL + WITHDRAWALS)
                .then()
                .contentType(JSON)
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("data.fromAccountId", equalTo(created.getId()))
                .body("data.toAccountId", equalTo(null))
                .body("data.amount", equalTo(300.1f));

        assertThat(accountRepository.getById(created.getId()).getBalance(), equalTo(BigDecimal.valueOf(699.900)));
    }

    @Test
    public void testWithdrawal_negative() {
        Account created = createAccount(BigDecimal.valueOf(1000));
        Map<String, Object> map = new HashMap<>();
        map.put("accountId", created.getId());
        map.put("amount", -100.1);
        given()
            .contentType(JSON)
            .body(map)
        .when()
            .post(BASE_URL + WITHDRAWALS)
        .then()
            .contentType(JSON)
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("success", equalTo(false))
            .body("data.errorCode", equalTo(1))
            .body("data.errorMessage", equalTo("Amount has to be positive number, amount: -100.1"));
    }

    @Test
    public void testTransfer() {
        Account from = createAccount(BigDecimal.valueOf(1000));
        Account to = createAccount(BigDecimal.valueOf(0));
        Map<String, Object> map = new HashMap<>();
        map.put("fromAccountId", from.getId());
        map.put("toAccountId", to.getId());
        map.put("amount", 500);

        given()
            .contentType(JSON)
            .body(map)
        .when()
            .post(BASE_URL + TRANSFERS)
        .then()
            .contentType(JSON)
            .statusCode(HttpStatus.SC_OK)
            .body("success", equalTo(true))
            .body("data.fromAccountId", equalTo(from.getId()))
            .body("data.toAccountId", equalTo(to.getId()))
            .body("data.amount", equalTo(500));

        assertThat(accountRepository.getById(from.getId()).getBalance(), equalTo(BigDecimal.valueOf(500)));
        assertThat(accountRepository.getById(to.getId()).getBalance(), equalTo(BigDecimal.valueOf(500)));
    }

    @Test
    public void testTransfer_list() {
        Account from = createAccount(BigDecimal.valueOf(1000));
        Account to = createAccount(BigDecimal.valueOf(0));
        Transaction tx = new Transaction();
        tx.setFromAccountId(from.getId());
        tx.setToAccountId((to.getId()));
        tx.setAmount(BigDecimal.valueOf(300));
        accountRepository.transfer(tx);
        accountRepository.transfer(tx);

        get(BASE_URL + TRANSFERS)
            .then()
                .contentType(JSON)
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("data.size()", greaterThan(1));

    }

    private Account createAccount(BigDecimal balance) {
        Account acc = new Account();
        acc.setBalance(balance);
        return accountRepository.create(acc);
    }
}

