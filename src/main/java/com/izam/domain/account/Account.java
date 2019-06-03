package com.izam.domain.account;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class Account {
    private String login;
    private BigDecimal amount;
}
