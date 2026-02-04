import { useNavigate } from "react-router-dom";

const NotFound = () => {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen bg-slate-50 flex flex-col items-center justify-center p-5 text-center">
            <span className="text-blue-600 font-black text-9xl opacity-10 absolute select-none">404</span>
            <div className="relative z-10">
                <h2 className="text-2xl font-bold text-slate-800 mb-2">Page non trouvée</h2>
                <p className="text-slate-500 mb-8">
                    La page que vous cherchez n'existe pas.
                </p>
                <button
                    onClick={() => navigate("/")}
                    className="px-10 py-3.5 bg-slate-900 text-white font-bold rounded-xl hover:bg-slate-800 transition-all active:scale-95 shadow-xl"
                >
                    Aller à l'accueil
                </button>
            </div>
        </div>
    );
};

export default NotFound;