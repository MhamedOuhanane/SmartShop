import { useEffect, useState } from "react";
import { 
    Loader2, 
    ChevronLeft, 
    ChevronRight, 
    Package,
    CircleDollarSign,
    Layers
} from "lucide-react";
import type { Product } from "@/type/ProductType";
import type { PaginationDTO } from "@/type/ApiResponse";
import { productService } from "@/services/productSefvice";

const AdminProduct = () => {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);
    const [pagination, setPagination] = useState<PaginationDTO | null>(null);
    const [currentPage, setCurrentPage] = useState(0);

    const fetchProducts = async (page: number) => {
        setLoading(true);
        try {
            const response = await productService.findAll(page, 5);
            setProducts(response.data);
            setPagination(response.pagination);
        } catch (error) {
            console.error("Erreur lors du chargement des produits", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProducts(currentPage);
    }, [currentPage]);

    return (
        <div className="w-full space-y-4">
            <div className="flex justify-between items-end">
                <div>
                    <h2 className="text-xl font-bold text-slate-800">Catalogue Actif</h2>
                    <p className="text-sm text-slate-500">Liste des produits disponibles à la vente</p>
                </div>
            </div>

            <div className="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden">
                {loading ? (
                    <div className="flex flex-col items-center justify-center h-64 gap-2">
                        <Loader2 className="animate-spin text-blue-500" size={32} />
                        <span className="text-slate-500 text-sm">Récupération des données...</span>
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="bg-slate-50/80 border-b border-slate-200">
                                    <th className="px-6 py-4 text-xs font-semibold text-slate-600 uppercase">Produit</th>
                                    <th className="px-6 py-4 text-xs font-semibold text-slate-600 uppercase">Prix HT</th>
                                    <th className="px-6 py-4 text-xs font-semibold text-slate-600 uppercase">TVA</th>
                                    <th className="px-6 py-4 text-xs font-semibold text-slate-600 uppercase">Stock</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100">
                                {products.map((product) => (
                                    <tr key={product.uuid} className="hover:bg-slate-50/50 transition-colors">
                                        <td className="px-6 py-4">
                                            <div className="flex items-center gap-3">
                                                <Package className="text-slate-400" size={18} />
                                                <span className="font-medium text-slate-700">{product.name}</span>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="flex items-center gap-1 text-slate-600">
                                                <CircleDollarSign size={14} className="text-slate-400" />
                                                <span>{product.price.toFixed(2)} €</span>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className="text-slate-500">{(product.prcTVA * 100).toFixed(0)}%</span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="flex items-center gap-2">
                                                <Layers size={14} className={product.stock < 10 ? "text-orange-500" : "text-slate-400"} />
                                                <span className={`text-sm font-medium ${product.stock < 10 ? "text-orange-600" : "text-slate-600"}`}>
                                                    {product.stock} unités
                                                </span>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {pagination && (
                    <div className="px-6 py-4 bg-slate-50/50 border-t border-slate-200 flex items-center justify-between">
                        <span className="text-sm text-slate-500">
                            Affichage de <span className="font-semibold text-slate-700">{products.length}</span> produits
                        </span>
                        
                        <div className="flex items-center gap-3">
                            <button
                                onClick={() => setCurrentPage(prev => prev - 1)}
                                disabled={pagination.isFirst || loading}
                                className="p-2 rounded-md border border-slate-300 bg-white text-slate-600 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-sm"
                                title="Page précédente"
                            >
                                <ChevronLeft size={18} />
                            </button>
                            
                            <span className="text-sm font-medium text-slate-700 bg-white border border-slate-300 px-3 py-1.5 rounded-md shadow-sm">
                                {pagination.page + 1} / {pagination.totalPages}
                            </span>
                            
                            <button
                                onClick={() => setCurrentPage(prev => prev + 1)}
                                disabled={pagination.isLast || loading}
                                className="p-2 rounded-md border border-slate-300 bg-white text-slate-600 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-sm"
                                title="Page suivante"
                            >
                                <ChevronRight size={18} />
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default AdminProduct;