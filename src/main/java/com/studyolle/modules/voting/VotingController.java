package com.studyolle.modules.voting;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.CurrentAccount;
import com.studyolle.modules.event.Event;
import com.studyolle.modules.event.EventService;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyRepository;
import com.studyolle.modules.study.StudyService;
import com.studyolle.modules.voting.form.VotingForm;
import com.studyolle.modules.voting.validator.VotingFormValidator;
import com.studyolle.modules.votingitem.form.VotingItemForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.rule.Mode;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.logging.Logger;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class VotingController {

    private final StudyRepository studyRepository;
    private final VotingService votingService;
    private final VotingFormValidator votingFormValidator;
    private final ModelMapper modelMapper;

    @InitBinder("VotingForm")
    public void votingFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(votingFormValidator);
    }


    @PostMapping("/events/{id}/new-voting")
    public String newVotingSubmit(@CurrentAccount Account account, Model model,
                            @Valid VotingForm votingForm,
                            @PathVariable String path,
                            @PathVariable("id") Event event){
        // TODO 중복 투표 생성 방지
        Voting newVoting = votingService.createNewVotingAndVotingItems(event, account,modelMapper.map(votingForm, Voting.class));
        model.addAttribute(account);
        model.addAttribute(event);
        model.addAttribute(new VotingForm());
        model.addAttribute(new VotingItemForm());
        model.addAttribute(studyRepository.findStudyWithManagersByPath(path));
        return "event/view";
    }




}
