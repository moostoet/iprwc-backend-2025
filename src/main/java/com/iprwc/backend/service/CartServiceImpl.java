package com.iprwc.backend.service;

import com.iprwc.backend.dto.request.CartItemRequest;
import com.iprwc.backend.model.Cart;
import com.iprwc.backend.model.CartItem;
import com.iprwc.backend.model.ShopItem;
import com.iprwc.backend.model.User;
import com.iprwc.backend.repository.CartRepository;
import com.iprwc.backend.repository.ShopItemRepository;
import com.iprwc.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ShopItemRepository shopItemRepository;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository, ShopItemRepository shopItemRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.shopItemRepository = shopItemRepository;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found."));
    }

    private Cart getOrCreateCart(User user) {
        Cart cart = cartRepository.findCartByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    @Override
    public Cart getCartByEmail(String email) {
        User user = getUserByEmail(email);
        return getOrCreateCart(user);
    }

    @Override
    public Cart addItem(String email, CartItemRequest request) {
        Cart cart = getCartByEmail(email);

        Long shopItemId = request.getProductId();
        if (shopItemId == null) {
            throw new IllegalArgumentException("Product id must not be null.");
        }

        ShopItem shopItem = shopItemRepository.findById(shopItemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Shop item with id " + shopItemId + " not found."));

        int requestedQuantity = request.getQuantity();

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getShopItem().getId().equals(shopItemId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + requestedQuantity;
            if (shopItem.getStock() < newQuantity) {
                throw new IllegalArgumentException("Insufficient stock for shop item with id " +
                        shopItemId + ". Only " + shopItem.getStock() + " available.");
            }

            existingItem.setQuantity(newQuantity);
        } else {
            if (shopItem.getStock() < requestedQuantity) {
                throw new IllegalArgumentException("Insufficient stock for shop item with id " +
                        shopItemId + ". Only " + shopItem.getStock() + " available.");
            }
            CartItem newItem = new CartItem();
            newItem.setShopItem(shopItem);
            newItem.setQuantity(requestedQuantity);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart updateItemQuantity(String email, Long itemId, int quantity) {

        if (quantity <= 0) {
            return removeItem(email, itemId);
        }

        Cart cart = getCartByEmail(email);

        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cart item with id " + itemId + " not found."));

        ShopItem shopItem = itemToUpdate.getShopItem();
        if (shopItem.getStock() < quantity) {
            throw new IllegalArgumentException(
                    "Insufficient stock for shop item with id " + shopItem.getId() +
                            ". Only " + shopItem.getStock() + " available.");
        }

        itemToUpdate.setQuantity(quantity);

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItem(String email, Long itemId) {
        Cart cart = getCartByEmail(email);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        return cartRepository.save(cart);
    }
}