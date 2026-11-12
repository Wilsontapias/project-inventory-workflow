import { useCallback, useEffect, useMemo, useState } from 'react'
import Icon from '../../components/common/Icon.jsx'
import { ErrorState, PageHeading, SearchField, StatusBadge } from '../../components/common/Ui.jsx'
import { useToast } from '../../components/layout/toastContext.js'
import { deleteSupplier, getSuppliers, createSupplier, updateSupplier } from '../../services/suppliersService.js'
import { getProducts } from '../../services/productsService.js'
import { formatCurrency, getCategoryLabel, getInitials } from '../../utils/formatters.js'
import SupplierFormModal from './SupplierFormModal.jsx'

const categories = ['', 'SERVERS', 'COMPONENTS', 'COMPUTERS', 'POWER', 'SOFTWARE']

/** Directorio de proveedores con búsqueda, filtros y operaciones CRUD. */
export default function SuppliersPage() {
  const notify = useToast()
  const [suppliers, setSuppliers] = useState([])
  const [productList, setProductList] = useState([])
  const [search, setSearch] = useState('')
  const [category, setCategory] = useState('')
  const [paymentTerm, setPaymentTerm] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [editingSupplier, setEditingSupplier] = useState(undefined)

  const fetchData = useCallback(async () => {
    try {
      const [supplierData, productData] = await Promise.all([getSuppliers(), getProducts()])
      setSuppliers(supplierData)
      setProductList(productData)
      setError('')
    } catch (requestError) { setError(requestError.message) } finally { setLoading(false) }
  }, [])
  const load = useCallback(() => { setLoading(true); setError(''); return fetchData() }, [fetchData])
  // El efecto solo inicia la consulta inicial; el estado se actualiza al recibir la respuesta HTTP.
  // oxlint-disable-next-line react/set-state-in-effect
  useEffect(() => { fetchData() }, [fetchData])

  const visibleSuppliers = useMemo(() => suppliers.filter((supplier) => {
    const query = search.toLowerCase()
    const matchesSearch = !query || `${supplier.businessName} ${supplier.taxId} ${supplier.representedBrands || ''}`.toLowerCase().includes(query)
    return matchesSearch && (!category || supplier.category === category)
      && (paymentTerm === '' || supplier.paymentTermDays === Number(paymentTerm))
  }), [suppliers, search, category, paymentTerm])

  const handleSave = async (payload) => {
    if (editingSupplier) await updateSupplier(editingSupplier.id, payload)
    else await createSupplier(payload)
    setEditingSupplier(undefined)
    notify(editingSupplier ? 'Proveedor actualizado.' : 'Proveedor agregado al directorio.')
    await load()
  }

  const handleDelete = async (supplier) => {
    if (!window.confirm(`¿Eliminar el proveedor ${supplier.businessName}?`)) return
    try {
      await deleteSupplier(supplier.id)
      notify('Proveedor eliminado.')
      await load()
    } catch (deleteError) { notify(deleteError.message, 'error') }
  }

  return (
    <>
      <PageHeading eyebrow="Abastecimiento y aliados comerciales" title="Directorio de proveedores" description="Consulta condiciones de pago, tiempos de entrega, cupo registrado y ofertas asociadas a los productos." actions={<button className="button button-primary" onClick={() => setEditingSupplier(null)} type="button"><Icon name="plus" size={15} /> Agregar proveedor</button>} />
      {error ? <ErrorState message={error} onRetry={load} /> : <>
        <div className="supplier-overview card">
          <div className="supplier-overview-item"><span>Proveedores registrados</span><strong>{suppliers.length}</strong></div>
          <div className="supplier-overview-item"><span>Promedio de cumplimiento</span><strong>{suppliers.length ? `${(suppliers.reduce((sum, supplier) => sum + Number(supplier.performanceScore), 0) / suppliers.length).toFixed(1)}%` : '—'}</strong></div>
          <div className="supplier-overview-item"><span>Cupo de crédito disponible*</span><strong>{formatCurrency(suppliers.reduce((sum, supplier) => sum + Number(supplier.availableCredit), 0))}</strong></div>
          <small>* Suma de datos ficticios de proveedores; no equivale a liquidez del negocio.</small>
        </div>
        <section className="toolbar" aria-label="Filtros de proveedores">
          <SearchField value={search} onChange={setSearch} placeholder="Buscar razón social, NIT o marca…" />
          <select aria-label="Filtrar categoría de proveedor" className="select" value={category} onChange={(event) => setCategory(event.target.value)}><option value="">Todas las categorías</option>{categories.filter(Boolean).map((value) => <option key={value} value={value}>{getCategoryLabel(value)}</option>)}</select>
          <select aria-label="Filtrar plazo de pago" className="select" value={paymentTerm} onChange={(event) => setPaymentTerm(event.target.value)}><option value="">Todos los plazos</option><option value="0">Contado</option><option value="15">15 días</option><option value="30">30 días</option><option value="45">45 días</option><option value="60">60 días</option></select>
          <button className="button button-small" onClick={() => { setSearch(''); setCategory(''); setPaymentTerm('') }} type="button"><Icon name="close" size={13} /> Limpiar</button>
          <span className="toolbar-count">{loading ? 'Cargando…' : `${visibleSuppliers.length} proveedores`}</span>
        </section>
        {loading ? <div className="card loading-state"><span className="loading-spinner" /><p className="state-title">Consultando directorio…</p></div> : visibleSuppliers.length ? <section className="supplier-grid" aria-label="Proveedores">
          {visibleSuppliers.map((supplier) => {
            const associatedProducts = productList.filter((product) => product.preferredSupplierId === supplier.id).length
            return <article className="card supplier-card" key={supplier.id}>
              <div className="supplier-card-top"><span className="supplier-logo">{getInitials(supplier.businessName)}</span><div><h2 className="supplier-name">{supplier.businessName}</h2><div className="supplier-meta">{supplier.taxId} · {supplier.city}</div><div className="supplier-meta">{supplier.contactName || 'Sin contacto registrado'}{supplier.billingEmail ? ` · ${supplier.billingEmail}` : ''}</div></div><div className="supplier-actions"><button aria-label={`Editar ${supplier.businessName}`} className="table-action" onClick={() => setEditingSupplier(supplier)} type="button" title="Editar"><Icon name="edit" size={14} /></button><button aria-label={`Eliminar ${supplier.businessName}`} className="table-action danger" onClick={() => handleDelete(supplier)} type="button" title="Eliminar"><Icon name="trash" size={14} /></button></div></div>
              <div className="supplier-tags"><StatusBadge status={Number(supplier.performanceScore) >= 95 ? 'OPTIMAL' : Number(supplier.performanceScore) >= 90 ? 'NEUTRAL' : 'OVERSTOCK'}>{Number(supplier.performanceScore).toFixed(1)}% cumplimiento</StatusBadge><span className="badge">{getCategoryLabel(supplier.category)}</span></div>
              <div className="supplier-stats">
                <div><div className="supplier-stat-label">Crédito disponible</div><div className="supplier-stat-value">{formatCurrency(supplier.availableCredit)}</div></div>
                <div><div className="supplier-stat-label">Plazo</div><div className="supplier-stat-value">{supplier.paymentTermDays} días</div></div>
                <div><div className="supplier-stat-label">Lead time</div><div className="supplier-stat-value">{supplier.leadTimeBusinessDays} días</div></div>
              </div>
              <div className="supplier-card-bottom"><span className="supplier-meta"><Icon name="box" size={12} /> {associatedProducts} productos principales</span><span className="supplier-meta">Marcas: {supplier.representedBrands || 'No registradas'}</span></div>
            </article>
          })}
        </section> : <div className="card"><div className="empty-state"><span className="empty-state-icon"><Icon name="building" size={18} /></span><p className="state-title">No hay proveedores con esos filtros</p><p className="state-description">Limpia los filtros o agrega un proveedor ficticio de prueba.</p></div></div>}
      </>}
      {editingSupplier !== undefined && <SupplierFormModal supplier={editingSupplier} onClose={() => setEditingSupplier(undefined)} onSave={handleSave} />}
    </>
  )
}
