import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { authService } from "../../services/authService";
import { logout } from "../../features/user/UserSlice";
import { toast } from "sonner";
import { LogOut } from "lucide-react";

interface LogoutButtonProps {
    hideText?: boolean;
}

const LogoutButton = ({ hideText = false }: LogoutButtonProps) => {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const handleLogout = async () => {
        try {
            await authService.logout();
            toast.success("Déconnexion réussie");
        } catch (error) {
            const message = error instanceof Error ? error.message : "Erreur lors de la déconnexion";
            toast.error(message);
        } finally {
            dispatch(logout());
            navigate("/login");
        }
    };

    return (
        <button 
            onClick={handleLogout}
            title={hideText ? "Se déconnecter" : ""}
            className={`flex items-center gap-3 w-full px-4 py-3 text-red-400 hover:bg-red-500/10 hover:text-red-500 rounded-xl transition-all duration-200 group ${
                hideText ? "justify-center" : ""
            }`}
        >
            <LogOut size={20} className="shrink-0 transition-transform group-hover:-translate-x-1" />
            
            {!hideText && (
                <span className="font-medium text-sm whitespace-nowrap overflow-hidden">
                    Se déconnecter
                </span>
            )}
        </button>
    );
};

export default LogoutButton;