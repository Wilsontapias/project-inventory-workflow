import { useCallback, useRef, useState } from 'react'
import Icon from '../common/Icon.jsx'
import { ToastContext } from './toastContext.js'

const navigation = [
  { id: 'dashboard', label: 'Dashboard', icon: 'grid', group: 'GENERAL' },
  { id: 'products', label: 'Catálogo de inventario', icon: 'box', group: 'INVENTARIO' },
  { id: 'alerts', label: 'Alertas y rotación', icon: 'alert', group: 'INVENTARIO' },
  { id: 'suppliers', label: 'Directorio de proveedores', icon: 'building', group: 'ABASTECIMIENTO' },
  { id: 'purchases', label: 'Órdenes sugeridas', icon: 'cart', group: 'ABASTECIMIENTO' },
  { id: 'cashflow', label: 'Flujo de caja', icon: 'wallet', group: 'FINANZAS' },
]

/** Marco de navegación inspirado en el sidebar empresarial de Stitch. */
export default function AppShell({ activePage, onNavigate, children }) {
  const [menuOpen, setMenuOpen] = useState(false)
  const [toast, setToast] = useState(null)
  const timer = useRef(null)

  const notify = useCallback((message, kind = 'success') => {
    window.clearTimeout(timer.current)
    setToast({ message, kind })
    timer.current = window.setTimeout(() => setToast(null), 3600)
  }, [])

  const groups = [...new Set(navigation.map((item) => item.group))]
  const handleNavigate = (page) => {
    onNavigate(page)
    setMenuOpen(false)
  }

  return (
    <ToastContext.Provider value={notify}>
      <div className="app-shell">
        <button aria-label="Cerrar menú" className={`mobile-overlay ${menuOpen ? 'open' : ''}`} onClick={() => setMenuOpen(false)} />
        <aside className={`sidebar ${menuOpen ? 'open' : ''}`}>
          <div className="brand">
            <span className="brand-mark"><Icon name="server" size={21} /></span>
            <span><span className="brand-name">NEXUS INVENTORY</span><span className="brand-caption">Control operativo</span></span>
          </div>
          {groups.map((group) => (
            <div key={group}>
              <div className="nav-label">{group}</div>
              <nav aria-label={group} className="nav-list">
                {navigation.filter((item) => item.group === group).map((item) => (
                  <button aria-current={activePage === item.id ? 'page' : undefined} className={`nav-link ${activePage === item.id ? 'active' : ''}`} key={item.id} onClick={() => handleNavigate(item.id)} type="button">
                    <Icon className="nav-icon" name={item.icon} size={17} /><span className="nav-link-label">{item.label}</span>
                  </button>
                ))}
              </nav>
            </div>
          ))}
          <div className="sidebar-spacer" />
          <div className="sidebar-note"><strong>Entorno de demostración</strong>Datos ficticios en H2 en memoria. No usar con información real.</div>
          <div className="sidebar-footer"><span className="avatar">IF</span><span><span className="footer-name">Inventario local</span><span className="footer-role">Prototipo sin autenticación</span></span></div>
        </aside>

        <div className="workspace">
          <header className="topbar">
            <div className="topbar-left">
              <button aria-label="Abrir menú" className="icon-button mobile-menu" onClick={() => setMenuOpen(true)} type="button"><Icon name="menu" /></button>
              <span className="topbar-label">Operaciones</span><span className="topbar-divider" /><span className="topbar-context">Inventario &amp; flujo de caja</span>
            </div>
            <div className="topbar-right"><span className="status-pill"><span className="status-dot" /> API de demostración</span><button aria-label="Estado de la API: ejecutándose en modo local" className="icon-button" type="button" title="API local de demostración"><Icon name="bell" size={17} /></button></div>
          </header>
          <main className="main-content"><ToastContext.Provider value={notify}>{children}</ToastContext.Provider></main>
        </div>
        {toast && <div className={`toast ${toast.kind === 'error' ? 'error' : ''}`} role="status"><Icon name={toast.kind === 'error' ? 'alert' : 'check'} size={17} /><span><strong>{toast.kind === 'error' ? 'No se pudo completar' : 'Operación completada'}</strong>{toast.message}</span></div>}
      </div>
    </ToastContext.Provider>
  )
}
