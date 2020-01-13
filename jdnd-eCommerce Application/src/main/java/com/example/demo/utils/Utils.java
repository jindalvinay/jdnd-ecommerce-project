package com.example.demo.utils;

import com.example.demo.exception.InvalidPasswordException;

public class Utils {
    public boolean validatePassword(String password, String confirmPassword) throws InvalidPasswordException{
        if(password.length() < 7){
            throw new InvalidPasswordException("Password should contain at least 7 characters.");
        }
        if(!password.equals(confirmPassword)){
            throw new InvalidPasswordException("Password and Confirm Password do not match.");
        }
        return true;
    }
}
