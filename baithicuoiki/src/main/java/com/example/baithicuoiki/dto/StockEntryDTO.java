package com.example.baithicuoiki.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StockEntryDTO {
    private Long productId;
    private Long supplierId;
    private int quantity;
    private Double price;
}
