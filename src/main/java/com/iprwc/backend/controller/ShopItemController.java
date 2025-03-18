package com.iprwc.backend.controller;

import com.iprwc.backend.model.ShopItem;
import com.iprwc.backend.repository.ShopItemRepository;
import com.iprwc.backend.service.ShopItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shopitems")
public class ShopItemController {

    private final ShopItemRepository shopItemRepository;
    private final ShopItemService shopItemService;

    public ShopItemController(ShopItemRepository shopItemRepository, ShopItemService shopItemService) {
        this.shopItemRepository = shopItemRepository;
        this.shopItemService = shopItemService;
    }

    // GET /api/shopitems – Returns all shop item data.
    @GetMapping
    public ResponseEntity<List<ShopItem>> getAllShopItems() {
        List<ShopItem> shopItems = shopItemRepository.findAll();
        return ResponseEntity.ok(shopItems);
    }

    // GET /api/shopitems/{id} – Returns a specific shop item by id.
    @GetMapping("/{id}")
    public ResponseEntity<ShopItem> getShopItemById(@PathVariable Long id) {
        return shopItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /api/shopitems – Creates a new shop item.
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createShopItem(@Valid @RequestBody ShopItem shopItem, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        ShopItem createdShopItem = shopItemService.createShopItem(shopItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdShopItem);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShopItem(@PathVariable Long id) {
        return shopItemRepository.findById(id)
                .map(shopItem -> {
                    shopItemRepository.delete(shopItem);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}