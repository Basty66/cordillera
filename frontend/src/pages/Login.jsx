import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { motion, AnimatePresence } from 'framer-motion';
import {
  LogIn, Eye, EyeOff, User, Lock, Sparkles, Store, TrendingUp,
  ShieldCheck, BarChart3, ArrowRight, Hexagon, Zap, Globe
} from 'lucide-react';

function AuroraBackground() {
  return (
    <div className="absolute inset-0 overflow-hidden">
      <div className="absolute inset-0 bg-slate-950" />
      <motion.div className="absolute -top-40 -left-40 w-[600px] h-[600px] rounded-full"
        style={{ background: 'radial-gradient(circle, rgba(16,185,129,0.15) 0%, transparent 60%)' }}
        animate={{ x: [0, 30, -20, 0], y: [0, -20, 30, 0] }}
        transition={{ duration: 20, repeat: Infinity, ease: 'easeInOut' }}
      />
      <motion.div className="absolute -bottom-40 -right-40 w-[500px] h-[500px] rounded-full"
        style={{ background: 'radial-gradient(circle, rgba(59,130,246,0.12) 0%, transparent 60%)' }}
        animate={{ x: [0, -30, 20, 0], y: [0, 30, -20, 0] }}
        transition={{ duration: 25, repeat: Infinity, ease: 'easeInOut' }}
      />
      <motion.div className="absolute top-1/3 left-1/3 w-[300px] h-[300px] rounded-full"
        style={{ background: 'radial-gradient(circle, rgba(168,85,247,0.1) 0%, transparent 60%)' }}
        animate={{ scale: [1, 1.2, 1], opacity: [0.3, 0.6, 0.3] }}
        transition={{ duration: 15, repeat: Infinity, ease: 'easeInOut' }}
      />
      <div className="absolute inset-0 opacity-[0.03]" style={{
        backgroundImage: 'radial-gradient(circle at 1px 1px, rgba(255,255,255,0.6) 1px, transparent 0)',
        backgroundSize: '32px 32px',
      }} />
      <div className="absolute inset-0" style={{
        background: 'linear-gradient(180deg, transparent 0%, rgba(8,14,26,0.8) 100%)',
      }} />
    </div>
  );
}

function FloatingOrbs() {
  return (
    <>
      {[...Array(8)].map((_, i) => (
        <motion.div key={i} className="absolute rounded-full pointer-events-none"
          style={{
            width: Math.random() * 4 + 2,
            height: Math.random() * 4 + 2,
            left: `${Math.random() * 100}%`,
            top: `${Math.random() * 100}%`,
            background: `rgba(16,185,129,${Math.random() * 0.3 + 0.1})`,
            boxShadow: `0 0 ${Math.random() * 6 + 2}px rgba(16,185,129,0.3)`,
          }}
          animate={{
            y: [0, -30 - Math.random() * 30, 0],
            opacity: [0.2, 0.6 - Math.random() * 0.2, 0.2],
          }}
          transition={{
            duration: Math.random() * 8 + 6,
            repeat: Infinity,
            delay: Math.random() * 5,
            ease: 'easeInOut',
          }}
        />
      ))}
    </>
  );
}

