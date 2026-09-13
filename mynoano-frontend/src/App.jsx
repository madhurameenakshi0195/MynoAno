import { BrowserRouter, Routes, Route } from "react-router-dom";

import Register from "./pages/register";
import Login from "./pages/Login";
import Profile from "./pages/Profile";
import MynoProfileCreate from "./pages/MynoProfileCreate";
import MynoProfile from "./pages/MynoProfile";

function App() {

    return (
        <BrowserRouter>

            <Routes>

                <Route
                    path="/register"
                    element={<Register />}
                />

                <Route
                    path="/login"
                    element={<Login />}
                />

                <Route
                    path="/profile"
                    element={<Profile />}
                />

                <Route
                    path="/myno/create"
                    element={<MynoProfileCreate />}
                />

                <Route
                    path="/myno/:userId"
                    element={<MynoProfile />}
                />

            </Routes>

        </BrowserRouter>
    );
}

export default App;