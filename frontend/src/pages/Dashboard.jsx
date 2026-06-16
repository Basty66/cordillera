import { useEffect, useState } from 'react';
import { getDashboard, getIndicadoresEconomicos, getClimaSucursales, getTicketAnalytics } from '../api/client';
import { motion } from 'framer-motion';
import { Bar, Doughnut, Line } from 'react-chartjs-2';
import {
  Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend,
  ArcElement, PointElement, LineElement, Filler
} from 'chart.js';
import {
  ShoppingCart, DollarSign, TrendingUp, RefreshCw, BarChart3, Package,
  AlertCircle, Users, Store, Server, Activity, Wifi, Sun, Cloud, CloudRain,
  Thermometer, Droplets, Wind, TrendingDown, Wallet, BadgePercent,
  TicketCheck, Clock, Target, Zap, HelpCircle, Shield
} from 'lucide-react';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement, PointElement, LineElement, Filler);

function formatCLP(n) {
  return '$' + Number(n).toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
}

const container = { hidden: { opacity: 0 }, show: { opacity: 1, transition: { staggerChildren: 0.04 } } };
const itemAnim = { hidden: { opacity: 0, y: 20 }, show: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 100 } } };

const microservices = [
  { name: 'ms-ventas', port: 8081, icon: ShoppingCart, color: 'from-blue-500 to-blue-600', desc: 'Ventas, Productos, Sucursales' },
  { name: 'ms-datos-org', port: 8082, icon: Users, color: 'from-amber-500 to-amber-600', desc: 'Empleados, Departamentos' },
  { name: 'ms-indicadores', port: 8083, icon: BarChart3, color: 'from-violet-500 to-violet-600', desc: 'KPIs, Indicadores' },
  { name: 'bff', port: 8090, icon: Activity, color: 'from-cyan-500 to-cyan-600', desc: 'Auth, Tickets, Reportes' },
  { name: 'api-gateway', port: 8084, icon: Wifi, color: 'from-rose-500 to-rose-600', desc: 'Enrutamiento, CB' },
];

const iconoClima = (desc) => {
  if (!desc) return <Sun className="w-5 h-5 text-amber-400" />;
  const d = desc.toLowerCase();
  if (d.includes('lluv') || d.includes('lloviz')) return <CloudRain className="w-5 h-5 text-blue-400" />;
  if (d.includes('nubl') || d.includes('nube')) return <Cloud className="w-5 h-5 text-slate-400" />;
  return <Sun className="w-5 h-5 text-amber-400" />;
};

const categoryIcons = {
  TÉCNICO: Zap, FACTURACIÓN: DollarSign, RECLAMO: AlertCircle, CONSULTA: HelpCircle,
};

function StatCard({ label, value, icon: Icon, color }) {
  return (
    <motion.div variants={itemAnim} whileHover={{ y: -6, scale: 1.02, transition: { type: 'spring', stiffness: 300 } }}
      className="glass-card-neon rounded-xl p-5 transition-all duration-300 group">
      <div className="flex items-center justify-between mb-3">
        <span className="text-sm font-medium text-slate-500 dark:text-slate-400">{label}</span>
        <div className={`p-2.5 rounded-lg bg-gradient-to-br ${color} shadow-lg group-hover:scale-110 transition-transform duration-300`}>
          <Icon className="w-4 h-4 text-white" />
        </div>
      </div>
      <p className="text-2xl font-bold text-slate-800 dark:text-white transition-all">{value}</p>
    </motion.div>
  );
}

