package com.company.service;

import com.company.dto.request.LoginRequestDto;
import com.company.dto.request.RegisterRequestDto;
import com.company.dto.response.JWTAuthResponse;

public interface UserService {
    void register(RegisterRequestDto requestDto);

    JWTAuthResponse login(LoginRequestDto requestDto);
}
