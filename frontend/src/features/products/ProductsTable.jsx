import Icon from '../../components/common/Icon.jsx'
import { EmptyState, StatusBadge } from '../../components/common/Ui.jsx'
import { formatCurrency, getCategoryLabel } from '../../utils/formatters.js'

/** Tabla compartida por catálogo y vista de alertas. */
export default function ProductsTable({ products, onEdit, onDelete, emptyTitle }) {
  if (!products.length) return <EmptyState title={emptyTitle || 'No hay productos para mostrar'} description="Ajusta los filtros o registra un producto para comenzar." />

  return (
    <div className="table-scroll">
      <table className="data-table product-table">
        <thead><tr><th>SKU</th><th>Producto y marca</th><th>Categoría</th><th>Proveedor principal</th><th className="text-right">Costo unit. (COP)</th><th className="text-right">Precio venta (COP)</th><th className="text-center">Stock actual</th><th className="text-center">Mín.</th><th className="text-center">Ideal</th><th className="text-center">Antigüedad</th><th>Rotación</th><th>Estado</th>{onEdit && <th className="text-right">Acciones</th>}</tr></thead>
        <tbody>
          {products.map((product) => <tr key={product.id}>
            <td><span className="sku">{product.sku}</span></td>
            <td><div className="product-cell"><span className="product-glyph"><Icon name={product.category === 'SERVERS' ? 'server' : product.category === 'LAPTOPS' ? 'laptop' : 'box'} size={15} /></span><span><span className="product-name">{product.name}</span><span className="product-brand">{product.brand}{product.model ? ` · ${product.model}` : ''}</span></span></div></td>
            <td><span className="badge">{getCategoryLabel(product.category)}</span></td>
            <td><span className="supplier-meta">{product.preferredSupplierName}</span></td>
            <td className="numeric text-right">{formatCurrency(product.unitCost)}</td>
            <td className="numeric text-right">{formatCurrency(product.salePrice)}</td>
            <td className="stock-cell"><div className="stock-main">{product.stockCurrent}</div><div className="stock-caption">unidades</div></td>
            <td className="numeric text-center">{product.stockMinimum}</td>
            <td className="numeric text-center">{product.stockIdeal}</td>
            <td className="text-center"><span className={`badge ${product.daysInInventory > 90 ? 'badge-overstock' : ''}`}>{product.daysInInventory} d</span></td>
            <td>{product.lowRotation ? <span className="badge badge-overstock">Baja rotación</span> : product.lastSaleDate ? <span className="badge badge-optimal">Venta reciente</span> : <span className="badge">Sin historial</span>}</td>
            <td><StatusBadge status={product.status} /></td>
            {onEdit && <td><div className="table-actions"><button aria-label={`Editar ${product.name}`} className="table-action" onClick={() => onEdit(product)} title="Editar" type="button"><Icon name="edit" size={14} /></button><button aria-label={`Eliminar ${product.name}`} className="table-action danger" onClick={() => onDelete(product)} title="Eliminar" type="button"><Icon name="trash" size={14} /></button></div></td>}
          </tr>)}
        </tbody>
      </table>
    </div>
  )
}
