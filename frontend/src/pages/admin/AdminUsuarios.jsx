import { useEffect, useState } from "react";
import { getUsuarios, createUsuario } from "../../api/client";
import { Shield, UserPlus, AlertCircle, CheckCircle } from "lucide-react";
import { motion } from "framer-motion";

const container = {
  hidden: { opacity: 0 },
  show: { opacity: 1, transition: { staggerChildren: 0.06 } },
};

const itemAnim = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0 },
};

const roles = [
  { value: "ADMIN", label: "Administrador" },
  { value: "VENDEDOR", label: "Vendedor" },
  { value: "BODEGA", label: "Bodega" },
];

export default function AdminUsuarios() {
  const [usuarios, setUsuarios] = useState([]);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({ username: "", password: "", rol: "VENDEDOR", nombre: "", email: "" });
  const [formErrors, setFormErrors] = useState({});

  const validate = () => {
    const errs = {};
    if (!form.nombre.trim()) errs.nombre = "El nombre es requerido";
    if (!form.username.trim()) errs.username = "El usuario es requerido";
    else if (form.username.trim().length < 3) errs.username = "Mínimo 3 caracteres";
    if (!form.password) errs.password = "La contraseña es requerida";
    else if (form.password.length < 6) errs.password = "Mínimo 6 caracteres";
    if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) errs.email = "Email inválido";
    setFormErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const loadUsuarios = async () => {
    try {
      const data = await getUsuarios();
      setUsuarios(data);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadUsuarios(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    if (!validate()) return;
    try {
      await createUsuario({ ...form, nombre: form.nombre.trim(), username: form.username.trim() });
      setSuccess("Usuario \"" + form.username + "\" creado exitosamente");
      setForm({ username: "", password: "", rol: "VENDEDOR", nombre: "", email: "" });
      setFormErrors({});
      loadUsuarios();
    } catch (e) {
      setError(e.response?.data?.error || e.message);
    }
  };

  const rolBadge = (rol) => {
    const colors = {
      ADMIN: "bg-purple-100 text-purple-700",
      VENDEDOR: "bg-blue-100 text-blue-700",
      BODEGA: "bg-amber-100 text-amber-700",
    };
    return colors[rol] || "bg-slate-100 text-slate-600";
  };

  return (
    <motion.div variants={container} initial="hidden" animate="show">
      <motion.div variants={itemAnim} className="flex items-center gap-3 mb-6">
        <div className="p-2 bg-purple-100 rounded-lg">
          <Shield className="w-6 h-6 text-purple-600" />
        </div>
        <div>
          <h2 className="text-2xl font-bold text-slate-800">Administracion de Usuarios</h2>
          <p className="text-sm text-slate-400">Gestiona los usuarios del sistema</p>
        </div>
      </motion.div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <motion.div variants={itemAnim} className="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <div className="flex items-center gap-2 mb-4">
            <UserPlus className="w-5 h-5 text-emerald-600" />
            <h3 className="font-semibold text-slate-800">Crear Nuevo Usuario</h3>
          </div>

          {error && (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="flex items-center gap-2 text-sm text-red-600 bg-red-50 px-3 py-2 rounded-lg mb-4">
              <AlertCircle className="w-4 h-4" />
              {error}
            </motion.div>
          )}

          {success && (
            <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="flex items-center gap-2 text-sm text-emerald-600 bg-emerald-50 px-3 py-2 rounded-lg mb-4">
              <CheckCircle className="w-4 h-4" />
              {success}
            </motion.div>
          )}

          <form onSubmit={handleSubmit} className="space-y-3">
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1">Nombre *</label>
              <input type="text" value={form.nombre} onChange={e => { setForm({ ...form, nombre: e.target.value }); setFormErrors({ ...formErrors, nombre: '' }); }}
                className={`w-full px-3 py-2 border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all ${formErrors.nombre ? 'border-red-400' : 'border-slate-200'}`} />
              {formErrors.nombre && <p className="text-xs text-red-500 mt-1">{formErrors.nombre}</p>}
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1">Email</label>
              <input type="email" value={form.email} onChange={e => { setForm({ ...form, email: e.target.value }); setFormErrors({ ...formErrors, email: '' }); }}
                className={`w-full px-3 py-2 border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all ${formErrors.email ? 'border-red-400' : 'border-slate-200'}`} />
              {formErrors.email && <p className="text-xs text-red-500 mt-1">{formErrors.email}</p>}
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1">Usuario *</label>
              <input type="text" value={form.username} onChange={e => { setForm({ ...form, username: e.target.value }); setFormErrors({ ...formErrors, username: '' }); }}
                className={`w-full px-3 py-2 border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all ${formErrors.username ? 'border-red-400' : 'border-slate-200'}`} />
              {formErrors.username && <p className="text-xs text-red-500 mt-1">{formErrors.username}</p>}
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1">Contraseña *</label>
              <input type="password" value={form.password} onChange={e => { setForm({ ...form, password: e.target.value }); setFormErrors({ ...formErrors, password: '' }); }}
                className={`w-full px-3 py-2 border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 transition-all ${formErrors.password ? 'border-red-400' : 'border-slate-200'}`} />
              {formErrors.password && <p className="text-xs text-red-500 mt-1">{formErrors.password}</p>}
            </div>
            <div>
              <label className="block text-xs font-medium text-slate-600 mb-1">Rol</label>
              <select value={form.rol} onChange={e => setForm({ ...form, rol: e.target.value })}
                className="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500">
                {roles.map(r => <option key={r.value} value={r.value}>{r.label}</option>)}
              </select>
            </div>
            <motion.button whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }} type="submit"
              className="w-full bg-emerald-600 hover:bg-emerald-500 text-white font-medium py-2 rounded-lg transition-colors text-sm">
              Crear Usuario
            </motion.button>
          </form>
        </motion.div>

        <motion.div variants={itemAnim} className="bg-white rounded-xl shadow-sm border border-slate-200 p-5">
          <h3 className="font-semibold text-slate-800 mb-4">Usuarios Registrados ({usuarios.length})</h3>
          {loading ? (
            <div className="flex justify-center py-8"><div className="animate-spin rounded-full h-6 w-6 border-b-2 border-emerald-600" /></div>
          ) : usuarios.length === 0 ? (
            <p className="text-sm text-slate-400 text-center py-8">No hay usuarios registrados</p>
          ) : (
            <div className="space-y-2">
              {usuarios.map(u => (
                <div key={u.id} className="flex items-center justify-between p-3 bg-slate-50 rounded-lg">
                  <div>
                    <p className="font-medium text-slate-700 text-sm">{u.nombre || u.username}</p>
                    <p className="text-xs text-slate-400">{u.email || u.username}</p>
                  </div>
                  <span className={"px-2 py-0.5 rounded-full text-xs font-medium " + rolBadge(u.rol)}>
                    {roles.find(r => r.value === u.rol)?.label || u.rol}
                  </span>
                </div>
              ))}
            </div>
          )}
        </motion.div>
      </div>
    </motion.div>
  );
}
