package com.company.mapstruct;


import com.company.dto.request.RegisterRequestDto;
import com.company.model.User;
import com.company.repository.RoleRepository;
import com.company.utils.DateUtil;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

import static com.company.enums.RoleEnums.ROLE_USER;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), imports = {Objects.class, DateUtil.class})
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected RoleRepository roleRepository;

    @Mapping(target = "password",expression = "java(toPassword(requestDto.getPassword()))")
    @Mapping(target = "expiredDate",expression = "java(DateUtil.prepareRegistrationExpirationDate())")
    @Mapping(target = "activationCode",expression = "java(DateUtil.getRandomNumberString())")
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "sixDigitCode",ignore = true)
    @Mapping(target = "forgetPasswordExpiredDate",ignore = true)
    @Mapping(target = "updateDate",ignore = true)
    public abstract User map(RegisterRequestDto requestDto);

    @Named(value = "toPassword")
    protected String toPassword(String password){
        return passwordEncoder.encode(password);
    }

    @AfterMapping
    void map(@MappingTarget User user){
        user.setRole(roleRepository.findRoleByName(ROLE_USER.getRoleName()));
    }
}
