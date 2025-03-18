package com.iprwc.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long id;
    private UserResponse user;
    private List<CartItemResponse> items;
}