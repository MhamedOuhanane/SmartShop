import { useEffect, useState } from "react";
import { Loader2, ChevronLeft, ChevronRight, Package } from "lucide-react";
import type { Product } from "@/type/ProductType";
import type { PaginationDTO } from "@/type/ApiResponse";
import { Button } from "@/components/ui/button";
import { productService } from "@/services/productService";
import { AddProductModal } from "@/components/product/AddProductForm";
import { EditProductModal } from "@/components/product/EditProductModal";

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
        <div className="w-full space-y-4 p-4">
            <div className="flex justify-between items-center bg-white p-5 rounded-xl border border-slate-200 shadow-sm">
                <div>
                    <h2 className="text-2xl font-bold text-slate-800">Gestion des Produits</h2>
                    <p className="text-sm text-slate-500">Consulter et modifier votre inventaire</p>
                </div>
                <AddProductModal onProductAdded={() => fetchProducts(currentPage)} />
            </div>

            <div className="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden">
                {loading ? (
                    <div className="flex flex-col items-center justify-center h-64 gap-2 text-slate-400">
                        <Loader2 className="animate-spin text-blue-500" size={32} />
                        Chargement des produits...
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead className="bg-slate-50 border-b border-slate-200">
                                <tr>
                                    <th className="px-6 py-4 text-xs font-bold uppercase text-slate-600">Désignation</th>
                                    <th className="px-6 py-4 text-xs font-bold uppercase text-slate-600">Prix HT</th>
                                    <th className="px-6 py-4 text-xs font-bold uppercase text-slate-600">Stock</th>
                                    <th className="px-6 py-4 text-xs font-bold uppercase text-slate-600">TVA</th>
                                    <th className="px-6 py-4 text-xs font-bold uppercase text-slate-600 text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {products.map((product) => (
                                    <tr key={product.uuid} className="hover:bg-slate-50 border-b last:border-0 transition-colors">
                                        <td className="px-6 py-4 flex items-center gap-3">
                                            <Package size={18} className="text-slate-400" />
                                            <span className="font-bold text-slate-700">{product.name}</span>
                                        </td>
                                        <td className="px-6 py-4 font-bold text-slate-600">
                                            {product.price.toFixed(2)} €
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className={`px-2 py-1 rounded text-[10px] font-bold ${product.stock < 10 ? 'bg-orange-100 text-orange-600' : 'bg-green-100 text-green-600'}`}>
                                                {product.stock} unités
                                            </span>
                                        </td>
                                        <td className="px-6 py-4">
                                            <span className="px-2 py-1 rounded text-[10px] font-bold 'bg-green-100 text-red-600">
                                                {product.prcTVA} %
                                            </span>
                                        </td>
                                        <td className="px-6 py-4 text-right">
                                            <EditProductModal 
                                                product={product} 
                                                onProductUpdated={() => fetchProducts(currentPage)} 
                                            />
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {pagination && (
                    <div className="px-6 py-4 bg-slate-50 border-t flex items-center justify-between">
                        <div className="flex items-center gap-2">
                            <Button 
                                variant="outline" 
                                size="sm" 
                                onClick={() => setCurrentPage(prev => prev - 1)} 
                                disabled={pagination.isFirst || loading}
                            >
                                <ChevronLeft size={16} />
                            </Button>
                            <span className="text-xs font-bold">
                                {pagination.page + 1} / {pagination.totalPages}
                            </span>
                            <Button 
                                variant="outline" 
                                size="sm" 
                                onClick={() => setCurrentPage(prev => prev + 1)} 
                                disabled={pagination.isLast || loading}
                            >
                                <ChevronRight size={16} />
                            </Button>
                        </div>
                        <span className="text-xs font-medium text-slate-400">Total: {pagination.totalElements} produits</span>
                    </div>
                )}
            </div>
        </div>
    );
};

export default AdminProduct;