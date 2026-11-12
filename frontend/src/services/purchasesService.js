import { apiRequest, buildQuery } from './apiClient.js'

export const getPurchaseSuggestions = (filters = {}, options) => apiRequest(`/purchase-suggestions${buildQuery(filters)}`, options)
export const getSupplierOffers = (productId, options) => apiRequest(`/products/${productId}/supplier-offers`, options)
