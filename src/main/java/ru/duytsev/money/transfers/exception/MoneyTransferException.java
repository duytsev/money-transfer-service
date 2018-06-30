package ru.duytsev.money.transfers.exception;

public abstract class MoneyTransferException extends RuntimeException {

    public abstract int errorCode();
    public abstract int httpStatus();

    public MoneyTransferException(String message) {
        super(message);
    }

}

