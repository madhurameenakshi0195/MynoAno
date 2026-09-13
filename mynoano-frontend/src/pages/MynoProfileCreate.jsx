
import { useState } from "react";
import api from "../services/api.js";
import "./MynoProfileCreate.css";

function MynoProfileCreate() {

    const [form, setForm] = useState({
        displayName: "",
        bio: "",
        profilePicture: ""
    });

    const [message, setMessage] = useState("");
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {

        setForm({
            ...form,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        setLoading(true);
        setMessage("");

        try {

            const response = await api.post(
                "/api/myno/profile",
                form
            );

            console.log(response.data);

            setMessage("MYNO profile created successfully! 🎉");

        } catch (error) {

            console.error(error);

            setMessage(
                error.response?.data?.message ||
                "Something went wrong"
            );

        } finally {

            setLoading(false);
        }
    };

    return (
        <div className="myno-profile-page">

            <div className="myno-profile-card">

                <div className="myno-profile-header">

                    <div className="myno-profile-logo">
                        MYNO
                    </div>

                    <h1>Create Your Profile</h1>

                    <p>
                        Tell people a little about yourself.
                    </p>

                </div>

                <form
                    className="myno-profile-form"
                    onSubmit={handleSubmit}
                >

                    <div className="myno-form-group">

                        <label>
                            Display Name
                        </label>

                        <input
                            type="text"
                            name="displayName"
                            value={form.displayName}
                            onChange={handleChange}
                            placeholder="What should people call you?"
                            required
                        />

                    </div>

                    <div className="myno-form-group">

                        <label>
                            Bio
                        </label>

                        <textarea
                            name="bio"
                            value={form.bio}
                            onChange={(e) => {
                                console.log("BIO:", e.target.value);

                                setForm({
                                    ...form,
                                    bio: e.target.value
                                });
                            }}
                            placeholder="Tell people something about yourself..."
                        />

                    </div>

                    <div className="myno-form-group">

                        <label>
                            Profile Picture URL
                        </label>

                        <input
                            type="text"
                            name="profilePicture"
                            value={form.profilePicture}
                            onChange={handleChange}
                            placeholder="Paste an image URL"
                        />

                    </div>

                    <button
                        className="myno-create-button"
                        type="submit"
                        disabled={loading}
                    >
                        {loading
                            ? "Creating..."
                            : "Create MYNO Profile"
                        }
                    </button>

                </form>

                {message && (
                    <p className="myno-profile-message">
                        {message}
                    </p>
                )}

            </div>

        </div>
    );
}

export default MynoProfileCreate;