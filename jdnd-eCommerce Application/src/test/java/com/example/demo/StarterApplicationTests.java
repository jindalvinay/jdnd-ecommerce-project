package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StarterApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void bCryptPasswordEncoderTest(){
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "notSoGr3at";

		assertTrue(passwordEncoder.matches(rawPassword, passwordEncoder.encode(rawPassword)));
	}
}
