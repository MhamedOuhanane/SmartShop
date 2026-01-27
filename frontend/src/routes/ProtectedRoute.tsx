import {Navigate, Outlet} from "react-router-dom";

interface ProtectedRouteProps {
    isAuth: boolean,
    allowedRole: String[],
    userRole: string
}

const ProtectedRoute = ({ isAuth, allowedRole, userRole}: ProtectedRouteProps) => {
    if (!isAuth) return <Navigate to={"/login"} replace />
    if (allowedRole.includes(userRole)) return <Navigate to={"/unauthorized"} replace />

    return <Outlet />;
}

export default ProtectedRoute;