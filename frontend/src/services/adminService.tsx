import type { ApiResponse } from "../type/ApiResponse";
import type { Client, ClientCreate } from "../type/ClientType";
import api from "./api"

export const adminService = {
    getClients: async (page: number = 0, size: number = 5) => {
        const response = await api.get<ApiResponse<Client[]>>('/admins/clients', {
            params: {page, size}
        });

        return response.data;
    },

    addClient: async (clientData: ClientCreate) => {
        const response = await api.post<ApiResponse<Client>>('/admins/clients', clientData);

        return response.data;
    }
} 