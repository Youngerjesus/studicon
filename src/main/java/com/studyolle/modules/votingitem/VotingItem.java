package com.studyolle.modules.votingitem;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.event.Enrollment;
import com.studyolle.modules.voting.Voting;
import lombok.*;
import org.hibernate.mapping.Set;
import org.springframework.stereotype.Controller;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class VotingItem {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Voting voting;

    @Column(nullable = false)
    private String question;

    @ManyToOne
    private Enrollment votedEnrollment;

    @ManyToOne
    private Enrollment votingEnrollment;

    private boolean answer;

    private String state;

    public boolean isAttendance(){
        return this.voting.getType().equals("attendance");
    }

    public boolean isYes() {
        return answer;
    }
}