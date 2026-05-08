import { useEffect, useState } from 'react';
import { getVentas } from '../api/client';
import { ShoppingCart, Search, Calendar, Store, Hash, DollarSign, Package } from 'lucide-react';
import { motion } from 'framer-motion';
import DetailModal from '../components/DetailModal';

const container = {
  hidden: { opacity: 0 },
  show: { opacity: 1, transition: { staggerChildren: 0.04 } },
};

const itemAnim = {
  hidden: { opacity: 0, y: 10 },
  show: { opacity: 1, y: 0 },
};

export default function Ventas() {
  const [ventas, setVentas] = useState([]);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState('');
  const [selected, setSelected] = useState(null);

  useEffect(() => {
    getVentas().then(setVentas).catch(e => setError(e.message));
  }, []);

  const filtered = ventas.filter(v =>
    String(v.id).includes(search) ||
    v.sucursal?.nombre?.toLowerCase().includes(search.toLowerCase())
  );

  if (error) return (
    <div className="flex items-center gap-2 text-red-600 bg-red-50 p-4 rounded-xl">
      <ShoppingCart className="w-5 h-5" /><span>{error}</span>
    </div>
  );

  return (
    <motion.div variants={container} initial="hidden" animate="show">
      <motion.div variants={itemAnim} className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div className="flex items-center gap-3">
          <div className="p-2.5 bg-blue-100 rounded-xl"><ShoppingCart className="w-6 h-6 text-blue-600" /></div>
          <div>
            <h2 className="text-2xl font-bold text-slate-800">Ventas <span className="text-slate-400 text-lg font-normal">({ventas.length})</span></h2>
            <p className="text-sm text-slate-400">Historial de transacciones — haz clic para ver detalles</p>
          </div>
        </div>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <input type="text" placeholder="Buscar venta..." value={search} onChange={e => setSearch(e.target.value)}
            className="pl-9 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 w-full sm:w-64 transition-all" />
        </div>
      </motion.div>

      <motion.div variants={itemAnim} className="glass-card rounded-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-slate-50/80 text-slate-600 uppercase text-xs">
                <th className="text-left p-4">ID</th>
                <th className="text-left p-4">Sucursal</th>
                <th className="text-left p-4">Fecha</th>
                <th className="text-right p-4">Precio Total</th>
                <th className="text-right p-4">Monto Total</th>
                <th className="text-center p-4">Items</th>
              </tr>
            </thead>
            <tbody>
              {filtered.length === 0 ? (
                <tr><td colSpan="6" className="text-center p-8 text-slate-400">Sin resultados</td></tr>
              ) : filtered.map(v => (
                <motion.tr
                  key={v.id}
                  variants={itemAnim}
                  onClick={() => setSelected(v)}
                  className="border-t border-slate-100 hover:bg-slate-50/70 transition-colors cursor-pointer"
                >
                  <td className="p-4 font-medium">{v.id}</td>
                  <td className="p-4">{v.sucursal?.nombre || '-'}</td>
                  <td className="p-4 text-slate-500">{new Date(v.fechaVenta).toLocaleDateString('es-CL')}</td>
                  <td className="p-4 text-right font-medium">${Number(v.precioTotal).toLocaleString('es-CL')}</td>
                  <td className="p-4 text-right font-medium text-emerald-600">${Number(v.montoTotal).toLocaleString('es-CL')}</td>
                  <td className="p-4 text-center">
                    <span className="px-2.5 py-1 bg-blue-50 text-blue-600 rounded-full text-xs font-medium">
                      {v.detalles?.length || 0}
                    </span>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </motion.div>

      {/* Detail Modal */}
      <DetailModal open={!!selected} onClose={() => setSelected(null)} title={`Venta #${selected?.id}`} size="max-w-lg">
        {selected && (
          <div className="space-y-5">
            <div className="grid grid-cols-2 gap-3">
              <div className="p-3 bg-slate-50 rounded-xl">
                <div className="flex items-center gap-2 text-slate-400 text-xs mb-1">
                  <Hash className="w-3.5 h-3.5" /> ID Venta
                </div>
                <p className="font-bold text-slate-800">{selected.id}</p>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl">
                <div className="flex items-center gap-2 text-slate-400 text-xs mb-1">
                  <Store className="w-3.5 h-3.5" /> Sucursal
                </div>
                <p className="font-bold text-slate-800">{selected.sucursal?.nombre || '-'}</p>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl">
                <div className="flex items-center gap-2 text-slate-400 text-xs mb-1">
                  <Calendar className="w-3.5 h-3.5" /> Fecha
                </div>
                <p className="font-bold text-slate-800">{new Date(selected.fechaVenta).toLocaleDateString('es-CL')}</p>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl">
                <div className="flex items-center gap-2 text-slate-400 text-xs mb-1">
                  <DollarSign className="w-3.5 h-3.5" /> Monto Total
                </div>
                <p className="font-bold text-emerald-600">${Number(selected.montoTotal).toLocaleString('es-CL')}</p>
              </div>
            </div>

            {selected.detalles?.length > 0 && (
              <div>
                <h4 className="font-semibold text-slate-800 mb-3 flex items-center gap-2">
                  <Package className="w-4 h-4" /> Detalles ({selected.detalles.length} items)
                </h4>
                <div className="space-y-2">
                  {selected.detalles.map((d, i) => (
                    <div key={i} className="flex items-center justify-between p-3 bg-slate-50 rounded-xl">
                      <div>
                        <p className="text-sm font-medium text-slate-700">{d.producto?.nombre || `Producto #${d.productoId}`}</p>
                        <p className="text-xs text-slate-400">Cant: {d.cantidad} × ${Number(d.precioUnitario).toLocaleString('es-CL')}</p>
                      </div>
                      <p className="font-bold text-slate-800">${Number(d.subtotal).toLocaleString('es-CL')}</p>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </DetailModal>
    </motion.div>
  );
}
