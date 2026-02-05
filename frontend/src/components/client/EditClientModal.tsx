import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { Loader2, UserCog } from "lucide-react";
import { adminService } from "../../services/adminService";
import type { Client, ClientCreate } from "../../type/ClientType";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "../ui/dialog";
import { Label } from "../ui/label";
import { Input } from "../ui/input";
import { Button } from "../ui/button";

interface EditClientModalProps {
    client: Client;
    isOpen: boolean;
    onClose: () => void;
    onClientUpdated: () => void;
}

type ClientUpdate = Omit<ClientCreate, "password">;

export function EditClientModal({ client, isOpen, onClose, onClientUpdated }: EditClientModalProps) {
    const [loading, setLoading] = useState(false);

    const { register, handleSubmit, reset, formState: { errors } } = useForm<ClientUpdate>();

    useEffect(() => {
        if (isOpen && client) {
            reset({
                username: client.username,
                name: client.name,
                email: client.email,
                loyaltyLevel: client.loyaltyLevel
            });
        }
    }, [client, isOpen, reset]);

    const onSubmit = async (data: ClientUpdate) => {
        setLoading(true);
        try {
            await adminService.updateClient(client.uuid, data);
            toast.success("Client mis à jour avec succès");
            onClose();
            onClientUpdated();
        } catch (error) {
            const message = error instanceof Error ? error.message : "Erreur lors de la modification";
            toast.error(message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="sm:max-w-[450px]">
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2">
                        <UserCog className="text-blue-600" size={20} /> Modifier le client
                    </DialogTitle>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 py-4">
                    <div className="space-y-1">
                        <Label htmlFor="edit-username">Nom d'utilisateur</Label>
                        <Input 
                            id="edit-username" 
                            disabled 
                            className="bg-slate-50"
                            {...register("username")} 
                        />
                        <p className="text-[10px] text-slate-400">L'identifiant ne peut pas être modifié.</p>
                    </div>

                    <div className="space-y-1">
                        <Label htmlFor="edit-name">Nom complet</Label>
                        <Input id="edit-name" {...register("name", { 
                            required: "Le nom est requis", 
                            minLength: { value: 6, message: "Minimum 6 caractères" } 
                        })} />
                        {errors.name && <p className="text-red-500 text-xs">{errors.name.message}</p>}
                    </div>

                    <div className="space-y-1">
                        <Label htmlFor="edit-email">Email</Label>
                        <Input id="edit-email" type="email" {...register("email", { 
                            required: "Email est requis",
                            pattern: { value: /^\S+@\S+$/i, message: "Email invalide" }
                        })} />
                        {errors.email && <p className="text-red-500 text-xs">{errors.email.message}</p>}
                    </div>

                    <DialogFooter className="pt-4">
                        <Button variant="outline" type="button" onClick={onClose}>
                            Annuler
                        </Button>
                        <Button type="submit" disabled={loading} className="bg-blue-600 hover:bg-blue-700 text-white">
                            {loading ? <Loader2 className="animate-spin mr-2" size={18} /> : null}
                            Enregistrer les modifications
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}