package ru.duytsev.money.transfers.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceChangeDto {
    private Integer accountId;
    private BigDecimal amount;
}

