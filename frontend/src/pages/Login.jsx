import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { motion, AnimatePresence } from 'framer-motion';
import {
  LogIn, Eye, EyeOff, User, Lock, Sparkles, Store, TrendingUp,
  ShieldCheck, BarChart3, Hexagon, Zap, Globe, DollarSign,
  ShoppingCart, Users, ArrowRight, ChevronRight, Activity
} from 'lucide-react';

function AuroraBackground() {
  return (
    <div className="absolute inset-0 overflow-hidden">
      <div className="absolute inset-0 bg-gradient-to-br from-slate-950 via-slate-900 to-emerald-950" />
      <motion.div className="absolute -top-48 -left-48 w-[700px] h-[700px] rounded-full"
        style={{ background: 'radial-gradient(circle, rgba(16,185,129,0.12) 0%, transparent 60%)' }}
        animate={{ x: [0, 40, -30, 0], y: [0, -30, 40, 0] }}
        transition={{ duration: 18, repeat: Infinity, ease: 'easeInOut' }}
      />
      <motion.div className="absolute -bottom-48 -right-48 w-[600px] h-[600px] rounded-full"
        style={{ background: 'radial-gradient(circle, rgba(59,130,246,0.1) 0%, transparent 60%)' }}
        animate={{ x: [0, -40, 30, 0], y: [0, 40, -30, 0] }}
        transition={{ duration: 22, repeat: Infinity, ease: 'easeInOut' }}
      />
      <motion.div className="absolute top-1/4 left-2/3 w-[400px] h-[400px] rounded-full"
        style={{ background: 'radial-gradient(circle, rgba(168,85,247,0.08) 0%, transparent 60%)' }}
        animate={{ scale: [1, 1.3, 1], opacity: [0.2, 0.5, 0.2] }}
        transition={{ duration: 12, repeat: Infinity, ease: 'easeInOut' }}
      />
      <div className="absolute inset-0 opacity-[0.025]" style={{
        backgroundImage: 'radial-gradient(circle at 1px 1px, rgba(255,255,255,0.6) 1px, transparent 0)',
        backgroundSize: '28px 28px',
      }} />
    </div>
  );
}

function FloatingParticles() {
  return (
    <>
      {[...Array(12)].map((_, i) => (
        <motion.div key={i} className="absolute rounded-full pointer-events-none"
          style={{
            width: Math.random() * 5 + 2, height: Math.random() * 5 + 2,
            left: `${Math.random() * 100}%`, top: `${Math.random() * 100}%`,
            background: `rgba(16,185,129,${Math.random() * 0.25 + 0.08})`,
            boxShadow: `0 0 ${Math.random() * 8 + 2}px rgba(16,185,129,0.2)`,
          }}
          animate={{ y: [0, -40 - Math.random() * 40, 0], opacity: [0.1, 0.5, 0.1] }}
          transition={{ duration: Math.random() * 10 + 6, repeat: Infinity, delay: Math.random() * 5, ease: 'easeInOut' }}
        />
      ))}
    </>
  );
}

const slides = [
  { icon: BarChart3, title: 'Dashboard en Tiempo Real', desc: 'Métrica de ventas, indicadores y KPIs actualizados al instante', color: 'from-emerald-500 to-emerald-600' },
  { icon: Store, title: 'Múltiples Sucursales', desc: 'Gestión centralizada de 12 sucursales a nivel nacional', color: 'from-blue-500 to-blue-600' },
  { icon: TrendingUp, title: 'Indicadores Económicos', desc: 'UF, Dólar, UTM e IPC integrados via mindicador.cl', color: 'from-violet-500 to-violet-600' },
  { icon: ShoppingCart, title: 'Ventas y Productos', desc: 'Catálogo inteligente con +1,000 productos en tiempo real', color: 'from-amber-500 to-amber-600' },
  { icon: ShieldCheck, title: 'Seguridad Empresarial', desc: 'Autenticación JWT con roles y permisos granulares', color: 'from-rose-500 to-rose-600' },
];

