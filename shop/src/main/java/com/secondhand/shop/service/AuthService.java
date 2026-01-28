package com.secondhand.shop.service;

import com.secondhand.shop.dto.AuthDTO;

public interface AuthService {

    AuthDTO.LoginResponse login(AuthDTO.LoginRequest request);

    AuthDTO.LoginResponse register(AuthDTO.RegisterRequest request);
}