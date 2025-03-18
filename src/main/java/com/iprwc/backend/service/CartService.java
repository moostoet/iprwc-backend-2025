package com.iprwc.backend.service;

import com.iprwc.backend.dto.request.CartItemRequest;
import com.iprwc.backend.model.Cart;

public interface CartService {
    Cart getCartByEmail(String email);
    Cart addItem(String email, CartItemRequest request);
    Cart removeItem(String email, Long itemId);
    Cart updateItemQuantity(String email, Long itemId, int quantity);
}
