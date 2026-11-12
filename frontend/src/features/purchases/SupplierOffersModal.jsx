import { useEffect, useState } from 'react'
import { ErrorState, LoadingState, Modal, StatusBadge } from '../../components/common/Ui.jsx'
import { getSupplierOffers } from '../../services/purchasesService.js'
import { formatCurrency } from '../../utils/formatters.js'

/** Muestra las cotizaciones registradas para una sugerencia, sin crear una orden. */
export default function SupplierOffersModal({ suggestion, onClose }) {
  const [offers, setOffers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    getSupplierOffers(suggestion.productId)
      .then((data) => { if (active) setOffers(data) })
      .catch((requestError) => { if (active) setError(requestError.message) })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [suggestion.productId])

  return (
    <Modal title="Comparar proveedores" description={`${suggestion.productName} · ${suggestion.sku} · cantidad sugerida: ${suggestion.suggestedQuantity}`} onClose={onClose} width="720px">
      {loading ? <LoadingState label="Consultando ofertas…" /> : error ? <ErrorState message={error} /> : offers.length ? <>
        <div className="offer-list">{offers.map((offer) => <article className={`offer-card ${offer.supplierId === suggestion.recommendedSupplierId ? 'recommended' : ''}`} key={offer.id}>
          <div className="offer-top"><div><div className="offer-name">{offer.supplierName}</div><div className="offer-terms">Entrega: {offer.leadTimeDays} días · Crédito: {offer.paymentTermDays} días · Disponible: {offer.availableStock} unid.</div></div><div className="text-right"><div className="offer-price">{formatCurrency(offer.unitCost)}</div><div className="offer-terms">Total estimado: {formatCurrency(offer.estimatedTotal)}</div></div></div>
          {offer.supplierId === suggestion.recommendedSupplierId && <div className="offer-recommended"><StatusBadge status="OPTIMAL">Recomendado por la regla de costo y disponibilidad</StatusBadge></div>}
          {offer.availableStock < suggestion.suggestedQuantity && <p className="offer-warning">No cubre por sí sola la cantidad sugerida; confirmar disponibilidad.</p>}
        </article>)}</div>
        <p className="form-help offer-disclaimer">Comparación informativa de ofertas ficticias. Esta pantalla no emite ni confirma órdenes de compra.</p>
      </> : <div className="empty-state"><span className="empty-state-icon">—</span><p className="state-title">Sin ofertas registradas</p><p className="state-description">No hay cotizaciones para comparar este producto.</p></div>}
      <footer className="modal-footer"><button className="button" onClick={onClose} type="button">Cerrar</button></footer>
    </Modal>
  )
}
