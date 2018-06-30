package ru.duytsev.money.transfers.controller;

import ru.duytsev.money.transfers.model.Account;
import ru.duytsev.money.transfers.repository.AccountRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

import static ru.duytsev.money.transfers.dto.ModelWrapper.wrap;

@Path("/v1/accounts")
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController() {
        accountRepository = AccountRepository.getInstance();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Integer id) {
        Account found = accountRepository.getById(id);
        return Response.ok(wrap(found)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        Collection<Account> list = accountRepository.listAccounts();
        return Response.ok(wrap(list)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Account account) {
        Account created = accountRepository.create(account);
        return Response.ok(wrap(created)).build();
    }
}

