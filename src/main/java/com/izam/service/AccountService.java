package com.izam.service;

import com.izam.app.api.ErrorResponse;
import com.izam.domain.account.Account;
import com.izam.domain.account.AccountRepository;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account getAccount(final String login) { return accountRepository.getAccount(login); }

    public Account setMoney(final String login, final BigDecimal value) {
        return accountRepository.setMoney(login, value);
    }

    public ErrorResponse transferMoney(final String from, final String to, final BigDecimal value) {
        return accountRepository.transferMoney(from, to, value);
    }
}
