import { useState } from "react";
import api from "../services/api";

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
        <div>

            <h1>Create Your MYNO Profile</h1>

            <form onSubmit={handleSubmit}>

                <div>
                    <label>Display Name</label>

                    <input
                        type="text"
                        name="displayName"
                        value={form.displayName}
                        onChange={handleChange}
                        placeholder="Enter your display name"
                    />
                </div>

                <div>
                    <label>Bio</label>

                    <textarea
                        name="bio"
                        value={form.bio}
                        onChange={handleChange}
                        placeholder="Tell people about yourself"
                    />
                </div>

                <div>
                    <label>Profile Picture URL</label>

                    <input
                        type="text"
                        name="profilePicture"
                        value={form.profilePicture}
                        onChange={handleChange}
                        placeholder="Paste image URL"
                    />
                </div>

                <button type="submit" disabled={loading}>

                    {loading
                        ? "Creating..."
                        : "Create MYNO Profile"
                    }

                </button>

            </form>

            {message && <p>{message}</p>}

        </div>
    );
}

export default MynoProfileCreate;