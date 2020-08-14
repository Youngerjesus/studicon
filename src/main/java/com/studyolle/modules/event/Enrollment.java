package com.studyolle.modules.event;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.voting.Voting;
import com.studyolle.modules.votingitem.VotingItem;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.EntityGraph;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NamedEntityGraph(
        name = "Enrollment.withEventAndStudy",
        attributeNodes = {
                @NamedAttributeNode(value = "event", subgraph = "study")
        },
        subgraphs = @NamedSubgraph(name = "study", attributeNodes = @NamedAttributeNode("study"))
)
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended = true;

    private boolean assignment = true;

    @OneToMany(mappedBy = "votedEnrollment")
    private List<VotingItem> votedItems = new ArrayList<>();

    @OneToMany(mappedBy = "votingEnrollment")
    private List<VotingItem> votingItems = new ArrayList<>();

    public void addVotingItem(VotingItem votingItem) {
       this.votingItems.add(votingItem);
        votingItem.setVotingEnrollment(this);
    }

    public void addVotedItem(VotingItem votingItem){
       this.votedItems.add(votingItem);
       votingItem.setVotedEnrollment(this);
    }

    public List<VotingItem> attendanceVotedList(){
        return this.votedItems.stream().filter(votedItem -> votedItem.getVoting().getType().equals("attendance")).collect(Collectors.toList());
    }

    public List<VotingItem> assignmentVotedList(){
        return this.votedItems.stream().filter(votedItem -> votedItem.getVoting().getType().equals("assignment")).collect(Collectors.toList());
    }

    public boolean checkCompleteAttendanceVoted() {
        List<VotingItem> attendanceVotedList = attendanceVotedList();
        for(VotingItem votingItem : attendanceVotedList){
            if(votingItem.getState().equals("incomplete")) return false;
        }
        return true;
    }

    public boolean checkCompleteAssignmentVoted() {
        List<VotingItem> assignmentVotedList = assignmentVotedList();
        for(VotingItem votingItem : assignmentVotedList){
            if(votingItem.getState().equals("incomplete")) return false;
        }
        return true;
    }

    public boolean isAttendanceWithVoting() {
        List<VotingItem> attendanceVotedList = attendanceVotedList();
        Integer votingCount = attendanceVotedList.size();
        Integer yesCount = 0;

        for(VotingItem votingItem : attendanceVotedList){
            if(votingItem.isYes()) yesCount += 1;
        }

        if(yesCount * 1.5  > votingCount){
            this.attended =  true;
            return true;
        }else{
            this.attended = false;
            return false;
        }
    }

    public boolean isAssignmentSentWithVoting() {
        List<VotingItem> assignmentVotedList = assignmentVotedList();
        Integer votingCount = assignmentVotedList.size();
        Integer yesCount = 0;

        for(VotingItem votingItem : assignmentVotedList){
            if(votingItem.isYes()) yesCount += 1;
        }

        if(yesCount * 1.5  > votingCount){
            this.assignment =  true;
            return true;
        }else{
            this.assignment = false;
            return false;
        }
    }
}
