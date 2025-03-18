package com.iprwc.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each CartItem is part of a specific cart
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // And refers to one ShopItem
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_item_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ShopItem shopItem;

    // Quantity requested for this item
    private int quantity;
}
