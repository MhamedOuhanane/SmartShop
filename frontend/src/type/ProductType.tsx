export interface Product {
    uuid: string;
    name: string;
    price: number;
    stock: number;
    prcTVA: number;
    deletedAt: string | null;
    createdAt?: string;
    updatedAt?: string;
}

export interface ProductRequest {
    name: string;
    price: number;
    stock: number;
    prcTVA: number;
}