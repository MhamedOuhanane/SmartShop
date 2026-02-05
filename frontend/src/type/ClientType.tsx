import type { User } from "./UserType";

export type CustomerTier = "BASIC" | "SILVER" | "GOLD" | "PLATINUM";

export interface Client extends User {
    name: string;
    email: string;
    loyaltyLevel: CustomerTier;
}

export interface ClientCreate{
    username: string;
    name: string;
    email: string;
    password: string;
    loyaltyLevel: CustomerTier;
}