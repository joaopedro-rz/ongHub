export type ApiResponse<T> = {
  success: boolean
  data: T
  message: string
  timestamp: string
}

export type ApiError = {
  code: string
  message: string
  details: string[]
  timestamp: string
  path: string
}

/** Spring Data `Page` JSON shape (Jackson). */
export type Page<T> = {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}
