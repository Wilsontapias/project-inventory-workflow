import { useState } from 'react'
import AppShell from '../components/layout/AppShell.jsx'
import AlertsPage from '../features/products/AlertsPage.jsx'
import ProductsPage from '../features/products/ProductsPage.jsx'
import DashboardPage from '../features/dashboard/DashboardPage.jsx'
import PurchaseSuggestionsPage from '../features/purchases/PurchaseSuggestionsPage.jsx'
import SuppliersPage from '../features/suppliers/SuppliersPage.jsx'
import CashFlowPage from '../features/cashflow/CashFlowPage.jsx'
import '../App.css'

const pages = {
  dashboard: DashboardPage,
  products: ProductsPage,
  alerts: AlertsPage,
  suppliers: SuppliersPage,
  purchases: PurchaseSuggestionsPage,
  cashflow: CashFlowPage,
}

/** Aplicación de una sola página para el prototipo de inventario y caja. */
export default function App() {
  const [activePage, setActivePage] = useState('dashboard')
  const Page = pages[activePage]

  return (
    <AppShell activePage={activePage} onNavigate={setActivePage}>
      <Page onNavigate={setActivePage} />
    </AppShell>
  )
}
