
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../services/api";
import "./MynoProfile.css";

function MynoProfile() {

    const { userId } = useParams();

    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        const fetchProfile = async () => {

            try {

                const response = await api.get(
                    `/api/myno/profile/${userId}`
                );

                setProfile(response.data);

            } catch (error) {

                console.error(error);

                setError("MYNO profile not found");

            } finally {

                setLoading(false);
            }
        };

        fetchProfile();

    }, [userId]);

    if (loading) {
        return (
            <div className="myno-page">
                <div className="myno-loading">
                    Loading MYNO...
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="myno-page">
                <div className="myno-error">
                    {error}
                </div>
            </div>
        );
    }

    return (
        <div className="myno-page">

            <div className="myno-profile-card">

                {/* Profile Header */}
                <div className="myno-header">

                    <img
                        className="myno-profile-picture"
                        src={profile.profilePicture}
                        alt={profile.displayName}
                    />

                    <div className="myno-profile-info">

                        <h1>{profile.displayName}</h1>

                        <p className="myno-username">
                            @myno_{userId}
                        </p>

                        <p className="myno-bio">
                            {profile.bio}
                        </p>

                    </div>

                </div>

                {/* Profile Stats */}
                <div className="myno-stats">

                    <div className="myno-stat">
                        <strong>0</strong>
                        <span>Friends</span>
                    </div>

                    <div className="myno-stat">
                        <strong>0</strong>
                        <span>Events</span>
                    </div>

                    <div className="myno-stat">
                        <strong>0</strong>
                        <span>Photos</span>
                    </div>

                </div>

                {/* Actions */}
                <div className="myno-actions">

                    <button className="myno-connect-btn">
                        Connect
                    </button>

                    <button className="myno-message-btn">
                        Message
                    </button>

                </div>

                {/* Photos Section */}
                <div className="myno-section">

                    <h2>Photos</h2>

                    <div className="myno-photo-grid">

                        <div className="myno-photo-placeholder">
                            +
                        </div>

                        <div className="myno-photo-placeholder">
                            +
                        </div>

                        <div className="myno-photo-placeholder">
                            +
                        </div>

                    </div>

                </div>

            </div>

        </div>
    );
}

export default MynoProfile;