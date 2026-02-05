import { useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { Plus, Loader2, UserPlus } from "lucide-react";
import { adminService } from "../../services/adminService";
import type { ClientCreate } from "../../type/ClientType";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "../ui/dialog";
import { DialogTrigger } from "@radix-ui/react-dialog";
import { Label } from "../ui/label";
import { Input } from "../ui/input";
import { Button } from "../ui/button";

interface AddClientModalProps {
    onClientAdded: () => void;
}

export function AddClientModal({ onClientAdded }: AddClientModalProps) {
    const [open, setOpen] = useState(false);
    const [loading, setLoading] = useState(false);

    const { register, handleSubmit, reset, formState: { errors } } = useForm({
        defaultValues: {
            username: "",
            name: "",
            email: "",
            password: "",
            loyaltyLevel: "BASIC"
        } as ClientCreate
    });

    const onSubmit = async (data: ClientCreate) => {
        setLoading(true);
        try {
            await adminService.addClient(data);
            toast.success("Client ajouté avec succès");
            reset();
            setOpen(false);
            onClientAdded();
        } catch (error) {
            const message = error instanceof Error ? error.message : "Erreur lors de l'ajout";
            toast.error(message);
        } finally {
        setLoading(false);
        }
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button className="flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white">
                    <Plus size={18} /> Nouveau Client
                </Button>
            </DialogTrigger>
        
            <DialogContent className="sm:max-w-[450px]">
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2">
                        <UserPlus className="text-blue-600" /> Ajouter un client
                    </DialogTitle>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 py-4">
                    {/* Nom d'utilisateur (Username) */}
                    <div className="space-y-1">
                        <Label htmlFor="username">Nom d'utilisateur</Label>
                        <Input id="username" {...register("username", { required: "Username est requis" })} />
                        {errors.username && <p className="text-red-500 text-xs">{errors.username.message}</p>}
                    </div>

                    {/* Nom complet (Match: @Size min=6) */}
                    <div className="space-y-1">
                        <Label htmlFor="name">Nom complet</Label>
                        <Input id="name" {...register("name", { 
                            required: "Le nom est requis", 
                            minLength: { value: 6, message: "Minimum 6 caractères" } 
                        })} />
                        {errors.name && <p className="text-red-500 text-xs">{errors.name.message}</p>}
                    </div>

                    {/* Email (Match: @Email) */}
                    <div className="space-y-1">
                        <Label htmlFor="email">Email</Label>
                        <Input id="email" type="email" {...register("email", { 
                            required: "Email est requis",
                            pattern: { value: /^\S+@\S+$/i, message: "Email invalide" }
                        })} />
                        {errors.email && <p className="text-red-500 text-xs">{errors.email.message}</p>}
                    </div>

                    {/* Mot de passe (Match: @Size min=6) */}
                    <div className="space-y-1">
                        <Label htmlFor="password">Mot de passe</Label>
                        <Input id="password" type="password" {...register("password", { 
                            required: "Mot de passe est requis",
                            minLength: { value: 6, message: "Minimum 6 caractères" }
                        })} />
                        {errors.password && <p className="text-red-500 text-xs">{errors.password.message}</p>}
                    </div>

                    <DialogFooter className="pt-4">
                        <Button variant="outline" type="button" onClick={() => setOpen(false)}>
                            Annuler
                        </Button>
                        <Button type="submit" disabled={loading} className="bg-blue-600 hover:bg-blue-700">
                            {loading ? <Loader2 className="animate-spin mr-2" size={18} /> : null}
                            Enregistrer
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}