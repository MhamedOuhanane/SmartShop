export interface PaginationDTO {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    isFirst: boolean;
    isLast: boolean;
}

export interface ApiResponse<T> {
    date: string;
    message: string;
    status: number;
    data: T;
    path: string;
    pagination: PaginationDTO | null;
}