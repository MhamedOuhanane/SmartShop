import {Outlet} from "react-router-dom";

const AdminLayout = () => {
    return (
        <div className="flex h-screen bg-gray-100">

            <aside className="w-64 bg-slate-900 text-white">
                <div className="p-4 font-bold text-xl border-b border-slate-700">
                    Admin
                </div>
            </aside>

            <div className="flex-1 flex flex-col">
                <header className="h-14 bg-white shadow flex items-center px-4">
                    Admin Navbar
                </header>

                <main className="flex-1 p-6 overflow-auto">
                    <Outlet />
                </main>

            </div>
        </div>
    )
}

export default AdminLayout;