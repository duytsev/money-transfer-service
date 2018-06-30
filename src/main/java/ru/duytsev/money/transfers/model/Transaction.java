package ru.duytsev.money.transfers.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {
    private Integer id;
    private Integer fromAccountId;
    private Integer toAccountId;
    private BigDecimal amount;
}

