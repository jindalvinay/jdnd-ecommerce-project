package com.example.demo.model.persistence.repositories;

import java.util.List;

import com.example.demo.model.persistence.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.persistence.UserOrder;

public interface OrderRepository extends JpaRepository<UserOrder, Long> {
	List<UserOrder> findByUser(User user);
}
