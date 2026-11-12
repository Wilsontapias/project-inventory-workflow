import { useCallback, useEffect, useMemo, useState } from 'react'
import Icon from '../../components/common/Icon.jsx'
import { ErrorState, PageHeading, SearchField } from '../../components/common/Ui.jsx'
import { getPurchaseSuggestions } from '../../services/purchasesService.js'
import { formatCurrency, formatNumber } from '../../utils/formatters.js'
import SupplierOffersModal from './SupplierOffersModal.jsx'

/** Sugerencias de reabastecimiento calculadas a partir de stock y ofertas reales de la API demo. */
export default function PurchaseSuggestionsPage() {
  const [suggestions, setSuggestions] = useState([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selectedSuggestion, setSelectedSuggestion] = useState(null)

  const fetchSuggestions = useCallback(async () => {
    try { setSuggestions(await getPurchaseSuggestions({ search })); setError('') } catch (requestError) { setError(requestError.message) } finally { setLoading(false) }
  }, [search])
  const load = useCallback(() => { setLoading(true); setError(''); return fetchSuggestions() }, [fetchSuggestions])
  // El efecto solo inicia la consulta inicial; el estado se actualiza al recibir la respuesta HTTP.
  // oxlint-disable-next-line react/set-state-in-effect
  useEffect(() => { fetchSuggestions() }, [fetchSuggestions])

  const totalValue = useMemo(() => suggestions.reduce((sum, suggestion) => sum + Number(suggestion.estimatedTotal), 0), [suggestions])
  const totalUnits = useMemo(() => suggestions.reduce((sum, suggestion) => sum + suggestion.suggestedQuantity, 0), [suggestions])

  return (
    <>
      <PageHeading eyebrow="Abastecimiento y comparación" title="Órdenes de compra sugeridas" description="Recomendaciones justificadas por stock, nivel ideal y ofertas registradas. Son propuestas de apoyo, no órdenes aprobadas." actions={<button className="button" onClick={load} type="button"><Icon name="refresh" size={14} /> Actualizar</button>} />
      {error ? <ErrorState message={error} onRetry={load} /> : <>
        <div className="suggestion-summary"><Icon name="info" size={15} /><span><strong>La cantidad sugerida se calcula hasta el stock ideal.</strong> Revisa disponibilidad, costo y plazo de cada proveedor antes de comprar.</span></div>
        <section className="toolbar"><SearchField value={search} onChange={setSearch} placeholder="Filtrar por SKU o producto…" /><span className="toolbar-count">{loading ? 'Consultando…' : `${suggestions.length} recomendaciones`}</span></section>
        <div className="suggestion-layout">
          <section className="card table-card" aria-label="Sugerencias de compra">
            {loading ? <div className="loading-state"><span className="loading-spinner" /><p className="state-title">Calculando sugerencias…</p></div> : suggestions.length ? <div className="table-scroll"><table className="data-table suggestion-table"><thead><tr><th>Producto / SKU</th><th>Stock actual / mín.</th><th className="text-center">Cantidad</th><th>Proveedor recomendado</th><th className="text-right">Costo estimado</th><th>Justificación</th><th /></tr></thead><tbody>
              {suggestions.map((suggestion) => <tr key={suggestion.productId}>
                <td><div className="product-primary">{suggestion.productName}</div><div className="product-secondary">{suggestion.sku}</div></td>
                <td><div className="stock-main">{suggestion.stockCurrent} / {suggestion.stockMinimum}</div><div className="stock-caption">actual / mínimo · ideal {suggestion.stockIdeal}</div></td>
                <td className="text-center"><span className="badge badge-neutral">{suggestion.suggestedQuantity} unid.</span></td>
                <td><div className="product-primary">{suggestion.recommendedSupplierName}</div><div className="product-secondary">{suggestion.supplierAlternatives.length} ofertas para comparar</div></td>
                <td className="numeric text-right">{formatCurrency(suggestion.estimatedTotal)}</td>
                <td><div className="justification">{suggestion.justification}</div></td>
                <td><button className="button button-small" onClick={() => setSelectedSuggestion(suggestion)} type="button">Comparar</button></td>
              </tr>)}
            </tbody></table></div> : <div className="empty-state"><span className="empty-state-icon"><Icon name="check" size={18} /></span><p className="state-title">No hay sugerencias pendientes</p><p className="state-description">No se encontraron productos bajo el mínimo que requieran reabastecimiento.</p></div>}
            {!loading && <div className="table-footer"><span>Regla: stock actual ≤ mínimo y menor que ideal</span><span>Importes calculados al costo ofertado</span></div>}
          </section>
          <aside className="card recommendation-panel">
            <h2>Resumen del lote sugerido</h2><p>Estimación de las recomendaciones que devuelve el backend para los filtros actuales.</p>
            <div className="recommendation-total"><span>Inversión estimada</span><strong>{formatCurrency(totalValue)}</strong></div>
            <div className="recommendation-metric"><span>Productos sugeridos</span><strong>{formatNumber(suggestions.length)}</strong></div>
            <div className="recommendation-metric"><span>Unidades a reponer</span><strong>{formatNumber(totalUnits)}</strong></div>
            {suggestions[0] && <><div className="recommendation-metric"><span>Mayor sugerencia</span><strong>{suggestions[0].suggestedQuantity} unid.</strong></div><p className="form-help">Los valores son ficticios y no descuentan pagos ni consideran un saldo disponible.</p></>}
            <button className="button button-primary" disabled={!suggestions.length} onClick={() => suggestions[0] && setSelectedSuggestion(suggestions[0])} type="button"><Icon name="building" size={14} /> Comparar primera recomendación</button>
          </aside>
        </div>
      </>}
      {selectedSuggestion && <SupplierOffersModal suggestion={selectedSuggestion} onClose={() => setSelectedSuggestion(null)} />}
    </>
  )
}
