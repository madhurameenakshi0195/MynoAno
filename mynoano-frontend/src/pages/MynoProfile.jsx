
import { useEffect, useState } from "react";
import api from "../services/api.js";
import "./MynoProfile.css";

function MynoProfile() {

    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        const loadProfile = async () => {

            try {

                // For now we are using user ID 5
                const response = await api.get(
                    "/api/myno/profile/5"
                );

                setProfile(response.data);

            } catch (error) {

                console.error(error);

                setError("Could not load MYNO profile");

            } finally {

                setLoading(false);

            }
        };

        loadProfile();

    }, []);

    if (loading) {
        return (
            <div className="myno-profile-loading">
                Loading MYNO profile...
            </div>
        );
    }

    if (error) {
        return (
            <div className="myno-profile-error">
                {error}
            </div>
        );
    }

    return (
        <div className="myno-profile-page">

            <div className="myno-profile-card">

                <div className="myno-profile-picture">

                    {profile.profilePicture ? (
                        <img
                            src={profile.profilePicture}
                            alt={profile.displayName}
                        />
                    ) : (
                        <span>
                            {profile.displayName
                                ?.charAt(0)
                                .toUpperCase()}
                        </span>
                    )}

                </div>

                <h1>
                    {profile.displayName}
                </h1>

                <p className="myno-profile-bio">
                    {profile.bio || "No bio yet."}
                </p>

                <div className="myno-profile-info">

                    <div>
                        <span>MYNO ID</span>
                        <strong>#{profile.id}</strong>
                    </div>

                    <div>
                        <span>USER ID</span>
                        <strong>#{profile.userId}</strong>
                    </div>

                </div>

                <button className="myno-edit-button">
                    Edit Profile
                </button>

            </div>

        </div>
    );
}

export default MynoProfile;
