package com.izam.app.api.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izam.app.api.*;
import com.izam.app.errors.GlobalExceptionHandler;
import com.izam.service.AccountService;

import java.io.InputStream;

public class TransferHandler extends PostHandler {
    private final AccountService accountService;

    public TransferHandler(AccountService accountService, ObjectMapper objectMapper,
                           GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.accountService = accountService;
    }

    @Override
    protected ResponseEntity<ErrorResponse> doPost(InputStream is) {
        TransferRequest transferRequest = super.readRequest(is, TransferRequest.class);

        ErrorResponse response = accountService.transferMoney(transferRequest.getFrom(),
                transferRequest.getTo(),
                transferRequest.getAmount());

        return new ResponseEntity<>(response,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON),
                StatusCode.fromRepoStatus(response.getCode()));
    }

}



