package com.elearning.elearning_support.dtos.lecture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureMaterialDto {
    private long id;
    private int type;
    private String fileEx;
    private int size;
    private int storedType;
    private String filePath;
    private String externalLink;
    private int creadtedBy;
    private String fileName;
}
