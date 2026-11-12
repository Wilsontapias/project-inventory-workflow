import { useCallback, useEffect, useMemo, useState } from 'react'
import Icon from '../../components/common/Icon.jsx'
import { ErrorState, KpiCard, PageHeading, SearchField, StatusBadge } from '../../components/common/Ui.jsx'
import { getProducts } from '../../services/productsService.js'
import { formatCurrency, formatNumber } from '../../utils/formatters.js'

/** Vista combinada de quiebres, antigüedad y baja rotación. */
export default function AlertsPage({ onNavigate }) {
  const [products, setProducts] = useState([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const fetchProducts = useCallback(async () => {
    try { setProducts(await getProducts()); setError('') } catch (requestError) { setError(requestError.message) } finally { setLoading(false) }
  }, [])
  const load = useCallback(() => { setLoading(true); setError(''); return fetchProducts() }, [fetchProducts])
  // El efecto solo inicia la consulta inicial; el estado se actualiza al recibir la respuesta HTTP.
  // oxlint-disable-next-line react/set-state-in-effect
  useEffect(() => { fetchProducts() }, [fetchProducts])

  const critical = products.filter((product) => product.status === 'CRITICAL')
  const aging = products.filter((product) => product.daysInInventory > 90)
  const lowRotation = products.filter((product) => product.lowRotation)
  const visible = useMemo(() => products
    .filter((product) => product.status === 'CRITICAL' || product.daysInInventory > 90 || product.lowRotation)
    .filter((product) => `${product.sku} ${product.name} ${product.brand}`.toLowerCase().includes(search.toLowerCase()))
    .sort((left, right) => Number(right.status === 'CRITICAL') - Number(left.status === 'CRITICAL') || right.daysInInventory - left.daysInInventory), [products, search])

  return (
    <>
      <PageHeading eyebrow="Riesgo de inventario" title="Alertas y rotación" description="Identifica productos críticos, con más de 90 días en bodega o sin ventas durante los últimos 90 días." actions={<button className="button" onClick={load} type="button"><Icon name="refresh" size={14} /> Actualizar</button>} />
      {error ? <ErrorState message={error} onRetry={load} /> : <>
        <div className="kpi-grid alert-kpis">
          <KpiCard label="Productos críticos" value={formatNumber(critical.length)} detail={<>Regla: stock actual <strong>≤ stock mínimo</strong></>} icon="alert" tone="red" />
          <KpiCard label="Más de 90 días" value={formatNumber(aging.length)} detail={<>Regla estricta: <strong>días en inventario &gt; 90</strong></>} icon="clock" tone="amber" />
          <KpiCard label="Baja rotación" value={formatNumber(lowRotation.length)} detail={<>Sin ventas en 90 días y edad &gt; 90 días</>} icon="chart" tone="amber" />
          <KpiCard label="Capital > 90 días" value={formatCurrency(aging.reduce((sum, product) => sum + Number(product.inventoryValue), 0))} detail={<>Valor al costo de las existencias antiguas</>} icon="wallet" tone="blue" compact />
          <KpiCard label="Productos monitoreados" value={formatNumber(products.length)} detail={<>Referencias del catálogo API</>} icon="box" tone="green" />
        </div>
        <div className="toolbar"><SearchField value={search} onChange={setSearch} placeholder="Buscar SKU, producto o marca…" /><span className="toolbar-count">{loading ? 'Cargando…' : `${visible.length} alertas`}</span></div>
        <section className="card table-card">
          {loading ? <div className="loading-state"><span className="loading-spinner" /><p className="state-title">Calculando alertas…</p></div> : visible.length ? <div className="table-scroll"><table className="data-table alert-table"><thead><tr><th>Producto / SKU</th><th>Motivo</th><th>Stock actual / mín. / ideal</th><th>Demanda media</th><th>Antigüedad</th><th>Última venta</th><th>Capital inmovilizado</th><th>Estado</th><th /></tr></thead><tbody>{visible.map((product) => <tr key={product.id}>
            <td><div className="product-primary">{product.name}</div><div className="product-secondary">{product.sku} · {product.brand}</div></td>
            <td><div className="alert-reasons">{product.status === 'CRITICAL' && <span className="badge badge-critical">Stock en mínimo</span>}{product.lowRotation && <span className="badge badge-overstock">Baja rotación</span>}{product.daysInInventory > 90 && <span className="badge badge-neutral">Permanencia &gt; 90 días</span>}</div></td>
            <td className="numeric text-center">{product.stockCurrent} / {product.stockMinimum} / {product.stockIdeal}</td>
            <td className="numeric">{product.averageMonthlyDemand} unid./mes</td>
            <td><span className={`badge ${product.daysInInventory > 90 ? 'badge-overstock' : ''}`}>{product.daysInInventory} días</span></td>
            <td className="numeric">{product.lastSaleDate || 'Sin registro'}</td>
            <td className="numeric">{formatCurrency(product.inventoryValue)}</td>
            <td><StatusBadge status={product.status} /></td>
            <td><button aria-label={`Ver ${product.name} en catálogo`} className="table-action" onClick={() => onNavigate('products')} title="Ir al catálogo" type="button"><Icon name="chevron" size={14} /></button></td>
          </tr>)}</tbody></table></div> : <div className="empty-state"><span className="empty-state-icon"><Icon name="check" size={18} /></span><p className="state-title">Sin alertas para esos filtros</p><p className="state-description">No hay productos críticos, antiguos o con baja rotación que coincidan con la búsqueda.</p></div>}
          {!loading && <div className="table-footer"><span>Datos leídos desde el catálogo de productos</span><span>Las reglas se calculan en el backend</span></div>}
        </section>
      </>}
    </>
  )
}
