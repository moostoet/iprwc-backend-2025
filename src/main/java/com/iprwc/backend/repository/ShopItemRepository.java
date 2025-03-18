package com.iprwc.backend.repository;

import com.iprwc.backend.model.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {
}
