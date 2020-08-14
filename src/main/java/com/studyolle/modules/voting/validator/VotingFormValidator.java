package com.studyolle.modules.voting.validator;

import com.studyolle.modules.voting.Voting;
import com.studyolle.modules.voting.form.VotingForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class VotingFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return VotingForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        VotingForm votingForm = (VotingForm) target;
        if(isNotValidEndVotingDateTime(votingForm)){
            errors.rejectValue("endVotingDateTime", "wrong.endVotingDateTime", "잘못된 투표 마감 시간을 사용할 수 없습니다");
        }

        if(isNotValidCreatedVotingDateTime(votingForm)){
            errors.rejectValue("createdVotingDateTime", "wrong.createdVotingDateTime", "잘못된 투표 시작 시간을 사용할 수 없습니다");
        }
    }

    private boolean isNotValidCreatedVotingDateTime(VotingForm votingForm) {
        return votingForm.getCreatedVotingDateTime().isBefore(LocalDateTime.now());
    }

    private boolean isNotValidEndVotingDateTime(VotingForm votingForm) {
        return votingForm.getEndVotingDateTime().isBefore(votingForm.getCreatedVotingDateTime());
    }

}
