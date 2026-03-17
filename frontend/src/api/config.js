function envValue(primaryKey, fallbackKey = "") {
  const meta = import.meta.env || {};
  return meta[primaryKey] || (fallbackKey ? meta[fallbackKey] : "") || "";
}

const gatewayUrl = envValue("REACT_APP_GATEWAY_URL", "VITE_GATEWAY_URL");

const serviceUrls = {
  user: envValue("REACT_APP_USER_SERVICE_URL", "VITE_USER_SERVICE_URL"),
  mdms: envValue("REACT_APP_MDMS_SERVICE_URL", "VITE_MDMS_SERVICE_URL"),
  workflow: envValue("REACT_APP_WORKFLOW_SERVICE_URL", "VITE_WORKFLOW_SERVICE_URL"),
  grievance: envValue("REACT_APP_GRIEVANCE_SERVICE_URL", "VITE_GRIEVANCE_SERVICE_URL"),
  notification: envValue("REACT_APP_NOTIFICATION_SERVICE_URL", "VITE_NOTIFICATION_SERVICE_URL"),
  reporting: envValue("REACT_APP_REPORTING_SERVICE_URL", "VITE_REPORTING_SERVICE_URL"),
  candidate: envValue("REACT_APP_CANDIDATE_SERVICE_URL", "VITE_CANDIDATE_SERVICE_URL"),
  callIntake: envValue("REACT_APP_CALL_INTAKE_SERVICE_URL", "VITE_CALL_INTAKE_SERVICE_URL"),
  transcript: envValue("REACT_APP_TRANSCRIPT_SERVICE_URL", "VITE_TRANSCRIPT_SERVICE_URL"),
  grouping: envValue("REACT_APP_GROUPING_SERVICE_URL", "VITE_GROUPING_SERVICE_URL"),
  audit: envValue("REACT_APP_AUDIT_SERVICE_URL", "VITE_AUDIT_SERVICE_URL")
};

export function resolveApiBase(serviceName) {
  if (gatewayUrl) {
    return gatewayUrl.replace(/\/$/, "");
  }
  return (serviceUrls[serviceName] || "").replace(/\/$/, "");
}

export function servicePath(serviceName, path) {
  const base = resolveApiBase(serviceName);
  return `${base}${path.startsWith("/") ? path : `/${path}`}`;
}
