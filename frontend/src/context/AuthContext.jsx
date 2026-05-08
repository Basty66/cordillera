import { createContext, useContext, useState } from 'react';
import { login as apiLogin } from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const stored = localStorage.getItem('user');
      return stored ? JSON.parse(stored) : null;
    } catch {
      localStorage.removeItem('user');
      return null;
    }
  });
  const [token, setToken] = useState(() => localStorage.getItem('token'));

  const login = async (username, password) => {
    const res = await apiLogin(username, password);
    setToken(res.token);
    setUser({ username: res.username, rol: res.rol, nombre: res.nombre });
    localStorage.setItem('token', res.token);
    localStorage.setItem('user', JSON.stringify({ username: res.username, rol: res.rol, nombre: res.nombre }));
    return res;
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const hasRole = (...roles) => user && roles.includes(user.rol);

  return (
    <AuthContext.Provider value={{ user, token, login, logout, hasRole, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
