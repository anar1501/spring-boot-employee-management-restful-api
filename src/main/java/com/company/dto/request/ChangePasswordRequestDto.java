package com.company.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequestDto implements Serializable {
    String sixDigitCode;
    String newPassword;
    String repeatNewPassword;
}
