import { useState } from 'react'
import { Modal } from '../../components/common/Ui.jsx'
import { getCategoryLabel } from '../../utils/formatters.js'

const categories = ['SERVERS', 'LAPTOPS', 'ACCESSORIES', 'SOFTWARE', 'COMPONENTS', 'UPS']
const emptyForm = {
  sku: '', name: '', brand: '', model: '', category: 'COMPONENTS', subcategory: '',
  unitCost: '', salePrice: '', stockCurrent: 0, stockMinimum: 0, stockIdeal: 1,
  daysInInventory: 0, averageMonthlyDemand: 0, lastSaleDate: '', preferredSupplierId: '',
}

function TextField({ label, name, value, onChange, type = 'text', required = false, min, step, placeholder }) {
  return <label className="form-field"><span className="form-label">{label}{required && ' *'}</span><input className="input" name={name} onChange={onChange} type={type} value={value ?? ''} required={required} min={min} step={step} placeholder={placeholder} /></label>
}

/** Formulario controlado para alta y edición de producto. */
export default function ProductFormModal({ product, suppliers, onClose, onSave }) {
  const [form, setForm] = useState(() => product ? { ...product } : { ...emptyForm })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const handleChange = (event) => setForm((current) => ({ ...current, [event.target.name]: event.target.value }))
  const handleSubmit = async (event) => {
    event.preventDefault()
    setSaving(true)
    setError('')
    const payload = {
      sku: form.sku.trim(), name: form.name.trim(), brand: form.brand.trim(),
      model: form.model?.trim() || null, category: form.category,
      subcategory: form.subcategory?.trim() || null,
      unitCost: Number(form.unitCost), salePrice: Number(form.salePrice),
      stockCurrent: Number(form.stockCurrent), stockMinimum: Number(form.stockMinimum),
      stockIdeal: Number(form.stockIdeal), daysInInventory: Number(form.daysInInventory),
      averageMonthlyDemand: Number(form.averageMonthlyDemand), lastSaleDate: form.lastSaleDate || null,
      preferredSupplierId: Number(form.preferredSupplierId),
    }
    try { await onSave(payload) } catch (saveError) { setError(saveError.message) } finally { setSaving(false) }
  }

  return (
    <Modal title={product ? 'Editar producto' : 'Agregar producto'} description="Los cambios se validan y guardan en el backend." onClose={onClose}>
      <form onSubmit={handleSubmit}>
        {error && <div className="form-error" role="alert">{error}</div>}
        <div className="form-grid">
          <TextField label="SKU" name="sku" value={form.sku} onChange={handleChange} required placeholder="Ej. LAP-EMP-001" />
          <TextField label="Nombre del producto" name="name" value={form.name} onChange={handleChange} required />
          <TextField label="Marca" name="brand" value={form.brand} onChange={handleChange} required />
          <TextField label="Modelo" name="model" value={form.model} onChange={handleChange} />
          <label className="form-field"><span className="form-label">Categoría *</span><select className="select" name="category" value={form.category} onChange={handleChange} required>{categories.map((category) => <option key={category} value={category}>{getCategoryLabel(category)}</option>)}</select></label>
          <TextField label="Subcategoría" name="subcategory" value={form.subcategory} onChange={handleChange} />
          <TextField label="Costo unitario (COP)" name="unitCost" value={form.unitCost} onChange={handleChange} type="number" min="0" step="0.01" required />
          <TextField label="Precio de venta (COP)" name="salePrice" value={form.salePrice} onChange={handleChange} type="number" min="0" step="0.01" required />
          <TextField label="Stock actual" name="stockCurrent" value={form.stockCurrent} onChange={handleChange} type="number" min="0" required />
          <TextField label="Stock mínimo" name="stockMinimum" value={form.stockMinimum} onChange={handleChange} type="number" min="0" required />
          <TextField label="Stock ideal" name="stockIdeal" value={form.stockIdeal} onChange={handleChange} type="number" min="0" required />
          <TextField label="Días en inventario" name="daysInInventory" value={form.daysInInventory} onChange={handleChange} type="number" min="0" required />
          <TextField label="Demanda media mensual (unidades)" name="averageMonthlyDemand" value={form.averageMonthlyDemand} onChange={handleChange} type="number" min="0" step="0.1" required />
          <TextField label="Fecha de última venta" name="lastSaleDate" value={form.lastSaleDate || ''} onChange={handleChange} type="date" />
          <label className="form-field"><span className="form-label">Proveedor principal *</span><select className="select" name="preferredSupplierId" value={form.preferredSupplierId} onChange={handleChange} required><option value="">Selecciona proveedor</option>{suppliers.map((supplier) => <option key={supplier.id} value={supplier.id}>{supplier.businessName}</option>)}</select></label>
          <p className="form-help full">El stock mínimo debe ser menor o igual al stock ideal. La recomendación de compra se calcula hasta alcanzar el nivel ideal.</p>
        </div>
        <footer className="modal-footer"><button className="button" onClick={onClose} type="button">Cancelar</button><button className="button button-primary" disabled={saving || !suppliers.length} type="submit">{saving ? 'Guardando…' : product ? 'Guardar cambios' : 'Crear producto'}</button></footer>
      </form>
    </Modal>
  )
}
