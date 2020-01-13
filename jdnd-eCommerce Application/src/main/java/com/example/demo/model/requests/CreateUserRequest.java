package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;

public class CreateUserRequest {

	@JsonProperty
	private String username;

	@JsonProperty
	@NotBlank(message = "Password is mandatory")
	private String password;

	@JsonProperty
	@NotBlank(message = "Confirm Password is mandatory")
	private String confirmPassword;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
}
