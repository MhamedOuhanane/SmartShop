import {Outlet} from "react-router-dom";

const ClientLayout = () => {
    return (
        <div className="flex h-screen bg-gray-100">

            <aside className="w-60 bg-white shadow-md">
                <div className="p-4 font-bold text-xl border-b">
                    Client
                </div>
            </aside>

            <div className="flex-1 flex flex-col">

                <header className="h-14 bg-white shadow flex items-center px-4">
                    Client Navbar
                </header>

                <main className="flex-1 p-6 overflow-auto">
                    <Outlet />
                </main>

            </div>
        </div>
    )
}

export default ClientLayout;