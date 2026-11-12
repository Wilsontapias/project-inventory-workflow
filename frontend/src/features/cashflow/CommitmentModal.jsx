import { useState } from 'react'
import { Modal } from '../../components/common/Ui.jsx'

function localDateValue(date) {
  return new Date(date.getTime() - date.getTimezoneOffset() * 60_000).toISOString().slice(0, 10)
}
const DEFAULT_DUE_DATE = (() => {
  const date = new Date()
  date.setDate(date.getDate() + 7)
  return localDateValue(date)
})()

/** Formulario para crear un compromiso de pago futuro demostrativo. */
export default function CommitmentModal({ suppliers, onClose, onSave }) {
  const [form, setForm] = useState({ dueDate: DEFAULT_DUE_DATE, description: '', amount: '', supplierId: '' })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const change = (event) => setForm((current) => ({ ...current, [event.target.name]: event.target.value }))
  const submit = async (event) => {
    event.preventDefault()
    setSaving(true)
    setError('')
    const payload = { ...form, amount: Number(form.amount), supplierId: form.supplierId ? Number(form.supplierId) : null }
    try { await onSave(payload) } catch (saveError) { setError(saveError.message) } finally { setSaving(false) }
  }

  return (
    <Modal title="Agregar compromiso" description="El compromiso abierto se descuenta de la capacidad de reorden si vence en los próximos 30 días." onClose={onClose} width="540px">
      <form onSubmit={submit}>
        {error && <div className="form-error" role="alert">{error}</div>}
        <div className="form-grid">
          <label className="form-field full"><span className="form-label">Descripción *</span><input className="input" name="description" onChange={change} value={form.description} maxLength="160" required /></label>
          <label className="form-field"><span className="form-label">Fecha de vencimiento *</span><input className="input" name="dueDate" onChange={change} type="date" value={form.dueDate} required /></label>
          <label className="form-field"><span className="form-label">Monto (COP) *</span><input className="input" name="amount" onChange={change} value={form.amount} type="number" min="0.01" step="0.01" required /></label>
          <label className="form-field full"><span className="form-label">Proveedor asociado</span><select className="select" name="supplierId" onChange={change} value={form.supplierId}><option value="">Sin proveedor asociado</option>{suppliers.map((supplier) => <option key={supplier.id} value={supplier.id}>{supplier.businessName}</option>)}</select></label>
          <p className="form-help full">Valores ficticios de demostración. Al marcarlo pagado se registra el gasto y deja de descontarse como compromiso futuro.</p>
        </div>
        <footer className="modal-footer"><button className="button" onClick={onClose} type="button">Cancelar</button><button className="button button-primary" disabled={saving} type="submit">{saving ? 'Guardando…' : 'Crear compromiso'}</button></footer>
      </form>
    </Modal>
  )
}
