import { useEffect, useState } from 'react';
import { getProductos } from '../api/client';
import { Package, Search, X, DollarSign, Hash, Layers, AlertCircle } from 'lucide-react';
import { motion } from 'framer-motion';
import DetailModal from '../components/DetailModal';

const container = {
  hidden: { opacity: 0 },
  show: { opacity: 1, transition: { staggerChildren: 0.06 } },
};

const itemAnim = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0 },
};

function getProductImage(producto) {
  if (producto.imagenUrl) return producto.imagenUrl;
  const cat = (producto.nombre || '').split(' ')[0].toLowerCase();
  return `https://picsum.photos/seed/${cat}${producto.id}/400/300`;
}

export default function Productos() {
  const [productos, setProductos] = useState([]);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState('');
  const [selected, setSelected] = useState(null);

  useEffect(() => {
    getProductos().then(setProductos).catch(e => setError(e.message));
  }, []);

  const filtered = productos.filter(p =>
    p.nombre?.toLowerCase().includes(search.toLowerCase()) ||
    p.descripcion?.toLowerCase().includes(search.toLowerCase())
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
            <Package className="w-6 h-6 text-white" />
          </div>
          <div>
            <h2 className="text-2xl font-bold text-slate-800">Productos <span className="text-slate-400 text-lg font-normal">({productos.length})</span></h2>
            <p className="text-sm text-slate-400">Catálogo de productos — haz clic para ver detalles</p>
          </div>
        </div>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <input type="text" placeholder="Buscar productos..." value={search} onChange={e => setSearch(e.target.value)}
            className="pl-9 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent w-full sm:w-64 transition-all input-neon" />
        </div>
      </motion.div>

      <motion.div variants={container} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
        {filtered.map(p => (
          <motion.div
            key={p.id}
            variants={itemAnim}
            whileHover={{ y: -8, transition: { type: 'spring', stiffness: 200 } }}
            onClick={() => setSelected(p)}
            className="glass-card-neon rounded-xl overflow-hidden hover:shadow-xl transition-all duration-300 group cursor-pointer"
          >
            <div className="relative h-48 bg-slate-100 overflow-hidden">
              <img src={getProductImage(p)} alt={p.nombre}
                className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" loading="lazy" />
              <div className="absolute inset-0 bg-gradient-to-t from-black/40 via-transparent to-transparent" />
              <div className="absolute bottom-3 right-3">
                <span className={`px-2.5 py-1 rounded-full text-xs font-medium shadow-lg backdrop-blur-sm ${
                  p.stock > 0 ? 'bg-emerald-500/90 text-white neon-glow-sm' : 'bg-red-500/90 text-white'
                }`}>
                  {p.stock > 0 ? `${p.stock} en stock` : 'Agotado'}
                </span>
              </div>
            </div>
            <div className="p-4">
              <h3 className="font-semibold text-slate-800 group-hover:text-emerald-600 transition-colors">{p.nombre}</h3>
              {p.descripcion && <p className="text-xs text-slate-400 mt-1 line-clamp-2">{p.descripcion}</p>}
              <div className="flex items-center justify-between mt-3 pt-3 border-t border-slate-100">
                <motion.p
                  initial={{ scale: 1 }}
                  whileHover={{ scale: 1.05 }}
                  className="text-lg font-bold text-emerald-600"
                >${Number(p.precio).toLocaleString('es-CL')}</motion.p>
                <span className="text-xs text-slate-400">ID: {p.id}</span>
              </div>
            </div>
          </motion.div>
        ))}
      </motion.div>

      {filtered.length === 0 && (
        <motion.div variants={itemAnim} className="text-center py-16 text-slate-400">
          <Package className="w-12 h-12 mx-auto mb-3 opacity-30" />
          <p className="text-lg font-medium">No se encontraron productos</p>
        </motion.div>
      )}

      <DetailModal open={!!selected} onClose={() => setSelected(null)} title={selected?.nombre} size="max-w-2xl">
        {selected && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="rounded-xl overflow-hidden bg-slate-100 shadow-inner">
              <img src={getProductImage(selected)} alt={selected.nombre}
                className="w-full h-64 object-cover" />
            </div>
            <div className="space-y-4">
              <div>
                <h4 className="text-xl font-bold text-slate-800">{selected.nombre}</h4>
                {selected.descripcion && <p className="text-sm text-slate-500 mt-2">{selected.descripcion}</p>}
              </div>
              <div className="space-y-2">
                {[
                  { icon: DollarSign, label: 'Precio', value: `$${Number(selected.precio).toLocaleString('es-CL')}`, color: 'text-emerald-600', bg: 'bg-emerald-50' },
                  { icon: Layers, label: 'Stock', value: `${selected.stock} unidades`, color: 'text-blue-600', bg: 'bg-blue-50' },
                  { icon: Hash, label: 'ID Producto', value: selected.id, color: 'text-violet-600', bg: 'bg-violet-50' },
                ].map((item, i) => (
                  <div key={i} className={`flex items-center gap-3 p-3 ${item.bg} rounded-xl border border-white/50`}>
                    <item.icon className={`w-5 h-5 ${item.color}`} />
                    <div>
                      <p className="text-xs text-slate-400">{item.label}</p>
                      <p className={`font-bold text-slate-800 text-lg ${item.color}`}>{item.value}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </DetailModal>
    </motion.div>
  );
}
