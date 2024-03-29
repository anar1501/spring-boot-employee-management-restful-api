package com.company.controller;

import com.company.dto.request.ChangePasswordRequestDto;
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
import com.company.utils.DateUtil;
import com.company.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping(value = "/resend/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public void resendEmail(@PathVariable(value = "id") Long id) {
        User user = userRepository.getOne(id);
        user.setActivationCode(DateUtil.getRandomNumberString());
        user.setExpiredDate(DateUtil.prepareRegistrationExpirationDate());
        User savedUser = userRepository.save(user);
        messageUtils.sendAsync(savedUser.getEmailorusername(), messageSubject, messageBody + "http://localhost:8080/api/v1/auth/register-confirm?code=" + savedUser.getActivationCode());
    }

    @PostMapping(value = "/forget-password")
    public HttpStatus forgetPassword(@RequestParam(value = "email")String email){
        User user=userRepository.findUserByEmailorusername(email).orElseThrow(EmailOrUsernameNotFoundException::new);
        user.setSixDigitCode(DateUtil.getRandomNumberString());
        user.setForgetPasswordExpiredDate(DateUtil.prepareForgetPasswordExpirationDate());
        User savedUser = userRepository.save(user);
        messageUtils.sendAsync(savedUser.getEmailorusername(),forgetMessageSubject,forgetMessageBody+savedUser.getSixDigitCode());
        return HttpStatus.OK;
    }

    @PostMapping(value = "/change-password")
    public HttpStatus changePassword(@RequestBody ChangePasswordRequestDto requestDto){
        if (!requestDto.getNewPassword().equals(requestDto.getRepeatNewPassword())){
            throw new ChangePasswordException();
        }
        else if (requestDto.getNewPassword().isEmpty()||requestDto.getRepeatNewPassword().isEmpty()){
            throw new ShouldntBlankException();
        }
        User user=userRepository.findUserBySixDigitCode(requestDto.getSixDigitCode());
        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
        userRepository.save(user);
        return HttpStatus.OK;
    }


}
