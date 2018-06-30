package ru.duytsev.money.transfers.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
    private Integer id;
    private BigDecimal balance;
}

