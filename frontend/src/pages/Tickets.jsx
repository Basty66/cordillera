import { useEffect, useState } from 'react';
import { getTickets, createTicket, updateTicketStatus, deleteTicket } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { motion, AnimatePresence } from 'framer-motion';
import {
  TicketCheck, Plus, X, AlertCircle, CheckCircle, Clock, Loader, Trash2, MessageSquare, ChevronDown, Bug
} from 'lucide-react';

const container = {
  hidden: { opacity: 0 },
  show: { opacity: 1, transition: { staggerChildren: 0.05 } },
};

const itemAnim = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0 },
};

const statusConfig = {
  ABIERTO: { label: 'Abierto', color: 'bg-blue-100 text-blue-700 border-blue-200', icon: AlertCircle },
  EN_PROGRESO: { label: 'En Progreso', color: 'bg-amber-100 text-amber-700 border-amber-200', icon: Loader },
  RESUELTO: { label: 'Resuelto', color: 'bg-emerald-100 text-emerald-700 border-emerald-200', icon: CheckCircle },
  CERRADO: { label: 'Cerrado', color: 'bg-slate-100 text-slate-600 border-slate-200', icon: X },
};

const prioridadConfig = {
  CRITICA: { label: 'Crítica', color: 'bg-red-100 text-red-700', border: 'border-l-red-500', glow: 'shadow-red-500/10' },
  ALTA: { label: 'Alta', color: 'bg-orange-100 text-orange-700', border: 'border-l-orange-500', glow: 'shadow-orange-500/10' },
  MEDIA: { label: 'Media', color: 'bg-blue-100 text-blue-700', border: 'border-l-blue-500', glow: 'shadow-blue-500/10' },
  BAJA: { label: 'Baja', color: 'bg-slate-100 text-slate-600', border: 'border-l-slate-400', glow: 'shadow-slate-500/10' },
};

