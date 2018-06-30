package ru.duytsev.money.transfers.controller;

import ru.duytsev.money.transfers.dto.BalanceChangeDto;
import ru.duytsev.money.transfers.model.Transaction;
import ru.duytsev.money.transfers.repository.AccountRepository;

import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Collection;

import static ru.duytsev.money.transfers.dto.ModelWrapper.wrap;

@Path("/v1")
public class TransactionController {

    private final AccountRepository accountRepository;

    public TransactionController() {
        accountRepository = AccountRepository.getInstance();
    }

    @GET
    @Path("/transfers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        Collection<Transaction> list = accountRepository.listTransactions();
        return Response.ok(wrap(list)).build();
    }

    @POST
    @Path("/transfers")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response transfer(Transaction tx) {
        validateAmount(tx.getAmount());
        Transaction transfer = accountRepository.transfer(tx);
        return Response.ok(wrap(transfer)).build();
    }

    @POST
    @Path("/deposits")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deposit(BalanceChangeDto change) {
        validateAmount(change.getAmount());
        Transaction transfer = accountRepository.changeBalance(change.getAccountId(), change.getAmount());
        return Response.ok(wrap(transfer)).build();
    }

    @POST
    @Path("/withdrawals")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response withdraw(BalanceChangeDto change) {
        validateAmount(change.getAmount());
        Transaction transfer = accountRepository.changeBalance(change.getAccountId(), change.getAmount().negate());
        return Response.ok(wrap(transfer)).build();
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Amount has to be positive number, amount: " + amount);
        }
    }
}

