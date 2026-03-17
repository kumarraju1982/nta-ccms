import { apiGet } from "./client";
import { servicePath } from "./config";

export function fetchExamMasters() {
  return apiGet(servicePath("mdms", "/api/v1/masters/exams"));
}

export function fetchCategoryMasters() {
  return apiGet(servicePath("mdms", "/api/v1/masters/categories"));
}
