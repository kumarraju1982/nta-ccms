import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginWithPassword } from "../api/auth-api";
import { useAuth } from "../state/auth-context";

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [username, setUsername] = useState("agent1");
  const [password, setPassword] = useState("password");
  const [error, setError] = useState("");

  async function onSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      const data = await loginWithPassword(username, password);
      login(data.user);
      navigate("/");
    } catch (err) {
      setError(err.message);
    }
  }

  return (
    <div className="login-wrap">
      <form className="panel" onSubmit={onSubmit}>
        <h3>Login</h3>
        <label>
          Username
          <input value={username} onChange={(e) => setUsername(e.target.value)} />
        </label>
        <label>
          Password
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </label>
        {error ? <p className="error">{error}</p> : null}
        <button type="submit">Sign In</button>
      </form>
    </div>
  );
}
