package org.nta.ccms.ticket.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ccms_ticket_history")
public class TicketHistoryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "ticket_id", nullable = false)
  private Long ticketId;

  @Column(name = "grievance_id", nullable = false, length = 32)
  private String grievanceId;

  @Column(name = "from_status", nullable = false, length = 40)
  private String fromStatus;

  @Column(name = "to_status", nullable = false, length = 40)
  private String toStatus;

  @Column(name = "action_type", nullable = false, length = 32)
  private String actionType;

  @Column(name = "action_by", nullable = false, length = 80)
  private String actionBy;

  @Column(name = "remarks", length = 400)
  private String remarks;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public Long getId() {
    return id;
  }

  public Long getTicketId() {
    return ticketId;
  }

  public void setTicketId(Long ticketId) {
    this.ticketId = ticketId;
  }

  public String getGrievanceId() {
    return grievanceId;
  }

  public void setGrievanceId(String grievanceId) {
    this.grievanceId = grievanceId;
  }

  public String getFromStatus() {
    return fromStatus;
  }

  public void setFromStatus(String fromStatus) {
    this.fromStatus = fromStatus;
  }

  public String getToStatus() {
    return toStatus;
  }

  public void setToStatus(String toStatus) {
    this.toStatus = toStatus;
  }

  public String getActionType() {
    return actionType;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  public String getActionBy() {
    return actionBy;
  }

  public void setActionBy(String actionBy) {
    this.actionBy = actionBy;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
