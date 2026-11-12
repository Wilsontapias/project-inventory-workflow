import { apiRequest, buildQuery } from './apiClient.js'

export const getProducts = (filters = {}, options) => apiRequest(`/products${buildQuery(filters)}`, options)
export const getProduct = (id, options) => apiRequest(`/products/${id}`, options)
export const createProduct = (payload) => apiRequest('/products', { method: 'POST', body: payload })
export const updateProduct = (id, payload) => apiRequest(`/products/${id}`, { method: 'PUT', body: payload })
export const deleteProduct = (id) => apiRequest(`/products/${id}`, { method: 'DELETE' })
