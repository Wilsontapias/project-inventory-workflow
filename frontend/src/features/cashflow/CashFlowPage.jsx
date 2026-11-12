import { useCallback, useEffect, useMemo, useState } from 'react'
import Icon from '../../components/common/Icon.jsx'
import { ErrorState, KpiCard, PageHeading, StatusBadge } from '../../components/common/Ui.jsx'
import { useToast } from '../../components/layout/toastContext.js'
import { createCashMovement, createCommitment, getCashFlowSummary, getCashMovements, getCommitments, payCommitment } from '../../services/cashFlowService.js'
import { getSuppliers } from '../../services/suppliersService.js'
import { formatCurrency, formatNumber } from '../../utils/formatters.js'
import CashMovementModal from './CashMovementModal.jsx'
import CommitmentModal from './CommitmentModal.jsx'

function localDateValue(date) {
  return new Date(date.getTime() - date.getTimezoneOffset() * 60_000).toISOString().slice(0, 10)
}
const TODAY = localDateValue(new Date())
const MONTH_START = `${TODAY.slice(0, 7)}-01`

/** Vista de flujo de caja de demostración y compromisos de pago. */
export default function CashFlowPage() {
  const notify = useToast()
  const [periodStart, setPeriodStart] = useState(MONTH_START)
  const [periodEnd, setPeriodEnd] = useState(TODAY)
  const [summary, setSummary] = useState(null)
  const [movements, setMovements] = useState([])
  const [commitments, setCommitments] = useState([])
  const [suppliers, setSuppliers] = useState([])
  const [modal, setModal] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const fetchData = useCallback(async () => {
    try {
      const [summaryData, movementData, commitmentData, supplierData] = await Promise.all([
        getCashFlowSummary({ from: periodStart, to: periodEnd }),
        getCashMovements({ from: periodStart, to: periodEnd }),
        getCommitments(),
        getSuppliers(),
      ])
      setSummary(summaryData)
      setMovements(movementData)
      setCommitments(commitmentData)
      setSuppliers(supplierData)
      setError('')
    } catch (requestError) { setError(requestError.message) } finally { setLoading(false) }
  }, [periodStart, periodEnd])

  const reload = useCallback(() => { setLoading(true); setError(''); return fetchData() }, [fetchData])
  // La carga inicial solo consulta servicios; los estados se actualizan al resolver las peticiones.
  // oxlint-disable-next-line react/set-state-in-effect
  useEffect(() => { fetchData() }, [fetchData])

  const openCommitments = useMemo(() => commitments.filter((commitment) => commitment.status === 'OPEN'), [commitments])
  const handleMovementSave = async (payload) => {
    await createCashMovement(payload)
    setModal(null)
    notify('Movimiento ficticio registrado.')
    await reload()
  }
  const handleCommitmentSave = async (payload) => {
    await createCommitment(payload)
    setModal(null)
    notify('Compromiso ficticio agregado.')
    await reload()
  }
  const handlePay = async (commitment) => {
    if (!window.confirm(`¿Marcar como pagado “${commitment.description}”? Se registrará un gasto de ${formatCurrency(commitment.amount)}.`)) return
    try {
      await payCommitment(commitment.id)
      notify('Compromiso pagado; el gasto se registró una sola vez.')
      await reload()
    } catch (paymentError) { notify(paymentError.message, 'error') }
  }

  return (
    <>
      <PageHeading eyebrow="Finanzas demostrativas" title="Flujo de caja y compromisos" description="Resumen ficticio de movimientos del mes, pagos pendientes y capacidad estimada para reordenar. No representa saldos reales." actions={<><button className="button" onClick={() => setModal('movement')} type="button"><Icon name="plus" size={14} /> Registrar movimiento</button><button className="button button-primary" onClick={() => setModal('commitment')} type="button"><Icon name="plus" size={14} /> Agregar compromiso</button></>} />
      {error ? <ErrorState message={error} onRetry={reload} /> : <>
        <div className="cash-period-toolbar card">
          <div className="cash-period-copy"><Icon name="calendar" size={15} /><span>Período del flujo</span></div>
          <label><span>Desde</span><input className="input" type="date" value={periodStart} onChange={(event) => { setLoading(true); setPeriodStart(event.target.value) }} /></label>
          <label><span>Hasta</span><input className="input" type="date" value={periodEnd} onChange={(event) => { setLoading(true); setPeriodEnd(event.target.value) }} /></label>
          <button className="button button-small" disabled={loading} onClick={reload} type="button"><Icon name="refresh" size={13} /> Actualizar</button>
        </div>

        {loading && !summary ? <div className="card loading-state"><span className="loading-spinner" /><p className="state-title">Calculando flujo de caja…</p></div> : <>
          <section className="kpi-grid cash-kpis" aria-label="Indicadores de caja">
            <KpiCard label="Saldo inicial demo" value={formatCurrency(summary?.openingBalanceCop)} detail={<>Monto de configuración ficticio</>} icon="wallet" tone="blue" compact />
            <KpiCard label="Saldo de caja demo" value={formatCurrency(summary?.cashBalanceCop)} detail={<>Saldo inicial + ingresos - gastos registrados</>} icon="wallet" tone="green" compact />
            <KpiCard label="Flujo neto del período" value={formatCurrency(summary?.periodNetFlowCop)} detail={<>{formatCurrency(summary?.periodIncomeCop)} ingresos − {formatCurrency(summary?.periodExpensesCop)} gastos</>} icon="chart" tone={Number(summary?.periodNetFlowCop) >= 0 ? 'green' : 'red'} compact />
            <KpiCard label="Compromisos próximos 30 días" value={formatCurrency(summary?.commitmentsDueNext30DaysCop)} detail={<>Compromisos abiertos con vencimiento próximo</>} icon="calendar" tone="amber" compact />
            <KpiCard label="Capacidad neta de reorden" value={formatCurrency(summary?.availableForReorderCop)} detail={<>Saldo menos compromisos pendientes</>} icon="cart" tone={Number(summary?.availableForReorderCop) >= 0 ? 'blue' : 'red'} compact />
          </section>
          <div className="info-note"><Icon name="info" size={15} /><span>{summary?.dataNote} La capacidad se contrasta con {formatCurrency(summary?.suggestedReorderValueCop)} en sugerencias de compra.</span></div>

          <section className="cash-flow-grid">
            <article className="card">
              <header className="card-header"><div><h2 className="card-title">Movimientos del período</h2><p className="card-subtitle">Ingresos y gastos registrados en COP.</p></div><span className="badge badge-neutral">{formatNumber(movements.length)} movimientos</span></header>
              {movements.length ? <div className="table-scroll"><table className="data-table cash-table"><thead><tr><th>Fecha</th><th>Tipo</th><th>Descripción</th><th>Categoría</th><th className="text-right">Monto</th></tr></thead><tbody>{movements.map((movement) => <tr key={movement.id}>
                <td className="numeric">{movement.movementDate}</td><td><StatusBadge status={movement.type === 'INCOME' ? 'OPTIMAL' : 'CRITICAL'}>{movement.type === 'INCOME' ? 'Ingreso' : 'Gasto'}</StatusBadge></td><td className="product-name">{movement.description}</td><td>{movement.category}</td><td className={`numeric text-right ${movement.type === 'INCOME' ? 'cash-positive' : 'cash-negative'}`}>{movement.type === 'INCOME' ? '+' : '−'}{formatCurrency(movement.amount)}</td>
              </tr>)}</tbody></table></div> : <div className="empty-state"><p className="state-title">Sin movimientos en este período</p><p className="state-description">Registra ingresos y gastos ficticios para visualizar el flujo.</p></div>}
              <div className="table-footer"><span>{summary?.periodStart} a {summary?.periodEnd}</span><span>Saldo inicial configurado: {formatCurrency(summary?.openingBalanceCop)}</span></div>
            </article>

            <article className="card">
              <header className="card-header"><div><h2 className="card-title">Compromisos de pago</h2><p className="card-subtitle">Los abiertos con vencimiento en los próximos 30 días restan capacidad; al pagar se registra un gasto.</p></div><span className="badge badge-overstock">{openCommitments.length} abiertos</span></header>
              {commitments.length ? <div className="table-scroll"><table className="data-table commitment-table"><thead><tr><th>Vence</th><th>Compromiso / proveedor</th><th className="text-right">Monto</th><th>Estado</th><th /></tr></thead><tbody>{commitments.map((commitment) => <tr key={commitment.id}>
                <td className="numeric">{commitment.dueDate}</td><td><div className="product-primary">{commitment.description}</div><div className="product-secondary">{commitment.supplierName || 'Sin proveedor asociado'}</div></td><td className="numeric text-right">{formatCurrency(commitment.amount)}</td><td><StatusBadge status={commitment.status === 'PAID' ? 'OPTIMAL' : commitment.dueDate < TODAY ? 'CRITICAL' : 'OVERSTOCK'}>{commitment.status === 'PAID' ? 'Pagado' : commitment.dueDate < TODAY ? 'Vencido' : 'Pendiente'}</StatusBadge></td><td>{commitment.status === 'OPEN' && <button className="button button-small" onClick={() => handlePay(commitment)} type="button">Marcar pagado</button>}</td>
              </tr>)}</tbody></table></div> : <div className="empty-state"><p className="state-title">Sin compromisos</p><p className="state-description">Agrega compromisos ficticios para evaluar su efecto en el reorden.</p></div>}
              <footer className="commitment-capacity"><span>Reabastecimiento sugerido</span><strong>{formatCurrency(summary?.suggestedReorderValueCop)}</strong><StatusBadge status={summary?.suggestedReorderWithinCapacity ? 'OPTIMAL' : 'CRITICAL'}>{summary?.suggestedReorderWithinCapacity ? 'Dentro de capacidad' : 'Supera capacidad'}</StatusBadge></footer>
            </article>
          </section>
        </>}
      </>}
      {modal === 'movement' && <CashMovementModal onClose={() => setModal(null)} onSave={handleMovementSave} />}
      {modal === 'commitment' && <CommitmentModal suppliers={suppliers} onClose={() => setModal(null)} onSave={handleCommitmentSave} />}
    </>
  )
}
