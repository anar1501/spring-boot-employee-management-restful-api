package com.company.service.impl;

import com.company.dto.request.LoginRequestDto;
import com.company.dto.request.RegisterRequestDto;
import com.company.dto.response.JWTAuthResponse;
import com.company.enums.UserStatusEnum;
import com.company.exception.*;
import com.company.mapstruct.UserMapper;
import com.company.model.User;
import com.company.repository.UserRepository;
import com.company.security.jwt.JwtUtil;
import com.company.service.UserService;
import com.company.utils.MessageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    final UserRepository userRepository;
    final UserMapper userMapper;
    final MessageUtils messageUtils;
    final AuthenticationManager authenticationManager;
    final PasswordEncoder passwordEncoder;
    final JwtUtil jwtUtil;
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
        User user=new User();
        user.setEmailorusername(requestDto.getEmailorusername());
        user.setPassword(requestDto.getPassword());
        User mappedUser = userMapper.map(requestDto);
        User savedUser = userRepository.save(mappedUser);
        messageUtils.sendAsync(savedUser.getEmailorusername(),messageSubject,messageBody+"http://localhost:8080/api/v1/auth/register-confirm?code="+savedUser.getActivationCode());
    }

    @Override
    public JWTAuthResponse login(LoginRequestDto requestDto) {
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
