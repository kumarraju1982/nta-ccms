import { useEffect, useState } from "react";
import {
  assignTicket,
  createTicket,
  fetchTicketHistory,
  fetchTickets,
  transitionTicket
} from "../api/ticket-api";

const defaultForm = {
  candidateName: "Aarav Sharma",
  candidateMobile: "9898989898",
  examCode: "JEE_MAIN",
  category: "ADMIT_CARD",
  subCategory: "DOWNLOAD_ISSUE"
};

export default function TicketsPage() {
  const [tickets, setTickets] = useState([]);
  const [form, setForm] = useState(defaultForm);
  const [selectedGrievanceId, setSelectedGrievanceId] = useState("");
  const [history, setHistory] = useState([]);
  const [actionError, setActionError] = useState("");
  const [assignForm, setAssignForm] = useState({
    assignedAgent: "agent1",
    assignedOfficer: "officer1",
    actionBy: "team-lead",
    remarks: "Assigned for processing"
  });
  const [transitionForm, setTransitionForm] = useState({
    toStatus: "IN_PROGRESS",
    actionBy: "agent1",
    remarks: "Started review"
  });

  async function load() {
    const next = await fetchTickets();
    setTickets(next);
    if (!selectedGrievanceId && next.length > 0) {
      setSelectedGrievanceId(next[0].grievanceId);
    }
  }

  useEffect(() => {
    load().catch(() => setTickets([]));
  }, []);

  async function onCreate(e) {
    e.preventDefault();
    await createTicket(form);
    await load();
  }

  async function loadHistory(grievanceId) {
    if (!grievanceId) {
      setHistory([]);
      return;
    }
    const next = await fetchTicketHistory(grievanceId);
    setHistory(next);
  }

  useEffect(() => {
    loadHistory(selectedGrievanceId).catch(() => setHistory([]));
  }, [selectedGrievanceId]);

  async function onAssign(e) {
    e.preventDefault();
    setActionError("");
    if (!selectedGrievanceId) return;
    try {
      await assignTicket(selectedGrievanceId, assignForm);
      await load();
      await loadHistory(selectedGrievanceId);
    } catch (err) {
      setActionError(err.message);
    }
  }

  async function onTransition(e) {
    e.preventDefault();
    setActionError("");
    if (!selectedGrievanceId) return;
    try {
      await transitionTicket(selectedGrievanceId, transitionForm);
      await load();
      await loadHistory(selectedGrievanceId);
    } catch (err) {
      setActionError(err.message);
    }
  }

  return (
    <section className="panel">
      <h3>Candidate Tickets</h3>
      <form className="ticket-form" onSubmit={onCreate}>
        <input value={form.candidateName} onChange={(e) => setForm({ ...form, candidateName: e.target.value })} placeholder="Candidate name" />
        <input value={form.candidateMobile} onChange={(e) => setForm({ ...form, candidateMobile: e.target.value })} placeholder="Mobile" />
        <input value={form.examCode} onChange={(e) => setForm({ ...form, examCode: e.target.value })} placeholder="Exam code" />
        <input value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} placeholder="Category" />
        <input value={form.subCategory} onChange={(e) => setForm({ ...form, subCategory: e.target.value })} placeholder="Sub-category" />
        <button type="submit">Create Ticket</button>
      </form>
      <table>
        <thead>
          <tr>
            <th>Grievance ID</th>
            <th>Candidate</th>
            <th>Exam</th>
            <th>Category</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {tickets.map((t) => (
            <tr
              key={t.grievanceId}
              className={selectedGrievanceId === t.grievanceId ? "row-selected" : ""}
              onClick={() => setSelectedGrievanceId(t.grievanceId)}
            >
              <td>{t.grievanceId}</td>
              <td>{t.candidateName}</td>
              <td>{t.examCode}</td>
              <td>{t.category}</td>
              <td>{t.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="ticket-action-grid">
        <form className="panel action-panel" onSubmit={onAssign}>
          <h4>Assign Ticket</h4>
          <p className="muted">Selected: {selectedGrievanceId || "None"}</p>
          <input
            value={assignForm.assignedAgent}
            onChange={(e) => setAssignForm({ ...assignForm, assignedAgent: e.target.value })}
            placeholder="Assigned Agent"
          />
          <input
            value={assignForm.assignedOfficer}
            onChange={(e) => setAssignForm({ ...assignForm, assignedOfficer: e.target.value })}
            placeholder="Assigned Officer"
          />
          <input
            value={assignForm.actionBy}
            onChange={(e) => setAssignForm({ ...assignForm, actionBy: e.target.value })}
            placeholder="Action By"
          />
          <input
            value={assignForm.remarks}
            onChange={(e) => setAssignForm({ ...assignForm, remarks: e.target.value })}
            placeholder="Remarks"
          />
          <button type="submit" disabled={!selectedGrievanceId}>Assign</button>
        </form>

        <form className="panel action-panel" onSubmit={onTransition}>
          <h4>Transition Status</h4>
          <p className="muted">Selected: {selectedGrievanceId || "None"}</p>
          <input
            value={transitionForm.toStatus}
            onChange={(e) => setTransitionForm({ ...transitionForm, toStatus: e.target.value })}
            placeholder="Target Status"
          />
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
          <button type="submit" disabled={!selectedGrievanceId}>Transition</button>
          {actionError ? <p className="error">{actionError}</p> : null}
        </form>
      </div>

      <div className="panel history-panel">
        <h4>Ticket History</h4>
        <p className="muted">Selected: {selectedGrievanceId || "None"}</p>
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
