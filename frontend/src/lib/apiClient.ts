import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios'
import { authStore } from './authStore'
import type { ApiError, ApiResponse } from '../types/api'

const baseURL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1'

type ApiClient = {
  get: <T>(url: string, config?: unknown) => Promise<T>
  delete: <T>(url: string, config?: unknown) => Promise<T>
  post: <T>(url: string, data?: unknown, config?: unknown) => Promise<T>
  put: <T>(url: string, data?: unknown, config?: unknown) => Promise<T>
  patch: <T>(url: string, data?: unknown, config?: unknown) => Promise<T>
  request: <T = unknown>(config: unknown) => Promise<T>
}

const rawClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
})

const refreshClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
})

let refreshPromise: Promise<string | null> | null = null

const toApiError = (error: AxiosError): ApiError => {
  if (error.response?.data && typeof error.response.data === 'object') {
    const data = error.response.data as ApiError
    if (data.code && data.message) {
      return data
    }
  }

  return {
    code: error.code ?? 'NETWORK_ERROR',
    message: error.message || 'Erro inesperado',
    details: [],
    timestamp: new Date().toISOString(),
    path: error.config?.url ?? '',
  }
}

const unwrapResponse = <T>(response: { data: ApiResponse<T> | T }) => {
  const payload = response.data
  if (payload && typeof payload === 'object' && 'data' in payload) {
    return (payload as ApiResponse<T>).data
  }
  return payload as T
}

rawClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = authStore.getAccessToken()
  if (token) {
    config.headers = config.headers ?? {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

rawClient.interceptors.response.use(
  (response) => unwrapResponse(response),
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean }
    const status = error.response?.status
    const isRefreshCall = originalRequest?.url?.includes('/auth/refresh')

    if (status === 401 && !originalRequest._retry && !isRefreshCall) {
      originalRequest._retry = true
      const refreshToken = authStore.getRefreshToken()

      if (!refreshToken) {
        authStore.clear()
        return Promise.reject(toApiError(error))
      }

      if (!refreshPromise) {
        refreshPromise = refreshClient
          .post<ApiResponse<{ accessToken: string; refreshToken: string }>>('/auth/refresh', {
            refreshToken,
          })
          .then((response) => {
            const data = unwrapResponse(response)
            authStore.setTokens(data)
            return data.accessToken
          })
          .catch(() => {
            authStore.clear()
            return null
          })
          .finally(() => {
            refreshPromise = null
          })
      }

      const newAccessToken = await refreshPromise
      if (newAccessToken) {
        originalRequest.headers = originalRequest.headers ?? {}
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
        return rawClient.request(originalRequest)
      }
    }

    return Promise.reject(toApiError(error))
  },
)

const apiClient = rawClient as unknown as ApiClient

export default apiClient
