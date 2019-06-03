package com.izam.app.api.account;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class AccountInfoResponse {
    String login;
    BigDecimal amount;
}
