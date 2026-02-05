import { useEffect, useState } from "react";
import { adminService } from "../../services/adminService";
import { toast } from "sonner";
import { 
    Loader2, 
    ChevronLeft, 
    ChevronRight,
    MoreHorizontal,
    Mail,
    Calendar,
    UserCog
} from "lucide-react";
import type { Client } from "../../type/ClientType";
import type { PaginationDTO } from "../../type/ApiResponse";
import { AddClientModal } from "@/components/client/AddClientModal";
import { LoyaltyBadge } from "@/components/client/LoyaltyBadge";
import { EditClientModal } from "@/components/client/EditClientModal";

const AdminClients = () => {
    const [clients, setClients] = useState<Client[]>([]);
    const [loading, setLoading] = useState(true);
    const [pagination, setPagination] = useState<PaginationDTO | null>(null);
    const [currentPage, setCurrentPage] = useState(0);

    const [selectedClient, setSelectedClient] = useState<Client | null>(null);
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);

    const fetchClients = async (page: number) => {
        setLoading(true);
        try {
            const response = await adminService.getClients(page, 5);
            setClients(response.data);
            setPagination(response.pagination);
        } catch (error) {
            const message = error instanceof Error ? error.message : "Impossible de récupérer les clients";

            toast.error("Erreur de chargement", {
                description: message
            });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchClients(currentPage);
    }, [currentPage]);

    const handleEditClick = (client: Client) => {
        setSelectedClient(client);
        setIsEditModalOpen(true);
    };

    return (
        <div className="space-y-6">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                    <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Gestion des Clients</h1>
                    <p className="text-slate-500 text-sm">Consultez et gérez les comptes de vos clients.</p>
                </div>
                <AddClientModal onClientAdded={() => fetchClients(currentPage)} />
            </div>

            <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
                {loading ? (
                    <div className="flex flex-col items-center justify-center h-80 gap-3">
                        <Loader2 className="animate-spin text-blue-600" size={40} />
                        <p className="text-slate-400 text-sm animate-pulse">Chargement des données...</p>
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="bg-slate-50/50 border-b border-slate-100">
                                    <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Client</th>
                                    <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Contact</th>
                                    <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider">Niveau</th>
                                    <th className="px-6 py-4 text-xs font-bold text-slate-500 uppercase tracking-wider text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-50">
                                {clients.map((client) => (
                                    <tr key={client.uuid} className="hover:bg-slate-50/50 transition-colors group">
                                        <td className="px-6 py-4">
                                            <div className="flex items-center gap-3">
                                                <div className="h-10 w-10 bg-blue-50 text-blue-600 rounded-full flex items-center justify-center font-bold">
                                                    {client.username.charAt(0).toUpperCase()}
                                                </div>
                                                <div>
                                                    <p className="text-sm font-bold text-slate-900">{client.name}</p>
                                                    <p className="text-xs text-slate-400">@{client.username}</p>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4">
                                            <div className="flex flex-col gap-1">
                                                <div className="flex items-center gap-2 text-slate-600">
                                                    <Mail size={14} className="text-slate-400" />
                                                    <span className="text-sm">{client.email}</span>
                                                </div>
                                                <div className="flex items-center gap-2 text-slate-400">
                                                    <Calendar size={14} />
                                                    <span className="text-[11px] uppercase tracking-tighter">
                                                        Inscrit le {new Date(client.createdAt).toLocaleDateString('fr-FR')}
                                                    </span>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4">
                                            <LoyaltyBadge tier={client.loyaltyLevel} />
                                        </td>
                                        <td className="px-6 py-4 text-right">
                                            <button 
                                                onClick={() => handleEditClick(client)}
                                                className="p-2 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-all"
                                                title="Modifier le client"
                                            >
                                                <UserCog size={18} />
                                            </button>
                                            <button className="p-2 text-slate-400 hover:text-slate-600 hover:bg-slate-100 rounded-lg transition-all">
                                                <MoreHorizontal size={20} />
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {pagination && (
                    <div className="px-6 py-4 bg-slate-50/50 border-t border-slate-100 flex items-center justify-between">
                        <p className="text-xs text-slate-500">
                            Affichage de <span className="font-bold">{clients.length}</span> sur <span className="font-bold">{pagination.totalElements}</span> clients
                        </p>
                        <div className="flex items-center gap-2">
                            <button
                                onClick={() => setCurrentPage(prev => prev - 1)}
                                disabled={pagination.isFirst || loading}
                                className="p-2 rounded-lg border bg-white text-slate-600 hover:bg-slate-50 disabled:opacity-40 transition-all shadow-sm"
                            >
                                <ChevronLeft size={18} />
                            </button>
                            <div className="px-4 py-1.5 rounded-lg bg-white border text-sm font-bold text-slate-700 shadow-sm">
                                {pagination.page + 1} / {pagination.totalPages}
                            </div>
                            <button
                                onClick={() => setCurrentPage(prev => prev + 1)}
                                disabled={pagination.isLast || loading}
                                className="p-2 rounded-lg border bg-white text-slate-600 hover:bg-slate-50 disabled:opacity-40 transition-all shadow-sm"
                            >
                                <ChevronRight size={18} />
                            </button>
                        </div>
                    </div>
                )}

                {selectedClient && (
                    <EditClientModal
                        client={selectedClient}
                        isOpen={isEditModalOpen}
                        onClose={() => {
                            setIsEditModalOpen(false);
                            setSelectedClient(null);
                        }}
                        onClientUpdated={() => fetchClients(currentPage)}
                    />
                )}
            </div>
        </div>
    );
};

export default AdminClients;