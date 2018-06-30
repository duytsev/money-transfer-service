package ru.duytsev.money.transfers.exception;

import org.eclipse.jetty.http.HttpStatus;

import java.math.BigDecimal;

public class NegativeBalanceException extends MoneyTransferException {

    public NegativeBalanceException(BigDecimal balance) {
        super(String.format("Failed to create account with negative balance: %s", balance));
    }

    @Override
    public int errorCode() {
        return 3;
    }

    @Override
    public int httpStatus() {
        return HttpStatus.CONFLICT_409;
    }
}

