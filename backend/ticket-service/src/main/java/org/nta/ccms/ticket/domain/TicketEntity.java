package org.nta.ccms.ticket.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ccms_ticket")
public class TicketEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "grievance_id", nullable = false, unique = true, length = 32)
  private String grievanceId;

  @Column(name = "candidate_name", nullable = false, length = 120)
  private String candidateName;

  @Column(name = "candidate_mobile", nullable = false, length = 20)
  private String candidateMobile;

  @Column(name = "exam_code", nullable = false, length = 64)
  private String examCode;

  @Column(name = "category", nullable = false, length = 64)
  private String category;

  @Column(name = "sub_category", nullable = false, length = 64)
  private String subCategory;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 40)
  private TicketStatus status;

  @Column(name = "assigned_agent", length = 80)
  private String assignedAgent;

  @Column(name = "assigned_officer", length = 80)
  private String assignedOfficer;

  @Column(name = "workflow_process_id", length = 80)
  private String workflowProcessId;

  @Column(name = "workflow_instance_id", length = 120)
  private String workflowInstanceId;

  @Column(name = "source_channel", nullable = false, length = 32)
  private String sourceChannel;

  @Column(name = "reopen_count", nullable = false)
  private int reopenCount;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  public Long getId() {
    return id;
  }

  public String getGrievanceId() {
    return grievanceId;
  }

  public void setGrievanceId(String grievanceId) {
    this.grievanceId = grievanceId;
  }

  public String getCandidateName() {
    return candidateName;
  }

  public void setCandidateName(String candidateName) {
    this.candidateName = candidateName;
  }

  public String getCandidateMobile() {
    return candidateMobile;
  }

  public void setCandidateMobile(String candidateMobile) {
    this.candidateMobile = candidateMobile;
  }

  public String getExamCode() {
    return examCode;
  }

  public void setExamCode(String examCode) {
    this.examCode = examCode;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getSubCategory() {
    return subCategory;
  }

  public void setSubCategory(String subCategory) {
    this.subCategory = subCategory;
  }

  public TicketStatus getStatus() {
    return status;
  }

  public void setStatus(TicketStatus status) {
    this.status = status;
  }

  public String getAssignedAgent() {
    return assignedAgent;
  }

  public void setAssignedAgent(String assignedAgent) {
    this.assignedAgent = assignedAgent;
  }

  public String getAssignedOfficer() {
    return assignedOfficer;
  }

  public void setAssignedOfficer(String assignedOfficer) {
    this.assignedOfficer = assignedOfficer;
  }

  public String getSourceChannel() {
    return sourceChannel;
  }

  public void setSourceChannel(String sourceChannel) {
    this.sourceChannel = sourceChannel;
  }

  public String getWorkflowProcessId() {
    return workflowProcessId;
  }

  public void setWorkflowProcessId(String workflowProcessId) {
    this.workflowProcessId = workflowProcessId;
  }

  public String getWorkflowInstanceId() {
    return workflowInstanceId;
  }

  public void setWorkflowInstanceId(String workflowInstanceId) {
    this.workflowInstanceId = workflowInstanceId;
  }

  public int getReopenCount() {
    return reopenCount;
  }

  public void setReopenCount(int reopenCount) {
    this.reopenCount = reopenCount;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
