import { useCallback, useEffect, useMemo, useState } from 'react'
import Icon from '../../components/common/Icon.jsx'
import { EmptyState, ErrorState, KpiCard, LoadingState, PageHeading, StatusBadge } from '../../components/common/Ui.jsx'
import { getDashboardSummary } from '../../services/dashboardService.js'
import { getCashFlowSummary } from '../../services/cashFlowService.js'
import { getProducts } from '../../services/productsService.js'
import { formatCurrency, formatNumber, getCategoryLabel } from '../../utils/formatters.js'

/** Dashboard con métricas calculadas por el backend y un resumen de alertas. */
export default function DashboardPage({ onNavigate }) {
  const [snapshot, setSnapshot] = useState(null)
  const [cashSummary, setCashSummary] = useState(null)
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const loadData = useCallback(async () => {
    try {
      const [summary, cash, catalog] = await Promise.all([
        getDashboardSummary(), getCashFlowSummary(), getProducts(),
      ])
      setSnapshot(summary)
      setCashSummary(cash)
      setProducts(catalog)
    } catch (requestError) {
      setError(requestError.message)
    } finally {
      setLoading(false)
    }
  }, [])

  const refresh = useCallback(() => {
    setLoading(true)
    setError('')
    return loadData()
  }, [loadData])

  // El primer render inicia la carga de red; las actualizaciones ocurren al resolver la promesa.
  // oxlint-disable-next-line react/set-state-in-effect
  useEffect(() => { loadData() }, [loadData])

  const categoryStats = useMemo(() => {
    const grouped = products.reduce((result, product) => {
      const key = product.category
      if (!result[key]) result[key] = { category: key, current: 0, ideal: 0, cost: 0 }
      result[key].current += product.stockCurrent
      result[key].ideal += product.stockIdeal
      result[key].cost += Number(product.inventoryValue)
      return result
    }, {})
    return Object.values(grouped).sort((left, right) => right.cost - left.cost)
  }, [products])

  const attentionProducts = useMemo(() => products
    .filter((product) => product.status === 'CRITICAL' || product.daysInInventory > 90 || product.lowRotation)
    .sort((left, right) => Number(right.status === 'CRITICAL') - Number(left.status === 'CRITICAL') || right.daysInInventory - left.daysInInventory)
    .slice(0, 5), [products])
  const highestAgingValue = useMemo(() => products
    .filter((product) => product.daysInInventory > 90)
    .sort((left, right) => Number(right.inventoryValue) - Number(left.inventoryValue))
    .slice(0, 5), [products])

  const maxStock = Math.max(1, ...categoryStats.flatMap((item) => [item.current, item.ideal]))
  const maxCost = Math.max(1, ...categoryStats.map((item) => item.cost))
  const generatedAt = snapshot?.generatedAt ? new Date(snapshot.generatedAt) : null

  if (loading && !snapshot) return <><PageHeading eyebrow="Resumen ejecutivo" title="Dashboard de inventario" description="Visibilidad del inventario, capital inmovilizado y necesidades de reabastecimiento." /><LoadingState label="Consultando métricas del backend…" /></>
  if (error && !snapshot) return <><PageHeading eyebrow="Resumen ejecutivo" title="Dashboard de inventario" description="Visibilidad del inventario, capital inmovilizado y necesidades de reabastecimiento." /><ErrorState message={error} onRetry={refresh} /></>

  return (
    <>
      <PageHeading
        eyebrow="Control presupuestario y existencias"
        title="Cierre mensual & salud del inventario"
        description="Evaluación de existencias, permanencia en bodega y costo estimado de reabastecimiento, calculada con los datos disponibles en la API."
        actions={<button className="button" onClick={refresh} type="button"><Icon name="refresh" size={14} /> Actualizar</button>}
      />

      {error && <div className="form-error" role="alert">La actualización falló: {error}. Se muestran los últimos datos recibidos.</div>}
      <div className="alert-banner">
        <div className="alert-copy"><span className="alert-icon"><Icon name="alert" size={17} /></span><span><strong>{snapshot?.criticalProductCount || 0} productos requieren atención.</strong> {snapshot?.productsOlderThan90Days || 0} referencias superan los 90 días y {snapshot?.lowRotationProductCount || 0} no tienen ventas recientes. Revisa las alertas y las sugerencias antes de decidir una compra.</span></div>
        <button className="button button-small" onClick={() => onNavigate('alerts')} type="button">Revisar alertas <Icon name="chevron" size={13} /></button>
      </div>

      <section aria-label="Indicadores ejecutivos" className="kpi-grid dashboard-kpis">
        <KpiCard label="Valor de inventario" value={formatCurrency(snapshot?.inventoryCostValueCop)} detail={<><strong>Costo de adquisición</strong> de las existencias</>} icon="box" tone="blue" compact />
        <KpiCard label="Capital > 90 días" value={formatCurrency(snapshot?.agingInventoryCostValueCop)} detail={<><strong>{formatNumber(snapshot?.productsOlderThan90Days)}</strong> productos sobre el umbral</>} icon="clock" tone="red" compact />
        <KpiCard label="Saldo de caja demo" value={formatCurrency(cashSummary?.cashBalanceCop)} detail={<>Saldo ficticio + movimientos registrados</>} icon="wallet" tone="blue" compact />
        <KpiCard label="Disponible para reorden" value={formatCurrency(cashSummary?.availableForReorderCop)} detail={<>Saldo menos compromisos próximos 30 días</>} icon="cart" tone={cashSummary?.availableForReorderCop >= 0 ? 'green' : 'red'} compact />
        <KpiCard label="Alertas críticas" value={formatNumber(snapshot?.criticalProductCount)} detail={<>Productos con stock actual <strong>≤ mínimo</strong></>} icon="alert" tone="red" />
        <KpiCard label="Baja rotación" value={formatNumber(snapshot?.lowRotationProductCount)} detail={<>Sin ventas en 90 días y edad &gt; 90</>} icon="chart" tone="amber" />
      </section>

      <div className="info-note"><Icon name="info" size={15} /><span>{cashSummary?.dataNote} {snapshot?.financialDataNote} {generatedAt && <>Última actualización: {generatedAt.toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit' })}.</>}</span></div>

      <section className="dashboard-grid">
        <article className="card">
          <header className="card-header"><div><h2 className="card-title">Stock actual vs. ideal por categoría</h2><p className="card-subtitle">Comparación calculada desde el catálogo; no representa ventas históricas.</p></div><span className="badge badge-neutral">{formatNumber(products.length)} SKU</span></header>
          <div className="card-body">
            {categoryStats.length === 0 ? <EmptyState title="Aún no hay productos" /> : <>
              <div className="mini-chart" role="img" aria-label="Gráfico de existencias actuales e ideales por categoría">
                {categoryStats.map((item) => <div className="chart-group" key={item.category} title={`${getCategoryLabel(item.category)}: actual ${item.current}, ideal ${item.ideal}`}>
                  <span className="chart-bar" style={{ height: `${Math.max(4, (item.ideal / maxStock) * 100)}%` }} />
                  <span className="chart-bar current" style={{ height: `${Math.max(4, (item.current / maxStock) * 100)}%` }} />
                </div>)}
              </div>
              <div className="category-list">
                {categoryStats.map((item) => <div className="category-row" key={item.category}>
                  <span>{getCategoryLabel(item.category)}</span>
                  <span className="category-track"><span className="category-fill" style={{ display: 'block', width: `${Math.max(2, (item.cost / maxCost) * 100)}%` }} /></span>
                  <span className="category-total">{item.current}/{item.ideal} unid.</span>
                </div>)}
              </div>
              <div className="chart-legend"><span className="legend-item"><span className="legend-swatch" /> Stock ideal</span><span className="legend-item"><span className="legend-swatch blue" /> Stock actual</span></div>
            </>}
          </div>
        </article>

        <article className="card">
          <header className="card-header"><div><h2 className="card-title">Mayor valor inmovilizado</h2><p className="card-subtitle">Productos con más de 90 días en bodega.</p></div><button className="button button-ghost button-small" onClick={() => onNavigate('alerts')} type="button">Ver alertas <Icon name="chevron" size={13} /></button></header>
          <div className="card-body attention-list">
            {highestAgingValue.length === 0
              ? <EmptyState title="Sin productos mayores a 90 días" description="El catálogo no tiene referencias que superen el umbral de antigüedad." />
              : highestAgingValue.map((product) => <div className="attention-row" key={product.id}>
                <div><div className="product-primary">{product.name}</div><div className="product-secondary">{product.sku} · {product.daysInInventory} días</div></div>
                <span className="numeric">{formatCurrency(product.inventoryValue)}</span>
                <StatusBadge status={product.status} />
              </div>)}
          </div>
        </article>
      </section>

      <section className="card cash-flow-preview">
        <header className="card-header"><div><h2 className="card-title">Flujo de caja y compromisos</h2><p className="card-subtitle">Resumen del período seleccionado por defecto: mes actual hasta hoy.</p></div><button className="button button-small" onClick={() => onNavigate('cashflow')} type="button">Ver movimientos <Icon name="chevron" size={13} /></button></header>
        <div className="cash-preview-grid">
          <div><span>Ingresos del período</span><strong className="cash-positive">{formatCurrency(cashSummary?.periodIncomeCop)}</strong></div>
          <div><span>Gastos del período</span><strong className="cash-negative">{formatCurrency(cashSummary?.periodExpensesCop)}</strong></div>
          <div><span>Flujo neto del período</span><strong>{formatCurrency(cashSummary?.periodNetFlowCop)}</strong></div>
          <div><span>Compromisos próximos 30 días</span><strong>{formatCurrency(cashSummary?.commitmentsDueNext30DaysCop)}</strong></div>
          <div className="cash-capacity-note"><span>Sugerencias de compra: {formatCurrency(cashSummary?.suggestedReorderValueCop)}</span><StatusBadge status={cashSummary?.suggestedReorderWithinCapacity ? 'OPTIMAL' : 'CRITICAL'}>{cashSummary?.suggestedReorderWithinCapacity ? 'Dentro de capacidad demo' : 'Supera capacidad demo'}</StatusBadge></div>
        </div>
      </section>

      <article className="card">
        <header className="card-header"><div><h2 className="card-title">Alertas de inventario</h2><p className="card-subtitle">Prioriza primero quiebres y luego referencias de mayor antigüedad.</p></div><button className="button button-small" onClick={() => onNavigate('products')} type="button">Abrir catálogo <Icon name="chevron" size={13} /></button></header>
        <div className="table-scroll">
          <table className="data-table"><thead><tr><th>Producto / SKU</th><th>Stock</th><th>Antigüedad</th><th>Valor inventario</th><th>Estado</th></tr></thead>
            <tbody>{attentionProducts.map((product) => <tr key={product.id}>
              <td><div className="product-primary">{product.name}</div><div className="product-secondary">{product.sku} · {product.brand}</div></td>
              <td><span className="numeric">{product.stockCurrent} / {product.stockMinimum} mín.</span></td>
              <td><span className={`badge ${product.daysInInventory > 90 ? 'badge-overstock' : 'badge-neutral'}`}>{product.daysInInventory} días</span></td>
              <td className="numeric">{formatCurrency(product.inventoryValue)}</td><td><StatusBadge status={product.status} /></td>
            </tr>)}</tbody>
          </table>
          {!attentionProducts.length && <EmptyState title="Inventario saludable" description="No hay alertas críticas ni referencias con antigüedad superior a 90 días." />}
        </div>
      </article>
    </>
  )
}
