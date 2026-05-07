import type { FieldValues, Path, UseFormSetError } from 'react-hook-form'
import type { ApiError } from '../types/api'

const isApiError = (error: unknown): error is ApiError => {
  if (!error || typeof error !== 'object') return false
  const e = error as Record<string, unknown>
  return (
    typeof e.code === 'string' &&
    typeof e.message === 'string' &&
    Array.isArray(e.details) &&
    typeof e.timestamp === 'string' &&
    typeof e.path === 'string'
  )
}

export const applyApiFieldErrors = <T extends FieldValues>(
  error: unknown,
  setError: UseFormSetError<T>,
) => {
  if (!isApiError(error) || !error.details?.length) {
    return false
  }

  let applied = false
  error.details.forEach((detail) => {
    if (typeof detail !== 'string') return
    const [field, ...rest] = detail.split(':')
    const name = field?.trim()
    if (!name) return

    const message = rest.join(':').trim() || detail
    setError(name as Path<T>, { type: 'server', message })
    applied = true
  })

  return applied
}
