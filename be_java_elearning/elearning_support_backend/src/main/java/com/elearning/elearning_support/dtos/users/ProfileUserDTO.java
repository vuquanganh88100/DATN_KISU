package com.elearning.elearning_support.dtos.users;

import java.util.Date;
import org.springframework.beans.BeanUtils;
import com.elearning.elearning_support.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileUserDTO {

    Long id;

    String name;

    Long avatarId;

    String avatarPath;

    Integer avatarStoredType;

    String code;

    String identificationNum;

    String identityType;

    String address;

    @JsonFormat(pattern = DateUtils.FORMAT_DATE_DD_MM_YYYY_SLASH, timezone = DateUtils.TIME_ZONE)
    Date birthDate;

    String phoneNumber;

    String email;

    String username;

    String role;

    String department;

    Long departmentId;

    @JsonFormat(pattern = DateUtils.FORMAT_DATE_DD_MM_YYYY_HH_MM_SS, timezone = DateUtils.TIME_ZONE)
    Date createdAt;

    @JsonFormat(pattern = DateUtils.FORMAT_DATE_DD_MM_YYYY_HH_MM_SS, timezone = DateUtils.TIME_ZONE)
    Date modifiedAt;

    @Schema(description = "FCM Token")
    String fcmToken;

    public ProfileUserDTO(IGetDetailUserDTO iGetDetailUserDTO) {
        BeanUtils.copyProperties(iGetDetailUserDTO, this);
    }

}
