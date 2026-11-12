const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api').replace(/\/$/, '')

/** Error de API que conserva los mensajes de validación devueltos por el backend. */
export class ApiError extends Error {
  constructor(message, status, fieldErrors = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

/** Cliente HTTP común para mantener centralizados URL, JSON y errores. */
export async function apiRequest(path, { method = 'GET', body, signal } = {}) {
  let response
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      method,
      signal,
      headers: body === undefined ? { Accept: 'application/json' } : { Accept: 'application/json', 'Content-Type': 'application/json' },
      body: body === undefined ? undefined : JSON.stringify(body),
    })
  } catch (error) {
    if (error.name === 'AbortError') throw error
    throw new ApiError('No hay conexión con la API. Comprueba que Spring Boot esté ejecutándose en localhost:8080.', 0)
  }

  if (response.status === 204) return null
  const contentType = response.headers.get('content-type') || ''
  const payload = contentType.includes('application/json') || contentType.includes('application/problem+json')
    ? await response.json()
    : null

  if (!response.ok) {
    const fieldErrors = payload?.errors || {}
    const message = payload?.detail || payload?.message || payload?.title || `Error HTTP ${response.status}`
    throw new ApiError(message, response.status, fieldErrors)
  }
  return payload
}

export function buildQuery(params) {
  const query = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') query.set(key, value)
  })
  const search = query.toString()
  return search ? `?${search}` : ''
}
