package ru.duytsev.money.transfers.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;
import ru.duytsev.money.transfers.dto.Error;
import ru.duytsev.money.transfers.dto.ModelWrapper;
import ru.duytsev.money.transfers.exception.MoneyTransferException;
import ru.duytsev.money.transfers.exception.NegativeBalanceException;
import ru.duytsev.money.transfers.exception.NotEnoughMoneyException;
import ru.duytsev.money.transfers.exception.ResourceNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

interface ExceptionHandlers {

    @Provider
    @Slf4j
    class GlobalExceptionHandler implements ExceptionMapper<Throwable> {
        @Override
        public Response toResponse(Throwable exception) {
            log.error("GlobalExceptionHandler", exception);
            return Response.status(HttpStatus.BAD_REQUEST_400).
                    entity(ModelWrapper.wrapError(new Error(1, exception.getMessage()))).
                    type("application/json").
                    build();
        }
    }

    @Provider
    @Slf4j
    class MoneyTransferHandler implements ExceptionMapper<MoneyTransferException> {
        @Override
        public Response toResponse(MoneyTransferException exception) {
            log.error("MoneyTransferHandler", exception);
            return Response.status(exception.httpStatus()).
                    entity(ModelWrapper.wrapError(new Error(exception.errorCode(), exception.getMessage()))).
                    type("application/json").
                    build();
        }
    }

    /*@Provider
    @Slf4j
    class ResourceNotFoundHandler implements ExceptionMapper<ResourceNotFoundException> {
        @Override
        public Response toResponse(ResourceNotFoundException exception) {
            log.error("ResourceNotFoundHandler", exception);
            return Response.status(HttpStatus.NOT_FOUND_404).
                    entity(ModelWrapper.wrapError(new Error(2, exception.getMessage()))).
                    type("application/json").
                    build();
        }
    }

    @Provider
    @Slf4j
    class NegativeBalanceHandler implements ExceptionMapper<NegativeBalanceException> {
        @Override
        public Response toResponse(NegativeBalanceException exception) {
            log.error("NegativeBalanceHandler", exception);
            return Response.status(HttpStatus.CONFLICT_409).
                    entity(ModelWrapper.wrapError(new Error(3, exception.getMessage()))).
                    type("application/json").
                    build();
        }
    }

    @Provider
    @Slf4j
    class NotEnoughMoneyHandler implements ExceptionMapper<NotEnoughMoneyException> {
        @Override
        public Response toResponse(NotEnoughMoneyException exception) {
            log.error("NotEnoughMoneyHandler", exception);
            return Response.status(HttpStatus.NOT_ACCEPTABLE_406).
                    entity(ModelWrapper.wrapError(new Error(4, exception.getMessage()))).
                    type("application/json").
                    build();
        }
    }*/
}

