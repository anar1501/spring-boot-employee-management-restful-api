package com.company.controller;

import com.company.dto.request.LoginRequestDto;
import com.company.dto.request.RegisterRequestDto;
import com.company.dto.response.JWTAuthResponse;
import com.company.enums.UserStatusEnum;
import com.company.exception.*;
import com.company.mapstruct.UserMapper;
import com.company.model.User;
import com.company.repository.UserRepository;
import com.company.repository.UserStatusRepository;
import com.company.security.jwt.JwtUtil;
import com.company.service.UserService;
import com.company.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    final UserService userService;
    final PasswordEncoder passwordEncoder;
    final UserRepository userRepository;
    final UserMapper userMapper;
    final MessageUtils messageUtils;
    final AuthenticationManager authenticationManager;
    final JwtUtil jwtUtil;
    final UserStatusRepository userStatusRepository;
    @Value("${my.message.subject}")
    String messageSubject;
    @Value("${my.message.body}")
    String messageBody;
    @Value("${my.message.forget-subject}")
    String forgetMessageSubject;
    @Value("${my.message.forget-body}")
    String forgetMessageBody;
    private final static Date currentDate = new Date();


    @PostMapping(value = "/register")
    public HttpStatus register(@RequestBody RegisterRequestDto requestDto) {
        userService.register(requestDto);
        return HttpStatus.CREATED;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(userService.login(requestDto));
    }

    @GetMapping(value = "/register-confirm")
    public void registerConfirm(@RequestParam(value = "code") String code) {
        User user = userRepository.findUserByActivationCode(code);
        if (user.getStatus().getId().equals(UserStatusEnum.CONFIRMED.getStatusId())) {
            throw new UserAlreadyConfirmedException();
        }
        Date expiredDate = user.getExpiredDate();
        if (expiredDate.before(currentDate)) {
            throw new ExpirationCodeIsExpiredException();
        } else {
            user.setStatus(userStatusRepository.findUserStatusById(UserStatusEnum.CONFIRMED.getStatusId()));
            userRepository.save(user);
        }
    }

}
