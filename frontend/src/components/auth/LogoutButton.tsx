import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { authService } from "../../services/authService";
import { logout } from "../../features/user/UserSlice";
import { toast } from "sonner";

const LogoutButton = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const handleLogout = async () => {
        try {
            await authService.logout();
            toast.success("Déconnexion réussie. ", {
                duration: 2000
            });
        } catch (error) {
            const message = error instanceof Error ? error.message : "Erreur lors de la déconnexion";
    
            toast.error(message, {
                duration: 5000
            });
        } finally {
            dispatch(logout());
            navigate("/login");
        }
    };

    return (
        <button 
            onClick={handleLogout}
            className="flex items-center gap-2 px-4 py-2 bg-red-50 text-red-600 hover:bg-red-100 rounded-lg transition-all"
        >
            <span>Se déconnecter</span>
        </button>
    );
};

export default LogoutButton;