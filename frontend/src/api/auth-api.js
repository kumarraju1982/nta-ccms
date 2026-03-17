import { apiPost } from "./client";
import { servicePath } from "./config";

export function loginWithPassword(username, password) {
  return apiPost(servicePath("user", "/api/v1/auth/login/password"), { username, password });
}
