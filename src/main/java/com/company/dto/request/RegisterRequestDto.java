package com.company.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequestDto {
    String username;
    String email;
    String password;
    String repeatPassword;
}