function LoadingSkeleton() {
  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-4">
        {[...Array(5)].map((_, i) => (
          <div key={i} className="glass-card rounded-xl p-5">
            <div className="flex justify-between mb-3"><div className="skeleton h-4 w-20" /><div className="skeleton h-8 w-8 rounded-lg" /></div>
            <div className="skeleton h-8 w-28" />
          </div>
        ))}
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {[...Array(2)].map((_, i) => (
          <div key={i} className="glass-card rounded-xl p-5">
            <div className="skeleton h-5 w-40 mb-4" /><div className="skeleton h-[250px] w-full" />
          </div>
        ))}
      </div>
    </div>
  );
}

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [economico, setEconomico] = useState(null);
  const [clima, setClima] = useState(null);
  const [ticketAnalytics, setTicketAnalytics] = useState(null);
  const [error, setError] = useState(null);
  const [refreshing, setRefreshing] = useState(false);

  const load = async () => {
    try {
      setRefreshing(true);
      const [d, ec, cl, ta] = await Promise.all([
        getDashboard(), getIndicadoresEconomicos(), getClimaSucursales(), getTicketAnalytics()
      ]);
      setData(d); setEconomico(ec); setClima(cl); setTicketAnalytics(ta);
      setError(null);
    } catch (e) {
      setError(e.response?.data?.message || e.message);
    } finally {
      setRefreshing(false);
    }
  };

  useEffect(() => { load(); }, []);

  if (error) return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="flex flex-col items-center justify-center h-64 gap-3">
      <motion.div animate={{ scale: [1, 1.1, 1] }} transition={{ duration: 2, repeat: Infinity }} className="p-4 bg-red-50 rounded-2xl">
        <AlertCircle className="w-8 h-8 text-red-500" />
      </motion.div>
      <span className="text-sm text-red-600 font-medium">{error}</span>
      <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.97 }}
        onClick={load} className="flex items-center gap-2 px-5 py-2.5 bg-red-50 text-red-600 rounded-xl text-sm font-medium hover:bg-red-100 transition-all border border-red-200">
        <RefreshCw className="w-4 h-4" /> Reintentar
      </motion.button>
    </motion.div>
  );

  if (!data) return <LoadingSkeleton />;

  const monthColors = ['#3b82f6','#60a5fa','#818cf8','#a78bfa','#c084fc','#e879f9','#f472b6','#fb7185','#f87171','#fbbf24','#34d399','#10b981'];
  const ventasMensuales = data.ventasMensuales || [];
  const ventasCategoria = data.ventasPorCategoria || [];
  const topProductos = data.topProductos || [];
  const indicadoresList = data.indicadores || [];
  const ecoIndicadores = economico?.indicadores || {};
  const climaList = clima || [];
  const ta = ticketAnalytics;

  const monthlyBarData = ventasMensuales.length ? {
    labels: ventasMensuales.map(v => v.mes.substring(0, 3) + ' ' + v.anio),
    datasets: [{
      label: 'Monto Total ($)',
      data: ventasMensuales.map(v => v.montoTotal),
      backgroundColor: ventasMensuales.map((_, i) => monthColors[i % 12]),
      borderRadius: 6,
    }],
  } : null;

  const categoryDoughnutData = ventasCategoria.length ? {
    labels: ventasCategoria.map(c => c.categoria),
    datasets: [{
      data: ventasCategoria.map(c => c.montoTotal),
      backgroundColor: ['#3b82f6','#10b981','#f59e0b','#ef4444','#8b5cf6','#ec4899','#14b8a6','#f97316'],
      borderWidth: 0,
    }],
  } : null;

  return (
    <motion.div variants={container} initial="hidden" animate="show">
      {/* Header */}
      <motion.div variants={itemAnim} className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h2 className="text-2xl font-bold text-slate-800 dark:text-white flex items-center gap-2">
            Dashboard Corporativo
            <span className="text-xs font-normal text-emerald-600 bg-emerald-50 dark:bg-emerald-500/10 px-2 py-0.5 rounded-full neon-border">En Vivo</span>
          </h2>
          <p className="text-sm text-slate-400 mt-0.5 capitalize">
            {new Date().toLocaleDateString('es-CL', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
          </p>
        </div>
        <motion.button whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.95 }}
          onClick={load} disabled={refreshing}
          className="flex items-center gap-2 px-4 py-2.5 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-sm font-medium text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700 transition-all shadow-sm hover:shadow-md">
          <RefreshCw className={`w-4 h-4 ${refreshing ? 'animate-spin' : ''}`} />
          {refreshing ? 'Actualizando...' : 'Actualizar'}
        </motion.button>
      </motion.div>

      {/* Indicadores Económicos - Widget */}
      <motion.div variants={itemAnim} className="glass-card-neon rounded-xl p-4 mb-6 overflow-hidden relative">
        <div className="absolute inset-0 bg-gradient-to-r from-emerald-500/3 to-blue-500/3 pointer-events-none" />
        <div className="flex items-center gap-2 mb-3">
          <BadgePercent className="w-4 h-4 text-emerald-500" />
          <span className="text-xs font-semibold uppercase tracking-widest text-slate-500">Indicadores Económicos · mindicador.cl</span>
          {economico?.fecha && economico.fecha !== 'Simulado' && (
            <span className="text-[9px] text-slate-400 ml-auto">Actualizado: {new Date(economico.fecha).toLocaleDateString('es-CL')}</span>
          )}
          {economico?.fecha === 'Simulado' && (
            <span className="text-[9px] text-amber-500 ml-auto flex items-center gap-1"><Cloud className="w-2.5 h-2.5" /> Simulado</span>
          )}
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
          {['UF', 'DOLAR', 'UTM', 'IPC'].map(key => {
            const ind = ecoIndicadores[key];
            if (!ind) return null;
            const isPositive = ind.valor >= 0;
            return (
              <motion.div key={key} whileHover={{ y: -2 }} className="bg-white/50 dark:bg-slate-800/40 rounded-xl p-3 border border-slate-100 dark:border-slate-700/30 transition-all">
                <div className="flex items-center justify-between mb-1">
                  <span className="text-[10px] font-semibold uppercase tracking-wider text-slate-400">{key}</span>
                  {key === 'IPC' ? (
                    isPositive ? <TrendingUp className="w-3 h-3 text-emerald-500" /> : <TrendingDown className="w-3 h-3 text-red-500" />
                  ) : (
                    <Wallet className="w-3 h-3 text-slate-400" />
                  )}
                </div>
                <p className="text-lg font-bold text-slate-800 dark:text-white">
                  {key === 'IPC' ? ind.valor.toFixed(1) + '%' : formatCLP(ind.valor)}
                </p>
                <p className="text-[9px] text-slate-400 truncate">{ind.nombre}</p>
              </motion.div>
            );
          })}
        </div>
      </motion.div>

      {/* Microservice Architecture */}
      <motion.div variants={itemAnim} className="glass-card-neon rounded-xl p-4 mb-6">
        <div className="flex items-center gap-2 mb-3">
          <Server className="w-4 h-4 text-emerald-500" />
          <span className="text-xs font-semibold uppercase tracking-widest text-slate-500">Arquitectura de Microservicios</span>
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-2">
          {microservices.map(ms => (
            <div key={ms.name} className="flex items-center gap-2 p-2 rounded-lg bg-slate-50/60 dark:bg-slate-800/30 border border-slate-100 dark:border-slate-700/30">
              <div className={`p-1.5 rounded-lg bg-gradient-to-br ${ms.color} shadow-md shrink-0`}>
                <ms.icon className="w-3 h-3 text-white" />
              </div>
              <div className="min-w-0">
                <p className="text-[11px] font-medium text-slate-700 dark:text-slate-300 truncate">{ms.name}</p>
                <p className="text-[8px] text-slate-400 truncate">{ms.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </motion.div>

      {/* Stat Cards */}
      <motion.div variants={container} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-4 mb-6">
        <StatCard label="Total Ventas" value={data.ventas.totalVentas} icon={ShoppingCart} color="from-blue-500 to-blue-600" />
        <StatCard label="Monto Total" value={formatCLP(data.ventas.montoTotal)} icon={DollarSign} color="from-emerald-500 to-emerald-600" />
        <StatCard label="Ticket Promedio" value={formatCLP(data.ventas.promedioVenta)} icon={TrendingUp} color="from-violet-500 to-violet-600" />
        <StatCard label="Empleados" value={data.totalEmpleados} icon={Users} color="from-amber-500 to-amber-600" />
        <StatCard label="Sucursales" value={data.totalSucursales} icon={Store} color="from-rose-500 to-rose-600" />
      </motion.div>

      {/* Clima Widget + Ticket Analytics Row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 mb-6">
        {/* Clima */}
        <motion.div variants={itemAnim} className="glass-card-neon rounded-xl p-4 lg:col-span-1">
          <div className="flex items-center gap-2 mb-3">
            <Sun className="w-4 h-4 text-amber-500" />
            <span className="text-xs font-semibold uppercase tracking-widest text-slate-500">Clima · Sucursales</span>
            <span className="text-[9px] text-slate-400 ml-auto">OpenWeatherMap</span>
          </div>
          <div className="space-y-2 max-h-[320px] overflow-y-auto pr-1 scrollbar-thin">
            {climaList.length > 0 ? climaList.map((c, i) => (
              <motion.div key={i} initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.03 }}
                className="flex items-center gap-3 p-2.5 rounded-lg bg-white/50 dark:bg-slate-800/30 hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-all border border-slate-100 dark:border-slate-700/20">
                <div className="p-1.5 rounded-lg bg-slate-100 dark:bg-slate-700/50">{iconoClima(c.descripcion)}</div>
                <div className="flex-1 min-w-0">
                  <p className="text-xs font-medium text-slate-700 dark:text-slate-300">{c.ciudad}</p>
                  <p className="text-[10px] text-slate-400 capitalize">{c.descripcion || '—'}</p>
                </div>
                <div className="text-right">
                  <p className="text-sm font-bold text-slate-800 dark:text-white">{c.temperatura?.toFixed(0) || '—'}°C</p>
                  <p className="text-[9px] text-slate-400 flex items-center gap-1 justify-end">
                    <Droplets className="w-2.5 h-2.5" />{c.humedad?.toFixed(0) || '—'}%
                  </p>
                </div>
              </motion.div>
            )) : (
              <p className="text-xs text-slate-400 text-center py-6">Cargando datos climáticos...</p>
            )}
          </div>
        </motion.div>

        {/* Ticket Analytics */}
        <motion.div variants={itemAnim} className="glass-card-neon rounded-xl p-4 lg:col-span-2">
          <div className="flex items-center gap-2 mb-3">
            <TicketCheck className="w-4 h-4 text-violet-500" />
            <span className="text-xs font-semibold uppercase tracking-widest text-slate-500">Analytics · Tickets</span>
            <span className="text-[9px] text-slate-400 ml-auto">Clasificación Inteligente</span>
          </div>
          {ta ? (
            <div className="space-y-3">
              <div className="grid grid-cols-4 gap-2">
                {[
                  { label: 'Abiertos', value: ta.abiertos, color: 'from-blue-500 to-blue-600', icon: AlertCircle },
                  { label: 'En Progreso', value: ta.enProgreso, color: 'from-amber-500 to-amber-600', icon: Clock },
                  { label: 'Críticos', value: ta.criticosAbiertos, color: 'from-red-500 to-red-600', icon: Zap },
                  { label: 'Tiempo Prom.', value: ta.tiempoPromedioResolucionHoras?.toFixed(0) + 'h', color: 'from-emerald-500 to-emerald-600', icon: Target },
                ].map(s => (
                  <motion.div key={s.label} whileHover={{ y: -2 }} className="bg-white/50 dark:bg-slate-800/30 rounded-xl p-3 text-center border border-slate-100 dark:border-slate-700/20">
                    <p className="text-lg font-bold text-slate-800 dark:text-white">{s.value}</p>
                    <p className="text-[9px] text-slate-400 mt-0.5">{s.label}</p>
                  </motion.div>
                ))}
              </div>
              {ta.porCategoria && Object.keys(ta.porCategoria).length > 0 && (
                <div className="flex flex-wrap gap-1.5">
                  {Object.entries(ta.porCategoria).map(([cat, count]) => {
                    const CatIcon = categoryIcons[cat] || HelpCircle;
                    const colors = { TÉCNICO: 'text-blue-600 bg-blue-100 dark:bg-blue-500/20 dark:text-blue-400 border-blue-200',
                      FACTURACIÓN: 'text-emerald-600 bg-emerald-100 dark:bg-emerald-500/20 dark:text-emerald-400 border-emerald-200',
                      RECLAMO: 'text-red-600 bg-red-100 dark:bg-red-500/20 dark:text-red-400 border-red-200',
                      CONSULTA: 'text-violet-600 bg-violet-100 dark:bg-violet-500/20 dark:text-violet-400 border-violet-200' };
                    const c = colors[cat] || 'text-slate-600 bg-slate-100 dark:bg-slate-700 dark:text-slate-400';
                    return (
                      <span key={cat} className={`flex items-center gap-1 px-2.5 py-1 rounded-full text-[10px] font-medium border ${c}`}>
                        <CatIcon className="w-3 h-3" />{cat} ({count})
                      </span>
                    );
                  })}
                </div>
              )}
            </div>
          ) : (
            <p className="text-xs text-slate-400 text-center py-6">Cargando analytics...</p>
          )}
        </motion.div>
      </div>

      {/* Charts Row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <motion.div variants={itemAnim} className="glass-card-neon rounded-xl p-5">
          <h3 className="text-lg font-semibold text-slate-800 dark:text-white mb-4 flex items-center gap-2">
            <BarChart3 className="w-5 h-5 text-emerald-500" />
            Tendencia de Ventas Mensuales
            <span className="text-[10px] font-normal text-slate-400 ml-auto">Data Warehouse</span>
          </h3>
          {monthlyBarData ? (
            <div className="h-[280px]">
              <Bar data={monthlyBarData} options={{
                responsive: true, maintainAspectRatio: false,
                plugins: { legend: { display: false }, tooltip: { callbacks: { label: ctx => formatCLP(ctx.raw) } } },
                scales: { y: { beginAtZero: true, ticks: { callback: v => formatCLP(v) } } },
              }} />
            </div>
          ) : (
            <p className="text-slate-400 text-sm text-center py-12">Cargando datos mensuales...</p>
          )}
        </motion.div>

        <motion.div variants={itemAnim} className="glass-card-neon rounded-xl p-5">
          <h3 className="text-lg font-semibold text-slate-800 dark:text-white mb-4 flex items-center gap-2">
            <Package className="w-5 h-5 text-violet-500" />
            Ventas por Categoría
            <span className="text-[10px] font-normal text-slate-400 ml-auto">ms-ventas</span>
          </h3>
          {categoryDoughnutData ? (
            <div className="h-[280px] flex items-center justify-center">
              <Doughnut data={categoryDoughnutData} options={{
                responsive: true, maintainAspectRatio: false,
                plugins: { legend: { position: 'bottom', labels: { usePointStyle: true, padding: 12, font: { size: 10 } } },
                  tooltip: { callbacks: { label: ctx => ctx.label + ': ' + formatCLP(ctx.raw) } } },
              }} />
            </div>
          ) : (
            <p className="text-slate-400 text-sm text-center py-12">Cargando categorías...</p>
          )}
        </motion.div>
      </div>

      {/* Charts Row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <motion.div variants={itemAnim} className="glass-card-neon rounded-xl p-5">
          <h3 className="text-lg font-semibold text-slate-800 dark:text-white mb-4 flex items-center gap-2">
            <TrendingUp className="w-5 h-5 text-blue-500" />
            Top Productos Más Vendidos
            <span className="text-[10px] font-normal text-slate-400 ml-auto">ms-ventas</span>
          </h3>
          {topProductos.length > 0 ? (
            <div className="space-y-2">
              {topProductos.map((p, i) => (
                <motion.div key={p.productoId} initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.04 }}
                  className="flex items-center gap-3 p-2.5 rounded-lg hover:bg-slate-50 dark:hover:bg-slate-800/30 transition-all">
                  <div className={`w-7 h-7 rounded-lg flex items-center justify-center text-xs font-bold text-white shadow-md ${
                    i === 0 ? 'bg-gradient-to-br from-amber-400 to-amber-600' :
                    i === 1 ? 'bg-gradient-to-br from-slate-300 to-slate-500' :
                    i === 2 ? 'bg-gradient-to-br from-amber-600 to-amber-800' :
                    'bg-gradient-to-br from-blue-400 to-blue-600'
                  }`}>{i + 1}</div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-slate-700 dark:text-slate-300 truncate">{p.nombre}</p>
                    <p className="text-xs text-slate-400">{p.totalVendido} unidades vendidas</p>
                  </div>
                  <span className="text-sm font-bold text-emerald-600">{formatCLP(p.montoTotal)}</span>
                </motion.div>
              ))}
            </div>
          ) : (
            <p className="text-slate-400 text-sm text-center py-12">Sin datos de productos</p>
          )}
        </motion.div>

        <motion.div variants={itemAnim} className="glass-card-neon rounded-xl p-5">
          <h3 className="text-lg font-semibold text-slate-800 dark:text-white mb-4 flex items-center gap-2">
            <BarChart3 className="w-5 h-5 text-violet-500" />
            Indicadores Clave
            <span className="text-[10px] font-normal text-slate-400 ml-auto">ms-indicadores</span>
          </h3>
          {indicadoresList.length > 0 ? (
            <div className="space-y-3">
              {indicadoresList.map((ind, i) => (
                <motion.div key={i} initial={{ opacity: 0, x: -15 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.08 }}
                  whileHover={{ x: 4 }} className="flex items-center justify-between p-3 bg-white/60 dark:bg-slate-800/30 rounded-xl hover:shadow-sm transition-all cursor-default border border-slate-100 dark:border-slate-700/30">
                  <div>
                    <p className="font-medium text-slate-700 dark:text-slate-300 text-sm">{ind.nombre}</p>
                    <p className="text-xs text-slate-400">{ind.unidad} · {ind.periodo}</p>
                  </div>
                  <motion.span initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ delay: i * 0.08 + 0.2, type: 'spring' }}
                    className={`text-lg font-bold ${ind.unidad === 'CLP' ? 'text-emerald-600' : 'text-violet-600'}`}>
                    {ind.unidad === 'CLP' ? formatCLP(ind.valorActual) : Number(ind.valorActual).toFixed(1) + (ind.unidad === '%' ? '%' : '')}
                  </motion.span>
                </motion.div>
              ))}
            </div>
          ) : (
            <p className="text-slate-400 text-sm text-center py-8">Indicadores no disponibles</p>
          )}
        </motion.div>
      </div>
    </motion.div>
  );
}
