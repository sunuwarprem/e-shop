package com.prem.ecommerce.repository;

import com.prem.ecommerce.model.Order;
import com.prem.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}