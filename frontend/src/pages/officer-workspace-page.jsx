import { useEffect, useState } from "react";
import {
  fetchOfficerQueue,
  fetchOfficerSummary,
  fetchTicketHistory,
  transitionTicket
} from "../api/ticket-api";

const reviewStatuses = [
  "UNDER_OFFICER_REVIEW",
  "FINAL_RESOLVED",
  "UNRESOLVED",
  "PENDING_INFO_FROM_CANDIDATE"
];

export default function OfficerWorkspacePage() {
  const [assignedOfficer, setAssignedOfficer] = useState("officer1");
  const [statusFilter, setStatusFilter] = useState("");
  const [summary, setSummary] = useState({
    total: 0,
    unresolved: 0,
    escalatedToOfficer: 0,
    underOfficerReview: 0,
    reopened: 0
  });
  const [queue, setQueue] = useState([]);
  const [selectedGrievanceId, setSelectedGrievanceId] = useState("");
  const [history, setHistory] = useState([]);
  const [transitionForm, setTransitionForm] = useState({
    toStatus: "UNDER_OFFICER_REVIEW",
    actionBy: "officer1",
    remarks: "Officer started review"
  });
  const [error, setError] = useState("");

  async function loadData() {
    const [nextSummary, nextQueue] = await Promise.all([
      fetchOfficerSummary(assignedOfficer),
      fetchOfficerQueue({ assignedOfficer, status: statusFilter })
    ]);
    setSummary(nextSummary);
    setQueue(nextQueue);
    if (!selectedGrievanceId && nextQueue.length > 0) {
      setSelectedGrievanceId(nextQueue[0].grievanceId);
    }
    if (selectedGrievanceId && !nextQueue.some((item) => item.grievanceId === selectedGrievanceId)) {
      setSelectedGrievanceId(nextQueue[0]?.grievanceId || "");
    }
  }

  async function loadHistory(grievanceId) {
    if (!grievanceId) {
      setHistory([]);
      return;
    }
    setHistory(await fetchTicketHistory(grievanceId));
  }

  useEffect(() => {
    loadData().catch(() => {
      setQueue([]);
      setSummary({
        total: 0,
        unresolved: 0,
        escalatedToOfficer: 0,
        underOfficerReview: 0,
        reopened: 0
      });
    });
  }, [assignedOfficer, statusFilter]);

  useEffect(() => {
    loadHistory(selectedGrievanceId).catch(() => setHistory([]));
  }, [selectedGrievanceId]);

  async function onTransition(e) {
    e.preventDefault();
    if (!selectedGrievanceId) return;
    setError("");
    try {
      await transitionTicket(selectedGrievanceId, transitionForm);
      await loadData();
      await loadHistory(selectedGrievanceId);
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <section className="panel">
      <h3>Officer Workspace</h3>
      <div className="officer-filter-row">
        <input
          value={assignedOfficer}
          onChange={(e) => {
            setAssignedOfficer(e.target.value);
            setTransitionForm((prev) => ({ ...prev, actionBy: e.target.value || "officer1" }));
          }}
          placeholder="Assigned Officer"
        />
        <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
          <option value="">All Officer Queue Statuses</option>
          <option value="UNRESOLVED">UNRESOLVED</option>
          <option value="ESCALATED_TO_OFFICER">ESCALATED_TO_OFFICER</option>
          <option value="UNDER_OFFICER_REVIEW">UNDER_OFFICER_REVIEW</option>
          <option value="REOPENED">REOPENED</option>
        </select>
      </div>

      <div className="kpi-grid">
        <article className="kpi-card"><h4>Total Queue</h4><p>{summary.total}</p></article>
        <article className="kpi-card"><h4>Unresolved</h4><p>{summary.unresolved}</p></article>
        <article className="kpi-card"><h4>Escalated</h4><p>{summary.escalatedToOfficer}</p></article>
        <article className="kpi-card"><h4>Under Review</h4><p>{summary.underOfficerReview}</p></article>
        <article className="kpi-card"><h4>Reopened</h4><p>{summary.reopened}</p></article>
      </div>

      <div className="ticket-action-grid">
        <div className="panel action-panel">
          <h4>Escalated Queue</h4>
          <table>
            <thead>
              <tr>
                <th>Grievance</th>
                <th>Candidate</th>
                <th>Status</th>
                <th>Exam</th>
              </tr>
            </thead>
            <tbody>
              {queue.map((item) => (
                <tr
                  key={item.grievanceId}
                  className={selectedGrievanceId === item.grievanceId ? "row-selected" : ""}
                  onClick={() => setSelectedGrievanceId(item.grievanceId)}
                >
                  <td>{item.grievanceId}</td>
                  <td>{item.candidateName}</td>
                  <td>{item.status}</td>
                  <td>{item.examCode}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <form className="panel action-panel" onSubmit={onTransition}>
          <h4>Officer Action</h4>
          <p className="muted">Selected: {selectedGrievanceId || "None"}</p>
          <select
            value={transitionForm.toStatus}
            onChange={(e) => setTransitionForm({ ...transitionForm, toStatus: e.target.value })}
          >
            {reviewStatuses.map((status) => (
              <option value={status} key={status}>{status}</option>
            ))}
          </select>
          <input
            value={transitionForm.actionBy}
            onChange={(e) => setTransitionForm({ ...transitionForm, actionBy: e.target.value })}
            placeholder="Action By"
          />
          <input
            value={transitionForm.remarks}
            onChange={(e) => setTransitionForm({ ...transitionForm, remarks: e.target.value })}
            placeholder="Remarks"
          />
          <button type="submit" disabled={!selectedGrievanceId}>Apply Officer Transition</button>
          {error ? <p className="error">{error}</p> : null}
        </form>
      </div>

      <div className="panel history-panel">
        <h4>Ticket Timeline</h4>
        <table>
          <thead>
            <tr>
              <th>Action</th>
              <th>From</th>
              <th>To</th>
              <th>By</th>
              <th>Remarks</th>
              <th>Time</th>
            </tr>
          </thead>
          <tbody>
            {history.map((item, idx) => (
              <tr key={`${item.createdAt}-${idx}`}>
                <td>{item.actionType}</td>
                <td>{item.fromStatus}</td>
                <td>{item.toStatus}</td>
                <td>{item.actionBy}</td>
                <td>{item.remarks || "-"}</td>
                <td>{item.createdAt}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
