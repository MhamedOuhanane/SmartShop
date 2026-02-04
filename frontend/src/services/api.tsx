import axios, {AxiosError} from 'axios';
import { toast } from 'sonner';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.response.use(
    response => response,
    (error: AxiosError)=> {
        if (!error.response) {
            console.error("Network error:", error)
            return Promise.reject(error)
        }

        const status: number = error.response?.status

        if (status === 401) {
            localStorage.removeItem("user");
            toast.error("Session expirée. Veuillez vous reconnecter.");
            setTimeout(() => {
                window.location.href = "/login";
            }, 1500);
        } else if (status === 403) {
            toast.error("Accès refusé : Vous n'avez pas les droits nécessaires.");
        }

        return Promise.reject(error)
    }
);

export default api;