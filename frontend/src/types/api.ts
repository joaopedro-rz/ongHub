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
