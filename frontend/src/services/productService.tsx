import type { ApiResponse } from "@/type/ApiResponse";
import type { Product, ProductRequest } from "@/type/ProductType";
import api from "./api";

export const productService = {
    
    findAll: async (page: number = 0, size: number = 5): Promise<ApiResponse<Product[]>> => {
        const response = await api.get<ApiResponse<Product[]>>("/products", {
            params: { page, size }
        });
        return response.data;
    },

    create: async (data: ProductRequest): Promise<ApiResponse<Product>> => {
        const response = await api.post<ApiResponse<Product>>("/products", data);
        return response.data;
    },
    
    update: async (uuid: string, data: ProductRequest): Promise<ApiResponse<Product>> => {
        const response = await api.put<ApiResponse<Product>>(`/products/${uuid}`, data);
        return response.data;
    },
    
    softDelete: async (uuid: string): Promise<ApiResponse<Product>> => {
        const response = await api.delete<ApiResponse<Product>>(`/products/${uuid}`);
        return response.data;
    },

    restore: async (uuid: string): Promise<ApiResponse<Product>> => {
        const response = await api.put<ApiResponse<Product>>(`/products/${uuid}/restore`);
        return response.data;
    },

    findAllDeleted: async (page: number = 0, size: number = 5): Promise<ApiResponse<Product[]>> => {
        const response = await api.get<ApiResponse<Product[]>>("/products/deleted", {
            params: { page, size }
        });
        return response.data;
    },

    findDeleted: async (uuid: string): Promise<ApiResponse<Product>> => {
        const response = await api.get<ApiResponse<Product>>(`/products/deleted/${uuid}`);
        return response.data;
    }
};