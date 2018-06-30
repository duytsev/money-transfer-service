package ru.duytsev.money.transfers.exception;

import org.eclipse.jetty.http.HttpStatus;

public class NotEnoughMoneyException extends MoneyTransferException {

    public NotEnoughMoneyException(Integer accountId) {
        super(String.format("There is not enough money to withdraw from account: %s", accountId));
    }

    @Override
    public int errorCode() {
        return 4;
    }

    @Override
    public int httpStatus() {
        return HttpStatus.NOT_ACCEPTABLE_406;
    }
}

