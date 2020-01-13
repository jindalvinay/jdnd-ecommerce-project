package com.example.demo.controllers;

import java.util.List;

import com.splunk.logging.SplunkCimLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			SplunkCimLogEvent event = new SplunkCimLogEvent("order", "no_user_found");
			event.addField("type", "error");
			event.addField("message", "Cannot submit order because username " + username + " was not found in the database");
			event.addField("user", username);
			log.info(event.toString());
			return ResponseEntity.notFound().build();
		}

		UserOrder order = UserOrder.createFromCart(user.getCart());
		if (order == null) {
			SplunkCimLogEvent event = new SplunkCimLogEvent("order", "no_order_found");
			event.addField("type", "error");
			event.addField("message", "Cannot submit order because username " + username + " does not have a cart");
			event.addField("user", username);
			log.info(event.toString());
			return ResponseEntity.notFound().build();
		}

		orderRepository.save(order);
		SplunkCimLogEvent event = new SplunkCimLogEvent("order", "order_submit_success");
		event.addField("type", "success");
		event.addField("message", "Successfully saved order for username " + username);
		event.addField("user", username);
		log.info(event.toString());

		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			SplunkCimLogEvent event = new SplunkCimLogEvent("order", "history_no_user_found");
			event.addField("type", "error");
			event.addField("message", "Cannot see order history of username " + username + ", because this username was not found in the database");
			event.addField("user", username);
			log.info(event.toString());
			return ResponseEntity.notFound().build();
		}

		SplunkCimLogEvent event = new SplunkCimLogEvent("order", "success");
		event.addField("type", "success");
		event.addField("message", "Successfully returned order history of username " + username);
		event.addField("user", username);
		log.info(event.toString());

		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
