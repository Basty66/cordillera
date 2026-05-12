import { useEffect, useState } from 'react';
import { getVentas } from '../api/client';
import { ShoppingCart, Search, Calendar, Store, Hash, DollarSign, Package, AlertCircle } from 'lucide-react';
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
    <motion.div initial={{ opacity: 0 }} className="flex items-center gap-2 text-red-600 bg-red-50 p-4 rounded-xl border border-red-200">
      <AlertCircle className="w-5 h-5" /><span className="font-medium">{error}</span>
    </motion.div>
  );

  return (
    <motion.div variants={container} initial="hidden" animate="show">
      <motion.div variants={itemAnim} className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div className="flex items-center gap-3">
          <div className="p-2.5 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-lg shadow-blue-500/20">
            <ShoppingCart className="w-6 h-6 text-white" />
          </div>
          <div>
            <h2 className="text-2xl font-bold text-slate-800">Ventas <span className="text-slate-400 text-lg font-normal">({ventas.length})</span></h2>
            <p className="text-sm text-slate-400">Historial de transacciones — haz clic para ver detalles</p>
          </div>
        </div>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <input type="text" placeholder="Buscar venta..." value={search} onChange={e => setSearch(e.target.value)}
            className="pl-9 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent w-full sm:w-64 transition-all input-neon" />
        </div>
      </motion.div>

      <motion.div variants={itemAnim} className="glass-card-neon rounded-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm table-neon">
            <thead>
              <tr className="text-slate-600 uppercase text-xs">
                <th className="text-left p-4 font-semibold">ID</th>
                <th className="text-left p-4 font-semibold">Sucursal</th>
                <th className="text-left p-4 font-semibold">Fecha</th>
                <th className="text-right p-4 font-semibold">Precio Total</th>
                <th className="text-right p-4 font-semibold">Monto Total</th>
                <th className="text-center p-4 font-semibold">Items</th>
              </tr>
            </thead>
            <tbody>
              {filtered.length === 0 ? (
                <tr><td colSpan="6" className="text-center p-12 text-slate-400">
                  <ShoppingCart className="w-8 h-8 mx-auto mb-2 opacity-30" />
                  <p>Sin resultados</p>
                </td></tr>
              ) : filtered.map((v, i) => (
                <motion.tr
                  key={v.id}
                  variants={itemAnim}
                  onClick={() => setSelected(v)}
                  className="cursor-pointer"
                >
                  <td className="p-4 font-medium">#{v.id}</td>
                  <td className="p-4">
                    <div className="flex items-center gap-2">
                      <Store className="w-3.5 h-3.5 text-slate-400" />
                      {v.sucursal?.nombre || '-'}
                    </div>
                  </td>
                  <td className="p-4 text-slate-500">
                    <div className="flex items-center gap-2">
                      <Calendar className="w-3.5 h-3.5 text-slate-400" />
                      {new Date(v.fechaVenta).toLocaleDateString('es-CL')}
                    </div>
                  </td>
                  <td className="p-4 text-right font-medium">${Number(v.precioTotal).toLocaleString('es-CL')}</td>
                  <td className="p-4 text-right font-medium text-emerald-600 font-bold">${Number(v.montoTotal).toLocaleString('es-CL')}</td>
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

      <DetailModal open={!!selected} onClose={() => setSelected(null)} title={`Venta #${selected?.id}`} size="max-w-lg">
        {selected && (
          <div className="space-y-5">
            <div className="grid grid-cols-2 gap-3">
              {[
                { icon: Hash, label: 'ID Venta', value: `#${selected.id}`, color: 'text-blue-600' },
                { icon: Store, label: 'Sucursal', value: selected.sucursal?.nombre || '-', color: 'text-rose-600' },
                { icon: Calendar, label: 'Fecha', value: new Date(selected.fechaVenta).toLocaleDateString('es-CL'), color: 'text-amber-600' },
                { icon: DollarSign, label: 'Monto Total', value: `$${Number(selected.montoTotal).toLocaleString('es-CL')}`, color: 'text-emerald-600', bold: true },
              ].map((item, i) => (
                <div key={i} className="p-3 bg-slate-50 rounded-xl border border-slate-100">
                  <div className="flex items-center gap-2 text-slate-400 text-xs mb-1">
                    <item.icon className="w-3.5 h-3.5" /> {item.label}
                  </div>
                  <p className={`font-bold text-slate-800 ${item.color || ''}`}>{item.value}</p>
                </div>
              ))}
            </div>

            {selected.detalles?.length > 0 && (
              <div>
                <h4 className="font-semibold text-slate-800 mb-3 flex items-center gap-2">
                  <Package className="w-4 h-4 text-emerald-600" /> Detalles ({selected.detalles.length} items)
                </h4>
                <div className="space-y-2">
                  {selected.detalles.map((d, i) => (
                    <motion.div
                      key={i}
                      initial={{ opacity: 0, x: -10 }}
                      animate={{ opacity: 1, x: 0 }}
                      transition={{ delay: i * 0.05 }}
                      className="flex items-center justify-between p-3 bg-slate-50 rounded-xl border border-slate-100"
                    >
                      <div>
                        <p className="text-sm font-medium text-slate-700">{d.producto?.nombre || `Producto #${d.productoId}`}</p>
                        <p className="text-xs text-slate-400">Cant: {d.cantidad} × ${Number(d.precioUnitario).toLocaleString('es-CL')}</p>
                      </div>
                      <p className="font-bold text-slate-800 text-sm">${Number(d.subtotal).toLocaleString('es-CL')}</p>
                    </motion.div>
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
