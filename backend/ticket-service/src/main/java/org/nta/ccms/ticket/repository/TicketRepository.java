package org.nta.ccms.ticket.repository;

import java.util.List;
import java.util.Optional;
import org.nta.ccms.ticket.domain.TicketEntity;
import org.nta.ccms.ticket.domain.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
  Optional<TicketEntity> findByGrievanceId(String grievanceId);

  List<TicketEntity> findByStatusInOrderByUpdatedAtDesc(List<TicketStatus> statuses);
}
