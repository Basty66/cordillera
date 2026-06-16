import { useEffect, useState } from 'react';
import { getTickets, createTicket, updateTicketStatus, deleteTicket, getTicketAnalytics, clasificarTicket } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { motion, AnimatePresence } from 'framer-motion';
import {
  TicketCheck, Plus, X, AlertCircle, CheckCircle, Clock, Loader, Trash2, MessageSquare, ChevronDown,
  BarChart3, TrendingUp, Target, Zap, HelpCircle, DollarSign, Shield, RefreshCw, Brain,
} from 'lucide-react';

const container = { hidden: { opacity: 0 }, show: { opacity: 1, transition: { staggerChildren: 0.04 } } };
const itemAnim = { hidden: { opacity: 0, y: 15 }, show: { opacity: 1, y: 0 } };

const statusConfig = {
  ABIERTO: { label: 'Abierto', color: 'bg-blue-100 dark:bg-blue-500/20 text-blue-700 dark:text-blue-400 border-blue-200 dark:border-blue-500/30', icon: AlertCircle },
  EN_PROGRESO: { label: 'En Progreso', color: 'bg-amber-100 dark:bg-amber-500/20 text-amber-700 dark:text-amber-400 border-amber-200 dark:border-amber-500/30', icon: Loader },
  RESUELTO: { label: 'Resuelto', color: 'bg-emerald-100 dark:bg-emerald-500/20 text-emerald-700 dark:text-emerald-400 border-emerald-200 dark:border-emerald-500/30', icon: CheckCircle },
  CERRADO: { label: 'Cerrado', color: 'bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-400 border-slate-200 dark:border-slate-600', icon: X },
};

const prioridadConfig = {
  CRITICA: { label: 'Crítica', color: 'bg-red-100 dark:bg-red-500/20 text-red-700 dark:text-red-400', border: 'border-l-red-500' },
  ALTA: { label: 'Alta', color: 'bg-orange-100 dark:bg-orange-500/20 text-orange-700 dark:text-orange-400', border: 'border-l-orange-500' },
  MEDIA: { label: 'Media', color: 'bg-blue-100 dark:bg-blue-500/20 text-blue-700 dark:text-blue-400', border: 'border-l-blue-500' },
  BAJA: { label: 'Baja', color: 'bg-slate-100 dark:bg-slate-700 text-slate-600 dark:text-slate-400', border: 'border-l-slate-400' },
};

const categoryConfig = {
  TÉCNICO: { label: 'Técnico', color: 'bg-blue-100 dark:bg-blue-500/20 text-blue-700 dark:text-blue-400 border-blue-200', icon: Zap },
  FACTURACIÓN: { label: 'Facturación', color: 'bg-emerald-100 dark:bg-emerald-500/20 text-emerald-700 dark:text-emerald-400 border-emerald-200', icon: DollarSign },
  RECLAMO: { label: 'Reclamo', color: 'bg-red-100 dark:bg-red-500/20 text-red-700 dark:text-red-400 border-red-200', icon: AlertCircle },
  CONSULTA: { label: 'Consulta', color: 'bg-violet-100 dark:bg-violet-500/20 text-violet-700 dark:text-violet-400 border-violet-200', icon: HelpCircle },
};

