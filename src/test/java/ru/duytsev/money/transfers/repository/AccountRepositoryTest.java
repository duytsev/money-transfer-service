package ru.duytsev.money.transfers.repository;

import org.junit.Before;
import org.junit.Test;
import ru.duytsev.money.transfers.exception.NegativeBalanceException;
import ru.duytsev.money.transfers.exception.NotEnoughMoneyException;
import ru.duytsev.money.transfers.exception.ResourceNotFoundException;
import ru.duytsev.money.transfers.exception.TransactionException;
import ru.duytsev.money.transfers.model.Account;
import ru.duytsev.money.transfers.model.Transaction;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

public class AccountRepositoryTest {

    private AccountRepository accountRepository = AccountRepository.getInstance();

    @Before
    public void beforeEvery() {
        accountRepository.clear();
    }

    @Test
    public void testCreateAccount() {
        accountRepository.create(account(new BigDecimal(100)));
        accountRepository.create(account(new BigDecimal(100)));
        assertThat(accountRepository.count(), equalTo(2));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testCreateAccount_idIsGenerated() {
        Account acc = new Account();
        acc.setId(100);
        acc.setBalance(new BigDecimal(100));
        accountRepository.create(acc);
        assertThat(accountRepository.count(), equalTo(1));
        accountRepository.getById(100); // must throw ex
    }

    @Test(expected = NegativeBalanceException.class)
    public void testCreate_negativeBalance() {
        accountRepository.create(account(new BigDecimal(-100)));
    }

    @Test
    public void testGetByIdSuccess() {
        Account created = accountRepository.create(account(new BigDecimal(100.99)));
        Account found = accountRepository.getById(created.getId());
        assertThat(found, equalTo(created));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetById_resourceNotFound() {
        accountRepository.getById(1000);
    }

    @Test
    public void testListAccounts() {
        accountRepository.create(account(new BigDecimal(100.99)));
        accountRepository.create(account(new BigDecimal(0)));
        accountRepository.create(account(new BigDecimal(0)));
        assertThat(accountRepository.listAccounts().size(), equalTo(3));
    }

    @Test
    public void testChangeBalance() {
        Account created = accountRepository.create(account(new BigDecimal(1000.1)));
        accountRepository.changeBalance(created.getId(), new BigDecimal(300));
        accountRepository.changeBalance(created.getId(), new BigDecimal(-400.1));
        assertThat(accountRepository.getById(created.getId()).getBalance(), is(closeTo(BigDecimal.valueOf(900), BigDecimal.valueOf(0.00001))));
    }

    @Test(expected = NotEnoughMoneyException.class)
    public void testChangeBalance_notEnoughMoney() {
        Account created = accountRepository.create(account(new BigDecimal(200.111)));
        accountRepository.changeBalance(created.getId(), new BigDecimal(-300));
    }

    @Test
    public void testTransfer() {
        Account from = accountRepository.create(account(new BigDecimal(1000)));
        Account to = accountRepository.create(account(new BigDecimal(0)));
        Transaction tx = tx(from.getId(), to.getId(), BigDecimal.valueOf(500));
        accountRepository.transfer(tx);
        assertThat(accountRepository.getById(from.getId()).getBalance(), equalTo(BigDecimal.valueOf(500)));
        assertThat(accountRepository.getById(to.getId()).getBalance(), equalTo(BigDecimal.valueOf(500)));
        assertThat(accountRepository.listTransactions().size(), equalTo(1));
    }

    @Test(expected = NotEnoughMoneyException.class)
    public void testTransfer_notEnoughMoney() {
        Account from = accountRepository.create(account(new BigDecimal(1000.1123)));
        Account to = accountRepository.create(account(new BigDecimal(123312.1323123)));
        Transaction tx = tx(from.getId(), to.getId(), BigDecimal.valueOf(5000.132));
        accountRepository.transfer(tx);
    }

    @Test(expected = TransactionException.class)
    public void testTransfer_sameAccounts() {
        Account from = accountRepository.create(account(new BigDecimal(999.999)));
        Transaction tx = tx(from.getId(), from.getId(), BigDecimal.valueOf(100.132));
        accountRepository.transfer(tx);
    }

    private Account account(BigDecimal balance) {
        Account acc = new Account();
        acc.setBalance(balance);
        return acc;
    }

    private Transaction tx(Integer from, Integer to, BigDecimal amount) {
        Transaction tx = new Transaction();
        tx.setFromAccountId(from);
        tx.setToAccountId(to);
        tx.setAmount(amount);
        return tx;
    }

}

