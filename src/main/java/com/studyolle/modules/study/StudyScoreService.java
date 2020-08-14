package com.studyolle.modules.study;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/* seungho */
@Service
@RequiredArgsConstructor
public class StudyScoreService {

    private final StudyService studyService;

    @Async
    public void deployScore(String path, Long managerId) {
        studyService.updateStudyScoreAddress(path, managerId);
    }

}
/* seungho */