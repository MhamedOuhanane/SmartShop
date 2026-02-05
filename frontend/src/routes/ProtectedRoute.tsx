import {Navigate, Outlet} from "react-router-dom";

interface ProtectedRouteProps {
    isAuth: boolean,
    allowedRole: string[],
    userRole: string
}

const ProtectedRoute = ({ isAuth, allowedRole, userRole}: ProtectedRouteProps) => {
    if (!isAuth) return <Navigate to={"/login"} replace />
    
    if (userRole && !allowedRole?.includes(userRole)) return <Navigate to={"/unauthorized"} replace />

    return <Outlet />;
}

export default ProtectedRoute;