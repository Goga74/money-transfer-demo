package com.izam.domain.account;

import com.izam.app.api.ErrorResponse;

import java.math.BigDecimal;

public interface AccountRepository {
    Account getAccount(final String login);
    Account setMoney(final String login, final BigDecimal value);
    ErrorResponse transferMoney(final String from, final String to, final BigDecimal value);
}
