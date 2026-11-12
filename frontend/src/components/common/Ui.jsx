import Icon from './Icon.jsx'

/** Encabezado reutilizable para cada módulo. */
export function PageHeading({ eyebrow, title, description, actions }) {
  return (
    <div className="page-heading">
      <div>
        <div className="eyebrow"><span>{eyebrow}</span><span className="eyebrow-dot" /></div>
        <h1>{title}</h1>
        <p className="page-description">{description}</p>
      </div>
      {actions && <div className="heading-actions">{actions}</div>}
    </div>
  )
}

/** Tarjeta KPI con icono y una nota contextual. */
export function KpiCard({ label, value, detail, icon, tone = '', compact = false }) {
  return (
    <article className="card kpi-card">
      <div>
        <div className="kpi-top"><span>{label}</span><span className={`kpi-icon ${tone}`}><Icon name={icon} size={16} /></span></div>
        <div className={`kpi-value ${compact ? 'compact' : ''}`}>{value}</div>
      </div>
      <div className="kpi-foot">{detail}</div>
    </article>
  )
}

/** Etiqueta semántica para estados de inventario o proveedor. */
export function StatusBadge({ status, children }) {
  const normalized = String(status || '').toUpperCase()
  const classes = normalized === 'CRITICAL' || normalized === 'CRÍTICO'
    ? 'badge badge-critical'
    : normalized === 'OVERSTOCK' || normalized === 'SOBRE STOCK'
      ? 'badge badge-overstock'
      : normalized === 'OPTIMAL' || normalized === 'ÓPTIMO'
        ? 'badge badge-optimal'
        : 'badge badge-neutral'
  const labels = { CRITICAL: 'Crítico', OPTIMAL: 'Óptimo', OVERSTOCK: 'Sobre stock' }
  return <span className={classes}><span aria-hidden="true">●</span>{children || labels[normalized] || status}</span>
}

/** Estados compartidos para peticiones de datos. */
export function LoadingState({ label = 'Cargando información…' }) {
  return <div className="card loading-state" role="status"><span className="loading-spinner" /><p className="state-title">{label}</p></div>
}

export function EmptyState({ title = 'Sin resultados', description = 'Prueba cambiar los filtros o vuelve a consultar más tarde.' }) {
  return <div className="empty-state"><span className="empty-state-icon"><Icon name="box" size={18} /></span><p className="state-title">{title}</p><p className="state-description">{description}</p></div>
}

export function ErrorState({ message, onRetry }) {
  return (
    <div className="card error-state" role="alert">
      <span className="error-state-icon"><Icon name="alert" size={18} /></span>
      <p className="state-title">No pudimos cargar los datos</p>
      <p className="state-description">{message || 'Verifica que el backend esté en ejecución y vuelve a intentarlo.'}</p>
      {onRetry && <button className="button button-small" type="button" onClick={onRetry}><Icon name="refresh" size={14} /> Reintentar</button>}
    </div>
  )
}

/** Ventana modal accesible para formularios y detalles. */
export function Modal({ title, description, onClose, children, width = '650px' }) {
  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <section aria-modal="true" className="modal" role="dialog" style={{ maxWidth: width }}>
        <header className="modal-header">
          <div><h2 className="modal-title">{title}</h2>{description && <p className="modal-description">{description}</p>}</div>
          <button aria-label="Cerrar" className="icon-button" type="button" onClick={onClose}><Icon name="close" /></button>
        </header>
        <div className="modal-content">{children}</div>
      </section>
    </div>
  )
}

export function SearchField({ value, onChange, placeholder = 'Buscar…' }) {
  return <div className="search-field"><Icon name="search" size={15} /><input aria-label={placeholder} className="input" value={value} onChange={(event) => onChange(event.target.value)} placeholder={placeholder} /></div>
}
