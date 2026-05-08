import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard, ShoppingCart, Package, Store, Users, BarChart3,
  Shield, LogOut, ChevronDown, TicketCheck, FileText
} from 'lucide-react';
import { useState } from 'react';

const links = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard, end: true, roles: ['ADMIN', 'VENDEDOR', 'BODEGA'] },
  { to: '/ventas', label: 'Ventas', icon: ShoppingCart, roles: ['ADMIN', 'VENDEDOR'] },
  { to: '/productos', label: 'Productos', icon: Package, roles: ['ADMIN', 'VENDEDOR', 'BODEGA'] },
  { to: '/sucursales', label: 'Sucursales', icon: Store, roles: ['ADMIN'] },
  { to: '/empleados', label: 'Empleados', icon: Users, roles: ['ADMIN'] },
  { to: '/indicadores', label: 'Indicadores', icon: BarChart3, roles: ['ADMIN', 'VENDEDOR', 'BODEGA'] },
  { to: '/tickets', label: 'Tickets', icon: TicketCheck, roles: ['ADMIN', 'VENDEDOR'] },
  { to: '/reportes', label: 'Reportes', icon: FileText, roles: ['ADMIN'] },
];

export default function Sidebar() {
  const { user, logout, hasRole } = useAuth();
  const navigate = useNavigate();
  const [showUserMenu, setShowUserMenu] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const filteredLinks = links.filter(l => l.roles.some(r => hasRole(r)));

  return (
    <aside className="w-64 bg-slate-900 text-white flex flex-col shrink-0">
      <div className="p-5 border-b border-slate-700/50">
        <div className="flex items-center gap-2">
          <div className="p-1.5 bg-emerald-500/20 rounded-lg">
            <BarChart3 className="w-5 h-5 text-emerald-400" />
          </div>
          <h1 className="text-lg font-semibold">Grupo Cordillera</h1>
        </div>
        <p className="text-xs text-slate-500 mt-1">Plataforma de Monitoreo</p>
      </div>

      <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
        {filteredLinks.map(l => (
          <NavLink
            key={l.to}
            to={l.to}
            end={l.end}
            className={({ isActive }) =>
              `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                isActive
                  ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-600/20'
                  : 'text-slate-300 hover:bg-slate-800 hover:text-white'
              }`
            }
          >
            <l.icon className="w-5 h-5 shrink-0" />
            {l.label}
          </NavLink>
        ))}

        {hasRole('ADMIN') && (
          <>
            <div className="pt-4 pb-1">
              <p className="px-3 text-xs font-semibold uppercase tracking-wider text-slate-600">Administración</p>
            </div>
            <NavLink
              to="/admin/usuarios"
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                  isActive
                    ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-600/20'
                    : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                }`
              }
            >
              <Shield className="w-5 h-5 shrink-0" />
              Usuarios
            </NavLink>
          </>
        )}
      </nav>

      <div className="p-3 border-t border-slate-700/50">
        <div className="relative">
          <button
            onClick={() => setShowUserMenu(!showUserMenu)}
            className="flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-sm text-slate-300 hover:bg-slate-800 hover:text-white transition-all"
          >
            <div className="w-8 h-8 bg-gradient-to-br from-emerald-500 to-emerald-700 rounded-full flex items-center justify-center text-white font-bold text-xs shrink-0">
              {user?.nombre?.charAt(0) || 'U'}
            </div>
            <div className="flex-1 text-left min-w-0">
              <p className="text-sm font-medium text-white truncate">{user?.nombre || 'Usuario'}</p>
              <p className="text-xs text-slate-500 truncate">{user?.rol || ''}</p>
            </div>
            <ChevronDown className="w-4 h-4 shrink-0" />
          </button>

          {showUserMenu && (
            <>
              <div className="fixed inset-0 z-10" onClick={() => setShowUserMenu(false)} />
              <div className="absolute bottom-full left-0 right-0 mb-2 bg-slate-800 rounded-xl border border-slate-700/50 shadow-xl overflow-hidden z-20">
                <div className="px-4 py-3 border-b border-slate-700/50">
                  <p className="text-sm font-medium text-white">{user?.nombre}</p>
                  <p className="text-xs text-slate-400">{user?.email || user?.username}</p>
                </div>
                <button
                  onClick={handleLogout}
                  className="flex items-center gap-3 w-full px-4 py-3 text-sm text-red-400 hover:bg-slate-700/50 transition-colors"
                >
                  <LogOut className="w-4 h-4" />
                  Cerrar Sesión
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </aside>
  );
}
