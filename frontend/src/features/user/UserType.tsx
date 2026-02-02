export type UserRole = "ADMIN" | "AGENT" | "CLIENT";

export interface User {
    uuid: string,
    username: string,
    role: UserRole
}

export interface UserState {
    user: User | null,
    isAuthenticated: boolean
}

export interface ClientProfile {
    uuid: string;
    username: string;
    role: UserRole;
    name: string;
    email: string;
    loyaltyLevel?: "BASIC" | "SILVER" | "GOLD" | "PLATINUM";
    createdAt: string;
    updatedAt: string;
}