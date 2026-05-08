import { useEffect, useState } from 'react';
import { getDashboard } from '../api/client';
import { ShoppingCart, DollarSign, Users, Store, TrendingUp, AlertCircle, RefreshCw } from 'lucide-react';
import { motion } from 'framer-motion';
import { Bar, Doughnut, Line } from 'react-chartjs-2';
import {
  Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend,
  ArcElement, PointElement, LineElement, Filler
} from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement, PointElement, LineElement, Filler);

function formatCLP(n) {
  return '$' + Number(n).toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
}

const container = {
  hidden: { opacity: 0 },
  show: { opacity: 1, transition: { staggerChildren: 0.07 } },
};

const itemAnim = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 100 } },
};

const cards = [
  { label: 'Total Ventas', key: 'totalVentas', icon: ShoppingCart, color: 'from-blue-500 to-blue-600', bg: 'bg-blue-50' },
  { label: 'Monto Total', key: 'montoTotal', icon: DollarSign, color: 'from-emerald-500 to-emerald-600', bg: 'bg-emerald-50', format: true },
  { label: 'Ticket Promedio', key: 'promedioVenta', icon: TrendingUp, color: 'from-violet-500 to-violet-600', bg: 'bg-violet-50', format: true },
  { label: 'Empleados', key: 'totalEmpleados', icon: Users, color: 'from-amber-500 to-amber-600', bg: 'bg-amber-50' },
  { label: 'Sucursales', key: 'totalSucursales', icon: Store, color: 'from-rose-500 to-rose-600', bg: 'bg-rose-50' },
];

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const [refreshing, setRefreshing] = useState(false);

  const load = async () => {
    try {
      setRefreshing(true);
      const d = await getDashboard();
      setData(d);
      setError(null);
    } catch (e) {
      setError(e.response?.data?.message || e.message);
    } finally {
      setRefreshing(false);
    }
  };

  useEffect(() => { load(); }, []);

  if (error) return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="flex flex-col items-center justify-center h-64 text-red-600 gap-3">
      <AlertCircle className="w-8 h-8" />
      <span className="text-sm">{error}</span>
      <button onClick={load} className="flex items-center gap-2 px-4 py-2 bg-red-50 text-red-600 rounded-xl text-sm hover:bg-red-100 transition-colors">
        <RefreshCw className="w-4 h-4" /> Reintentar
      </button>
    </motion.div>
  );

  if (!data) return (
    <div className="flex items-center justify-center h-64">
      <div className="animate-spin rounded-full h-12 w-12 border-4 border-emerald-600 border-t-transparent" />
    </div>
  );

  const cardValues = [
    { label: 'Total Ventas', value: data.ventas.totalVentas, icon: ShoppingCart, color: 'from-blue-500 to-blue-600', bg: 'bg-blue-50' },
    { label: 'Monto Total', value: formatCLP(data.ventas.montoTotal), icon: DollarSign, color: 'from-emerald-500 to-emerald-600', bg: 'bg-emerald-50' },
    { label: 'Ticket Promedio', value: formatCLP(data.ventas.promedioVenta), icon: TrendingUp, color: 'from-violet-500 to-violet-600', bg: 'bg-violet-50' },
    { label: 'Empleados', value: data.totalEmpleados, icon: Users, color: 'from-amber-500 to-amber-600', bg: 'bg-amber-50' },
    { label: 'Sucursales', value: data.totalSucursales, icon: Store, color: 'from-rose-500 to-rose-600', bg: 'bg-rose-50' },
  ];

  const barData = {
    labels: ['Ventas', 'Empleados', 'Sucursales'],
    datasets: [{
      label: 'Totales',
      data: [data.ventas.totalVentas, data.totalEmpleados, data.totalSucursales],
      backgroundColor: ['#3b82f6', '#10b981', '#f59e0b'],
      borderRadius: 8,
    }],
  };

  const doughnutData = data.indicadores?.length ? {
    labels: data.indicadores.map(i => i.nombre),
    datasets: [{
      data: data.indicadores.map(i => { const v = parseFloat(i.valorActual); return isNaN(v) ? 0 : Math.abs(v); }),
      backgroundColor: ['#10b981', '#8b5cf6', '#f59e0b', '#ef4444', '#3b82f6'],
      borderWidth: 0,
    }],
  } : null;

  const lineData = data.indicadores?.length ? {
    labels: data.indicadores.map(i => i.nombre),
    datasets: [{
      label: 'Valor Actual',
      data: data.indicadores.map(i => { const v = parseFloat(i.valorActual); return isNaN(v) ? 0 : v; }),
      borderColor: '#10b981',
      backgroundColor: 'rgba(16, 185, 129, 0.1)',
      fill: true,
      tension: 0.4,
      pointBackgroundColor: '#10b981',
      pointRadius: 6,
    }],
  } : null;

  return (
    <motion.div variants={container} initial="hidden" animate="show">
      {/* Header */}
      <motion.div variants={itemAnim} className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h2 className="text-2xl font-bold text-slate-800">Dashboard</h2>
          <p className="text-sm text-slate-400 mt-0.5">
            {new Date().toLocaleDateString('es-CL', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
          </p>
        </div>
        <motion.button
          whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.95 }}
          onClick={load}
          disabled={refreshing}
          className="flex items-center gap-2 px-4 py-2.5 bg-white border border-slate-200 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-50 transition-all shadow-sm"
        >
          <RefreshCw className={`w-4 h-4 ${refreshing ? 'animate-spin' : ''}`} />
          {refreshing ? 'Actualizando...' : 'Actualizar'}
        </motion.button>
      </motion.div>

      {/* Cards */}
      <motion.div variants={container} className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-4 mb-6">
        {cardValues.map(c => (
          <motion.div
            key={c.label}
            variants={itemAnim}
            whileHover={{ y: -5, transition: { type: 'spring', stiffness: 300 } }}
            className="glass-card rounded-xl p-5 hover:shadow-lg transition-all duration-300"
          >
            <div className="flex items-center justify-between mb-3">
              <span className="text-sm font-medium text-slate-500">{c.label}</span>
              <div className={`p-2 rounded-lg bg-gradient-to-br ${c.color} shadow-lg`}>
                <c.icon className="w-4 h-4 text-white" />
              </div>
            </div>
            <p className="text-2xl font-bold text-slate-800">{c.value}</p>
          </motion.div>
        ))}
      </motion.div>

      {/* Charts Row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <motion.div variants={itemAnim} className="glass-card rounded-xl p-5">
          <h3 className="text-lg font-semibold text-slate-800 mb-4">Resumen General</h3>
          <div className="h-[250px]">
            <Bar data={barData} options={{
              responsive: true,
              maintainAspectRatio: false,
              plugins: { legend: { display: false } },
              scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },
            }} />
          </div>
        </motion.div>

        <motion.div variants={itemAnim} className="glass-card rounded-xl p-5">
          <h3 className="text-lg font-semibold text-slate-800 mb-4">Distribución de KPIs</h3>
          {doughnutData ? (
            <div className="h-[250px] flex items-center justify-center">
              <Doughnut data={doughnutData} options={{
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { position: 'bottom', labels: { usePointStyle: true } } },
              }} />
            </div>
          ) : (
            <p className="text-slate-400 text-sm text-center py-8">Sin datos de KPI</p>
          )}
        </motion.div>
      </div>

      {/* Charts Row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <motion.div variants={itemAnim} className="glass-card rounded-xl p-5">
          <h3 className="text-lg font-semibold text-slate-800 mb-4">Valores de Indicadores</h3>
          {lineData ? (
            <div className="h-[250px]">
              <Line data={lineData} options={{
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: { y: { beginAtZero: true } },
              }} />
            </div>
          ) : (
            <p className="text-slate-400 text-sm text-center py-8">Sin indicadores</p>
          )}
        </motion.div>

        <motion.div variants={itemAnim} className="glass-card rounded-xl p-5">
          <h3 className="text-lg font-semibold text-slate-800 mb-4">Indicadores Clave</h3>
          {data.indicadores?.length > 0 ? (
            <div className="space-y-3">
              {data.indicadores.map((ind, i) => (
                <motion.div
                  key={i}
                  initial={{ opacity: 0, x: -15 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: i * 0.08 }}
                  whileHover={{ x: 4 }}
                  className="flex items-center justify-between p-3 bg-white/60 rounded-xl hover:bg-white/90 transition-all cursor-default"
                >
                  <div>
                    <p className="font-medium text-slate-700 text-sm">{ind.nombre}</p>
                    <p className="text-xs text-slate-400">{ind.unidad}</p>
                  </div>
                  <span className={`text-lg font-bold ${ind.unidad === 'CLP' ? 'text-emerald-600' : 'text-violet-600'}`}>
                    {ind.unidad === 'CLP' ? formatCLP(ind.valorActual) : Number(ind.valorActual).toFixed(1) + '%'}
                  </span>
                </motion.div>
              ))}
            </div>
          ) : (
            <p className="text-slate-400 text-sm text-center py-8">Sin indicadores calculados</p>
          )}
        </motion.div>
      </div>
    </motion.div>
  );
}
