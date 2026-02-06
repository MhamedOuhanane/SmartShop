import { useState } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import { Loader2, PackagePlus, AlertCircle, Plus } from "lucide-react";
import { toast } from "sonner";
import { productService } from "@/services/productSefvice";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
    DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

const productSchema = yup.object({
    name: yup.string()
        .required("Le nom du produit est obligatoire")
        .min(4, "Le nom doit avoir au moins 4 caractères")
        .max(100, "Le nom ne doit pas dépasser 100 caractères"),
    price: yup.number()
        .typeError("Le prix doit être un nombre")
        .required("Le prix est obligatoire")
        .positive("Le prix doit être supérieur à 0"),
    stock: yup.number()
        .typeError("Le stock doit être un nombre")
        .required("Le stock est obligatoire")
        .integer("Le stock doit être un nombre entier")
        .min(0, "Le stock ne peut pas être négatif"),
    prcTVA: yup.number()
        .typeError("La TVA est obligatoire")
        .required("La TVA est obligatoire")
        .min(0.01)
        .max(1),
}).required();

type ProductFormData = yup.InferType<typeof productSchema>;

interface AddProductModalProps {
    onProductAdded: () => void;
}

export function AddProductModal({ onProductAdded }: AddProductModalProps) {
    const [open, setOpen] = useState(false);

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
        reset
    } = useForm<ProductFormData>({
        resolver: yupResolver(productSchema),
        defaultValues: {
            name: "",
            price: 0,
            stock: 0,
            prcTVA: 0.20
        }
    });

    const onSubmit = async (data: ProductFormData) => {
        try {
            await productService.create(data);
            toast.success("Produit créé avec succès");
            reset();
            setOpen(false);
            onProductAdded();
        } catch (error) {
            const message = error instanceof Error ? error.message : "Erreur lors de la création";
            toast.error(message);
        }
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button className="bg-blue-600 hover:bg-blue-700 text-white flex gap-2">
                    <Plus size={18} /> Nouveau Produit
                </Button>
            </DialogTrigger>

            <DialogContent className="sm:max-w-[450px]">
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2 text-blue-600">
                        <PackagePlus size={24} />
                        <span className="text-xl font-bold">Ajouter un nouveau produit</span>
                    </DialogTitle>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 py-4">
                    <div className="space-y-1">
                        <Label htmlFor="name">Nom du produit</Label>
                        <Input
                            id="name"
                            {...register("name")}
                            className={errors.name ? "border-red-500" : "border-slate-300"}
                        />
                        {errors.name && (
                            <p className="text-red-500 text-xs mt-1 flex items-center gap-1">
                                <AlertCircle size={12} /> {errors.name.message}
                            </p>
                        )}
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-1">
                            <Label htmlFor="price">Prix HT</Label>
                            <Input
                                id="price"
                                type="number"
                                step="0.01"
                                {...register("price")}
                                className={errors.price ? "border-red-500" : "border-slate-300"}
                            />
                            {errors.price && <p className="text-red-500 text-xs mt-1">{errors.price.message}</p>}
                        </div>

                        <div className="space-y-1">
                            <Label htmlFor="stock">Stock</Label>
                            <Input
                                id="stock"
                                type="number"
                                {...register("stock")}
                                className={errors.stock ? "border-red-500" : "border-slate-300"}
                            />
                            {errors.stock && <p className="text-red-500 text-xs mt-1">{errors.stock.message}</p>}
                        </div>
                    </div>

                    <div className="space-y-1">
                        <Label htmlFor="prcTVA">TVA (Taux)</Label>
                        <select
                            id="prcTVA"
                            {...register("prcTVA")}
                            className="w-full h-10 px-3 py-2 border border-slate-300 rounded-lg bg-white outline-none focus:border-blue-500 transition-all text-sm"
                        >
                            <option value={0.055}>5.5%</option>
                            <option value={0.10}>10%</option>
                            <option value={0.20}>20%</option>
                        </select>
                        {errors.prcTVA && <p className="text-red-500 text-xs mt-1">{errors.prcTVA.message}</p>}
                    </div>

                    <DialogFooter className="flex gap-3 pt-4">
                        <Button
                            type="button"
                            variant="outline"
                            onClick={() => setOpen(false)}
                            className="flex-1"
                        >
                            Annuler
                        </Button>
                        <Button
                            type="submit"
                            disabled={isSubmitting}
                            className="flex-[2] bg-blue-600 hover:bg-blue-700 text-white"
                        >
                            {isSubmitting ? <Loader2 className="animate-spin" size={18} /> : "Enregistrer"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}