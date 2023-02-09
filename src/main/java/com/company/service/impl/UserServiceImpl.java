package com.company.service.impl;

import com.company.dto.request.RegisterRequestDto;
import com.company.exception.EmailAlreadyTakenException;
import com.company.exception.PasswordRepeatNotSameException;
import com.company.exception.UsernameAlreadyTakenException;
import com.company.mapstruct.UserMapper;
import com.company.model.User;
import com.company.repository.UserRepository;
import com.company.service.UserService;
import com.company.utils.MessageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

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

    @Override
    public void register(RegisterRequestDto requestDto) {
        Boolean existsByUsername = userRepository.existsByEmailorusername(requestDto.getEmailorusername());
        if (existsByUsername) {
            throw new UsernameAlreadyTakenException();
        }
        Boolean existsByEmail = userRepository.existsByEmailorusername(requestDto.getEmailorusername());
        if (existsByEmail) {
            throw new EmailAlreadyTakenException();
        } else if (!requestDto.getPassword().equals(requestDto.getRepeatPassword())) {
            throw new PasswordRepeatNotSameException();
        }
        User mappedUser = userMapper.map(requestDto);
        User savedUser = userRepository.save(mappedUser);
        messageUtils.sendAsync(savedUser.getEmailorusername(),messageSubject,messageBody+"http://localhost:8080/register-confirm?code="+savedUser.getActivationCode());
    }
}