export default function Tickets() {
  const { user } = useAuth();
  const [tab, setTab] = useState('listar');
  const [tickets, setTickets] = useState([]);
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [filter, setFilter] = useState('TODOS');
  const [form, setForm] = useState({ titulo: '', descripcion: '', prioridad: 'MEDIA' });
  const [formErrors, setFormErrors] = useState({});
  const [categoriaSugerida, setCategoriaSugerida] = useState(null);
  const [clasificando, setClasificando] = useState(false);
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
      const [data, an] = await Promise.all([getTickets(), getTicketAnalytics()]);
      setTickets(data);
      setAnalytics(an);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleAutoClasificar = async () => {
    if (!form.titulo.trim()) return;
    setClasificando(true);
    try {
      const res = await clasificarTicket(form.titulo, form.descripcion);
      setCategoriaSugerida(res.categoria);
    } catch {
      setCategoriaSugerida('CONSULTA');
    } finally {
      setClasificando(false);
    }
  };

  useEffect(() => {
    if (showModal) {
      setCategoriaSugerida(null);
      setForm({ titulo: '', descripcion: '', prioridad: 'MEDIA' });
      setFormErrors({});
    }
  }, [showModal]);

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    try {
      await createTicket({ ...form, titulo: form.titulo.trim(), descripcion: form.descripcion.trim(), creadoPor: user?.username });
      showToast('Ticket creado exitosamente');
      setShowModal(false);
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
    } catch {
      showToast('Error al actualizar', 'error');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Eliminar este ticket?')) return;
    try {
      await deleteTicket(id);
      showToast('Ticket eliminado');
      load();
    } catch {
      showToast('Error al eliminar', 'error');
    }
  };

  const filtered = filter === 'TODOS' ? tickets : tickets.filter(t => t.status === filter);
  const stats = {
    total: tickets.length, abiertos: tickets.filter(t => t.status === 'ABIERTO').length,
    enProgreso: tickets.filter(t => t.status === 'EN_PROGRESO').length, resueltos: tickets.filter(t => t.status === 'RESUELTO').length,
  };

  return (
    <motion.div variants={container} initial="hidden" animate="show">
      {/* Toast */}
      <AnimatePresence>
        {toast && (
          <motion.div initial={{ opacity: 0, y: -50, x: '-50%' }} animate={{ opacity: 1, y: 0, x: '-50%' }} exit={{ opacity: 0, y: -50, x: '-50%' }}
            className={`fixed top-4 left-1/2 z-50 px-5 py-3 rounded-xl shadow-2xl text-sm font-medium ${toast.type === 'error' ? 'bg-red-600 text-white' : 'bg-emerald-600 text-white'}`}>
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
            <h2 className="text-2xl font-bold text-slate-800 dark:text-white">Tickets</h2>
            <p className="text-sm text-slate-400">Sistema de seguimiento con clasificación inteligente</p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }}
            onClick={load} className="flex items-center gap-2 px-3 py-2.5 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-xs font-medium text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700 transition-all">
            <RefreshCw className="w-3.5 h-3.5" /> Actualizar
          </motion.button>
          <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }}
            onClick={() => setShowModal(true)}
            className="flex items-center gap-2 bg-gradient-to-r from-violet-600 to-violet-500 hover:from-violet-500 hover:to-violet-400 text-white px-4 py-2.5 rounded-xl text-sm font-medium transition-all shadow-lg shadow-violet-600/20">
            <Plus className="w-4 h-4" /> Nuevo Ticket
          </motion.button>
        </div>
      </motion.div>

      {/* Tabs */}
      <motion.div variants={itemAnim} className="flex gap-1 mb-6 p-1 bg-slate-100 dark:bg-slate-800/50 rounded-xl w-fit">
        {[
          { id: 'listar', label: 'Listado', icon: TicketCheck },
          { id: 'analytics', label: 'Analytics', icon: BarChart3 },
        ].map(t => (
          <motion.button key={t.id} whileTap={{ scale: 0.97 }} onClick={() => setTab(t.id)}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg text-xs font-medium transition-all ${tab === t.id ? 'bg-white dark:bg-slate-700 text-violet-700 dark:text-violet-300 shadow-sm' : 'text-slate-500 hover:text-slate-700 dark:hover:text-slate-300'}`}>
            <t.icon className="w-3.5 h-3.5" />{t.label}
          </motion.button>
        ))}
      </motion.div>

      {tab === 'analytics' ? (
        /* === ANALYTICS TAB === */
        <motion.div variants={container} initial="hidden" animate="show">
          {analytics ? (
            <>
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-6">
                {[
                  { label: 'Total', value: analytics.totalTickets, color: 'from-slate-500 to-slate-600', icon: TicketCheck },
                  { label: 'Abiertos', value: analytics.abiertos, color: 'from-blue-500 to-blue-600', icon: AlertCircle },
                  { label: 'En Progreso', value: analytics.enProgreso, color: 'from-amber-500 to-amber-600', icon: Clock },
                  { label: 'Críticos Abiertos', value: analytics.criticosAbiertos, color: 'from-red-500 to-red-600', icon: Zap },
                ].map(s => (
                  <motion.div key={s.label} variants={itemAnim} whileHover={{ y: -3 }} className="glass-card-neon rounded-xl p-4 text-center">
                    <p className="text-2xl font-bold text-slate-800 dark:text-white">{s.value}</p>
                    <p className="text-xs text-slate-500 mt-1">{s.label}</p>
                  </motion.div>
                ))}
              </div>

              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
                {/* Clasificación por Categoría */}
                <motion.div variants={itemAnim} className="glass-card-neon rounded-xl p-5">
                  <h3 className="font-semibold text-slate-800 dark:text-white mb-4 flex items-center gap-2">
                    <Brain className="w-4 h-4 text-violet-500" /> Clasificación Inteligente
                    <span className="text-[9px] font-normal text-slate-400 ml-auto">IA por palabras clave</span>
                  </h3>
                  {analytics.porCategoria && Object.keys(analytics.porCategoria).length > 0 ? (
                    <div className="space-y-2">
                      {Object.entries(analytics.porCategoria).map(([cat, count], i) => {
                        const cfg = categoryConfig[cat] || { label: cat, color: 'bg-slate-100 text-slate-600 border-slate-200', icon: HelpCircle };
                        const total = analytics.totalTickets || 1;
                        const pct = ((count / total) * 100).toFixed(0);
                        const CatIcon = cfg.icon;
                        return (
                          <div key={cat} className="flex items-center gap-3">
                            <div className={`p-1.5 rounded-lg ${cfg.color}`}><CatIcon className="w-3.5 h-3.5" /></div>
                            <div className="flex-1">
                              <div className="flex justify-between text-xs mb-1">
                                <span className="font-medium text-slate-700 dark:text-slate-300">{cfg.label}</span>
                                <span className="text-slate-400">{count} ({pct}%)</span>
                              </div>
                              <div className="h-1.5 bg-slate-100 dark:bg-slate-700 rounded-full overflow-hidden">
                                <motion.div initial={{ width: 0 }} animate={{ width: pct + '%' }} transition={{ duration: 0.8, delay: i * 0.1 }}
                                  className="h-full rounded-full bg-gradient-to-r from-violet-500 to-violet-400" />
                              </div>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  ) : (
                    <p className="text-xs text-slate-400 text-center py-6">Sin datos de clasificación</p>
                  )}
                </motion.div>

                {/* Tiempo de Resolución */}
                <motion.div variants={itemAnim} className="glass-card-neon rounded-xl p-5">
                  <h3 className="font-semibold text-slate-800 dark:text-white mb-4 flex items-center gap-2">
                    <Target className="w-4 h-4 text-emerald-500" /> Métricas de Resolución
                    <span className="text-[9px] font-normal text-slate-400 ml-auto">Tiempo promedio</span>
                  </h3>
                  <div className="space-y-4">
                    <div className="text-center p-6 bg-white/50 dark:bg-slate-800/30 rounded-xl border border-slate-100 dark:border-slate-700/30">
                      <p className="text-3xl font-bold text-emerald-600">{analytics.tiempoPromedioResolucionHoras?.toFixed(1) || 0} <span className="text-sm font-normal text-slate-400">horas</span></p>
                      <p className="text-xs text-slate-400 mt-1">Tiempo promedio de resolución</p>
                    </div>
                    <div className="grid grid-cols-2 gap-3">
                      <div className="text-center p-3 bg-white/50 dark:bg-slate-800/30 rounded-xl border border-slate-100 dark:border-slate-700/30">
                        <p className="text-xl font-bold text-slate-800 dark:text-white">{analytics.resueltos || 0}</p>
                        <p className="text-[10px] text-slate-400">Resueltos</p>
                      </div>
                      <div className="text-center p-3 bg-white/50 dark:bg-slate-800/30 rounded-xl border border-slate-100 dark:border-slate-700/30">
                        <p className="text-xl font-bold text-slate-800 dark:text-white">{analytics.cerrados || 0}</p>
                        <p className="text-[10px] text-slate-400">Cerrados</p>
                      </div>
                    </div>
                  </div>
                  {/* Tendencia últimos 7 días */}
                  {analytics.tendenciaUltimos7Dias?.length > 0 && (
                    <div className="mt-4">
                      <p className="text-xs font-medium text-slate-500 mb-2">Tendencia últimos 7 días</p>
                      <div className="flex items-end gap-1.5 h-16">
                        {analytics.tendenciaUltimos7Dias.map((d, i) => {
                          const max = Math.max(...analytics.tendenciaUltimos7Dias.map(x => Math.max(x.creados, x.resueltos)), 1);
                          const hCreados = (d.creados / max) * 100;
                          const hResueltos = (d.resueltos / max) * 100;
                          return (
                            <div key={i} className="flex-1 flex flex-col items-center gap-0.5">
                              <div className="w-full flex flex-col items-center" style={{ height: '60px', justifyContent: 'flex-end' }}>
                                <motion.div initial={{ height: 0 }} animate={{ height: hCreados + '%' }}
                                  className="w-3 rounded-t bg-violet-400 opacity-70" style={{ minHeight: d.creados > 0 ? '4px' : 0 }} />
                                <motion.div initial={{ height: 0 }} animate={{ height: hResueltos + '%' }}
                                  className="w-3 rounded-t bg-emerald-400 opacity-70" style={{ minHeight: d.resueltos > 0 ? '4px' : 0 }} />
                              </div>
                              <span className="text-[7px] text-slate-400">{d.fecha?.substring(5)}</span>
                            </div>
                          );
                        })}
                      </div>
                      <div className="flex justify-center gap-4 mt-2">
                        <span className="flex items-center gap-1 text-[9px] text-slate-400"><span className="w-2 h-2 rounded-full bg-violet-400" /> Creados</span>
                        <span className="flex items-center gap-1 text-[9px] text-slate-400"><span className="w-2 h-2 rounded-full bg-emerald-400" /> Resueltos</span>
                      </div>
                    </div>
                  )}
                </motion.div>
              </div>

              {/* Distribución por Prioridad y Usuario */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <motion.div variants={itemAnim} className="glass-card-neon rounded-xl p-5">
                  <h3 className="font-semibold text-slate-800 dark:text-white mb-3 flex items-center gap-2">
                    <BarChart3 className="w-4 h-4 text-amber-500" /> Distribución por Prioridad
                  </h3>
                  <div className="space-y-2">
                    {analytics.porPrioridad && Object.entries(analytics.porPrioridad).map(([p, count]) => {
                      const cfg = prioridadConfig[p] || { label: p, color: 'bg-slate-100 text-slate-600' };
                      return (
                        <div key={p} className="flex items-center justify-between p-2.5 rounded-lg bg-white/50 dark:bg-slate-800/30 border border-slate-100 dark:border-slate-700/20">
                          <span className={`px-2 py-0.5 rounded-full text-[10px] font-medium ${cfg.color}`}>{cfg.label}</span>
                          <span className="text-sm font-bold text-slate-800 dark:text-white">{count}</span>
                        </div>
                      );
                    })}
                  </div>
                </motion.div>
                <motion.div variants={itemAnim} className="glass-card-neon rounded-xl p-5">
                  <h3 className="font-semibold text-slate-800 dark:text-white mb-3 flex items-center gap-2">
                    <MessageSquare className="w-4 h-4 text-cyan-500" /> Tickets por Usuario
                  </h3>
                  <div className="space-y-2 max-h-[260px] overflow-y-auto pr-1">
                    {analytics.ticketsPorUsuario && Object.entries(analytics.ticketsPorUsuario)
                      .sort((a, b) => b[1] - a[1]).map(([user, count]) => (
                        <div key={user} className="flex items-center justify-between p-2 rounded-lg hover:bg-slate-50 dark:hover:bg-slate-800/30 transition-all">
                          <div className="flex items-center gap-2">
                            <div className="w-6 h-6 rounded-full bg-gradient-to-br from-violet-400 to-violet-600 flex items-center justify-center text-white text-[10px] font-bold">
                              {user.charAt(0).toUpperCase()}
                            </div>
                            <span className="text-xs text-slate-700 dark:text-slate-300">{user}</span>
                          </div>
                          <span className="text-xs font-bold text-slate-600 dark:text-slate-400">{count}</span>
                        </div>
                      ))}
                  </div>
                </motion.div>
              </div>
            </>
          ) : (
            <div className="flex justify-center py-20">
              <div className="skeleton h-40 w-full rounded-xl" />
            </div>
          )}
        </motion.div>
      ) : (
        /* === LIST TAB === */
        <>
          {/* Stats */}
          <motion.div variants={itemAnim} className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-6">
            {[
              { label: 'Total', value: stats.total, color: 'from-slate-500 to-slate-600', icon: TicketCheck },
              { label: 'Abiertos', value: stats.abiertos, color: 'from-blue-500 to-blue-600', icon: AlertCircle },
              { label: 'En Progreso', value: stats.enProgreso, color: 'from-amber-500 to-amber-600', icon: Loader },
              { label: 'Resueltos', value: stats.resueltos, color: 'from-emerald-500 to-emerald-600', icon: CheckCircle },
            ].map(s => (
              <motion.div key={s.label} whileHover={{ y: -3 }} className="glass-card-neon rounded-xl p-4 text-center">
                <p className="text-2xl font-bold text-slate-800 dark:text-white">{s.value}</p>
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
                  filter === f ? 'bg-violet-600 text-white shadow-md' : 'bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-400 border border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-700'
                }`}>
                {f === 'TODOS' ? 'Todos' : statusConfig[f]?.label || f}
              </motion.button>
            ))}
          </motion.div>

          {/* List */}
          {loading ? (
            <div className="flex justify-center py-20">
              <motion.div animate={{ rotate: 360 }} transition={{ duration: 1, repeat: Infinity, ease: 'linear' }} className="w-10 h-10 border-4 border-violet-600 border-t-transparent rounded-full" />
            </div>
          ) : error ? (
            <div className="flex items-center gap-2 text-red-600 justify-center py-20"><AlertCircle className="w-5 h-5" /><span>{error}</span></div>
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
                const catConf = categoryConfig[t.categoria] || { label: t.categoria, color: 'bg-slate-100 text-slate-600 border-slate-200', icon: HelpCircle };
                const CatIcon = catConf.icon;
                return (
                  <motion.div key={t.id} variants={itemAnim} layout
                    className={`glass-card-neon rounded-xl p-4 sm:p-5 border-l-4 ${pConf.border} hover:shadow-lg transition-all`}>
                    <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3">
                      <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2 mb-1 flex-wrap">
                          <h3 className="font-semibold text-slate-800 dark:text-white truncate">{t.titulo}</h3>
                          <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${pConf.color} shrink-0 border`}>{pConf.label}</span>
                          {t.categoria && (
                            <span className={`flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-medium ${catConf.color}`}>
                              <CatIcon className="w-2.5 h-2.5" />{catConf.label}
                            </span>
                          )}
                        </div>
                        {t.descripcion && <p className="text-sm text-slate-500 dark:text-slate-400 line-clamp-2 mt-1">{t.descripcion}</p>}
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
                          <button className="p-2 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors"><ChevronDown className="w-4 h-4 text-slate-400" /></button>
                          <motion.div initial={{ opacity: 0, scale: 0.95, y: 5 }} whileHover={{ opacity: 1, scale: 1, y: 0 }}
                            className="absolute right-0 top-full mt-1 bg-white dark:bg-slate-800 rounded-xl shadow-xl border border-slate-200 dark:border-slate-700 py-1 min-w-[140px] hidden group-hover:block z-10">
                            {Object.entries(statusConfig).map(([key, val]) => (
                              <button key={key} onClick={() => handleStatus(t.id, key)}
                                className={`flex items-center gap-2 w-full px-3 py-2 text-xs hover:bg-slate-50 dark:hover:bg-slate-700/50 transition-colors ${t.status === key ? 'font-semibold text-slate-800 dark:text-white' : 'text-slate-600 dark:text-slate-400'}`}>
                                <val.icon className="w-3.5 h-3.5" />{val.label}
                              </button>
                            ))}
                            <div className="border-t border-slate-100 mt-1 pt-1">
                              <button onClick={() => handleDelete(t.id)} className="flex items-center gap-2 w-full px-3 py-2 text-xs text-red-600 hover:bg-red-50 transition-colors">
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
        </>
      )}

      {/* Create Modal */}
      <AnimatePresence>
        {showModal && (
          <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/40 backdrop-blur-md z-50 flex items-center justify-center p-4" onClick={() => setShowModal(false)}>
            <motion.div initial={{ opacity: 0, scale: 0.9, y: 20 }} animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: 20 }} className="glass-card-neon rounded-2xl shadow-2xl p-6 w-full max-w-lg" onClick={e => e.stopPropagation()}>
              <div className="flex items-center justify-between mb-5">
                <h3 className="text-lg font-bold text-slate-800 dark:text-white">Crear Nuevo Ticket</h3>
                <motion.button whileHover={{ rotate: 90 }} onClick={() => setShowModal(false)}
                  className="p-1 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors">
                  <X className="w-5 h-5 text-slate-400" />
                </motion.button>
              </div>

              {/* Clasificación inteligente */}
              {(form.titulo.trim().length >= 5 || form.descripcion.trim()) && !categoriaSugerida && (
                <motion.button initial={{ opacity: 0 }} animate={{ opacity: 1 }}
                  onClick={handleAutoClasificar} disabled={clasificando}
                  className="w-full mb-4 flex items-center justify-center gap-2 px-3 py-2 bg-gradient-to-r from-violet-500/10 to-violet-600/10 border border-violet-200 dark:border-violet-500/30 rounded-xl text-xs text-violet-700 dark:text-violet-300 hover:bg-violet-500/20 transition-all">
                  {clasificando ? (
                    <><Loader className="w-3 h-3 animate-spin" /> Clasificando...</>
                  ) : (
                    <><Brain className="w-3.5 h-3.5" /> Clasificar automáticamente con IA</>
                  )}
                </motion.button>
              )}
              {categoriaSugerida && (
                <div className="mb-4 flex items-center gap-2 px-3 py-2 bg-emerald-50 dark:bg-emerald-500/10 border border-emerald-200 dark:border-emerald-500/30 rounded-xl text-xs">
                  <Brain className="w-3.5 h-3.5 text-emerald-600" />
                  <span className="text-emerald-700 dark:text-emerald-300">Categoría sugerida:</span>
                  {(() => {
                    const cfg = categoryConfig[categoriaSugerida] || { label: categoriaSugerida, color: 'bg-emerald-100 text-emerald-700 border-emerald-200', icon: HelpCircle };
                    const CatIcon = cfg.icon;
                    return <span className={`flex items-center gap-1 px-2 py-0.5 rounded-full font-medium ${cfg.color}`}><CatIcon className="w-3 h-3" />{cfg.label}</span>;
                  })()}
                </div>
              )}

              <form onSubmit={handleCreate} className="space-y-4">
                <div>
                  <label className="block text-xs font-medium text-slate-600 dark:text-slate-400 mb-1">Título *</label>
                  <input type="text" value={form.titulo} onChange={e => { setForm({ ...form, titulo: e.target.value }); setFormErrors({ ...formErrors, titulo: '' }); setCategoriaSugerida(null); }}
                    className={`w-full px-3 py-2.5 border rounded-xl text-sm bg-white dark:bg-slate-800 text-slate-800 dark:text-white focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent transition-all input-neon ${formErrors.titulo ? 'border-red-400' : 'border-slate-200 dark:border-slate-700'}`}
                    placeholder="Ej: Error en el sistema de facturación..." />
                  {formErrors.titulo && <p className="text-xs text-red-500 mt-1">{formErrors.titulo}</p>}
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-600 dark:text-slate-400 mb-1">Descripción</label>
                  <textarea value={form.descripcion} onChange={e => { setForm({ ...form, descripcion: e.target.value }); setFormErrors({ ...formErrors, descripcion: '' }); }}
                    className={`w-full px-3 py-2.5 border rounded-xl text-sm bg-white dark:bg-slate-800 text-slate-800 dark:text-white focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent resize-none transition-all input-neon ${formErrors.descripcion ? 'border-red-400' : 'border-slate-200 dark:border-slate-700'}`}
                    rows={3} placeholder="Detalles del problema..." />
                  {formErrors.descripcion && <p className="text-xs text-red-500 mt-1">{formErrors.descripcion}</p>}
                </div>
                <div>
                  <label className="block text-xs font-medium text-slate-600 dark:text-slate-400 mb-1">Prioridad</label>
                  <select value={form.prioridad} onChange={e => setForm({ ...form, prioridad: e.target.value })}
                    className="w-full px-3 py-2.5 border border-slate-200 dark:border-slate-700 rounded-xl text-sm bg-white dark:bg-slate-800 text-slate-800 dark:text-white focus:outline-none focus:ring-2 focus:ring-violet-500">
                    <option value="BAJA">Baja</option>
                    <option value="MEDIA">Media</option>
                    <option value="ALTA">Alta</option>
                    <option value="CRITICA">Crítica</option>
                  </select>
                </div>
                <div className="flex gap-3 pt-2">
                  <button type="button" onClick={() => setShowModal(false)}
                    className="flex-1 px-4 py-2.5 border border-slate-200 dark:border-slate-700 rounded-xl text-sm font-medium text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors">Cancelar</button>
                  <motion.button type="submit" whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }}
                    className="flex-1 bg-gradient-to-r from-violet-600 to-violet-500 hover:from-violet-500 hover:to-violet-400 text-white py-2.5 rounded-xl text-sm font-medium transition-all shadow-lg shadow-violet-600/20">Crear Ticket</motion.button>
                </div>
              </form>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
}
