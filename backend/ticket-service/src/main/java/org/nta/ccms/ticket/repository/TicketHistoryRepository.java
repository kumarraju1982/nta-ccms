package org.nta.ccms.ticket.repository;

import java.util.List;
import org.nta.ccms.ticket.domain.TicketHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketHistoryRepository extends JpaRepository<TicketHistoryEntity, Long> {
  List<TicketHistoryEntity> findByGrievanceIdOrderByCreatedAtAsc(String grievanceId);
}
