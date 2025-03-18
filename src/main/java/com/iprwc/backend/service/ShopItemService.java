package com.iprwc.backend.service;

import com.iprwc.backend.model.ShopItem;
import com.iprwc.backend.repository.ShopItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopItemService {
    private final ShopItemRepository shopItemRepository;

    public ShopItem createShopItem(ShopItem shopItem) {
        return shopItemRepository.save(shopItem);
    }
}
