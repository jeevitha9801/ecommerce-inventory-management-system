package com.jeevitha.inventorysystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @Min(value = 1,
            message = "Price must be greater than 0")
    private double price;

    @Min(value = 1,
            message = "Stock must be greater than 0")
    private int stock;
}