import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";
import type { RootState } from "../../app/store";

const HomeRedirect = () => {
    const { user, isAuthenticated } = useSelector((state: RootState) => state.user);

    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    switch (user?.role) {
        case "ADMIN":
            return <Navigate to="/admin" replace />;
        case "AGENT":
            return <Navigate to="/agent" replace />;
        case "CLIENT":
            return <Navigate to="/client" replace />;
        default:
            return <Navigate to="/login" replace />;
    }
};

export default HomeRedirect;