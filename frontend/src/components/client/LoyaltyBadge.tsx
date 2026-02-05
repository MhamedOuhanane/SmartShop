import type { CustomerTier } from "@/type/ClientType";
import { Shield, ShieldCheck, ShieldAlert, Crown } from "lucide-react";

export const LoyaltyBadge = ({ tier }: { tier: CustomerTier }) => {
    const config = {
        BASIC: {
            label: "Basique",
            classes: "bg-slate-100 text-slate-600 border-slate-200",
            icon: <Shield size={12} />
        },
        SILVER: {
            label: "Argent",
            classes: "bg-blue-50 text-blue-700 border-blue-200",
            icon: <ShieldCheck size={12} />
        },
        GOLD: {
            label: "Or",
            classes: "bg-amber-50 text-amber-700 border-amber-200",
            icon: <ShieldAlert size={12} />
        },
        PLATINUM: {
            label: "Platine",
            classes: "bg-purple-50 text-purple-700 border-purple-200",
            icon: <Crown size={12} />
        }
    };

    const style = config[tier] || config.BASIC;

    return (
        <span className={`flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[11px] font-bold border ${style.classes} transition-all`}>
            {style.icon}
            {style.label}
        </span>
    );
};