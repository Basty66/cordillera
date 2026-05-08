# Frontend - Grupo Cordillera

Plataforma de monitoreo inteligente construida con React 19 + Vite 8 + Tailwind CSS v4.

## Tecnologias

- React 19.2.5
- Vite 8.0.10
- Tailwind CSS 4.2.4
- React Router DOM 7.15.0
- Axios 1.16.0
- Chart.js 4.5.1 + Recharts 3.8.1
- Framer Motion 12.38.0
- Lucide React 1.14.0

## Componentes NPM

### Dependencias de Produccion

| Paquete | Version | Uso |
|---------|---------|-----|
| react | 19.2.5 | UI Framework |
| react-dom | 19.2.5 | Renderizado DOM |
| react-router-dom | 7.15.0 | Enrutamiento |
| axios | 1.16.0 | Cliente HTTP |
| chart.js | 4.5.1 | Graficos |
| react-chartjs-2 | 5.3.1 | Wrapper Chart.js |
| recharts | 3.8.1 | Graficos adicionales |
| framer-motion | 12.38.0 | Animaciones |
| lucide-react | 1.14.0 | Iconos |

## Patrones de Diseno en Frontend

- **Provider Pattern**: AuthContext para gestion de estado global de autenticacion
- **Custom Hook Pattern**: useApi, useMutation hooks para data fetching
- **Protected Route Pattern**: ProtectedRoute para control de acceso por roles
- **Compound Components**: Layout + Sidebar + Outlet para estructura de pagina

## Estructura

```
src/
├── api/client.js       # Axios interceptors + API calls
├── context/            # AuthContext (JWT management)
├── components/         # Sidebar, Layout, ProtectedRoute, DetailModal
├── hooks/              # useApi, useMutation (Custom Hook Pattern)
└── pages/              # Dashboard, Ventas, Productos, etc.
```

## Ejecucion

```bash
npm install
npm run dev
```

Puerto: 5173 (con proxy a api-gateway:8084)
