package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
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

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerTest {
    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    public void testSubmitOrder_withoutCart() throws Exception {
        User user = new User();
        user.setUsername("Vinay");
        user.setPassword("notSoSecretPassword");
        when(userRepository.findByUsername("Vinay")).thenReturn(user);

        UserOrder order = new UserOrder();
        order.setUser(user);
        order.setTotal(new BigDecimal("100.00"));
        orderRepository.save(order);

        this.mockMvc.perform(post("/api/order/submit/Vikas"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSubmitOrder_happy() throws Exception {
        Item item = new Item();
        item.setDescription("Some item");
        item.setName("Best item");
        item.setId(12L);
        item.setPrice(new BigDecimal("150.50"));

        User user = new User();
        user.setUsername("Vinay");
        user.setPassword("notSoSecretPassword");

        Cart cart = new Cart();
        cart.addItem(item);
        cart.setTotal(new BigDecimal("150.50"));
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);
        when(userRepository.findByUsername("Vinay")).thenReturn(user);

        UserOrder order = new UserOrder();
        order.setUser(user);
        order.setId(20L);
        order.setTotal(new BigDecimal("100.00"));
        orderRepository.save(order);

        this.mockMvc.perform(post("/api/order/submit/Vinay"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testSubmitOrder_withoutUser() throws Exception {
        when(userRepository.findByUsername("Vinay")).thenReturn(null);

        this.mockMvc.perform(post("/api/order/submit/Vinay"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOrdersForUser_happy() throws Exception {
        Item item = new Item();
        item.setDescription("Some item");
        item.setName("Best item");
        item.setId(12L);
        item.setPrice(new BigDecimal("150.50"));

        User user = new User();
        user.setUsername("Vinay");
        user.setPassword("notSoSecretPassword");

        Cart cart = new Cart();
        cart.addItem(item);
        cart.setTotal(new BigDecimal("150.50"));
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);
        when(userRepository.findByUsername("Vinay")).thenReturn(user);

        UserOrder order = new UserOrder();
        order.setUser(user);
        order.setId(20L);
        order.setTotal(new BigDecimal("100.00"));
        when(orderRepository.findByUser(user)).thenReturn(Collections.singletonList(order));

        this.mockMvc.perform(get("/api/order/history/Vinay"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getOrdersForUser_noOrderFound() throws Exception {
        Item item = new Item();
        item.setDescription("Some item");
        item.setName("Best item");
        item.setId(12L);
        item.setPrice(new BigDecimal("150.50"));

        User user = new User();
        user.setUsername("Vinay");
        user.setPassword("notSoSecretPassword");

        Cart cart = new Cart();
        cart.addItem(item);
        cart.setTotal(new BigDecimal("150.50"));
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);
        when(userRepository.findByUsername("Vinay")).thenReturn(user);

        when(orderRepository.findByUser(new User())).thenReturn(null);

        this.mockMvc.perform(get("/api/order/history/Vikas"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOrdersForUser_noUserFound() throws Exception {
        when(userRepository.findByUsername("Vinay")).thenReturn(null);

        this.mockMvc.perform(get("/api/order/history/Vinay"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}