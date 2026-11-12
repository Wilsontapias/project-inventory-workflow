import { useCallback, useEffect, useState } from 'react'
import Icon from '../../components/common/Icon.jsx'
import { ErrorState, KpiCard, PageHeading, SearchField } from '../../components/common/Ui.jsx'
import { useToast } from '../../components/layout/toastContext.js'
import { getProducts, createProduct, updateProduct, deleteProduct } from '../../services/productsService.js'
import { getSuppliers } from '../../services/suppliersService.js'
import { formatCurrency, getCategoryLabel } from '../../utils/formatters.js'
import ProductFormModal from './ProductFormModal.jsx'
import ProductsTable from './ProductsTable.jsx'

const statuses = [
  ['', 'Todos los estados'], ['CRITICAL', 'Crítico'], ['OPTIMAL', 'Óptimo'], ['OVERSTOCK', 'Sobre stock'],
]
const categories = ['', 'SERVERS', 'LAPTOPS', 'ACCESSORIES', 'SOFTWARE', 'COMPONENTS', 'UPS']

/** Catálogo con filtros conectados a la API y operaciones CRUD. */
export default function ProductsPage() {
  const notify = useToast()
  const [products, setProducts] = useState([])
  const [suppliers, setSuppliers] = useState([])
  const [search, setSearch] = useState('')
  const [category, setCategory] = useState('')
  const [status, setStatus] = useState('')
  const [age, setAge] = useState('')
  const [lowRotation, setLowRotation] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [formProduct, setFormProduct] = useState(undefined)

  const fetchProducts = useCallback(async (signal) => {
    try {
      const data = await getProducts({ search, category, status, olderThanDays: age, lowRotation }, { signal })
      setProducts(data)
      setError('')
    } catch (loadError) {
      if (loadError.name !== 'AbortError') setError(loadError.message)
    } finally { setLoading(false) }
  }, [search, category, status, age, lowRotation])

  const loadProducts = useCallback((signal) => {
    setLoading(true)
    setError('')
    return fetchProducts(signal)
  }, [fetchProducts])

  // El efecto solo dispara la consulta inicial; las mutaciones de estado llegan tras la respuesta HTTP.
  // oxlint-disable-next-line react/set-state-in-effect
  useEffect(() => {
    const controller = new AbortController()
    // oxlint-disable-next-line react/set-state-in-effect
    fetchProducts(controller.signal)
    return () => controller.abort()
  }, [fetchProducts])

  useEffect(() => { getSuppliers().then(setSuppliers).catch((loadError) => setError(loadError.message)) }, [])

  const handleSave = async (payload) => {
    if (formProduct) await updateProduct(formProduct.id, payload)
    else await createProduct(payload)
    setFormProduct(undefined)
    notify(formProduct ? 'Producto actualizado en el catálogo.' : 'Producto agregado al catálogo.')
    await loadProducts()
  }

  const handleDelete = async (product) => {
    if (!window.confirm(`¿Eliminar ${product.name} (${product.sku})?`)) return
    try {
      await deleteProduct(product.id)
      notify('Producto y ofertas asociadas eliminados.')
      await loadProducts()
    } catch (deleteError) { notify(deleteError.message, 'error') }
  }

  const clearFilters = () => { setSearch(''); setCategory(''); setStatus(''); setAge(''); setLowRotation('') }
  const inventoryValue = products.reduce((sum, product) => sum + Number(product.inventoryValue), 0)
  const averageAge = products.length ? products.reduce((sum, product) => sum + product.daysInInventory, 0) / products.length : 0
  const criticalCount = products.filter((product) => product.status === 'CRITICAL').length
  const lowRotationCount = products.filter((product) => product.lowRotation).length

  return (
    <>
      <PageHeading eyebrow="Existencias y catálogo tecnológico" title="Catálogo de inventario" description="Consulta SKU, costos, proveedor principal, niveles de stock y antigüedad. Los filtros se aplican en la API." actions={<button className="button button-primary" onClick={() => setFormProduct(null)} type="button"><Icon name="plus" size={15} /> Agregar producto</button>} />
      <section aria-label="Indicadores del catálogo filtrado" className="kpi-grid catalog-kpis">
        <KpiCard label="Referencias" value={products.length} detail={<>Coincidencias de los filtros actuales</>} icon="box" tone="blue" />
        <KpiCard label="Valor de existencias" value={formatCurrency(inventoryValue)} detail={<>Costo de adquisición del resultado</>} icon="wallet" tone="green" compact />
        <KpiCard label="Días promedio" value={`${averageAge.toFixed(0)} días`} detail={<>Permanencia media en bodega</>} icon="clock" tone="amber" />
        <KpiCard label="Stock crítico" value={criticalCount} detail={<>{lowRotationCount} productos con baja rotación</>} icon="alert" tone="red" />
      </section>
      <section aria-label="Filtros del catálogo" className="toolbar">
        <SearchField value={search} onChange={setSearch} placeholder="Buscar SKU, nombre o marca…" />
        <select aria-label="Filtrar categoría" className="select" value={category} onChange={(event) => setCategory(event.target.value)}><option value="">Todas las categorías</option>{categories.filter(Boolean).map((value) => <option key={value} value={value}>{getCategoryLabel(value)}</option>)}</select>
        <select aria-label="Filtrar estado" className="select" value={status} onChange={(event) => setStatus(event.target.value)}>{statuses.map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select>
        <select aria-label="Filtrar antigüedad" className="select" value={age} onChange={(event) => setAge(event.target.value)}><option value="">Cualquier antigüedad</option><option value="90">Más de 90 días</option><option value="60">Más de 60 días</option><option value="30">Más de 30 días</option></select>
        <select aria-label="Filtrar baja rotación" className="select" value={lowRotation} onChange={(event) => setLowRotation(event.target.value)}><option value="">Toda rotación</option><option value="true">Baja rotación</option><option value="false">Con ventas recientes</option></select>
        <button className="button button-small" onClick={clearFilters} type="button"><Icon name="close" size={13} /> Limpiar</button>
        <span className="toolbar-count">{loading ? 'Actualizando…' : `${products.length} referencias`}</span>
      </section>
      <section className="card table-card" aria-label="Resultados de productos">
        {error ? <ErrorState message={error} onRetry={() => loadProducts()} /> : loading ? <div className="loading-state"><span className="loading-spinner" /><p className="state-title">Consultando catálogo…</p></div> : <ProductsTable products={products} onEdit={(product) => setFormProduct(product)} onDelete={handleDelete} />}
        {!loading && !error && <div className="table-footer"><span>Mostrando {products.length} producto{products.length === 1 ? '' : 's'} recibidos del backend</span><span>Moneda de referencia: COP</span></div>}
      </section>
      {formProduct !== undefined && <ProductFormModal product={formProduct} suppliers={suppliers} onClose={() => setFormProduct(undefined)} onSave={handleSave} />}
    </>
  )
}
