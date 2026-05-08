import { useEffect, useState } from 'react';
import { getReportesDashboard, getReportesTickets, getDashboard } from '../api/client';
import { motion } from 'framer-motion';
import {
  FileText, Download, BarChart3, TicketCheck, Users, AlertTriangle,
  TrendingUp, ShoppingCart
} from 'lucide-react';
import { Bar, Doughnut } from 'react-chartjs-2';

const container = {
  hidden: { opacity: 0 },
  show: { opacity: 1, transition: { staggerChildren: 0.06 } },
};

const itemAnim = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0 },
};

export default function Reportes() {
  const [reportData, setReportData] = useState(null);
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('general');

  useEffect(() => {
    Promise.all([
      getReportesDashboard().catch(() => null),
      getDashboard().catch(() => null),
    ]).then(([rep, dash]) => {
      setReportData(rep);
      setDashboardData(dash);
    }).finally(() => setLoading(false));
  }, []);

  const exportJSON = (data, filename) => {
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
  };

  const exportCSV = (data, filename) => {
    if (!data?.length) return;
    const headers = Object.keys(data[0]);
    const csv = [headers.join(','), ...data.map(r => headers.map(h => `"${r[h] || ''}"`).join(','))].join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
  };

  if (loading) return (
    <div className="flex justify-center py-20"><div className="animate-spin rounded-full h-10 w-10 border-4 border-emerald-600 border-t-transparent" /></div>
  );

  const barData = dashboardData ? {
    labels: ['Ventas', 'Empleados', 'Sucursales'],
    datasets: [{
      label: 'Totales',
      data: [
        dashboardData.ventas?.totalVentas || 0,
        dashboardData.totalEmpleados || 0,
        dashboardData.totalSucursales || 0,
      ],
      backgroundColor: ['#3b82f6', '#10b981', '#f59e0b'],
      borderRadius: 8,
    }],
  } : null;

  const ticketBarData = reportData ? {
    labels: ['Abiertos', 'En Progreso', 'Resueltos', 'Cerrados'],
    datasets: [{
      label: 'Tickets',
      data: [
        reportData.ticketsAbiertos || 0,
        reportData.ticketsEnProgreso || 0,
        reportData.ticketsResueltos || 0,
        reportData.ticketsCerrados || 0,
      ],
      backgroundColor: ['#3b82f6', '#f59e0b', '#10b981', '#94a3b8'],
      borderRadius: 8,
    }],
  } : null;

  return (
    <motion.div variants={container} initial="hidden" animate="show">
      <motion.div variants={itemAnim} className="flex items-center gap-3 mb-6">
        <div className="p-2.5 bg-emerald-100 rounded-xl"><FileText className="w-6 h-6 text-emerald-600" /></div>
        <div>
          <h2 className="text-2xl font-bold text-slate-800">Reportes</h2>
          <p className="text-sm text-slate-400">Análisis y exportación de datos</p>
        </div>
      </motion.div>

      {/* Tabs */}
      <motion.div variants={itemAnim} className="flex gap-2 mb-6 overflow-x-auto pb-2">
        {[
          { id: 'general', label: 'General', icon: BarChart3 },
          { id: 'tickets', label: 'Tickets', icon: TicketCheck },
          { id: 'ventas', label: 'Ventas', icon: ShoppingCart },
        ].map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-medium transition-all whitespace-nowrap ${
              activeTab === tab.id
                ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-600/20'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            <tab.icon className="w-4 h-4" />
            {tab.label}
          </button>
        ))}
      </motion.div>

      {activeTab === 'general' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <motion.div variants={itemAnim} className="glass-card rounded-xl p-5">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-semibold text-slate-800">Resumen General</h3>
              <button onClick={() => exportJSON(dashboardData, 'resumen-general.json')}
                className="flex items-center gap-1 text-xs text-emerald-600 hover:text-emerald-500">
                <Download className="w-3.5 h-3.5" /> Exportar
              </button>
            </div>
            {barData && <div className="h-[250px]"><Bar data={barData} options={{ responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } }} /></div>}
          </motion.div>

          {reportData && (
            <motion.div variants={itemAnim} className="glass-card rounded-xl p-5">
              <h3 className="font-semibold text-slate-800 mb-4">Estadísticas del Sistema</h3>
              <div className="space-y-3">
                {[
                  { icon: TicketCheck, label: 'Total Tickets', value: reportData.totalTickets, color: 'bg-violet-500' },
                  { icon: Users, label: 'Usuarios Registrados', value: reportData.totalUsuarios, color: 'bg-blue-500' },
                  { icon: AlertTriangle, label: 'Tickets Críticos', value: reportData.ticketsCriticos, color: 'bg-red-500' },
                  { icon: TrendingUp, label: 'Tickets Resueltos', value: reportData.ticketsResueltos, color: 'bg-emerald-500' },
                ].map(s => (
                  <div key={s.label} className="flex items-center justify-between p-3 bg-white/60 rounded-xl">
                    <div className="flex items-center gap-3">
                      <div className={`p-2 rounded-lg ${s.color}`}>
                        <s.icon className="w-4 h-4 text-white" />
                      </div>
                      <span className="text-sm font-medium text-slate-600">{s.label}</span>
                    </div>
                    <span className="text-lg font-bold text-slate-800">{s.value}</span>
                  </div>
                ))}
              </div>
            </motion.div>
          )}
        </div>
      )}

      {activeTab === 'tickets' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <motion.div variants={itemAnim} className="glass-card rounded-xl p-5">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-semibold text-slate-800">Tickets por Estado</h3>
              <div className="flex gap-2">
                <button onClick={() => exportJSON(reportData, 'reporte-tickets.json')}
                  className="flex items-center gap-1 text-xs text-emerald-600 hover:text-emerald-500">
                  <Download className="w-3.5 h-3.5" /> JSON
                </button>
              </div>
            </div>
            {ticketBarData && (
              <div className="h-[250px]">
                <Bar data={ticketBarData} options={{
                  responsive: true,
                  maintainAspectRatio: false,
                  plugins: { legend: { display: false } },
                  scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },
                }} />
              </div>
            )}
          </motion.div>

          {reportData?.porPrioridad && (
            <motion.div variants={itemAnim} className="glass-card rounded-xl p-5">
              <h3 className="font-semibold text-slate-800 mb-4">Distribución por Prioridad</h3>
              <div className="h-[250px] flex items-center justify-center">
                <Doughnut data={{
                labels: Object.keys(reportData.porPrioridad).map(p =>
                  ({ CRITICA: 'Crítica', ALTA: 'Alta', MEDIA: 'Media', BAJA: 'Baja' })[p] || p
                ),
                datasets: [{
                  data: Object.values(reportData.porPrioridad),
                  backgroundColor: ['#ef4444', '#f97316', '#3b82f6', '#94a3b8'],
                  borderWidth: 0,
                }],
              }} options={{ responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }} />
              </div>
            </motion.div>
          )}
        </div>
      )}

      {activeTab === 'ventas' && dashboardData?.ventas && (
        <motion.div variants={itemAnim} className="glass-card rounded-xl p-5">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-slate-800">Resumen de Ventas</h3>
            <button onClick={() => exportJSON(dashboardData.ventas, 'reporte-ventas.json')}
              className="flex items-center gap-1 text-xs text-emerald-600 hover:text-emerald-500">
              <Download className="w-3.5 h-3.5" /> Exportar
            </button>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            {[
              { label: 'Total Ventas', value: dashboardData.ventas.totalVentas, icon: ShoppingCart },
              { label: 'Monto Total', value: '$' + Number(dashboardData.ventas.montoTotal).toLocaleString('es-CL'), icon: TrendingUp },
              { label: 'Ticket Promedio', value: '$' + Number(dashboardData.ventas.promedioVenta).toLocaleString('es-CL'), icon: BarChart3 },
            ].map(s => (
              <div key={s.label} className="p-4 bg-white/60 rounded-xl text-center">
                <s.icon className="w-5 h-5 mx-auto mb-2 text-emerald-600" />
                <p className="text-2xl font-bold text-slate-800">{s.value}</p>
                <p className="text-xs text-slate-500 mt-1">{s.label}</p>
              </div>
            ))}
          </div>
        </motion.div>
      )}
    </motion.div>
  );
}
