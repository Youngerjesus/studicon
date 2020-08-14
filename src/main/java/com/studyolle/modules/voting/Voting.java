package com.studyolle.modules.voting;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.event.Event;
import com.studyolle.modules.votingitem.VotingItem;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @EqualsAndHashCode(of = "id")
@Getter @Setter
public class Voting {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String type;

    @Column(nullable =  false)
    private LocalDateTime createdVotingDateTime;

    @Column(nullable = false)
    private LocalDateTime endVotingDateTime;

    @ManyToOne
    private Event event;

    @OneToOne
    private Account account;

    @OneToMany(mappedBy = "voting")
    private List<VotingItem> votingItems = new ArrayList<>();

    public String getVotingTypeQuestion(){
        if(this.type.equals("attendance")) return "출석을";
        else return "과제를 제출";
    }

    public void addVotingItem(VotingItem newVotingItem) {
        this.votingItems.add(newVotingItem);
        newVotingItem.setVoting(this);
    }

    public boolean checkIsCompleteVoting() {
        for(VotingItem votingItem : this.votingItems){
            if(votingItem.getState().equals("incomplete")) return false;
        }
        return true;
    }
}
