import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { motion, AnimatePresence } from 'framer-motion';
import {
  LogIn, Eye, EyeOff, User, Lock, Sparkles, Store, TrendingUp,
  ShieldCheck, BarChart3, ArrowRight
} from 'lucide-react';

function Particle({ index }) {
  const x = Math.random() * 100;
  const y = Math.random() * 100;
  const size = Math.random() * 3 + 1;
  const duration = Math.random() * 15 + 10;
  const delay = Math.random() * 10;
  return (
    <motion.div className="absolute rounded-full" style={{
      width: size, height: size, left: `${x}%`, top: `${y}%`,
      background: `radial-gradient(circle, rgba(16,185,129,${Math.random() * 0.2 + 0.05}) 0%, transparent 70%)`,
    }}
    animate={{ y: [0, -20, 0], opacity: [0.1, 0.4, 0.1], scale: [1, 1.3, 1] }}
    transition={{ duration, repeat: Infinity, delay, ease: 'easeInOut' }} />
  );
}

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState({});
  const { login } = useAuth();
  const navigate = useNavigate();
  const [particles] = useState(() => Array.from({ length: 20 }, (_, i) => i));

  const validate = () => {
    const errs = {};
    if (!username.trim()) errs.username = 'El usuario es requerido';
    if (!password.trim()) errs.password = 'La contraseña es requerida';
    else if (password.length < 6) errs.password = 'Mínimo 6 caracteres';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (!validate()) return;
    setLoading(true);
    try {
      await login(username.trim(), password);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.error || 'Credenciales inválidas. Intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const demoUsers = [
    { user: 'admin', pass: 'admin123', role: 'Administrador', color: 'from-emerald-500 to-emerald-600', icon: ShieldCheck },
    { user: 'vendedor', pass: 'ventas123', role: 'Vendedor', color: 'from-blue-500 to-blue-600', icon: TrendingUp },
    { user: 'bodega', pass: 'bodega123', role: 'Bodega', color: 'from-amber-500 to-amber-600', icon: Store },
  ];

  const fillDemo = (u, p) => {
    setUsername(u); setPassword(p); setErrors({}); setError('');
  };

  const features = [
    { icon: BarChart3, text: 'Dashboard en tiempo real' },
    { icon: Store, text: 'Múltiples sucursales' },
    { icon: TrendingUp, text: 'Indicadores económicos' },
    { icon: ShieldCheck, text: 'Seguridad empresarial' },
  ];

  return (
    <div className="min-h-screen flex relative overflow-hidden bg-slate-950">
      {/* ===== LEFT PANEL: Brand / Visual ===== */}
      <div className="hidden lg:flex lg:w-1/2 relative overflow-hidden bg-gradient-to-br from-slate-900 via-emerald-950 to-slate-900">
        <div className="absolute inset-0 opacity-20" style={{
          backgroundImage: `
            radial-gradient(circle at 20% 50%, rgba(16,185,129,.15) 0%, transparent 50%),
            radial-gradient(circle at 80% 20%, rgba(59,130,246,.1) 0%, transparent 50%),
            radial-gradient(circle at 50% 80%, rgba(139,92,246,.1) 0%, transparent 50%)
          `,
        }} />
        <div className="absolute inset-0" style={{
          backgroundImage: 'linear-gradient(rgba(255,255,255,.02) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,.02) 1px, transparent 1px)',
          backgroundSize: '48px 48px',
        }} />
        <div className="absolute -top-40 -right-40 w-[500px] h-[500px] bg-emerald-500/10 rounded-full blur-[120px]" />
        <div className="absolute -bottom-40 -left-40 w-[400px] h-[400px] bg-blue-500/10 rounded-full blur-[100px]" />

        <div className="relative flex flex-col justify-between p-12 w-full">
          <div>
            <div className="flex items-center gap-3 mb-16">
              <div className="p-2.5 bg-emerald-500/15 rounded-xl">
                <BarChart3 className="w-6 h-6 text-emerald-400" />
              </div>
              <div>
                <h2 className="text-lg font-bold text-white tracking-tight">Grupo Cordillera</h2>
                <p className="text-[10px] text-emerald-400/60 font-semibold uppercase tracking-[0.2em]">Monitoreo Inteligente</p>
              </div>
            </div>

            <div className="space-y-10">
              <div>
                <h1 className="text-4xl font-bold text-white leading-tight tracking-tight">
                  Plataforma de<br />
                  <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-emerald-300">Monitoreo Corporativo</span>
                </h1>
                <p className="text-slate-400 mt-3 text-sm leading-relaxed max-w-md">
                  Gestión centralizada de ventas, indicadores, tickets y más.
                  Toda tu operación en un solo lugar con inteligencia integrada.
                </p>
              </div>

              <div className="grid grid-cols-2 gap-3">
                {features.map((f, i) => (
                  <motion.div key={i} initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.3 + i * 0.08 }}
                    className="flex items-center gap-2.5 p-3 bg-white/[0.03] rounded-xl border border-white/[0.06]">
                    <div className="p-1.5 bg-emerald-500/10 rounded-lg"><f.icon className="w-3.5 h-3.5 text-emerald-400" /></div>
                    <span className="text-xs text-slate-300 font-medium">{f.text}</span>
                  </motion.div>
                ))}
              </div>
            </div>
          </div>

          <div className="flex items-center gap-3 text-xs text-slate-600">
            <div className="flex -space-x-2">
              {['#10b981','#3b82f6','#8b5cf6','#f59e0b'].map((c, i) => (
                <div key={i} className="w-7 h-7 rounded-full border-2 border-slate-800" style={{ background: c }} />
              ))}
            </div>
            <span>Confianza de +2,000 empresas chilenas</span>
          </div>
        </div>
      </div>

      {/* ===== RIGHT PANEL: Login Form ===== */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-4 sm:p-8 relative bg-gradient-to-br from-slate-950 via-slate-900 to-slate-950">
        <div className="absolute inset-0 opacity-[0.03]" style={{
          backgroundImage: 'radial-gradient(circle at 2px 2px, rgba(255,255,255,.3) 1px, transparent 0)',
          backgroundSize: '24px 24px',
        }} />
        {particles.map(i => <Particle key={i} index={i} />)}

        <motion.div initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.5 }} className="w-full max-w-sm relative z-10">
          <div className="text-center mb-8 lg:hidden">
            <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ type: 'spring', stiffness: 200, damping: 15 }}
              className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-2xl shadow-2xl shadow-emerald-500/30 mb-4">
              <BarChart3 className="w-8 h-8 text-white" />
            </motion.div>
            <h1 className="text-2xl font-bold text-white">Grupo Cordillera</h1>
            <p className="text-emerald-400/60 text-xs mt-1 font-medium tracking-wider uppercase">Monitoreo Inteligente</p>
          </div>

          <div className="relative">
            <div className="absolute inset-0 bg-gradient-to-r from-emerald-500/8 to-blue-500/8 rounded-2xl blur-xl" />
            <div className="relative bg-white/[0.03] backdrop-blur-xl rounded-2xl shadow-2xl border border-white/[0.06] p-6 sm:p-8">
              <div className="mb-6">
                <h2 className="text-lg font-semibold text-white">Acceder</h2>
                <p className="text-sm text-slate-500 mt-0.5">Ingresa tus credenciales</p>
              </div>

              <AnimatePresence>
                {error && (
                  <motion.div initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: 'auto' }} exit={{ opacity: 0, height: 0 }}
                    className="mb-4 p-3 bg-red-500/10 border border-red-500/20 rounded-xl text-sm text-red-400 flex items-center gap-2 overflow-hidden">
                    <div className="w-1.5 h-1.5 bg-red-500 rounded-full shrink-0 animate-pulse" />{error}
                  </motion.div>
                )}
              </AnimatePresence>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1.5">Usuario</label>
                  <div className="relative">
                    <User className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                    <input type="text" value={username} onChange={e => { setUsername(e.target.value); setErrors({ ...errors, username: '' }); }}
                      className={`w-full pl-10 pr-4 py-2.5 bg-white/[0.05] border rounded-xl text-sm text-white placeholder-slate-600 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 focus:border-emerald-500/50 transition-all ${errors.username ? 'border-red-400/40' : 'border-white/[0.08] hover:border-white/[0.15]'}`}
                      placeholder="Ingresa tu usuario" />
                  </div>
                  {errors.username && <p className="text-xs text-red-400 mt-1">{errors.username}</p>}
                </div>

                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1.5">Contraseña</label>
                  <div className="relative">
                    <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500" />
                    <input type={showPassword ? 'text' : 'password'} value={password}
                      onChange={e => { setPassword(e.target.value); setErrors({ ...errors, password: '' }); }}
                      className={`w-full pl-10 pr-12 py-2.5 bg-white/[0.05] border rounded-xl text-sm text-white placeholder-slate-600 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 focus:border-emerald-500/50 transition-all ${errors.password ? 'border-red-400/40' : 'border-white/[0.08] hover:border-white/[0.15]'}`}
                      placeholder="Ingresa tu contraseña" />
                    <button type="button" onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-300 transition-colors p-0.5">
                      {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                  {errors.password && <p className="text-xs text-red-400 mt-1">{errors.password}</p>}
                </div>

                <motion.button whileHover={{ scale: 1.01 }} whileTap={{ scale: 0.99 }} type="submit" disabled={loading}
                  className="w-full relative flex items-center justify-center gap-2 bg-gradient-to-r from-emerald-500 to-emerald-400 hover:from-emerald-400 hover:to-emerald-300 text-slate-900 font-semibold py-2.5 rounded-xl transition-all shadow-lg shadow-emerald-500/20 disabled:opacity-50 disabled:cursor-not-allowed mt-1 overflow-hidden group">
                  <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent -translate-x-full group-hover:translate-x-full transition-transform duration-700" />
                  {loading ? (
                    <motion.div animate={{ rotate: 360 }} transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}>
                      <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none">
                        <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" className="opacity-25" />
                        <path d="M4 12a8 8 0 018-8" stroke="currentColor" strokeWidth="4" strokeLinecap="round" className="opacity-75" />
                      </svg>
                    </motion.div>
                  ) : (<><LogIn className="w-4 h-4" /> Ingresar</>)}
                </motion.button>
              </form>

              <div className="mt-6 pt-5 border-t border-white/[0.06]">
                <p className="text-[10px] font-semibold text-slate-600 uppercase tracking-widest text-center mb-2.5">Acceso rápido de prueba</p>
                <div className="grid grid-cols-3 gap-2">
                  {demoUsers.map(d => (
                    <motion.button key={d.user} whileHover={{ y: -2 }} whileTap={{ scale: 0.97 }}
                      onClick={() => fillDemo(d.user, d.pass)}
                      className="p-2 bg-white/[0.03] hover:bg-white/[0.06] rounded-xl text-center transition-all border border-white/[0.04] hover:border-white/[0.1] group">
                      <div className={`w-7 h-7 bg-gradient-to-br ${d.color} rounded-lg mx-auto mb-1 flex items-center justify-center shadow-lg`}>
                        <d.icon className="w-3.5 h-3.5 text-white" />
                      </div>
                      <p className="text-emerald-400 font-bold text-xs">{d.user}</p>
                      <p className="text-slate-600 text-[9px] leading-tight">{d.role}</p>
                    </motion.button>
                  ))}
                </div>
              </div>
            </div>
          </div>

          <p className="text-center text-[10px] text-slate-700 mt-5 tracking-wide">
            &copy; 2026 Grupo Cordillera &mdash; Plataforma de Monitoreo Inteligente
          </p>
        </motion.div>
      </div>
    </div>
  );
}
