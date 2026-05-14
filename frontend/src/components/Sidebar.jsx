import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard, ShoppingCart, Package, Store, Users, BarChart3,
  Shield, LogOut, ChevronDown, TicketCheck, FileText, Server,
  Activity, UserCircle, Wifi
} from 'lucide-react';
import { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

const API = import.meta.env.VITE_API_URL || '/api';

const services = [
  { id: 'ventas', name: 'ms-ventas', endpoint: `${API}/productos`, icon: ShoppingCart },
  { id: 'datos-org', name: 'ms-datos-org', endpoint: `${API}/empleados/count`, icon: Users },
  { id: 'indicadores', name: 'ms-indicadores', endpoint: `${API}/indicadores/categorias`, icon: BarChart3 },
  { id: 'bff', name: 'bff', endpoint: `${API}/auth/health`, icon: Activity },
  { id: 'gateway', name: 'api-gateway', endpoint: `${API.replace('/api', '')}/health`, icon: Wifi },
];

const navGroups = [
  {
    section: 'General', serviceLabel: 'BFF',
    items: [
      { to: '/', label: 'Dashboard', icon: LayoutDashboard, end: true, roles: ['ADMIN', 'VENDEDOR', 'BODEGA'] },
    ],
  },
  {
    section: 'Comercial', serviceLabel: 'ms-ventas',
    items: [
      { to: '/ventas', label: 'Ventas', icon: ShoppingCart, roles: ['ADMIN', 'VENDEDOR'] },
      { to: '/productos', label: 'Productos', icon: Package, roles: ['ADMIN', 'VENDEDOR', 'BODEGA'] },
      { to: '/sucursales', label: 'Sucursales', icon: Store, roles: ['ADMIN'] },
    ],
  },
  {
    section: 'Organización', serviceLabel: 'ms-datos-org',
    items: [
      { to: '/empleados', label: 'Empleados', icon: Users, roles: ['ADMIN'] },
    ],
  },
  {
    section: 'Analítica', serviceLabel: 'ms-indicadores',
    items: [
      { to: '/indicadores', label: 'Indicadores', icon: BarChart3, roles: ['ADMIN', 'VENDEDOR', 'BODEGA'] },
    ],
  },
  {
    section: 'Sistema', serviceLabel: 'BFF',
    items: [
      { to: '/tickets', label: 'Tickets', icon: TicketCheck, roles: ['ADMIN', 'VENDEDOR'] },
      { to: '/reportes', label: 'Reportes', icon: FileText, roles: ['ADMIN'] },
    ],
  },
];

const badgeColors = {
  'ms-ventas': 'bg-blue-500/15 text-blue-400 border-blue-500/25',
  'ms-datos-org': 'bg-amber-500/15 text-amber-400 border-amber-500/25',
  'ms-indicadores': 'bg-violet-500/15 text-violet-400 border-violet-500/25',
  'BFF': 'bg-cyan-500/15 text-cyan-400 border-cyan-500/25',
};

export default function Sidebar({ onClose }) {
  const { user, logout, hasRole } = useAuth();
  const navigate = useNavigate();
  const [showUserMenu, setShowUserMenu] = useState(false);
  const [showServices, setShowServices] = useState(false);
  const [svcStatus, setSvcStatus] = useState({});
  const mounted = useRef(true);

  useEffect(() => {
    mounted.current = true;
    const check = async () => {
      const results = {};
      for (const s of services) {
        try {
          const ctrl = new AbortController();
          const tid = setTimeout(() => ctrl.abort(), 3000);
          const res = await fetch(s.endpoint, { signal: ctrl.signal });
          clearTimeout(tid);
          results[s.id] = res.ok ? 'online' : 'offline';
        } catch {
          results[s.id] = 'offline';
        }
      }
      if (mounted.current) setSvcStatus(results);
    };
    check();
    const interval = setInterval(check, 30000);
    return () => { mounted.current = false; clearInterval(interval); };
  }, []);

  const handleLogout = () => { logout(); navigate('/login'); };

  const filteredGroups = navGroups
    .map(g => ({ ...g, items: g.items.filter(l => l.roles.some(r => hasRole(r))) }))
    .filter(g => g.items.length > 0);

  if (hasRole('ADMIN')) {
    filteredGroups.push({
      section: 'Admin', serviceLabel: 'BFF',
      items: [{ to: '/admin/usuarios', label: 'Usuarios', icon: Shield, roles: ['ADMIN'] }],
    });
  }

  return (
    <aside className="h-full w-64 bg-slate-900 text-white flex flex-col shrink-0 border-r border-slate-700/30 relative">
      <div className="absolute top-0 left-0 right-0 h-[2px] bg-gradient-to-r from-transparent via-emerald-500 to-transparent animate-shimmer" />

      {/* Logo */}
      <div className="px-4 py-3.5 border-b border-slate-700/30">
        <div className="flex items-center gap-2.5">
          <div className="p-1.5 bg-emerald-500/15 rounded-lg neon-glow-sm shrink-0">
            <LayoutDashboard className="w-4 h-4 text-emerald-400" />
          </div>
          <div className="min-w-0">
            <h1 className="text-sm font-semibold tracking-tight truncate">Grupo Cordillera</h1>
            <p className="text-[9px] text-emerald-500/50 font-medium tracking-widest uppercase" style={{textShadow:'0 0 8px rgba(16,185,129,0.3)'}}>
              Monitoreo Inteligente
            </p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 overflow-y-auto overflow-x-hidden py-2 px-2 space-y-3 scrollbar-thin">
        {filteredGroups.map(group => (
          <div key={group.section}>
            <div className="flex items-center justify-between px-2 py-1">
              <span className="text-[10px] font-semibold uppercase tracking-widest text-slate-500">
                {group.section}
              </span>
              <span className={`text-[7px] font-mono px-1.5 py-[2px] rounded border ${badgeColors[group.serviceLabel] || 'bg-slate-700/40 text-slate-400 border-slate-600/30'}`}>
                {group.serviceLabel}
              </span>
            </div>
            <div className="space-y-[2px]">
              {group.items.map(item => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.end}
                  onClick={onClose}
                  className={({ isActive }) =>
                    `group relative flex items-center gap-2.5 px-3 py-2 rounded-lg text-xs font-medium transition-all duration-300 ${
                      isActive ? 'text-white' : 'text-slate-400 hover:text-white'
                    }`
                  }
                >
                  {({ isActive }) => (
                    <>
                      {isActive && (
                        <motion.div
                          layoutId="activeNav"
                          className="absolute inset-0 bg-gradient-to-r from-emerald-600/90 to-emerald-500/80 rounded-lg neon-glow-sm"
                          transition={{ type: 'spring', stiffness: 300, damping: 30 }}
                        />
                      )}
                      {!isActive && (
                        <div className="absolute inset-0 rounded-lg transition-all duration-200 hover:bg-slate-800/60" />
                      )}
                      <div className={`relative z-10 transition-transform duration-200 group-hover:scale-110 ${
                        isActive ? 'text-white' : 'text-slate-500 group-hover:text-emerald-400'
                      }`}>
                        <item.icon className="w-4 h-4" />
                      </div>
                      <span className={`relative z-10 ${isActive ? 'font-semibold' : ''}`}>
                        {item.label}
                      </span>
                      {isActive && (
                        <motion.div
                          initial={{ scale: 0 }}
                          animate={{ scale: 1 }}
                          className="relative z-10 ml-auto w-1.5 h-1.5 rounded-full bg-emerald-300 shadow-lg shadow-emerald-400/50"
                        />
                      )}
                    </>
                  )}
                </NavLink>
              ))}
            </div>
          </div>
        ))}
      </nav>

      {/* Services Status */}
      <div className="border-t border-slate-700/30">
        <button
          onClick={() => setShowServices(!showServices)}
          className="flex items-center justify-between w-full px-4 py-2.5 text-[11px] text-slate-500 hover:text-slate-300 hover:bg-slate-800/40 transition-all"
        >
          <span className="flex items-center gap-2">
            <Server className="w-3.5 h-3.5" />
            Microservicios
          </span>
          <motion.div animate={{ rotate: showServices ? 180 : 0 }} transition={{ duration: 0.2 }}>
            <ChevronDown className="w-3 h-3" />
          </motion.div>
        </button>
        <AnimatePresence>
          {showServices && (
            <motion.div
              initial={{ height: 0, opacity: 0 }}
              animate={{ height: 'auto', opacity: 1 }}
              exit={{ height: 0, opacity: 0 }}
              transition={{ duration: 0.2 }}
              className="overflow-hidden"
            >
              <div className="px-3 pb-2 space-y-[2px]">
                {services.map(s => {
                  const status = svcStatus[s.id];
                  const Icon = s.icon;
                  return (
                    <div key={s.id} className="flex items-center gap-2.5 px-2 py-1.5 rounded-lg hover:bg-slate-800/40 transition-colors">
                      <div className={`w-2 h-2 rounded-full shrink-0 ${
                        status === 'online' ? 'bg-emerald-400 shadow-lg shadow-emerald-400/30' :
                        status === 'offline' ? 'bg-red-400' : 'bg-slate-600 animate-pulse'
                      }`} />
                      <Icon className="w-3 h-3 text-slate-500 shrink-0" />
                      <span className="text-[11px] text-slate-400 flex-1 truncate">{s.name}</span>
                    </div>
                  );
                })}
                <div className="flex items-center gap-1.5 pt-1.5 px-2">
                  <div className="w-1.5 h-1.5 rounded-full bg-emerald-400 shadow-lg shadow-emerald-400/30" />
                  <span className="text-[9px] text-emerald-500/60 font-medium">Todos los servicios activos</span>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      {/* User Menu */}
      <div className="relative border-t border-slate-700/30">
        <button
          onClick={() => setShowUserMenu(!showUserMenu)}
          className="group flex items-center gap-2.5 w-full px-3 py-2.5 text-xs text-slate-400 hover:bg-slate-800/60 hover:text-white transition-all duration-200"
        >
          <div className="w-7 h-7 bg-gradient-to-br from-emerald-500 to-emerald-700 rounded-full flex items-center justify-center text-white font-bold text-[11px] shrink-0 shadow-lg shadow-emerald-600/20 group-hover:shadow-emerald-600/40 transition-shadow">
            {user?.nombre?.charAt(0)?.toUpperCase() || 'U'}
          </div>
          <div className="flex-1 text-left min-w-0">
            <p className="text-xs font-medium text-white truncate leading-tight">{user?.nombre || 'Usuario'}</p>
            <p className="text-[9px] text-slate-500 truncate tracking-wide">{user?.rol || ''}</p>
          </div>
          <motion.div animate={{ rotate: showUserMenu ? 180 : 0 }} transition={{ duration: 0.2 }}>
            <ChevronDown className="w-3 h-3 shrink-0" />
          </motion.div>
        </button>

        <AnimatePresence>
          {showUserMenu && (
            <>
              <div className="fixed inset-0 z-10" onClick={() => setShowUserMenu(false)} />
              <motion.div
                initial={{ opacity: 0, y: 10, scale: 0.95 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                exit={{ opacity: 0, y: 10, scale: 0.95 }}
                transition={{ duration: 0.15 }}
                className="absolute bottom-full left-0 right-0 mb-2 bg-slate-800/95 backdrop-blur-xl rounded-xl border border-slate-700/50 shadow-2xl overflow-hidden z-20"
              >
                <div className="px-4 py-3 border-b border-slate-700/50">
                  <p className="text-sm font-medium text-white">{user?.nombre}</p>
                  <p className="text-xs text-slate-400">{user?.email || user?.username}</p>
                </div>
                <button
                  onClick={() => { navigate('/profile'); setShowUserMenu(false); }}
                  className="flex items-center gap-3 w-full px-4 py-3 text-sm text-slate-300 hover:bg-slate-700/50 transition-colors group"
                >
                  <UserCircle className="w-4 h-4 group-hover:scale-110 transition-transform" />
                  Perfil
                </button>
                <button
                  onClick={handleLogout}
                  className="flex items-center gap-3 w-full px-4 py-3 text-sm text-red-400 hover:bg-red-500/10 transition-colors group border-t border-slate-700/30"
                >
                  <LogOut className="w-4 h-4 group-hover:scale-110 transition-transform" />
                  Cerrar Sesión
                </button>
              </motion.div>
            </>
          )}
        </AnimatePresence>
      </div>
    </aside>
  );
}
