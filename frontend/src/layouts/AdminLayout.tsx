import { useState } from "react";
import { Outlet } from "react-router-dom";
import { useSelector } from "react-redux";
import Sidebar from "../components/admin/Sidebar";
import Navbar from "../components/auth/Navbar";
import type { RootState } from "../app/store";

const AdminLayout = () => {
    const [isSidebarOpen, setSidebarOpen] = useState(true);
    const { user } = useSelector((state: RootState) => state.user);

    return (
        <div className="flex h-screen bg-slate-50 overflow-hidden font-sans text-slate-900">
            <Sidebar isOpen={isSidebarOpen} />

            <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
                <Navbar
                    isOpen={isSidebarOpen} 
                    toggleSidebar={() => setSidebarOpen(!isSidebarOpen)} 
                    user={user} 
                />

                {/* Main View Port */}
                <main className="flex-1 p-8 overflow-auto">
                    <div className="max-w-7xl mx-auto animate-in fade-in slide-in-from-bottom-4 duration-500">
                        <Outlet />
                    </div>
                </main>
            </div>
        </div>
    );
};

export default AdminLayout;