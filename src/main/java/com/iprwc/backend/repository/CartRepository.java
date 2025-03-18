package com.iprwc.backend.repository;

import com.iprwc.backend.model.Cart;
import com.iprwc.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findCartByUser(User user);
}
