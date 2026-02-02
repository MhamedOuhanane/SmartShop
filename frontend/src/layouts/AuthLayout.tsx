import { Outlet } from "react-router-dom";

const AuthLayout = () => {
    return (
        <div className="flex flex-col md:flex-row min-h-screen w-full bg-white overflow-hidden">
        
            <div className="hidden md:flex md:w-1/2 bg-blue-600 relative overflow-hidden">
                <div className="absolute inset-0 bg-gradient-to-br from-blue-700 to-indigo-900 opacity-90"></div>
                <div className="relative z-10 flex flex-col items-center justify-center w-full p-12 text-white">
                    <div className="bg-white/10 p-4 rounded-2xl backdrop-blur-md mb-6">
                        <svg className="w-16 h-16 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                        </svg>
                    </div>
                    <h1 className="text-4xl font-bold mb-4">SmartShop</h1>
                    <p className="text-blue-100 text-center text-lg max-w-md">
                        Gérez votre boutique intelligemment avec notre plateforme tout-en-un.
                    </p>
                </div>
                <div className="absolute -bottom-24 -left-24 w-64 h-64 bg-white/10 rounded-full blur-3xl"></div>
                <div className="absolute -top-24 -right-24 w-64 h-64 bg-blue-400/20 rounded-full blur-3xl"></div>
            </div>
            
            <div className="w-full md:w-1/2 flex items-center justify-center p-8 bg-gray-50 overflow-y-auto">
                <div className="w-full max-w-md">
                    <Outlet />
                </div>
            </div>
        </div>
    );
};

export default AuthLayout;