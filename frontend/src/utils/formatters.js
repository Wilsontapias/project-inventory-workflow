const currencyFormatter = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 })
const integerFormatter = new Intl.NumberFormat('es-CO', { maximumFractionDigits: 0 })

export const formatCurrency = (value) => currencyFormatter.format(Number(value || 0))
export const formatNumber = (value) => integerFormatter.format(Number(value || 0))

export const categoryLabels = {
  SERVERS: 'Servidores', LAPTOPS: 'Portátiles', ACCESSORIES: 'Accesorios',
  SOFTWARE: 'Software', COMPONENTS: 'Componentes', UPS: 'UPS y energía',
  COMPUTERS: 'Cómputo', POWER: 'Energía',
}

export const getCategoryLabel = (category) => categoryLabels[category] || category || 'Sin categoría'
export const getStatusLabel = (status) => ({ CRITICAL: 'Crítico', OPTIMAL: 'Óptimo', OVERSTOCK: 'Sobre stock' }[status] || status || 'Sin estado')
export const getInitials = (value = '') => value.split(/\s+/).filter(Boolean).slice(0, 2).map((part) => part[0]).join('').toUpperCase()
