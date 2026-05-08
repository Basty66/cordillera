import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ roles, children }) {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (roles && !roles.includes(user?.rol)) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center text-slate-500">
          <p className="text-5xl mb-3">🔒</p>
          <p className="text-lg font-semibold">Acceso Denegado</p>
          <p className="text-sm">No tienes permisos para ver esta pagina</p>
        </div>
      </div>
    );
  }

  return children || <Outlet />;
}
