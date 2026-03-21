import { apiGet, apiPost } from "./client";
import { servicePath } from "./config";

export function fetchTickets() {
  return apiGet(servicePath("grievance", "/api/v1/tickets"));
}

export function createTicket(payload) {
  return apiPost(servicePath("grievance", "/api/v1/tickets"), payload);
}

export function assignTicket(grievanceId, payload) {
  return apiPost(servicePath("grievance", `/api/v1/tickets/${grievanceId}/assign`), payload);
}

export function transitionTicket(grievanceId, payload) {
  return apiPost(servicePath("grievance", `/api/v1/tickets/${grievanceId}/transition`), payload);
}

export function fetchTicketHistory(grievanceId) {
  return apiGet(servicePath("grievance", `/api/v1/tickets/${grievanceId}/history`));
}

export function fetchOfficerQueue(params = {}) {
  const query = new URLSearchParams();
  if (params.assignedOfficer) query.set("assignedOfficer", params.assignedOfficer);
  if (params.status) query.set("status", params.status);
  const suffix = query.toString();
  const path = `/api/v1/tickets/officer-queue${suffix ? `?${suffix}` : ""}`;
  return apiGet(servicePath("grievance", path));
}

export function fetchOfficerSummary(assignedOfficer = "") {
  const query = assignedOfficer ? `?assignedOfficer=${encodeURIComponent(assignedOfficer)}` : "";
  return apiGet(servicePath("grievance", `/api/v1/tickets/officer-summary${query}`));
}
