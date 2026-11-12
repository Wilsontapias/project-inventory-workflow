import { apiRequest, buildQuery } from './apiClient.js'

export const getSuppliers = (filters = {}, options) => apiRequest(`/suppliers${buildQuery(filters)}`, options)
export const createSupplier = (payload) => apiRequest('/suppliers', { method: 'POST', body: payload })
export const updateSupplier = (id, payload) => apiRequest(`/suppliers/${id}`, { method: 'PUT', body: payload })
export const deleteSupplier = (id) => apiRequest(`/suppliers/${id}`, { method: 'DELETE' })
