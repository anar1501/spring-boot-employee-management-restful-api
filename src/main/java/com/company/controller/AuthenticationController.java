package com.company.controller;

import com.company.dto.request.LoginRequestDto;
import com.company.dto.request.RegisterRequestDto;
import com.company.dto.response.JWTAuthResponse;
import com.company.enums.UserStatusEnum;
import com.company.exception.EmailOrUsernameNotFoundException;
import com.company.exception.UnconfirmedException;
import com.company.exception.WrongPasswordException;
import com.company.mapstruct.UserMapper;
import com.company.model.User;
import com.company.repository.UserRepository;
import com.company.security.jwt.JwtUtil;
import com.company.service.UserService;
import com.company.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Value("${my.message.subject}")
    String messageSubject;
    @Value("${my.message.body}")
    String messageBody;
    @Value("${my.message.forget-subject}")
    String forgetMessageSubject;
    @Value("${my.message.forget-body}")
    String forgetMessageBody;


    @PostMapping(value = "/register")
    public HttpStatus register(@RequestBody RegisterRequestDto requestDto) {
        userService.register(requestDto);
        return HttpStatus.CREATED;
    }

    @PostMapping(value = "/login")
    public JWTAuthResponse login(@RequestBody LoginRequestDto requestDto) {
        User user = userRepository.findUserByEmailorusername(requestDto.getEmailOrUsername())
                .orElseThrow(EmailOrUsernameNotFoundException::new);
        boolean isMatches = passwordEncoder.matches(requestDto.getPassword(), user.getPassword());
        if (!isMatches) {
            throw new WrongPasswordException();
        }
        boolean isConfirmed = user.getStatus().getId().equals(UserStatusEnum.UNCONFIRMED.getStatusId());
        if (isConfirmed) {
            throw new UnconfirmedException();
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getEmailOrUsername(), requestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new JWTAuthResponse(jwtUtil.generateToken(authentication));
    }

}
