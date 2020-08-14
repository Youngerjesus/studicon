package com.studyolle.modules.study.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/* seungho */
@Data
public class StudyTxHashForm {

    @NotBlank
    private String closeStudyTxHash;
}
/* seungho */
