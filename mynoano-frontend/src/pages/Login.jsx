
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import "./Login.css";

function Login() {

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    setError("");
    setLoading(true);

    try {

      const response = await axios.post(
        "http://localhost:8080/api/auth/login",
        {
          email: email,
          password: password
        }
      );

      console.log("Login successful:", response.data);

      // Save JWT token
      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
      }

      // Go to MYNO profile
      navigate("/myno/create");

    } catch (err) {

      console.error(err);

      if (err.response?.data) {
        setError(
          typeof err.response.data === "string"
            ? err.response.data
            : "Invalid email or password"
        );
      } else {
        setError("Unable to connect to the server");
      }

    } finally {
      setLoading(false);
    }
  };


  return (
    <div className="login-page">

      <div className="login-card">

        <h1 className="login-title">
          MYNO
        </h1>

        <p className="login-subtitle">
          Welcome back. Login to continue.
        </p>


        <form
          className="login-form"
          onSubmit={handleLogin}
        >

          {/* EMAIL */}

          <div className="login-field">

            <label htmlFor="email">
              Email
            </label>

            <input
              id="email"
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />

          </div>


          {/* PASSWORD */}

          <div className="login-field">

            <label htmlFor="password">
              Password
            </label>

            <input
              id="password"
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

          </div>


          {/* ERROR */}

          {error && (
            <div className="login-error">
              {error}
            </div>
          )}


          {/* BUTTON */}

          <button
            type="submit"
            className="login-button"
            disabled={loading}
          >
            {loading ? "Logging in..." : "Login"}
          </button>

        </form>


        {/* REGISTER */}

        <p className="login-register">

          Don't have an account?

          <Link to="/register">
            Create Account
          </Link>

        </p>

      </div>

    </div>
  );
}

export default Login;