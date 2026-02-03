import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { loginSuccess } from "../../features/user/UserSlice";
import { authService } from "../../services/authService";
import type { AxiosError } from "axios";

interface MyBackendError {
    errors?: Record<string, string>;
    message?: string;
}

const schema = yup.object({
    username: yup.string().required("Le nom d'utilisateur est requis"),
    password: yup.string().min(6, "Minimum 6 caractères").required("Le mot de passe est requis")
}).required();

type FormData = yup.InferType<typeof schema>;

const LoginForm = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { 
        register, 
        handleSubmit, 
        setError, 
        formState: { errors, isSubmitting } 
    } = useForm<FormData>({
        resolver: yupResolver(schema)
    });

    const onSubmit = async (data: FormData) => {
        try {
            const responseData = await authService.login(data);
            
            dispatch(loginSuccess(responseData.data));
            
            const role = responseData.data.role;
            if (role === "ADMIN") navigate("/admin");
            else if (role === "AGENT") navigate("/agent");
            else navigate("/client");

        } catch (err) {
            const axiosError = err as AxiosError<MyBackendError>;
            const responseBody = axiosError.response?.data;

            if (axiosError.response?.status === 400 && responseBody?.errors) {
                Object.keys(responseBody.errors).forEach((field) => {
                    setError(field as keyof FormData, {
                        type: "server",
                        message: responseBody.errors![field]
                    });
                });
            } else {
                alert(responseBody?.message || "Erreur de connexion au serveur");
            }
        }
    };

    return (
        <div className="animate-in fade-in duration-700">
            <div className="mb-10 text-left">
                <h2 className="text-3xl font-extrabold text-slate-900">Connexion</h2>
                <p className="text-slate-500 mt-2 font-medium">Bon retour ! Veuillez saisir vos identifiants.</p>
            </div>

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-2">Nom d'utilisateur</label>
                    <input
                        {...register("username")}
                        type="text"
                        placeholder="Ex: admin_01"
                        className={`w-full px-4 py-3 rounded-xl border bg-white transition-all duration-300 focus:outline-none focus:ring-4 ${
                            errors.username ? 'border-red-400 focus:ring-red-100' : 'border-slate-200 focus:border-blue-500 focus:ring-blue-100'
                        }`}
                    />
                    {errors.username && <p className="text-red-500 text-xs mt-1 ml-1">{errors.username?.message}</p>}
                </div>

                <div>
                    <label className="block text-sm font-semibold text-slate-700 mb-2">Mot de passe</label>
                    <input
                        {...register("password")}
                        type="password"
                        placeholder="••••••••"
                        className={`w-full px-4 py-3 rounded-xl border bg-white transition-all duration-300 focus:outline-none focus:ring-4 ${
                            errors.password ? 'border-red-400 focus:ring-red-100' : 'border-slate-200 focus:border-blue-500 focus:ring-blue-100'
                        }`}
                    />
                    {errors.password && <p className="text-red-500 text-xs mt-1 ml-1">{errors.password?.message}</p>}
                </div>

                <button
                    type="submit"
                    disabled={isSubmitting}
                    className="w-full py-3.5 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl shadow-lg shadow-blue-200 transition-all active:scale-95 disabled:bg-blue-300"
                >
                    {isSubmitting ? "Chargement..." : "Se connecter"}
                </button>
            </form>

            <div className="mt-10 border-t border-slate-200 pt-6 text-center">
                <p className="text-xs text-slate-400 uppercase tracking-widest">
                    Système de gestion SmartShop
                </p>
            </div>
        </div>
    );
};

export default LoginForm;