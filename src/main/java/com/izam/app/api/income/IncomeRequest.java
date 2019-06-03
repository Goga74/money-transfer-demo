package com.izam.app.api.income;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class IncomeRequest {
    String login;
    BigDecimal amount;
}



