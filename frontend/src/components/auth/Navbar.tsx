import { Menu, X } from "lucide-react";
import type { User } from "../../type/UserType";

interface NavbarProps {
    isOpen: boolean;
    toggleSidebar: () => void;
    user: User | null;
}

const Navbar = ({ isOpen, toggleSidebar, user }: NavbarProps) => {
    return (
        <header className="h-16 bg-white border-b border-slate-200 flex items-center justify-between px-8 shadow-sm">
            <button 
                onClick={toggleSidebar} 
                className="p-2 hover:bg-slate-100 rounded-lg text-slate-500 transition-colors"
            >
                {isOpen ? <X size={20} /> : <Menu size={20} />}
            </button>

            <div className="flex items-center gap-6">
                <div className="flex items-center gap-3 pl-6 border-l border-slate-200">
                    <div className="text-right hidden sm:block">
                        <p className="text-sm font-bold text-slate-900 leading-none">{user?.username}</p>
                        <span className="text-[10px] font-semibold text-blue-600 uppercase tracking-wider">Administrateur</span>
                    </div>
                    <div className="h-10 w-10 bg-gradient-to-br from-slate-100 to-slate-200 rounded-full flex items-center justify-center text-slate-600 font-bold border border-slate-300 shadow-sm">
                        {user?.username?.charAt(0).toUpperCase()}
                    </div>
                </div>
            </div>
        </header>
    );
};

export default Navbar;