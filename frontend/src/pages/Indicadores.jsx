import { useEffect, useState } from 'react';
import { getIndicadores, getValoresActuales, getCategorias } from '../api/client';
import { AlertCircle, BarChart3, TrendingUp, DollarSign, RefreshCw } from 'lucide-react';
import { motion } from 'framer-motion';
import { Bar, Line } from 'react-chartjs-2';

const container = {
  hidden: { opacity: 0 },
  show: { opacity: 1, transition: { staggerChildren: 0.06 } },
};

const itemAnim = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0 },
};

function formatCLP(n) {
  return '$' + Number(n).toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
}

const iconMap = {
  Rentabilidad: DollarSign,
  Ventas: TrendingUp,
  Inventario: BarChart3,
};

const colorMap = {
  Rentabilidad: { bg: 'bg-emerald-50', text: 'text-emerald-700', accent: 'from-emerald-500 to-emerald-600' },
  Ventas: { bg: 'bg-blue-50', text: 'text-blue-700', accent: 'from-blue-500 to-blue-600' },
  Inventario: { bg: 'bg-violet-50', text: 'text-violet-700', accent: 'from-violet-500 to-violet-600' },
};

export default function Indicadores() {
  const [indicadores, setIndicadores] = useState([]);
  const [valores, setValores] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    try {
      const [ind, val, cat] = await Promise.all([
        getIndicadores().catch(() => []),
        getValoresActuales().catch(() => []),
        getCategorias().catch(() => []),
      ]);
      setIndicadores(ind);
      setValores(val);
      setCategorias(cat);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  if (error) return (
    <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="flex items-center gap-2 text-red-600">
      <AlertCircle className="w-5 h-5" /><span>{error}</span>
    </motion.div>
  );

  const getValor = (id) => valores.find(v => v.indicador?.id === id);

  const barData = {
    labels: indicadores.map(i => i.nombre),
    datasets: [{
      label: 'Valor Actual',
      data: indicadores.map(i => {
        const v = getValor(i.id);
        const val = parseFloat(v?.valor);
        return isNaN(val) ? 0 : val;
      }),
      backgroundColor: ['#10b981', '#8b5cf6', '#3b82f6', '#f59e0b'],
      borderRadius: 8,
    }],
  };

  const lineData = valores.length > 0 ? {
    labels: valores.slice(0, 10).map(v => v.periodo || v.fechaCalculo?.slice(0, 10)).reverse(),
    datasets: [{
      label: 'Evolución',
      data: valores.slice(0, 10).map(v => {
        const val = parseFloat(v.valor);
        return isNaN(val) ? 0 : val;
      }).reverse(),
      borderColor: '#10b981',
      backgroundColor: 'rgba(16, 185, 129, 0.1)',
      fill: true,
      tension: 0.4,
      pointBackgroundColor: '#10b981',
      pointRadius: 4,
    }],
  } : null;

  return (
    <motion.div variants={container} initial="hidden" animate="show">
      <motion.div variants={itemAnim} className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div className="flex items-center gap-3">
          <div className="p-2.5 bg-violet-100 rounded-xl"><BarChart3 className="w-6 h-6 text-violet-600" /></div>
          <div>
            <h2 className="text-2xl font-bold text-slate-800">Indicadores KPI</h2>
            <p className="text-sm text-slate-400">Monitoreo de rendimiento y métricas clave</p>
          </div>
        </div>
        <motion.button
          whileHover={{ scale: 1.03 }} whileTap={{ scale: 0.95 }}
          onClick={load}
          disabled={loading}
          className="flex items-center gap-2 px-4 py-2.5 bg-white border border-slate-200 rounded-xl text-sm font-medium text-slate-600 hover:bg-slate-50 transition-all shadow-sm"
        >
          <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          {loading ? 'Cargando...' : 'Actualizar'}
        </motion.button>
      </motion.div>

      {loading ? (
        <div className="flex justify-center py-20"><div className="animate-spin rounded-full h-10 w-10 border-4 border-violet-600 border-t-transparent" /></div>
      ) : (
        <>
          {/* Categorías */}
          <motion.div variants={container} className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
            {categorias.map(cat => {
              const Icon = iconMap[cat.nombre] || BarChart3;
              const colors = colorMap[cat.nombre] || { bg: 'bg-slate-50', text: 'text-slate-700', accent: 'from-slate-500 to-slate-600' };
              return (
                <motion.div
                  key={cat.id}
                  variants={itemAnim}
                  whileHover={{ y: -4, transition: { type: 'spring', stiffness: 200 } }}
                  className={`${colors.bg} rounded-xl p-5 shadow-sm border border-white/50`}
                >
                  <div className="flex items-center gap-2 mb-4">
                    <div className={`p-2 rounded-lg bg-gradient-to-br ${colors.accent} shadow-lg`}>
                      <Icon className="w-5 h-5 text-white" />
                    </div>
                    <h3 className={`font-semibold ${colors.text}`}>{cat.nombre}</h3>
                  </div>
                  <p className="text-xs text-slate-400 mb-4">{cat.descripcion}</p>
                  <div className="space-y-2">
                    {indicadores.filter(i => i.categoria?.id === cat.id).map(ind => {
                      const v = getValor(ind.id);
                      return (
                        <motion.div
                          key={ind.id}
                          initial={{ opacity: 0, x: -10 }}
                          animate={{ opacity: 1, x: 0 }}
                          className="p-3 bg-white/80 rounded-xl shadow-sm"
                        >
                          <div className="flex items-center justify-between">
                            <span className="text-sm font-medium text-slate-700">{ind.nombre}</span>
                            {v ? (
                              <span className="text-lg font-bold text-emerald-600">
                                {ind.unidad === 'CLP' ? formatCLP(v.valor) : Number(v.valor).toFixed(1) + '%'}
                              </span>
                            ) : (
                              <span className="text-xs text-slate-400">—</span>
                            )}
                          </div>
                          <div className="flex items-center gap-2 mt-1">
                            <span className="text-xs text-slate-400">{ind.formula}</span>
                            <span className="text-xs text-slate-300">·</span>
                            <span className="text-xs text-slate-400">{ind.frecuencia}</span>
                          </div>
                        </motion.div>
                      );
                    })}
                  </div>
                </motion.div>
              );
            })}
          </motion.div>

          {/* Charts */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
            <motion.div variants={itemAnim} className="glass-card rounded-xl p-5">
              <h3 className="text-lg font-semibold text-slate-800 mb-4">Comparativa de Indicadores</h3>
              <div className="h-[250px]">
                <Bar data={barData} options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  plugins: { legend: { display: false } },
                }} />
              </div>
            </motion.div>

            {lineData && (
              <motion.div variants={itemAnim} className="glass-card rounded-xl p-5">
                <h3 className="text-lg font-semibold text-slate-800 mb-4">Evolución de Valores</h3>
                <div className="h-[250px]">
                  <Line data={lineData} options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { display: false } },
                    scales: { y: { beginAtZero: true } },
                  }} />
                </div>
              </motion.div>
            )}
          </div>

          {/* Historial */}
          {valores.length > 0 && (
            <motion.div variants={itemAnim} className="glass-card rounded-xl p-5">
              <h3 className="text-lg font-semibold text-slate-800 mb-4">Historial de Valores <span className="text-slate-400 text-sm font-normal">({valores.length} registros)</span></h3>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="bg-slate-50/80 text-slate-600 uppercase text-xs">
                      <th className="text-left p-4">Indicador</th>
                      <th className="text-right p-4">Valor</th>
                      <th className="text-left p-4">Periodo</th>
                      <th className="text-left p-4">Fecha Cálculo</th>
                    </tr>
                  </thead>
                  <tbody>
                    {valores.map(v => (
                      <tr key={v.id} className="border-t border-slate-100 hover:bg-slate-50/30 transition-colors">
                        <td className="p-4 font-medium">{v.indicador?.nombre}</td>
                        <td className="p-4 text-right font-bold text-emerald-600">{v.valor}</td>
                        <td className="p-4 text-slate-500">{v.periodo}</td>
                        <td className="p-4 text-slate-500">{v.fechaCalculo}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </motion.div>
          )}
        </>
      )}
    </motion.div>
  );
}
