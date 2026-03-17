import { useEffect, useState } from "react";
import { createTicket, fetchTickets } from "../api/ticket-api";

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

  async function load() {
    const next = await fetchTickets();
    setTickets(next);
  }

  useEffect(() => {
    load().catch(() => setTickets([]));
  }, []);

  async function onCreate(e) {
    e.preventDefault();
    await createTicket(form);
    await load();
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
            <tr key={t.grievanceId}>
              <td>{t.grievanceId}</td>
              <td>{t.candidateName}</td>
              <td>{t.examCode}</td>
              <td>{t.category}</td>
              <td>{t.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
