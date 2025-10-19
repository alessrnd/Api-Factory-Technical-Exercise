package com.vaudoiseassurances.technicalexercise.dto;

import java.math.BigDecimal;

public record ContractCostSumDTO(
    Long clientId,
    BigDecimal totalCostAmount
) {}