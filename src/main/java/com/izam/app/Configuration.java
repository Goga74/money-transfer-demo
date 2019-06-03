package com.izam.app;

import com.izam.app.errors.GlobalExceptionHandler;
import com.izam.data.account.InMemoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izam.domain.account.AccountRepository;
import com.izam.service.AccountService;

class Configuration {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final com.izam.domain.account.AccountRepository REPOSITORY = new InMemoryRepository();
    private static final AccountService USER_ACCOUNT_SERVICE = new AccountService(REPOSITORY);
    private static final GlobalExceptionHandler GLOBAL_ERROR_HANDLER = new GlobalExceptionHandler(OBJECT_MAPPER);

    static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    static AccountService getUserAccountService() {
        return USER_ACCOUNT_SERVICE;
    }

    // ToDO: implement yet another in-memory-repositories
    static AccountRepository getUserAccountRepository() {
        return REPOSITORY;
    }

    public static GlobalExceptionHandler getErrorHandler() {
        return GLOBAL_ERROR_HANDLER;
    }
}
