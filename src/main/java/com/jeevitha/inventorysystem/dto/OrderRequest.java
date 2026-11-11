package com.jeevitha.inventorysystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Min(value = 1,
            message = "Quantity must be greater than 0")
    private int quantity;
}
