package com.iprwc.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    // The cart item's unique id (primary key)
    private Long id;
    // The quantity of this item in the cart
    private int quantity;
    // The id of the associated shop item
    private Long shopItemId;
    // The name of the shop item (for display purposes)
    private String itemName;
    // The total price for this entry (price * quantity)
    private BigDecimal totalPrice;
}