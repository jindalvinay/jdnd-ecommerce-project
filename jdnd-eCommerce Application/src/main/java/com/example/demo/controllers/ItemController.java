package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import com.splunk.logging.SplunkCimLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {
	Logger log = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		List<Item> items = itemRepository.findAll();
		if (CollectionUtils.isEmpty(items)) {
			SplunkCimLogEvent event = new SplunkCimLogEvent("get items", "get_items_empty_list");
			event.addField("type", "error");
			event.addField("message", "No Items found");
			log.info(event.toString());
			return ResponseEntity.notFound().build();
		}
		SplunkCimLogEvent event = new SplunkCimLogEvent("get items", "get_items_success");
		event.addField("type", "success");
		event.addField("message", "Items are returned");
		log.info(event.toString());
		return ResponseEntity.ok(items);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {

		Optional<Item> optionalItem = itemRepository.findById(id);

		if (!optionalItem.isPresent()) {
			SplunkCimLogEvent event = new SplunkCimLogEvent("get item by id", "get_item_by_id_not_found");
			event.addField("type", "error");
			event.addField("message", "Item with id " + id + " not found");
			log.info(event.toString());
			ResponseEntity.notFound().build();
		}
		SplunkCimLogEvent event = new SplunkCimLogEvent("get item by id", "get_item_by_id_success");
		event.addField("type", "success");
		event.addField("message", "Item id " + id + " was returned");
		log.info(event.toString());

		return ResponseEntity.of(optionalItem);
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		List<Item> items = itemRepository.findByName(name);

		if(CollectionUtils.isEmpty(items)){
			SplunkCimLogEvent event = new SplunkCimLogEvent("get item by name", "get_item_by_name_not_found");
			event.addField("type", "error");
			event.addField("message", "No items for name " + name + " were found in the database");
			log.info(event.toString());
			return ResponseEntity.notFound().build();
		}

		SplunkCimLogEvent event = new SplunkCimLogEvent("get item by name", "get_item_by_name_success");
		event.addField("type", "success");
		event.addField("message", "Item " + name + " was returned");
		log.info(event.toString());

		return items == null || items.isEmpty() ? ResponseEntity.notFound().build()
				: ResponseEntity.ok(items);
			
	}
	
}
