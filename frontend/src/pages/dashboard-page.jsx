import { useEffect, useState } from "react";
import { fetchExamMasters } from "../api/master-api";

export default function DashboardPage() {
  const [exams, setExams] = useState([]);

  useEffect(() => {
    fetchExamMasters().then(setExams).catch(() => setExams([]));
  }, []);

  return (
    <section className="panel">
      <h3>Dashboard</h3>
      <div className="card-grid">
        <div className="card">
          <p className="muted">Configured Exams</p>
          <p className="count">{exams.length}</p>
        </div>
        <div className="card">
          <p className="muted">Agent Queue</p>
          <p className="count">0</p>
        </div>
        <div className="card">
          <p className="muted">Officer Escalations</p>
          <p className="count">0</p>
        </div>
      </div>
    </section>
  );
}
