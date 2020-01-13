package com.example.demo.controllers;

import com.example.demo.exception.InvalidPasswordException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.utils.Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private Utils utils;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void createUser_happyPath() throws Exception {
        String userName = "vikas";
        String hashedPassword = "1234567";

        User user = new User();
        user.setId(0);
        user.setUsername(userName);
        user.setPassword(hashedPassword);

        userRepository.save(user);

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(user.getUsername());
        createUserRequest.setPassword(user.getPassword());
        createUserRequest.setConfirmPassword(user.getPassword());

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User responseUser = response.getBody();
        assertNotNull(responseUser);
        assertEquals(0, responseUser.getId());
        assertEquals(userName, responseUser.getUsername());
    }

    @Test(expected = InvalidPasswordException.class)
    public void createUser_invalidPassword() throws Exception {
        String password = "test";
        String confirmPassword = "test";
        doThrow(InvalidPasswordException.class).when(utils).validatePassword(password, confirmPassword);

        utils.validatePassword(password, confirmPassword);
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("Roshan");
        r.setPassword(password);
        r.setConfirmPassword(confirmPassword);

        final ResponseEntity<User> response = userController.createUser(r);
    }

    @Test(expected = InvalidPasswordException.class)
    public void createUser_passwordMismatch() throws Exception {
        String password = "test";
        String confirmPassword = "test123R";
        doThrow(InvalidPasswordException.class).when(utils).validatePassword(password, confirmPassword);

        utils.validatePassword(password, confirmPassword);
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("Roshan");
        r.setPassword(password);
        r.setConfirmPassword(confirmPassword);

        final ResponseEntity<User> response = userController.createUser(r);
    }

    @Test
    public void findUserById_happy() {
        User user = new User();
        user.setId(0);
        user.setUsername("Vinay");
        user.setPassword("password");
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.findById(0L)).thenReturn(optionalUser);

        final ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final User body = response.getBody();
        assertNotNull(body);
        assertEquals(0L, body.getId());
        assertEquals(user.getPassword(), body.getPassword());
        assertEquals(user.getUsername(), body.getUsername());
    }

    @Test
    public void findUserByUserName_happy() {
        User user = new User();
        user.setId(0);
        user.setUsername("Vinay");
        user.setPassword("password");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName(user.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final User body = response.getBody();
        assertNotNull(body);
        assertEquals(0L, body.getId());
        assertEquals(user.getPassword(), body.getPassword());
        assertEquals(user.getUsername(), body.getUsername());
    }

    @Test
    public void testSubmitOrder_withoutUser() throws Exception {
        when(userRepository.findByUsername("Vinay")).thenReturn(null);

        this.mockMvc.perform(get("/api/user/Vinay"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
