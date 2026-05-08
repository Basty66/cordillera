import axios from 'axios';

const api = axios.create({ baseURL: '/api' });

api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

export const login = (username, password) =>
  api.post('/auth/login', { username, password }).then(r => r.data);

export const getDashboard = () => api.get('/bff/dashboard').then(r => r.data);

export const getVentas = () => api.get('/ventas').then(r => r.data);
export const createVenta = (data) => api.post('/ventas', data).then(r => r.data);

export const getProductos = () => api.get('/productos').then(r => r.data);

export const getSucursales = () => api.get('/sucursales').then(r => r.data);

export const getEmpleados = () => api.get('/empleados').then(r => r.data);

export const getDepartamentos = () => api.get('/departamentos').then(r => r.data);

export const getIndicadores = () => api.get('/indicadores').then(r => r.data);
export const getValoresActuales = () => api.get('/indicadores/valores/actuales').then(r => r.data);
export const inicializarIndicadores = () => api.post('/indicadores/inicializar').then(r => r.data);
export const getCategorias = () => api.get('/indicadores/categorias').then(r => r.data);

export const getUsuarios = () => api.get('/auth/usuarios').then(r => r.data);
export const createUsuario = (data) => api.post('/auth/register', data).then(r => r.data);

export const getTickets = () => api.get('/tickets').then(r => r.data);
export const getTicket = (id) => api.get(`/tickets/${id}`).then(r => r.data);
export const createTicket = (data) => api.post('/tickets', data).then(r => r.data);
export const updateTicketStatus = (id, status) =>
  api.put(`/tickets/${id}/status`, { status }).then(r => r.data);
export const updateTicket = (id, data) => api.put(`/tickets/${id}`, data).then(r => r.data);
export const deleteTicket = (id) => api.delete(`/tickets/${id}`).then(r => r.data);

export const getReportesDashboard = () => api.get('/reportes/dashboard').then(r => r.data);
export const getReportesTickets = () => api.get('/reportes/tickets').then(r => r.data);
