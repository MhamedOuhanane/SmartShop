import type { ApiResponse } from "../type/ApiResponse";
import type { Client } from "../type/ClientType";
import api from "./api"

export const adminService = {
    getClients: async (page: number = 0, size: number = 5) => {
        const response = await api.get<ApiResponse<Client[]>>('/admins/clients', {
            params: {page, size}
        });

        return response.data;
    }
} 