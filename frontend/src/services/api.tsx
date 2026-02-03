import axios, {AxiosError} from 'axios'

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
            console.log("Unauthorized! Redirect to login")
            localStorage.removeItem("user");
            window.location.href = "/login";
        } else if (status === 403) {
            console.log("Forbidden! Access denied")
        }

        return Promise.reject(error)
    }
);

export default api;