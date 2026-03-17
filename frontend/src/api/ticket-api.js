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
