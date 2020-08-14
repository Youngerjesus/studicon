package com.studyolle.modules.votingitem;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.CurrentAccount;
import com.studyolle.modules.event.Event;
import com.studyolle.modules.study.StudyRepository;
import com.studyolle.modules.voting.form.VotingForm;
import com.studyolle.modules.votingitem.form.VotingItemForm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study/{path}")
public class VotingItemController {
    private final VotingItemService votingItemService;
    private final StudyRepository studyRepository;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/events/{eventId}/complete-voting/{votingItemId}")
    public String completeVotingItem(
            @CurrentAccount Account account,
            @PathVariable String path,
            @PathVariable("eventId") Event event,
            @PathVariable("votingItemId") VotingItem votingItem,
            @Valid VotingItemForm votingItemForm,
            Model model)
    {
        votingItemService.completeVoting(votingItem, votingItemForm);
        model.addAttribute(account);
        model.addAttribute(studyRepository.findStudyWithManagersByPath(path));
        model.addAttribute(event);
        model.addAttribute(new VotingForm());
        model.addAttribute(new VotingItemForm());
        return "event/view";
    }
}
