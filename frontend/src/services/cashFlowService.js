import { apiRequest, buildQuery } from './apiClient.js'

export const getCashFlowSummary = (filters = {}, options) => apiRequest(`/cash-flow/summary${buildQuery(filters)}`, options)
export const getCashMovements = (filters = {}, options) => apiRequest(`/cash-flow/movements${buildQuery(filters)}`, options)
export const createCashMovement = (payload) => apiRequest('/cash-flow/movements', { method: 'POST', body: payload })
export const getCommitments = (filters = {}, options) => apiRequest(`/cash-flow/commitments${buildQuery(filters)}`, options)
export const createCommitment = (payload) => apiRequest('/cash-flow/commitments', { method: 'POST', body: payload })
export const payCommitment = (id) => apiRequest(`/cash-flow/commitments/${id}/pay`, { method: 'PATCH' })
