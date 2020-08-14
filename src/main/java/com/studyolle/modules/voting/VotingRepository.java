package com.studyolle.modules.voting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface VotingRepository extends JpaRepository<Voting, Long> {
}
