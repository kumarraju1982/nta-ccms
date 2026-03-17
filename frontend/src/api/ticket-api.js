import { apiGet, apiPost } from "./client";
import { servicePath } from "./config";

export function fetchTickets() {
  return apiGet(servicePath("grievance", "/api/v1/tickets"));
}

export function createTicket(payload) {
  return apiPost(servicePath("grievance", "/api/v1/tickets"), payload);
}
