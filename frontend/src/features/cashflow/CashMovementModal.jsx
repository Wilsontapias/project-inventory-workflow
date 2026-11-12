import { useState } from 'react'
import { Modal } from '../../components/common/Ui.jsx'

function localDateValue(date) {
  return new Date(date.getTime() - date.getTimezoneOffset() * 60_000).toISOString().slice(0, 10)
}
const TODAY = localDateValue(new Date())

/** Formulario para registrar un ingreso o gasto ficticio. */
export default function CashMovementModal({ onClose, onSave }) {
  const [form, setForm] = useState({
    movementDate: TODAY, type: 'INCOME', description: '', category: 'Operación', amount: '',
  })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const change = (event) => setForm((current) => ({ ...current, [event.target.name]: event.target.value }))
  const submit = async (event) => {
    event.preventDefault()
    setSaving(true)
    setError('')
    try { await onSave({ ...form, amount: Number(form.amount) }) } catch (saveError) { setError(saveError.message) } finally { setSaving(false) }
  }

  return (
    <Modal title="Registrar movimiento" description="Agrega un ingreso o gasto ficticio para el flujo del período." onClose={onClose} width="540px">
      <form onSubmit={submit}>
        {error && <div className="form-error" role="alert">{error}</div>}
        <div className="form-grid">
          <label className="form-field"><span className="form-label">Tipo de movimiento *</span><select className="select" name="type" onChange={change} value={form.type}><option value="INCOME">Ingreso</option><option value="EXPENSE">Gasto</option></select></label>
          <label className="form-field"><span className="form-label">Fecha *</span><input className="input" name="movementDate" onChange={change} type="date" value={form.movementDate} required /></label>
          <label className="form-field full"><span className="form-label">Descripción *</span><input className="input" name="description" onChange={change} value={form.description} maxLength="160" required /></label>
          <label className="form-field"><span className="form-label">Categoría *</span><input className="input" name="category" onChange={change} value={form.category} maxLength="80" required /></label>
          <label className="form-field"><span className="form-label">Monto (COP) *</span><input className="input" name="amount" onChange={change} value={form.amount} type="number" min="0.01" step="0.01" required /></label>
          <p className="form-help full">El saldo inicial y todos los movimientos son ficticios. No registres cifras de caja real.</p>
        </div>
        <footer className="modal-footer"><button className="button" onClick={onClose} type="button">Cancelar</button><button className="button button-primary" disabled={saving} type="submit">{saving ? 'Guardando…' : 'Registrar movimiento'}</button></footer>
      </form>
    </Modal>
  )
}
