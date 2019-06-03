package com.izam.app.api.transfer;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class TransferRequest {
    String from;
    String to;
    BigDecimal amount;
}


