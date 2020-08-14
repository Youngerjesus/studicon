package com.studyolle.modules.voting;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.event.Enrollment;
import com.studyolle.modules.event.Event;
import com.studyolle.modules.voting.form.VotingForm;
import com.studyolle.modules.votingitem.VotingItem;
import com.studyolle.modules.votingitem.VotingItemRepository;
import com.studyolle.modules.votingitem.VotingItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VotingService {

    private final VotingRepository votingRepository;
    private final VotingItemService votingItemService;

    public Voting createNewVotingAndVotingItems(Event event, Account account, Voting voting){
        // TODO 모임이 마감 되어있어야 한다.
        checkCreateVoting(event);
        Voting newVoting = createVoting(event, account, voting);
        votingItemService.createVotingItems(newVoting, event);
        return newVoting;
    }

    private Voting createVoting(Event event, Account account, Voting voting) {
        voting.setEvent(event);
        voting.setAccount(account);
        Voting newVoting = votingRepository.save(voting);
        event.addVoting(newVoting);
        return newVoting;
    }


    private void checkCreateVoting(Event event) {
        if(!event.isNotClosed()){
            throw  new IllegalArgumentException(event.getTitle() + "가 마감되지 않았습니다");
        }
    }

    public void completeVoting(Voting voting){
        // TODO 투표가 끝난 시점
        voting.setState("complete");
        votingRepository.save(voting);
    }

}
