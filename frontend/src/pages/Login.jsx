import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { motion, AnimatePresence } from 'framer-motion';
import { LineChart, LogIn, Eye, EyeOff, User, Lock, Sparkles } from 'lucide-react';

function Particle({ index }) {
  const x = Math.random() * 100;
  const y = Math.random() * 100;
  const size = Math.random() * 4 + 2;
  const duration = Math.random() * 20 + 10;
  const delay = Math.random() * 10;

  return (
    <motion.div
      className="absolute rounded-full"
      style={{
        width: size, height: size, left: `${x}%`, top: `${y}%`,
        background: `radial-gradient(circle, rgba(16,185,129,${Math.random() * 0.3 + 0.1}) 0%, transparent 70%)`,
        boxShadow: `0 0 ${size * 2}px rgba(16,185,129,0.1)`,
      }}
      animate={{ y: [0, -30, 0], opacity: [0.2, 0.6, 0.2], scale: [1, 1.2, 1] }}
      transition={{ duration, repeat: Infinity, delay, ease: 'easeInOut' }}
    />
  );
}

function FloatingShape({ index }) {
  const x = 10 + Math.random() * 80;
  const startY = 10 + Math.random() * 80;
  const duration = Math.random() * 15 + 15;
  const delay = Math.random() * 5;
  const size = Math.random() * 60 + 30;

  return (
    <motion.div
      className="absolute border border-emerald-500/10 rounded-3xl"
      style={{ width: size, height: size, left: `${x}%`, top: `${startY}%` }}
      animate={{
        y: [0, -40, 0, 40, 0], x: [0, 30, 0, -30, 0],
        rotate: [0, 90, 180, 270, 360], opacity: [0.1, 0.3, 0.1],
      }}
      transition={{ duration, repeat: Infinity, delay, ease: 'linear' }}
    />
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
  const [particles] = useState(() => Array.from({ length: 30 }, (_, i) => i));
  const [shapes] = useState(() => Array.from({ length: 5 }, (_, i) => i));

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
    { user: 'admin', pass: 'admin123', role: 'Administrador', color: 'from-emerald-500 to-emerald-600' },
    { user: 'vendedor', pass: 'ventas123', role: 'Vendedor', color: 'from-blue-500 to-blue-600' },
    { user: 'bodega', pass: 'bodega123', role: 'Bodega', color: 'from-amber-500 to-amber-600' },
  ];

  const fillDemo = (u, p) => {
    setUsername(u); setPassword(p); setErrors({}); setError('');
  };

  return (
    <div className="min-h-screen animated-bg flex items-center justify-center p-4 relative overflow-hidden">
      {/* Animated Background */}
      {particles.map(i => <Particle key={i} index={i} />)}
      {shapes.map(i => <FloatingShape key={i} index={i} />)}

      {/* Gradient Orbs */}
      <div className="absolute -top-60 -right-60 w-96 h-96 bg-emerald-500/10 rounded-full blur-[100px] animate-pulse" />
      <div className="absolute -bottom-60 -left-60 w-96 h-96 bg-blue-500/10 rounded-full blur-[100px] animate-pulse" style={{ animationDelay: '2s' }} />
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-violet-500/5 rounded-full blur-[120px]" />

      {/* Grid Pattern */}
      <div className="absolute inset-0 opacity-[0.03]" style={{
        backgroundImage: 'linear-gradient(rgba(255,255,255,.1) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,.1) 1px, transparent 1px)',
        backgroundSize: '60px 60px',
      }} />

      <motion.div
        initial={{ opacity: 0, y: 40 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.7, ease: 'easeOut' }}
        className="w-full max-w-md relative z-10"
      >
        {/* Brand Badge */}
        <motion.div initial={{ opacity: 0, y: -20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }} className="text-center mb-8">
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{ delay: 0.2, type: 'spring', stiffness: 200, damping: 15 }}
            className="inline-flex items-center justify-center mb-4"
          >
            <div className="relative">
              <motion.div
                animate={{ boxShadow: ['0 0 20px rgba(16,185,129,0.2)', '0 0 40px rgba(16,185,129,0.4)', '0 0 20px rgba(16,185,129,0.2)'] }}
                transition={{ duration: 3, repeat: Infinity, ease: 'easeInOut' }}
                className="w-20 h-20 bg-gradient-to-br from-emerald-400 to-emerald-600 rounded-2xl shadow-2xl shadow-emerald-500/30 flex items-center justify-center rotate-3"
              >
                <LineChart className="w-10 h-10 text-white" />
              </motion.div>
              <div className="absolute -top-2 -right-2">
                <motion.div animate={{ rotate: 360 }} transition={{ duration: 8, repeat: Infinity, ease: 'linear' }}>
                  <Sparkles className="w-5 h-5 text-emerald-300" />
                </motion.div>
              </div>
            </div>
          </motion.div>

          <motion.h1 initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.4 }} className="text-3xl font-bold text-white">
            Grupo Cordillera
          </motion.h1>
          <motion.p initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.5 }} className="text-emerald-400/70 text-sm mt-1 font-medium tracking-wide neon-text">
            Plataforma de Monitoreo Inteligente
          </motion.p>
        </motion.div>

        {/* Login Card */}
        <motion.div initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3, duration: 0.6 }} className="relative">
          <div className="absolute inset-0 bg-gradient-to-r from-emerald-500/10 to-blue-500/10 rounded-2xl blur-xl" />
          <div className="relative glass-dark rounded-2xl shadow-2xl p-8 border border-white/10 neon-border">
            <div className="text-center mb-6">
              <h2 className="text-xl font-semibold text-white">Bienvenido</h2>
              <p className="text-sm text-slate-400 mt-1">Ingresa tus credenciales para continuar</p>
            </div>

            <AnimatePresence>
              {error && (
                <motion.div
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: 'auto' }}
                  exit={{ opacity: 0, height: 0 }}
                  className="mb-4 p-3 bg-red-500/10 border border-red-500/20 rounded-xl text-sm text-red-400 flex items-center gap-2"
                >
                  <div className="w-1.5 h-1.5 bg-red-500 rounded-full shrink-0 animate-pulse" />
                  {error}
                </motion.div>
              )}
            </AnimatePresence>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">
                  <User className="w-3.5 h-3.5 inline mr-1.5" /> Usuario
                </label>
                <motion.div animate={errors.username ? { x: [0, -4, 4, -4, 4, 0] } : {}} transition={{ duration: 0.3 }}>
                  <input type="text" value={username} onChange={e => { setUsername(e.target.value); setErrors({ ...errors, username: '' }); }}
                    className={`w-full px-4 py-3 bg-white/10 border rounded-xl text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all input-neon ${errors.username ? 'border-red-400/50' : 'border-white/10'}`}
                    placeholder="Ingresa tu usuario" />
                  {errors.username && <p className="text-xs text-red-400 mt-1">{errors.username}</p>}
                </motion.div>
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">
                  <Lock className="w-3.5 h-3.5 inline mr-1.5" /> Contraseña
                </label>
                <motion.div animate={errors.password ? { x: [0, -4, 4, -4, 4, 0] } : {}} transition={{ duration: 0.3 }} className="relative">
                  <input type={showPassword ? 'text' : 'password'} value={password}
                    onChange={e => { setPassword(e.target.value); setErrors({ ...errors, password: '' }); }}
                    className={`w-full px-4 py-3 pr-12 bg-white/10 border rounded-xl text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-emerald-500 focus:border-transparent transition-all input-neon ${errors.password ? 'border-red-400/50' : 'border-white/10'}`}
                    placeholder="Ingresa tu contraseña" />
                  <button type="button" onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-white transition-colors p-1">
                    {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                  </button>
                  {errors.password && <p className="text-xs text-red-400 mt-1">{errors.password}</p>}
                </motion.div>
              </div>

              <motion.button whileHover={{ scale: 1.01 }} whileTap={{ scale: 0.99 }} type="submit" disabled={loading}
                className="w-full relative flex items-center justify-center gap-2 bg-gradient-to-r from-emerald-600 to-emerald-500 hover:from-emerald-500 hover:to-emerald-400 text-white font-semibold py-3 rounded-xl transition-all shadow-lg shadow-emerald-600/30 disabled:opacity-50 disabled:cursor-not-allowed mt-2 overflow-hidden group"
              >
                <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent -translate-x-full group-hover:translate-x-full transition-transform duration-700" />
                {loading ? (
                  <motion.div animate={{ rotate: 360 }} transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}>
                    <svg className="w-5 h-5" viewBox="0 0 24 24" fill="none">
                      <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" className="opacity-25" />
                      <path d="M4 12a8 8 0 018-8" stroke="currentColor" strokeWidth="4" strokeLinecap="round" className="opacity-75" />
                    </svg>
                  </motion.div>
                ) : (<><LogIn className="w-4 h-4" /> Iniciar Sesión</>)}
              </motion.button>
            </form>

            {/* Demo Users */}
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.6 }} className="mt-6 p-4 bg-white/5 rounded-xl border border-white/5">
              <p className="text-xs font-medium text-slate-500 mb-3 text-center uppercase tracking-wider">Acceso Rápido</p>
              <div className="grid grid-cols-3 gap-2">
                {demoUsers.map(d => (
                  <motion.button key={d.user} whileHover={{ scale: 1.05, y: -2 }} whileTap={{ scale: 0.95 }}
                    onClick={() => fillDemo(d.user, d.pass)}
                    className="p-2.5 bg-white/5 hover:bg-white/10 rounded-xl text-center transition-all border border-white/5 hover:border-white/10 group"
                  >
                    <div className={`w-7 h-7 bg-gradient-to-br ${d.color} rounded-lg mx-auto mb-1.5 flex items-center justify-center shadow-lg group-hover:shadow-xl transition-shadow`}>
                      <User className="w-3.5 h-3.5 text-white" />
                    </div>
                    <p className="text-emerald-400 font-semibold text-xs">{d.user}</p>
                    <p className="text-slate-600 text-[10px]">{d.role}</p>
                  </motion.button>
                ))}
              </div>
            </motion.div>
          </div>
        </motion.div>

        <motion.p initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.8 }}
          className="text-center text-xs text-slate-700 mt-6">
          © 2026 Grupo Cordillera — Plataforma de Monitoreo Inteligente v1.0
        </motion.p>
      </motion.div>
    </div>
  );
}
