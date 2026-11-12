import { useState } from 'react'
import { Modal } from '../../components/common/Ui.jsx'
import { getCategoryLabel } from '../../utils/formatters.js'

const categories = ['SERVERS', 'COMPONENTS', 'COMPUTERS', 'POWER', 'SOFTWARE']
const emptyForm = {
  taxId: '', businessName: '', city: '', address: '', billingEmail: '', phone: '', contactName: '',
  category: 'COMPUTERS', paymentTermDays: 30, leadTimeBusinessDays: 5,
  creditLimit: 0, creditUsed: 0, performanceScore: 90, representedBrands: '',
}

/** Formulario de proveedor alineado con el DTO validado del backend. */
export default function SupplierFormModal({ supplier, onClose, onSave }) {
  const [form, setForm] = useState(() => supplier ? { ...supplier } : { ...emptyForm })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const handleChange = (event) => setForm((current) => ({ ...current, [event.target.name]: event.target.value }))

  const submit = async (event) => {
    event.preventDefault()
    setSaving(true)
    setError('')
    const payload = {
      taxId: form.taxId.trim(), businessName: form.businessName.trim(), city: form.city.trim(),
      address: form.address?.trim() || null, billingEmail: form.billingEmail?.trim() || null,
      phone: form.phone?.trim() || null, contactName: form.contactName?.trim() || null,
      category: form.category, representedBrands: form.representedBrands?.trim() || null,
      paymentTermDays: Number(form.paymentTermDays), leadTimeBusinessDays: Number(form.leadTimeBusinessDays),
      creditLimit: Number(form.creditLimit), creditUsed: Number(form.creditUsed), performanceScore: Number(form.performanceScore),
    }
    try { await onSave(payload) } catch (saveError) { setError(saveError.message) } finally { setSaving(false) }
  }

  return (
    <Modal title={supplier ? 'Editar proveedor' : 'Agregar proveedor'} description="Registra información comercial ficticia para el prototipo." onClose={onClose}>
      <form onSubmit={submit}>
        {error && <div className="form-error" role="alert">{error}</div>}
        <div className="form-grid">
          <label className="form-field"><span className="form-label">Razón social *</span><input className="input" name="businessName" onChange={handleChange} value={form.businessName || ''} required maxLength="160" /></label>
          <label className="form-field"><span className="form-label">NIT / RUT ficticio *</span><input className="input" name="taxId" onChange={handleChange} value={form.taxId || ''} required maxLength="30" /></label>
          <label className="form-field"><span className="form-label">Ciudad *</span><input className="input" name="city" onChange={handleChange} value={form.city || ''} required maxLength="80" /></label>
          <label className="form-field"><span className="form-label">Categoría *</span><select className="select" name="category" onChange={handleChange} value={form.category || 'COMPUTERS'}>{categories.map((category) => <option key={category} value={category}>{getCategoryLabel(category)}</option>)}</select></label>
          <label className="form-field full"><span className="form-label">Dirección</span><input className="input" name="address" onChange={handleChange} value={form.address || ''} maxLength="180" /></label>
          <label className="form-field"><span className="form-label">Correo de facturación</span><input className="input" name="billingEmail" onChange={handleChange} value={form.billingEmail || ''} type="email" maxLength="140" placeholder="contacto@example.invalid" /></label>
          <label className="form-field"><span className="form-label">Teléfono</span><input className="input" name="phone" onChange={handleChange} value={form.phone || ''} maxLength="40" /></label>
          <label className="form-field"><span className="form-label">Contacto comercial</span><input className="input" name="contactName" onChange={handleChange} value={form.contactName || ''} maxLength="120" /></label>
          <label className="form-field"><span className="form-label">Marcas representadas</span><input className="input" name="representedBrands" onChange={handleChange} value={form.representedBrands || ''} maxLength="300" /></label>
          <label className="form-field"><span className="form-label">Plazo de pago (días) *</span><input className="input" name="paymentTermDays" onChange={handleChange} value={form.paymentTermDays} type="number" min="0" required /></label>
          <label className="form-field"><span className="form-label">Lead time (días hábiles) *</span><input className="input" name="leadTimeBusinessDays" onChange={handleChange} value={form.leadTimeBusinessDays} type="number" min="1" required /></label>
          <label className="form-field"><span className="form-label">Cupo de crédito (COP) *</span><input className="input" name="creditLimit" onChange={handleChange} value={form.creditLimit} type="number" min="0" step="0.01" required /></label>
          <label className="form-field"><span className="form-label">Cupo utilizado (COP) *</span><input className="input" name="creditUsed" onChange={handleChange} value={form.creditUsed} type="number" min="0" step="0.01" required /></label>
          <label className="form-field"><span className="form-label">Desempeño (%) *</span><input className="input" name="performanceScore" onChange={handleChange} value={form.performanceScore} type="number" min="0" max="100" step="0.1" required /></label>
          <p className="form-help full">No registres datos bancarios ni información comercial real. El cupo usado no puede superar el cupo total.</p>
        </div>
        <footer className="modal-footer"><button className="button" onClick={onClose} type="button">Cancelar</button><button className="button button-primary" disabled={saving} type="submit">{saving ? 'Guardando…' : supplier ? 'Guardar cambios' : 'Crear proveedor'}</button></footer>
      </form>
    </Modal>
  )
}
