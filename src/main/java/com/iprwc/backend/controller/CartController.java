package com.iprwc.backend.controller;

import com.iprwc.backend.dto.request.CartItemRequest;
import com.iprwc.backend.dto.response.CartItemResponse;
import com.iprwc.backend.dto.response.CartResponse;
import com.iprwc.backend.dto.response.UserResponse;
import com.iprwc.backend.model.Cart;
import com.iprwc.backend.model.CartItem;
import com.iprwc.backend.model.User;
import com.iprwc.backend.service.CartService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /api/cart
    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        System.out.println("GET /api/cart");
        System.out.println("Authentication: " + authentication.getName() + " " + authentication.getAuthorities());
        String email = getEmail(authentication);
        Cart cart = cartService.getCartByEmail(email);
        return ResponseEntity.ok(toCartResponse(cart));
    }

    // POST /api/cart/items
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@RequestBody CartItemRequest itemRequest,
                                                Authentication authentication) {
        String email = getEmail(authentication);
        Cart cart = cartService.addItem(email, itemRequest);
        return ResponseEntity.ok(toCartResponse(cart));
    }

    // DELETE /api/cart/items/{itemId}
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable("itemId") Long itemId,
                                                   Authentication authentication) {
        String email = getEmail(authentication);
        Cart cart = cartService.removeItem(email, itemId);
        return ResponseEntity.ok(toCartResponse(cart));
    }

    @PatchMapping("/items/{itemId}/quantity/{quantity}")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @PathVariable Long itemId,
            @PathVariable int quantity,
            Authentication authentication) {
        Cart updatedCart = cartService.updateItemQuantity(authentication.getName(), itemId, quantity);
        return ResponseEntity.ok(toCartResponse(updatedCart));
    }

    // Helper to get the authenticated user's email
    private String getEmail(Authentication authentication) {
        return authentication.getName();
    }

    // Converts a Cart entity to a sanitized CartResponse DTO.
    private CartResponse toCartResponse(Cart cart) {
        User user = cart.getUser();
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::toCartItemResponse)
                .collect(Collectors.toList());

        return CartResponse.builder()
                .id(cart.getId())
                .user(userResponse)
                .items(itemResponses)
                .build();
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        BigDecimal price = item.getShopItem().getPrice();
        int quantity = item.getQuantity();
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));

        return CartItemResponse.builder()
                .id(item.getId())
                .quantity(quantity)
                .shopItemId(item.getShopItem().getId())
                .itemName(item.getShopItem().getName())
                .totalPrice(totalPrice)
                .build();
    }

    // Exception handler for known errors.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Return a 400 Bad Request error along with the error message.
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
    }

    // Catch-all exception handler.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred."));
    }

    // Simple DTO for error responses.
    @Data
    public static class ErrorResponse {
        private String message;

        public ErrorResponse() {
        }

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}