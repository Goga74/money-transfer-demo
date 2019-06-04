package com.izam.app.api.income;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izam.app.api.Constants;
import com.izam.app.api.PostHandler;
import com.izam.app.api.ResponseEntity;
import com.izam.app.api.StatusCode;
import com.izam.app.api.account.AccountInfoResponse;
import com.izam.app.errors.GlobalExceptionHandler;
import com.izam.domain.account.Account;
import com.izam.service.AccountService;

import java.io.InputStream;

public class IncomeHandler extends PostHandler {
    private final AccountService accountService;

    public IncomeHandler(AccountService accountService, ObjectMapper objectMapper,
                         GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.accountService = accountService;
    }

    @Override
    protected ResponseEntity<AccountInfoResponse> doPost(InputStream is) {
        IncomeRequest incomeRequest = super.readRequest(is, IncomeRequest.class);

        Account accountInfo = accountService.setMoney(incomeRequest.getLogin(),
                incomeRequest.getAmount());

        AccountInfoResponse response = new AccountInfoResponse(accountInfo.getLogin(),
                accountInfo.getAmount());

        if (accountInfo.getAmount() == null) {
            return new ResponseEntity<>(response,
                    getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.NOT_FOUND);
        }

        return new ResponseEntity<>(response,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }
}