const slideAnim = {
  enter: { x: 80, opacity: 0 },
  center: { x: 0, opacity: 1 },
  exit: { x: -80, opacity: 0 },
};

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState({});
  const [slideIdx, setSlideIdx] = useState(0);
  const { login } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setInterval(() => setSlideIdx(p => (p + 1) % slides.length), 4500);
    return () => clearInterval(timer);
  }, []);

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
    { user: 'admin', pass: 'admin123', role: 'Admin', color: 'from-emerald-500 to-emerald-600', icon: ShieldCheck },
    { user: 'vendedor', pass: 'ventas123', role: 'Vendedor', color: 'from-blue-500 to-blue-600', icon: TrendingUp },
    { user: 'bodega', pass: 'bodega123', role: 'Bodega', color: 'from-amber-500 to-amber-600', icon: Store },
  ];

  const fillDemo = (u, p) => { setUsername(u); setPassword(p); setErrors({}); setError(''); };

  return (
    <div className="min-h-screen flex relative overflow-hidden">
      <AuroraBackground />
      <FloatingParticles />

      {/* Left: Brand + Auto-sliding showcase */}
      <div className="hidden lg:flex lg:w-1/2 relative items-center justify-center px-12">
        <div className="w-full max-w-lg">
          <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6 }}>
            <div className="flex items-center gap-3 mb-16">
              <motion.div initial={{ scale: 0, rotate: -180 }} animate={{ scale: 1, rotate: 0 }}
                transition={{ type: 'spring', stiffness: 150, damping: 15 }}
                className="p-3 bg-gradient-to-br from-emerald-500 to-emerald-600 rounded-2xl shadow-2xl shadow-emerald-500/30">
                <Hexagon className="w-7 h-7 text-white" />
              </motion.div>
              <div>
                <h2 className="text-xl font-bold text-white tracking-tight">Grupo Cordillera</h2>
                <p className="text-[10px] text-emerald-400/60 font-semibold uppercase tracking-[0.2em]">Monitoreo Inteligente</p>
              </div>
            </div>

            {/* Tagline */}
            <motion.p initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.3 }}
              className="text-4xl font-bold text-white leading-tight tracking-tight mb-2">
              Plataforma de<br />
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 via-emerald-300 to-blue-400">
                Monitoreo Corporativo
              </span>
            </motion.p>
            <motion.p initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.4 }}
              className="text-slate-400 text-sm leading-relaxed mb-10">
              Gestión centralizada de ventas, indicadores, tickets y más.
            </motion.p>

            {/* Auto-sliding carousel */}
            <div className="relative h-[140px]">
              <AnimatePresence mode="wait">
                <motion.div key={slideIdx} variants={slideAnim} initial="enter" animate="center" exit="exit"
                  transition={{ duration: 0.4, ease: [0.16, 1, 0.3, 1] }}
                  className="absolute inset-0"
                >
                  {(() => {
                    const s = slides[slideIdx];
                    return (
                      <div className="flex items-start gap-4 p-5 bg-white/[0.03] rounded-2xl border border-white/[0.06] backdrop-blur-sm">
                        <div className={`p-3 bg-gradient-to-br ${s.color} rounded-xl shadow-lg shrink-0`}>
                          <s.icon className="w-6 h-6 text-white" />
                        </div>
                        <div>
                          <p className="text-lg font-bold text-white mb-1">{s.title}</p>
                          <p className="text-sm text-slate-400">{s.desc}</p>
                        </div>
                      </div>
                    );
                  })()}
                </motion.div>
              </AnimatePresence>
            </div>

            {/* Slide indicators */}
            <div className="flex items-center gap-2 mt-4">
              {slides.map((_, i) => (
                <button key={i} onClick={() => setSlideIdx(i)}
                  className={`h-1.5 rounded-full transition-all duration-500 ${i === slideIdx ? 'w-8 bg-emerald-400' : 'w-2 bg-slate-600 hover:bg-slate-500'}`} />
              ))}
            </div>
          </motion.div>
        </div>
      </div>

      {/* Right: Login Form */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-4 sm:p-8 relative">
        <motion.div
          initial={{ opacity: 0, x: 60 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.7, ease: [0.16, 1, 0.3, 1] }}
          className="w-full max-w-md relative z-10"
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
          <motion.div className="relative"
            initial={{ opacity: 0, y: 30 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2, duration: 0.5 }}
          >
            <div className="absolute -inset-1 bg-gradient-to-r from-emerald-500/20 via-blue-500/20 to-emerald-500/20 rounded-2xl blur-xl opacity-70" />
            <div className="relative bg-white/[0.04] backdrop-blur-2xl rounded-2xl border border-white/[0.08] shadow-2xl p-8 sm:p-10">
              <motion.div className="mb-6"
                initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.3, duration: 0.4 }}
              >
                <h2 className="text-xl font-semibold text-white tracking-tight">Bienvenido</h2>
                <p className="text-sm text-slate-500 mt-0.5">Ingresa tus credenciales para continuar</p>
              </motion.div>

              <AnimatePresence>
                {error && (
                  <motion.div initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: 'auto' }} exit={{ opacity: 0, height: 0 }}
                    className="mb-4 p-3 bg-red-500/10 border border-red-500/20 rounded-xl text-sm text-red-400 flex items-center gap-2 overflow-hidden">
                    <div className="w-1.5 h-1.5 bg-red-500 rounded-full shrink-0 animate-pulse" />{error}
                  </motion.div>
                )}
              </AnimatePresence>

              <form onSubmit={handleSubmit} className="space-y-4">
                <motion.div initial={{ opacity: 0, x: 40 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.35, duration: 0.4 }}>
                  <label className="block text-xs font-medium text-slate-400 mb-1.5">Usuario</label>
                  <div className="relative group">
                    <User className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500 group-focus-within:text-emerald-400 transition-colors duration-300" />
                    <input type="text" value={username} onChange={e => { setUsername(e.target.value); setErrors({ ...errors, username: '' }); }}
                      className={`w-full pl-10 pr-4 py-3 bg-white/[0.05] border rounded-xl text-sm text-white placeholder-slate-600 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 focus:border-emerald-500/50 transition-all duration-300 input-glow ${errors.username ? 'border-red-400/40' : 'border-white/[0.08] hover:border-white/[0.18]'}`}
                      placeholder="Ingresa tu usuario" />
                  </div>
                  {errors.username && <p className="text-xs text-red-400 mt-1.5 animate-slide-up">{errors.username}</p>}
                </motion.div>

                <motion.div initial={{ opacity: 0, x: 40 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.4, duration: 0.4 }}>
                  <label className="block text-xs font-medium text-slate-400 mb-1.5">Contraseña</label>
                  <div className="relative group">
                    <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-500 group-focus-within:text-emerald-400 transition-colors duration-300" />
                    <input type={showPassword ? 'text' : 'password'} value={password}
                      onChange={e => { setPassword(e.target.value); setErrors({ ...errors, password: '' }); }}
                      className={`w-full pl-10 pr-12 py-3 bg-white/[0.05] border rounded-xl text-sm text-white placeholder-slate-600 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 focus:border-emerald-500/50 transition-all duration-300 input-glow ${errors.password ? 'border-red-400/40' : 'border-white/[0.08] hover:border-white/[0.18]'}`}
                      placeholder="Ingresa tu contraseña" />
                    <button type="button" onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-300 transition-colors p-0.5">
                      {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                    </button>
                  </div>
                  {errors.password && <p className="text-xs text-red-400 mt-1.5 animate-slide-up">{errors.password}</p>}
                </motion.div>

                <motion.button whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }} type="submit" disabled={loading}
                  initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.5, duration: 0.4 }}
                  className="w-full relative flex items-center justify-center gap-2 bg-gradient-to-r from-emerald-500 to-emerald-400 hover:from-emerald-400 hover:to-emerald-300 text-slate-900 font-semibold py-3 rounded-xl transition-all shadow-lg shadow-emerald-500/25 disabled:opacity-50 disabled:cursor-not-allowed mt-2 overflow-hidden group">
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

              <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.6, duration: 0.4 }}
                className="mt-6 pt-5 border-t border-white/[0.06]">
                <p className="text-[10px] font-semibold text-slate-600 uppercase tracking-widest text-center mb-3">Acceso rápido de prueba</p>
                <div className="grid grid-cols-3 gap-2.5">
                  {demoUsers.map((d, i) => (
                    <motion.button key={d.user} whileHover={{ y: -4, scale: 1.03 }} whileTap={{ scale: 0.97 }}
                      initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.7 + i * 0.1, duration: 0.3 }}
                      onClick={() => fillDemo(d.user, d.pass)}
                      className="p-3 bg-white/[0.03] hover:bg-white/[0.07] rounded-xl text-center transition-all duration-200 border border-white/[0.04] hover:border-white/[0.12] group relative overflow-hidden">
                      <div className={`w-9 h-9 bg-gradient-to-br ${d.color} rounded-xl mx-auto mb-1.5 flex items-center justify-center shadow-lg group-hover:scale-110 transition-transform duration-300`}>
                        <d.icon className="w-4 h-4 text-white" />
                      </div>
                      <p className="text-emerald-400 font-bold text-xs group-hover:text-emerald-300 transition-colors">{d.user}</p>
                      <p className="text-slate-600 text-[9px] leading-tight">{d.role}</p>
                    </motion.button>
                  ))}
                </div>
              </motion.div>
            </div>
          </motion.div>

          <motion.p initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: 0.9 }}
            className="text-center text-[10px] text-slate-700 mt-5 tracking-wide">
            &copy; 2026 Grupo Cordillera &mdash; Plataforma de Monitoreo Inteligente
          </motion.p>
        </motion.div>
      </div>
    </div>
  );
}