function AnimatedBrandIcon({ icon: Icon, delay = 0, x = 0, y = 0 }) {
  return (
    <motion.div
      initial={{ opacity: 0, scale: 0 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ delay, type: 'spring', stiffness: 100, damping: 15 }}
      className="absolute"
      style={{ left: `${50 + x}%`, top: `${50 + y}%` }}
    >
      <motion.div
        animate={{ y: [0, -6, 0], rotate: [0, 5, -5, 0] }}
        transition={{ duration: 4, repeat: Infinity, delay: delay + 0.5 }}
        className="p-3 bg-white/[0.04] rounded-2xl border border-white/[0.06] backdrop-blur-sm"
      >
        <Icon className="w-5 h-5 text-emerald-400" />
      </motion.div>
    </motion.div>
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

  return (
    <div className="min-h-screen flex relative overflow-hidden">
      <AuroraBackground />
      <FloatingOrbs />

      {/* Left Panel */}
      <div className="hidden lg:flex lg:w-1/2 relative items-center justify-center">
        <div className="relative w-full max-w-lg px-12">
          <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6 }}>
            <div className="flex items-center gap-3 mb-12">
              <motion.div initial={{ scale: 0, rotate: -180 }} animate={{ scale: 1, rotate: 0 }}
                transition={{ type: 'spring', stiffness: 150, damping: 15 }}
                className="p-3 bg-gradient-to-br from-emerald-500 to-emerald-600 rounded-2xl shadow-2xl shadow-emerald-500/30">
                <Hexagon className="w-6 h-6 text-white" />
              </motion.div>
              <div>
                <h2 className="text-lg font-bold text-white tracking-tight">Grupo Cordillera</h2>
                <p className="text-[10px] text-emerald-400/60 font-semibold uppercase tracking-[0.2em]">Monitoreo Inteligente</p>
              </div>
            </div>

            <div className="relative h-[320px]">
              <div className="absolute inset-0 flex items-center justify-center">
                <motion.div animate={{ scale: [1, 1.03, 1] }} transition={{ duration: 6, repeat: Infinity }}
                  className="w-48 h-48 bg-gradient-to-br from-emerald-500/20 via-emerald-400/10 to-transparent rounded-full blur-[60px]" />
              </div>
              <AnimatedBrandIcon icon={BarChart3} delay={0.2} x={-25} y={-20} />
              <AnimatedBrandIcon icon={Zap} delay={0.4} x={25} y={-15} />
              <AnimatedBrandIcon icon={Globe} delay={0.6} x={-20} y={20} />
              <AnimatedBrandIcon icon={ShieldCheck} delay={0.8} x={20} y={25} />

              <motion.div initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.6 }}
                className="absolute bottom-0 left-0 right-0 text-center">
                <h1 className="text-4xl font-bold text-white leading-tight tracking-tight mb-3">
                  Plataforma de<br />
                  <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 via-emerald-300 to-blue-400">
                    Monitoreo Corporativo
                  </span>
                </h1>
                <p className="text-slate-400 text-sm leading-relaxed max-w-md mx-auto">
                  Gestión centralizada de ventas, indicadores y tickets.
                  Toda tu operación con inteligencia integrada.
                </p>
              </motion.div>
            </div>
          </motion.div>
        </div>
      </div>

      {/* Right Panel */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-4 sm:p-8 relative">
        <motion.div
          initial={{ opacity: 0, y: 40, scale: 0.96 }}
          animate={{ opacity: 1, y: 0, scale: 1 }}
          transition={{ duration: 0.6, ease: [0.16, 1, 0.3, 1] }}
          className="w-full max-w-sm relative z-10"
        >
          {/* Mobile logo */}
          <div className="text-center mb-8 lg:hidden">
            <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }}
              transition={{ type: 'spring', stiffness: 200, damping: 15 }}
              className="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-2xl shadow-2xl shadow-emerald-500/30 mb-4">
              <Hexagon className="w-8 h-8 text-white" />
            </motion.div>
            <h1 className="text-2xl font-bold text-white">Grupo Cordillera</h1>
            <p className="text-emerald-400/60 text-xs mt-1 font-medium tracking-wider uppercase">Monitoreo Inteligente</p>
          </div>

          {/* Glass card */}
          <div className="relative">
            <div className="absolute -inset-1 bg-gradient-to-r from-emerald-500/20 via-blue-500/20 to-emerald-500/20 rounded-2xl blur-xl opacity-60" />
            <div className="relative bg-white/[0.04] backdrop-blur-2xl rounded-2xl border border-white/[0.08] shadow-2xl p-6 sm:p-8">
              <div className="mb-6">
                <h2 className="text-xl font-semibold text-white tracking-tight">Acceder</h2>
                <p className="text-sm text-slate-500 mt-0.5">Ingresa tus credenciales empresariales</p>
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
                  <div className="relative group">
                    <User className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500 group-focus-within:text-emerald-400 transition-colors" />
                    <input type="text" value={username} onChange={e => { setUsername(e.target.value); setErrors({ ...errors, username: '' }); }}
                      className={`w-full pl-10 pr-4 py-2.5 bg-white/[0.05] border rounded-xl text-sm text-white placeholder-slate-600 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 focus:border-emerald-500/50 transition-all duration-300 input-glow ${errors.username ? 'border-red-400/40' : 'border-white/[0.08] hover:border-white/[0.18]'}`}
                      placeholder="Ingresa tu usuario" />
                  </div>
                  {errors.username && <p className="text-xs text-red-400 mt-1.5 animate-slide-up">{errors.username}</p>}
                </div>

                <div>
                  <label className="block text-xs font-medium text-slate-400 mb-1.5">Contraseña</label>
                  <div className="relative group">
                    <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500 group-focus-within:text-emerald-400 transition-colors" />
                    <input type={showPassword ? 'text' : 'password'} value={password}
                      onChange={e => { setPassword(e.target.value); setErrors({ ...errors, password: '' }); }}
                      className={`w-full pl-10 pr-12 py-2.5 bg-white/[0.05] border rounded-xl text-sm text-white placeholder-slate-600 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 focus:border-emerald-500/50 transition-all duration-300 input-glow ${errors.password ? 'border-red-400/40' : 'border-white/[0.08] hover:border-white/[0.18]'}`}
                      placeholder="Ingresa tu contraseña" />
                    <button type="button" onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-300 transition-colors p-0.5">
                      {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                  {errors.password && <p className="text-xs text-red-400 mt-1.5 animate-slide-up">{errors.password}</p>}
                </div>

                <motion.button whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }} type="submit" disabled={loading}
                  className="w-full relative flex items-center justify-center gap-2 bg-gradient-to-r from-emerald-500 to-emerald-400 hover:from-emerald-400 hover:to-emerald-300 text-slate-900 font-semibold py-2.5 rounded-xl transition-all shadow-lg shadow-emerald-500/25 disabled:opacity-50 disabled:cursor-not-allowed mt-2 overflow-hidden group">
                  <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/25 to-transparent -translate-x-full group-hover:translate-x-full transition-transform duration-700" />
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
                <p className="text-[10px] font-semibold text-slate-600 uppercase tracking-widest text-center mb-3">Acceso rápido de prueba</p>
                <div className="grid grid-cols-3 gap-2">
                  {demoUsers.map(d => (
                    <motion.button key={d.user} whileHover={{ y: -3, scale: 1.02 }} whileTap={{ scale: 0.97 }}
                      onClick={() => fillDemo(d.user, d.pass)}
                      className="p-2.5 bg-white/[0.03] hover:bg-white/[0.07] rounded-xl text-center transition-all duration-200 border border-white/[0.04] hover:border-white/[0.12] group relative overflow-hidden">
                      <div className={`w-8 h-8 bg-gradient-to-br ${d.color} rounded-xl mx-auto mb-1.5 flex items-center justify-center shadow-lg shadow-${d.color.split(' ')[1]}/20 group-hover:scale-110 transition-transform`}>
                        <d.icon className="w-4 h-4 text-white" />
                      </div>
                      <p className="text-emerald-400 font-bold text-xs group-hover:text-emerald-300 transition-colors">{d.user}</p>
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
