package com.studyolle.modules.votingitem;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.event.Enrollment;
import com.studyolle.modules.event.EnrollmentRepository;
import com.studyolle.modules.event.Event;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.voting.Voting;
import com.studyolle.modules.voting.VotingRepository;
import com.studyolle.modules.votingitem.form.VotingItemForm;
import foundation.icon.icx.*;
import foundation.icon.icx.data.Address;
import foundation.icon.icx.data.Bytes;
import foundation.icon.icx.data.IconAmount;
import foundation.icon.icx.transport.http.HttpProvider;
import foundation.icon.icx.transport.jsonrpc.RpcObject;
import foundation.icon.icx.transport.jsonrpc.RpcValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VotingItemService {

    private final VotingRepository votingRepository;
    private final VotingItemRepository votingItemRepository;
    private final EnrollmentRepository enrollmentRepository;

    public void createVotingItems(Voting newVoting, Event event){
        List<Enrollment> votedEnrollments = event.getEnrollments();
        List<Enrollment> votingEnrollments = event.getEnrollments();

        votedEnrollments.forEach(votedEnrollment -> {
            votingEnrollments.forEach(votingEnrollment -> {
                VotingItem votingItem = new VotingItem();
                votingItem.setVotingEnrollment(votingEnrollment);
                votingItem.setVotedEnrollment(votedEnrollment);
                votingItem.setQuestion(createVotingQuestion(event, newVoting, votedEnrollment));
                votingItem.setVoting(newVoting);
                votingItem.setState("incomplete");
                VotingItem newVotingItem = votingItemRepository.save(votingItem);
                newVoting.addVotingItem(newVotingItem);
                votingEnrollment.addVotingItem(newVotingItem);
                votedEnrollment.addVotedItem(newVotingItem);
            });
        });
    }

    private String createVotingQuestion(Event event, Voting voting, Enrollment enrollment) {
        Account votedAccount = enrollment.getAccount();
        String votingTypeQuestion = voting.getVotingTypeQuestion();
        String attendDateTime = event.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분"));
        return attendDateTime +"에 " + votedAccount.getNickname() + "님은 "+ votingTypeQuestion +" 하셨습니까?";
    }

    public VotingItem completeVotingItem(VotingItem votingItem, VotingItemForm votingItemForm) {
        votingItem.setAnswer(votingItemForm.getAnswer());
        votingItem.setState("complete");
        return votingItemRepository.save(votingItem);
    }

    public void completeVoting(VotingItem votingItem, VotingItemForm votingItemForm) {
        VotingItem newVotingItem = completeVotingItem(votingItem, votingItemForm);
        Voting voting = newVotingItem.getVoting();
        String votingType = newVotingItem.getVoting().getType();
        Enrollment enrollment = newVotingItem.getVotedEnrollment();

        if(votingType.equals("attendance")){
            if(enrollment.checkCompleteAttendanceVoted()){
                if(!enrollment.isAttendanceWithVoting()){
                    /* seungho */
                    Study study = enrollment.getEvent().getStudy();
                    Long accountId = enrollment.getAccount().getId();
                    applyPenalty("applyAttendPenalty", study, accountId);
                    /* seungho */
                }
            }
        }else if(votingType.equals("assignment")) {
            if(enrollment.checkCompleteAssignmentVoted()){
                if(!enrollment.isAssignmentSentWithVoting()){
                    /* seungho */
                    Study study = enrollment.getEvent().getStudy();
                    Long accountId = enrollment.getAccount().getId();
                    applyPenalty("applyHomeworkPenalty", study, accountId);
                    /* seungho */
                }
            }
        }

        if(voting.checkIsCompleteVoting()){
            voting.setState("complete");
            votingRepository.save(voting);
        }
    }

    /* seungho */
    private void applyPenalty(String methodName, Study study, Long accountId) {
        HttpProvider httpProvider = new HttpProvider("https://bicon.net.solidwallet.io/api/v3");
        IconService iconService = new IconService(httpProvider);
        BigInteger networkId = new BigInteger("3");
        Wallet wallet = KeyWallet.load(new Bytes("0xb7a7f8c25301cd4a630fdb4f9b61c0d879ca7d6b9e67059a65fc2ccb1c0c2d8c"));
        Address address = new Address(study.getScoreAddress());

        RpcObject paramsSet = new RpcObject.Builder()
                .put("_accountId", new RpcValue(new BigInteger(String.valueOf(accountId))))
                .build();

        // call a method in SCORE
        Transaction transactionSet = TransactionBuilder.newBuilder()
                .nid(networkId)
                .from(wallet.getAddress())
                .to(address)
                .stepLimit(new BigInteger("1000000"))
                .nonce(new BigInteger("1000000"))
                .call(methodName)
                .params(paramsSet)
                .build();

        SignedTransaction signedTransactionSet = new SignedTransaction(transactionSet, wallet);
        Request<Bytes> requestSet = iconService.sendTransaction(signedTransactionSet);

        // Synchronized request execution
        try {
            Bytes result = requestSet.execute();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    /* seungho */
}
