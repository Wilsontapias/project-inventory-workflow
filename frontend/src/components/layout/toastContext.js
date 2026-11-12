import { createContext, useContext } from 'react'

export const ToastContext = createContext(() => {})

/** Hook para mostrar notificaciones del sistema desde una página o componente. */
export function useToast() {
  return useContext(ToastContext)
}
