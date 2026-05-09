import { useEffect, useState } from 'react';
import { getProductos } from '../api/client';
import { Package, Search, X, DollarSign, Hash, FileText, Layers } from 'lucide-react';
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
    <div className="flex items-center gap-2 text-red-600 bg-red-50 p-4 rounded-xl">
      <X className="w-5 h-5" /><span>{error}</span>
    </div>
  );

  return (
    <motion.div variants={container} initial="hidden" animate="show">
      <motion.div variants={itemAnim} className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div className="flex items-center gap-3">
          <div className="p-2.5 bg-blue-100 rounded-xl"><Package className="w-6 h-6 text-blue-600" /></div>
          <div>
            <h2 className="text-2xl font-bold text-slate-800">Productos <span className="text-slate-400 text-lg font-normal">({productos.length})</span></h2>
            <p className="text-sm text-slate-400">Catálogo de productos — haz clic para ver detalles</p>
          </div>
        </div>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <input type="text" placeholder="Buscar productos..." value={search} onChange={e => setSearch(e.target.value)}
            className="pl-9 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 w-full sm:w-64 transition-all" />
        </div>
      </motion.div>

      <motion.div variants={container} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
        {filtered.map(p => (
          <motion.div
            key={p.id}
            variants={itemAnim}
            whileHover={{ y: -6, transition: { type: 'spring', stiffness: 200 } }}
            onClick={() => setSelected(p)}
            className="glass-card rounded-xl overflow-hidden hover:shadow-xl transition-all duration-300 group cursor-pointer"
          >
            <div className="relative h-48 bg-slate-100 overflow-hidden">
              <img src={getProductImage(p)} alt={p.nombre}
                className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" loading="lazy" />
              <div className="absolute inset-0 bg-gradient-to-t from-black/30 to-transparent" />
              <div className="absolute top-3 right-3">
                <span className={`px-2.5 py-1 rounded-full text-xs font-medium shadow-lg backdrop-blur-sm ${
                  p.stock > 0 ? 'bg-emerald-500/90 text-white' : 'bg-red-500/90 text-white'
                }`}>
                  {p.stock > 0 ? `${p.stock} en stock` : 'Agotado'}
                </span>
              </div>
            </div>
            <div className="p-4">
              <h3 className="font-semibold text-slate-800 group-hover:text-emerald-600 transition-colors">{p.nombre}</h3>
              {p.descripcion && <p className="text-xs text-slate-400 mt-1 line-clamp-2">{p.descripcion}</p>}
              <div className="flex items-center justify-between mt-3 pt-3 border-t border-slate-100">
                <p className="text-lg font-bold text-emerald-600">${Number(p.precio).toLocaleString('es-CL')}</p>
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

      {/* Detail Modal */}
      <DetailModal open={!!selected} onClose={() => setSelected(null)} title={selected?.nombre} size="max-w-2xl">
        {selected && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="rounded-xl overflow-hidden bg-slate-100">
              <img src={getProductImage(selected)} alt={selected.nombre}
                className="w-full h-64 object-cover" />
            </div>
            <div className="space-y-4">
              <div>
                <h4 className="text-xl font-bold text-slate-800">{selected.nombre}</h4>
                {selected.descripcion && (
                  <p className="text-sm text-slate-500 mt-2">{selected.descripcion}</p>
                )}
              </div>
              <div className="space-y-2">
                <div className="flex items-center gap-3 p-3 bg-slate-50 rounded-xl">
                  <DollarSign className="w-5 h-5 text-emerald-600" />
                  <div>
                    <p className="text-xs text-slate-400">Precio</p>
                    <p className="font-bold text-emerald-600 text-lg">${Number(selected.precio).toLocaleString('es-CL')}</p>
                  </div>
                </div>
                <div className="flex items-center gap-3 p-3 bg-slate-50 rounded-xl">
                  <Layers className="w-5 h-5 text-blue-600" />
                  <div>
                    <p className="text-xs text-slate-400">Stock</p>
                    <p className="font-bold text-slate-800">{selected.stock} unidades</p>
                  </div>
                </div>
                <div className="flex items-center gap-3 p-3 bg-slate-50 rounded-xl">
                  <Hash className="w-5 h-5 text-violet-600" />
                  <div>
                    <p className="text-xs text-slate-400">ID Producto</p>
                    <p className="font-bold text-slate-800">{selected.id}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </DetailModal>
    </motion.div>
  );
}
