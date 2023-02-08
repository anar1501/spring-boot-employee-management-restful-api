package com.company.controller;

import com.company.dto.request.RegisterRequestDto;
import com.company.exception.EmailAlreadyTakenException;
import com.company.exception.PasswordRepeatNotSameException;
import com.company.exception.UsernameAlreadyTakenException;
import com.company.mapstruct.UserMapper;
import com.company.model.User;
import com.company.repository.UserRepository;
import com.company.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    final UserRepository userRepository;
    final UserMapper userMapper;
    final MessageUtils messageUtils;
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
        Boolean existsByUsername = userRepository.existsByUsername(requestDto.getUsername());
        if (existsByUsername) {
            throw new UsernameAlreadyTakenException();
        }
        Boolean existsByEmail = userRepository.existsByEmail(requestDto.getEmail());
        if (existsByEmail) {
            throw new EmailAlreadyTakenException();
        } else if (!requestDto.getPassword().equals(requestDto.getRepeatPassword())) {
            throw new PasswordRepeatNotSameException();
        }
        User mappedUser = userMapper.map(requestDto);
        User savedUser = userRepository.save(mappedUser);
        messageUtils.sendAsync(savedUser.getEmail(),messageSubject,messageBody+"http://localhost:8080/register-confirm?code="+savedUser.getActivationCode());
        return HttpStatus.CREATED;
    }
}
