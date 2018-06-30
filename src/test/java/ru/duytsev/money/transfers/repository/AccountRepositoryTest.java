package ru.duytsev.money.transfers.repository;

import org.junit.Before;
import org.junit.Test;
import ru.duytsev.money.transfers.exception.NegativeBalanceException;
import ru.duytsev.money.transfers.exception.ResourceNotFoundException;
import ru.duytsev.money.transfers.model.Account;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class AccountRepositoryTest {

    private AccountRepository accountRepository = AccountRepository.getInstance();

    @Before
    public void beforeEvery() {
        accountRepository.clear();
    }

    @Test
    public void testCreateSuccess() {
        accountRepository.create(account(new BigDecimal(100)));
        accountRepository.create(account(new BigDecimal(100)));
        assertThat(accountRepository.count(), equalTo(2));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testCreateSuccess_idIsGenerated() {
        Account acc = new Account();
        acc.setId(100);
        acc.setBalance(new BigDecimal(100));
        accountRepository.create(acc);
        assertThat(accountRepository.count(), equalTo(1));
        accountRepository.getById(100); // must throw ex
    }

    @Test(expected = NegativeBalanceException.class)
    public void testCreateFail_negativeBalance() {
        accountRepository.create(account(new BigDecimal(-100)));
    }

    @Test
    public void testGetByIdSuccess() {
        Account created = accountRepository.create(account(new BigDecimal(100.99)));
        Account found = accountRepository.getById(created.getId());
        assertThat(found, equalTo(created));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetByIdFail_resourceNotFound() {
        accountRepository.getById(1000);
    }

    private Account account(BigDecimal balance) {
        Account acc = new Account();
        acc.setBalance(balance);
        return acc;
    }

}

