
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import "./Register.css";

function Register() {

const [username, setUsername] = useState("");
const [email, setEmail] = useState("");
const [password, setPassword] = useState("");

const [message, setMessage] = useState("");
const [error, setError] = useState("");

    const navigate = useNavigate();

    const handleRegister = async (e) => {
e.preventDefault();

setMessage("");
setError("");

try {

    const response = await axios.post(
    "http://localhost:8080/api/auth/register",
    {
    username: username,
    email: email,
    password: password
}
);

setMessage("Account created successfully!");

console.log(response.data);

// Go to login after registration
setTimeout(() => {
navigate("/login");
}, 1000);

} catch (err) {

console.error(err);

    if (err.response?.data) {
setError(
typeof err.response.data === "string"
? err.response.data
: "Registration failed"
);
} else {
  setError("Unable to connect to the server");
  }
}
};

    return (
    <div className="register-page">

    <div className="register-card">

    <h1 className="register-title">
    MYNO
    </h1>

    <p className="register-subtitle">
    Create your account
    </p>

    <form
    className="register-form"
    onSubmit={handleRegister}
>

{/* USERNAME */}

<div className="register-field">

<label htmlFor="username">
Username
</label>

<input
id="username"
type="text"
placeholder="Enter username"
value={username}
    onChange={(e) => setUsername(e.target.value)}
    required
    />

    </div>


{/* EMAIL */}

<div className="register-field">

<label htmlFor="email">
Email
</label>

<input
id="email"
type="email"
placeholder="Enter email"
value={email}
    onChange={(e) => setEmail(e.target.value)}
    required
    />

    </div>


{/* PASSWORD */}

<div className="register-field">

<label htmlFor="password">
Password
</label>

<input
id="password"
type="password"
placeholder="Minimum 6 characters"
value={password}
    onChange={(e) => setPassword(e.target.value)}
    minLength={6}
    required
    />

    </div>


{/* ERROR */}

{error && (
<div className="register-error">
{error}
</div>
)}


{/* SUCCESS */}

{message && (
<div className="register-success">
{message}
</div>
)}


{/* BUTTON */}

<button
type="submit"
className="register-button"
>
Create Account
</button>

</form>


{/* LOGIN */}

<p className="register-login">

Already have an account?

<Link to="/login">
Login
</Link>

</p>

</div>

</div>
);
}

export default Register;