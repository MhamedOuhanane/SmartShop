import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import type { RootState } from "../../app/store";

const Unauthorized = () => {
    const navigate = useNavigate();
    const { isAuthenticated } = useSelector((state: RootState) => state.user);

    return (
        <div className="min-h-screen bg-white flex flex-col items-center justify-center p-5 text-center">
            <div className="w-20 h-20 bg-red-100 text-red-600 rounded-full flex items-center justify-center mb-6">
                <svg className="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                </svg>
            </div>
            
            <h2 className="text-3xl font-bold text-slate-900 mb-2">Accès Refusé</h2>
            <p className="text-slate-500 mb-8 max-w-sm">
                Désolé, vous ne disposez pas des autorisations suffisantes pour consulter cette page.
            </p>

            <div className="flex gap-4">
                <button
                    onClick={() => navigate(-1)}
                    className="px-6 py-3 border border-slate-200 text-slate-600 font-semibold rounded-xl hover:bg-slate-50 transition-all"
                >
                    Retour
                </button>
                <button
                    onClick={() => navigate("/")}
                    className="px-6 py-3 bg-blue-600 text-white font-semibold rounded-xl hover:bg-blue-700 shadow-lg shadow-blue-200 transition-all"
                >
                    {isAuthenticated ? "Ma Table de bord" : "Connexion"}
                </button>
            </div>
        </div>
    );
};

export default Unauthorized;