export default function Tickets() {
  const { user } = useAuth();
  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [filter, setFilter] = useState('TODOS');
  const [form, setForm] = useState({ titulo: '', descripcion: '', prioridad: 'MEDIA' });
  const [formErrors, setFormErrors] = useState({});
  const [toast, setToast] = useState(null);

  const validateForm = () => {
    const errs = {};
    if (!form.titulo.trim()) errs.titulo = 'El título es requerido';
    else if (form.titulo.trim().length < 5) errs.titulo = 'Mínimo 5 caracteres';
    else if (form.titulo.trim().length > 200) errs.titulo = 'Máximo 200 caracteres';
    if (form.descripcion && form.descripcion.length > 2000) errs.descripcion = 'Máximo 2000 caracteres';
    setFormErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3000);
  };

  const load = async () => {
    try {
      setLoading(true);
      const data = await getTickets();
      setTickets(data);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    try {
      await createTicket({ ...form, titulo: form.titulo.trim(), descripcion: form.descripcion.trim(), creadoPor: user?.username });
      showToast('Ticket creado exitosamente');
      setShowModal(false);
      setForm({ titulo: '', descripcion: '', prioridad: 'MEDIA' });
      setFormErrors({});
      load();
    } catch (e) {
      showToast(e.response?.data?.error || 'Error al crear ticket', 'error');
    }
  };

  const handleStatus = async (id, status) => {
    try {
      await updateTicketStatus(id, status);
      showToast(`Ticket marcado como ${statusConfig[status]?.label}`);
      load();
    } catch (e) {
      showToast('Error al actualizar', 'error');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Eliminar este ticket?')) return;
    try {
      await deleteTicket(id);
      showToast('Ticket eliminado');
      load();
    } catch (e) {
      showToast('Error al eliminar', 'error');
    }
  };

  const filtered = filter === 'TODOS' ? tickets : tickets.filter(t => t.status === filter);

  const stats = {
    total: tickets.length,
    abiertos: tickets.filter(t => t.status === 'ABIERTO').length,
    enProgreso: tickets.filter(t => t.status === 'EN_PROGRESO').length,
    resueltos: tickets.filter(t => t.status === 'RESUELTO').length,
  };

  return (
    <motion.div variants={container} initial="hidden" animate="show">
      {/* Toast */}
      <AnimatePresence>
        {toast && (
          <motion.div
            initial={{ opacity: 0, y: -50, x: '-50%' }}
            animate={{ opacity: 1, y: 0, x: '-50%' }}
            exit={{ opacity: 0, y: -50, x: '-50%' }}
            className={`fixed top-4 left-1/2 z-50 px-5 py-3 rounded-xl shadow-2xl text-sm font-medium ${
              toast.type === 'error' ? 'bg-red-600 text-white' : 'bg-emerald-600 text-white'
            }`}
          >
            {toast.msg}
          </motion.div>
        )}
      </AnimatePresence>

      {/* Header */}
      <motion.div variants={itemAnim} className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div className="flex items-center gap-3">
          <div className="p-2.5 bg-gradient-to-br from-violet-500 to-violet-600 rounded-xl shadow-lg shadow-violet-500/20">
            <TicketCheck className="w-6 h-6 text-white" />
          </div>
          <div>
            <h2 className="text-2xl font-bold text-slate-800">Tickets</h2>
            <p className="text-sm text-slate-400">Sistema de seguimiento de incidencias</p>
          </div>
        </div>
        <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }}
          onClick={() => setShowModal(true)}
          className="flex items-center gap-2 bg-gradient-to-r from-violet-600 to-violet-500 hover:from-violet-500 hover:to-violet-400 text-white px-4 py-2.5 rounded-xl text-sm font-medium transition-all shadow-lg shadow-violet-600/20">
          <Plus className="w-4 h-4" /> Nuevo Ticket
        </motion.button>
      </motion.div>

      {/* Stats */}
      <motion.div variants={itemAnim} className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-6">
        {[
          { label: 'Total', value: stats.total, color: 'from-slate-500 to-slate-600', icon: TicketCheck },
          { label: 'Abiertos', value: stats.abiertos, color: 'from-blue-500 to-blue-600', icon: AlertCircle },
          { label: 'En Progreso', value: stats.enProgreso, color: 'from-amber-500 to-amber-600', icon: Loader },
          { label: 'Resueltos', value: stats.resueltos, color: 'from-emerald-500 to-emerald-600', icon: CheckCircle },
        ].map(s => (
          <motion.div key={s.label} whileHover={{ y: -3 }} className="glass-card-neon rounded-xl p-4 text-center">
            <p className="text-2xl font-bold text-slate-800">{s.value}</p>
            <p className="text-xs text-slate-500 mt-1">{s.label}</p>
          </motion.div>
        ))}
      </motion.div>

      {/* Filter */}
      <motion.div variants={itemAnim} className="flex gap-2 mb-4 overflow-x-auto pb-2">
        {['TODOS', 'ABIERTO', 'EN_PROGRESO', 'RESUELTO', 'CERRADO'].map(f => (
          <motion.button key={f} whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }}
            onClick={() => setFilter(f)}
            className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all whitespace-nowrap ${
              filter === f
                ? 'bg-violet-600 text-white shadow-md'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            {f === 'TODOS' ? 'Todos' : statusConfig[f]?.label || f}
          </motion.button>
        ))}
      </motion.div>

      {/* List */}
      {loading ? (
        <div className="flex justify-center py-20">
          <motion.div animate={{ rotate: 360 }} transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
            className="w-10 h-10 border-4 border-violet-600 border-t-transparent rounded-full" />
        </div>
      ) : error ? (
        <div className="flex items-center gap-2 text-red-600 justify-center py-20">
          <AlertCircle className="w-5 h-5" /><span>{error}</span>
        </div>
      ) : filtered.length === 0 ? (
        <div className="text-center py-20 text-slate-400">
          <TicketCheck className="w-12 h-12 mx-auto mb-3 opacity-30" />
          <p className="font-medium">No hay tickets {filter !== 'TODOS' && statusConfig[filter]?.label.toLowerCase()}</p>
        </div>
      ) : (
        <motion.div variants={container} className="space-y-3">
          {filtered.map(t => {
            const pConf = prioridadConfig[t.prioridad] || prioridadConfig.MEDIA;
            const sConf = statusConfig[t.status] || statusConfig.ABIERTO;
            const SIcon = sConf.icon;
            return (
              <motion.div
                key={t.id}
                variants={itemAnim}
                layout
                className={`glass-card-neon rounded-xl p-4 sm:p-5 border-l-4 ${pConf.border} hover:shadow-lg transition-all`}
              >
                <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <h3 className="font-semibold text-slate-800 truncate">{t.titulo}</h3>
                      <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${pConf.color} shrink-0 border`}>
                        {pConf.label}
                      </span>
                    </div>
                    {t.descripcion && <p className="text-sm text-slate-500 line-clamp-2 mt-1">{t.descripcion}</p>}
                    <div className="flex flex-wrap items-center gap-3 mt-2 text-xs text-slate-400">
                      <span className="flex items-center gap-1"><MessageSquare className="w-3 h-3" />{t.creadoPor}</span>
                      {t.asignadoA && <span>Asignado: {t.asignadoA}</span>}
                      <span>{new Date(t.createdAt).toLocaleDateString('es-CL')}</span>
                    </div>
                  </div>

                  <div className="flex items-center gap-2 shrink-0">
                    <span className={`flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium ${sConf.color}`}>
                      <SIcon className="w-3 h-3" />{sConf.label}
                    </span>
                    <div className="relative group">
                      <button className="p-2 hover:bg-slate-100 rounded-lg transition-colors">
                        <ChevronDown className="w-4 h-4 text-slate-400" />
                      </button>
                      <motion.div
                        initial={{ opacity: 0, scale: 0.95, y: 5 }}
                        whileHover={{ opacity: 1, scale: 1, y: 0 }}
                        className="absolute right-0 top-full mt-1 bg-white rounded-xl shadow-xl border border-slate-200 py-1 min-w-[140px] hidden group-hover:block z-10"
                      >
                        {Object.entries(statusConfig).map(([key, val]) => (
                          <button key={key} onClick={() => handleStatus(t.id, key)}
                            className={`flex items-center gap-2 w-full px-3 py-2 text-xs hover:bg-slate-50 transition-colors ${t.status === key ? 'font-semibold text-slate-800' : 'text-slate-600'}`}>
                            <val.icon className="w-3.5 h-3.5" />{val.label}
                          </button>
                        ))}
                        <div className="border-t border-slate-100 mt-1 pt-1">
                          <button onClick={() => handleDelete(t.id)}
                            className="flex items-center gap-2 w-full px-3 py-2 text-xs text-red-600 hover:bg-red-50 transition-colors">
                            <Trash2 className="w-3.5 h-3.5" /> Eliminar
                          </button>
                        </div>
                      </motion.div>
                    </div>
                  </div>
                </div>
              </motion.div>
            );
          })}
        </motion.div>
      )}

      {/* Modal */}
      <AnimatePresence>
        {showModal && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/40 backdrop-blur-md z-50 flex items-center justify-center p-4"
            onClick={() => setShowModal(false)}>
            <motion.div initial={{ opacity: 0, scale: 0.9, y: 20 }} animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: 20 }} className="glass-card-neon rounded-2xl shadow-2xl p-6 w-full max-w-lg"
              onClick={e => e.stopPropagation()}>
              <div className="flex items-center justify-between mb-5">
                <h3 className="text-lg font-bold text-slate-800">Crear Nuevo Ticket</h3>
                <motion.button whileHover={{ rotate: 90 }} onClick={() => setShowModal(false)}
                  className="p-1 hover:bg-slate-100 rounded-lg transition-colors">
                  <X className="w-5 h-5 text-slate-400" />
                </motion.button>
              </div>
              <form onSubmit={handleCreate} className="space-y-4">
                <div>
                  <label className="block text-xs font-medium text-slate-600 mb-1">Título *</label>
                  <input type="text" value={form.titulo} onChange={e => { setForm({ ...form, titulo: e.target.value }); setFormErrors({ ...formErrors, titulo: '' }); }}
                    className={`w-full px-3 py-2.5 border rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all input-neon ${formErrors.titulo ? 'border-red-400' : 'border-slate-200'}`}
                    placeholder="Describe el problema..." />
                  {formErrors.titulo && <p className="text-xs text-red-500 mt-1">{formErrors.titulo}</p>}
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-600 mb-1">Descripción</label>
                  <textarea value={form.descripcion} onChange={e => { setForm({ ...form, descripcion: e.target.value }); setFormErrors({ ...formErrors, descripcion: '' }); }}
                    className={`w-full px-3 py-2.5 border rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent resize-none transition-all input-neon ${formErrors.descripcion ? 'border-red-400' : 'border-slate-200'}`}
                    rows={3} placeholder="Detalles del ticket..." />
                  {formErrors.descripcion && <p className="text-xs text-red-500 mt-1">{formErrors.descripcion}</p>}
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-600 mb-1">Prioridad</label>
                  <select value={form.prioridad} onChange={e => setForm({ ...form, prioridad: e.target.value })}
                    className="w-full px-3 py-2.5 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-violet-500">
                    <option value="BAJA">Baja</option>
                    <option value="MEDIA">Media</option>
                    <option value="ALTA">Alta</option>
                    <option value="CRITICA">Crítica</option>
                  </select>
                </div>
                <div className="flex gap-3 pt-2">
                  <button type="button" onClick={() => setShowModal(false)}
                    className="flex-1 px-4 py-2.5 border border-slate-200 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-50 transition-colors">
                    Cancelar
                  </button>
                  <motion.button type="submit" whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}
                    className="flex-1 bg-gradient-to-r from-violet-600 to-violet-500 hover:from-violet-500 hover:to-violet-400 text-white py-2.5 rounded-xl text-sm font-medium transition-all shadow-lg shadow-violet-600/20">
                    Crear Ticket
                  </motion.button>
                </div>
              </form>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
}
