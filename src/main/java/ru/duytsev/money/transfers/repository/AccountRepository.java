package ru.duytsev.money.transfers.repository;

import ru.duytsev.money.transfers.exception.NegativeBalanceException;
import ru.duytsev.money.transfers.exception.NotEnoughMoneyException;
import ru.duytsev.money.transfers.exception.ResourceNotFoundException;
import ru.duytsev.money.transfers.exception.TransactionException;
import ru.duytsev.money.transfers.model.Account;
import ru.duytsev.money.transfers.model.Transaction;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// singleton
public class AccountRepository {

    private static volatile AccountRepository INSTANCE;

    private AtomicInteger accountSequence = new AtomicInteger(0);
    private Map<Integer, Account> accounts;

    private AtomicInteger transactionSequence = new AtomicInteger(0);
    private Map<Integer, Transaction> transactions;

    private Map<Integer, Object> locks;

    private AccountRepository() {
        accounts = new ConcurrentHashMap<>();
        transactions = new ConcurrentHashMap<>();
        locks = new ConcurrentHashMap<>();
    }

    public static AccountRepository getInstance() {
        AccountRepository localInstance = INSTANCE;
        if (localInstance == null) {
            synchronized (AccountRepository.class) {
                localInstance = INSTANCE;
                if (localInstance == null) {
                    INSTANCE = localInstance = new AccountRepository();
                }
            }
        }
        return localInstance;
    }

    public Account create(Account account) {
        validateBalance(account.getBalance());
        account.setId(accountSequence.incrementAndGet());
        accounts.put(account.getId(), account);
        return account;
    }

    public Account getById(Integer id) {
        Account found = accounts.get(id);
        checkAccount(id, found);
        return found;
    }

    public Collection<Account> listAccounts() {
        return accounts.values();
    }

    public int count() {
        return accounts.size();
    }

    public void clear() {
        accounts.clear();
    }

    public Collection<Transaction> listTransactions() {
        return transactions.values();
    }

    public Transaction changeBalance(Integer accountId, BigDecimal delta) {
        Account account = getById(accountId);
        synchronized (locks.computeIfAbsent(accountId, key -> new Object())) {
            BigDecimal newBalance = account.getBalance().add(delta);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new NotEnoughMoneyException(account.getId());
            }

            account.setBalance(newBalance);
        }
        Transaction tx = new Transaction();
        tx.setId(transactionSequence.incrementAndGet());
        tx.setAmount(delta.abs());
        if (delta.compareTo(BigDecimal.ZERO) > 0) {
            tx.setToAccountId(accountId);
        } else {
            tx.setFromAccountId(accountId);
        }
        return transactions.compute(tx.getId(), (k,v) -> tx);
    }

    public Transaction transfer(Transaction tx) {
        checkTransaction(tx);
        Account accFrom = getById(tx.getFromAccountId());
        Account accTo = getById(tx.getToAccountId());
        synchronized (locks.computeIfAbsent(Math.min(accFrom.getId(), accTo.getId()), k -> new Object())) {
            synchronized (locks.computeIfAbsent(Math.max(accFrom.getId(), accTo.getId()), k -> new Object())) {
                BigDecimal accFromNewBalance = accFrom.getBalance().subtract(tx.getAmount());
                if (accFromNewBalance.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NotEnoughMoneyException(accFrom.getId());
                }
                BigDecimal accToNewBalance = accTo.getBalance().add(tx.getAmount());
                accFrom.setBalance(accFromNewBalance);
                accTo.setBalance(accToNewBalance);
            }
        }
        tx.setId(transactionSequence.incrementAndGet());
        return transactions.compute(tx.getId(), (k,v) -> tx);
    }

    private void validateBalance(BigDecimal balance) {
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeBalanceException(balance);
        }
    }

    private void checkAccount(Integer id, Account acc) {
        if (acc == null) {
            throw new ResourceNotFoundException(id);
        }
    }

    private void checkTransaction(Transaction tx) {
        if (Objects.equals(tx.getFromAccountId(), tx.getToAccountId())) {
            throw new TransactionException(tx);
        }
    }
}

