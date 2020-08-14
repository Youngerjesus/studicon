package com.studyolle.modules.votingitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface VotingItemRepository extends JpaRepository<VotingItem, Long> {
}
