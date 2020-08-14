package com.studyolle.modules.votingitem.form;

import com.studyolle.modules.event.Event;
import com.studyolle.modules.voting.Voting;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class VotingItemForm {
    private boolean yes;
    private boolean no;


    public boolean getAnswer() {
        return yes;
    }
}
