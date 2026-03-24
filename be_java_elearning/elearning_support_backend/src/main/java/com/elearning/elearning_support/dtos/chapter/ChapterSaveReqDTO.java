package com.elearning.elearning_support.dtos.chapter;


import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterSaveReqDTO {

    @NotNull
    @NotBlank
    @Max(value = 255)
    @Schema(description = "Tiêu đề của chương")
    String title;

    @Schema(description = "Mô tả chương")
    String description;

    @NotNull
    Integer orders;

}
