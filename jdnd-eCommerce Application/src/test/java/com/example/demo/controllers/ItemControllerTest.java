package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    public void getItems_noItemsFound_emptyList() throws Exception {
        when(itemRepository.findAll()).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get("/api/item"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItems_noItemsFound_null() throws  Exception {
        when(itemRepository.findAll()).thenReturn(null);

        this.mockMvc.perform(get("/api/item"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItems_happy() throws Exception {
        Item item = new Item();
        item.setId(0L);
        item.setPrice(new BigDecimal(10));
        item.setName("Cheap item");
        item.setDescription("It will break in no-time");

        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));

        this.mockMvc.perform(get("/api/item"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getItemById_happy() throws Exception {
        Long itemId = 0L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));

        this.mockMvc.perform(get("/api/item/" + itemId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getItemById_noItemFound() throws Exception {
        Long itemId = 0L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/api/item/" + itemId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getItemsByName_happy() throws Exception {
        when(itemRepository.findByName("Round Widget")).thenReturn(Collections.singletonList(new Item()));

        this.mockMvc.perform(get("/api/item/name/{name}", "Round Widget"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getItemsByName_noItemFound() throws Exception {
        when(itemRepository.findByName("Udacity")).thenReturn(Collections.singletonList(new Item()));

        this.mockMvc.perform(get("/api/item/name/{name}", "Round Widget"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
