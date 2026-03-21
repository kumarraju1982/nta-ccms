import { Navigate, Route, Routes } from "react-router-dom";
import Layout from "./components/layout";
import DashboardPage from "./pages/dashboard-page";
import TicketsPage from "./pages/tickets-page";
import OfficerWorkspacePage from "./pages/officer-workspace-page";
import LoginPage from "./pages/login-page";
import AdminMastersPage from "./pages/admin-masters-page";
import { AuthProvider, useAuth } from "./state/auth-context";

function ProtectedRoute({ children }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  return children;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Layout>
              <DashboardPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/tickets"
        element={
          <ProtectedRoute>
            <Layout>
              <TicketsPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/officer"
        element={
          <ProtectedRoute>
            <Layout>
              <OfficerWorkspacePage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/masters"
        element={
          <ProtectedRoute>
            <Layout>
              <AdminMastersPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <AppRoutes />
    </AuthProvider>
  );
}
