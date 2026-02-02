import type { User } from "../features/user/UserType";
import api from "./api";

interface LoginResponse {
    data: User,
    message: string,
    status: number
}

export const authService = {

    login: async (credentials: Record<string,string>) => {
        const response = await api.post<LoginResponse>("/auth/login", credentials);
        return response.data;
    },

    logout: async () => {
        const response = await api.get("/auth/logout");
        return response.data;
    }
}