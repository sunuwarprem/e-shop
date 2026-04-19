package com.prem.ecommerce.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    private Long productId;
    private String productName;
    private double price;
    private int quantity;
    private String imageUrl;

    public double getSubtotal() {
        return price * quantity;
    }
}