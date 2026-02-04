export type UserRole = "ADMIN" | "AGENT" | "CLIENT";

export interface User {
    uuid: string;
    username: string;
    role: UserRole;
    createdAt: string;
    updatedAt: string;
}

export interface UserState {
    user: User | null,
    isAuthenticated: boolean
}