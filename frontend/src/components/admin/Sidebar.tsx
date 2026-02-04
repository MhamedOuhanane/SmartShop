import { Link, useLocation } from "react-router-dom";
import { LayoutDashboard, Users, ShoppingBag } from "lucide-react";
import LogoutButton from "../auth/LogoutButton";

interface SidebarProps {
    isOpen: boolean;
}

const Sidebar = ({ isOpen }: SidebarProps) => {
    const location = useLocation();

    const menuItems = [
        { path: "/admin", name: "Tableau de bord", icon: <LayoutDashboard size={20} /> },
        { path: "/admin/clients", name: "Gestion Clients", icon: <Users size={20} /> },
        { path: "/admin/products", name: "Produits", icon: <ShoppingBag size={20} /> },
    ];

    return (
        <aside className={`${isOpen ? "w-64" : "w-20"} bg-slate-900 text-slate-300 transition-all duration-300 ease-in-out flex flex-col shadow-xl`}>
        
        <div className="p-6 flex items-center gap-3 border-b border-slate-800">
            <div className="h-8 w-8 bg-blue-600 rounded-lg flex items-center justify-center text-white font-bold shrink-0">S</div>
            {isOpen && <span className="font-bold text-xl text-white tracking-tight truncate">SmartShop</span>}
        </div>

        <nav className="flex-1 p-4 space-y-2 overflow-y-auto mt-4">
            {menuItems.map((item) => (
            <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-4 px-4 py-3 rounded-xl transition-all duration-200 ${
                location.pathname === item.path
                    ? "bg-blue-600 text-white shadow-lg shadow-blue-900/20"
                    : "hover:bg-slate-800 hover:text-white"
                }`}
            >
                <span className="shrink-0">{item.icon}</span>
                {isOpen && <span className="font-medium text-sm truncate">{item.name}</span>}
            </Link>
            ))}
        </nav>

        <div className="p-4 border-t border-slate-800">
            <LogoutButton hideText={!isOpen} />
        </div>
        </aside>
    );
};

export default Sidebar;