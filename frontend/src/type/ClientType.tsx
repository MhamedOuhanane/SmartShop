import type { User } from "./UserType";

export type CustomerTier = "BRONZE" | "SILVER" | "GOLD" | "PLATINUM";

export interface Client extends User {
    name: string;
    email: string;
    loyaltyLevel: CustomerTier;
}