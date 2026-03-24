package com.elearning.elearning_support.dtos.users;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordReqDTO {

    @NotNull
    @NotEmpty
    Long userId;

    @Max(value = 50)
    String oldPassword;

    @NotNull
    @NotEmpty
    @Min(value = 6)
    @Max(value = 50)
    String newPassword;

    @NotNull
    @NotEmpty
    String changeType; // RESET/UPDATE
}
