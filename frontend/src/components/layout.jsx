import { Link } from "react-router-dom";

export default function Layout({ children }) {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <h2>NTA CCMS</h2>
        <nav>
          <Link to="/">Dashboard</Link>
          <Link to="/tickets">Tickets</Link>
          <Link to="/admin/masters">Admin Masters</Link>
        </nav>
      </aside>
      <main className="main-content">
        <header className="top-header">Candidate Call Centre Management System</header>
        {children}
      </main>
    </div>
  );
}
