package ru.duytsev.money.transfers.exception;

import org.eclipse.jetty.http.HttpStatus;
import ru.duytsev.money.transfers.model.Transaction;

public class TransactionException extends MoneyTransferException {

    public TransactionException(Transaction tx) {
        super("Could not perform transaction: " + tx);
    }

    @Override
    public int errorCode() {
        return 5;
    }

    @Override
    public int httpStatus() {
        return HttpStatus.CONFLICT_409;
    }
}

