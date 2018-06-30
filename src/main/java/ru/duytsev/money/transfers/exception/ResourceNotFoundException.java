package ru.duytsev.money.transfers.exception;

import org.eclipse.jetty.http.HttpStatus;

public class ResourceNotFoundException extends MoneyTransferException {

    public ResourceNotFoundException(Integer id) {
        super(String.format("Resource with id %s was not found", id));
    }

    @Override
    public int errorCode() {
        return 2;
    }

    @Override
    public int httpStatus() {
        return HttpStatus.NOT_FOUND_404;
    }
}

