import { Navigate, Outlet } from "react-router-dom"

interface PublicRouteProps {
    isAuth: boolean
}

const PublicRoute = ({isAuth}: PublicRouteProps) =>  {
    if(isAuth) return <Navigate to={"/unauthorized"} replace/>
    return <Outlet />
}

export default PublicRoute;