package com.izam.app.api.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izam.app.api.Constants;
import com.izam.app.api.Handler;
import com.izam.app.api.ResponseEntity;
import com.izam.app.api.StatusCode;
import com.izam.app.errors.ApplicationExceptions;
import com.izam.app.errors.GlobalExceptionHandler;
import com.izam.domain.account.Account;
import com.izam.service.AccountService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static com.izam.app.api.ApiUtils.splitQuery;

public class AccountInfoHandler extends Handler {
    private final AccountService accountService;

    public AccountInfoHandler(AccountService accountService, ObjectMapper objectMapper,
                              GlobalExceptionHandler exceptionHandler) {
        super(objectMapper, exceptionHandler);
        this.accountService = accountService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws IOException {
        byte[] response;
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
            String login;
            try {
                login = params.get("login").stream().findFirst().orElse(null);
            } catch (java.lang.NullPointerException npe) {
                throw ApplicationExceptions.invalidRequestWithMessage(
                        "Mandatory parameter 'login' expected").get();
            }

            try {
                ResponseEntity e = doGet(login);
                exchange.getResponseHeaders().putAll(e.getHeaders());
                exchange.sendResponseHeaders(e.getStatusCode().getCode(), 0);
                response = super.writeResponse(e.getBody());
            } catch (java.lang.NullPointerException npe) {
                throw ApplicationExceptions.notFound(
                        String.format("Account with login '%s' not found", login)).get();
            }
        } else {
            throw ApplicationExceptions.methodNotAllowed(
                    "Method " + exchange.getRequestMethod() + " is not allowed for " + exchange.getRequestURI()).get();
        }

        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }

    private ResponseEntity<AccountInfoResponse> doGet(final String login) {
        Account accountInfo = accountService.getAccount(login);

        AccountInfoResponse response = new AccountInfoResponse(accountInfo.getLogin(),
                accountInfo.getAmount());

        return new ResponseEntity<>(response,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }
}
