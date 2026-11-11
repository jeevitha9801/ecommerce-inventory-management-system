package com.jeevitha.inventorysystem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {

        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
        private String status;
}

