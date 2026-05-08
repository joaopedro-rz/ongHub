import apiClient from '../../../lib/apiClient'
import { authStore } from '../../../lib/authStore'
import type {
  LoginRequest,
  RegisterRequest,
  PasswordResetRequest,
  PasswordUpdateRequest,
  VerifyEmailRequest,
  AuthResponse,
} from '../types/auth'

export const authApi = {
  login: async (payload: LoginRequest) => {
    const data = await apiClient.post<AuthResponse>('/auth/login', payload)
    authStore.setTokens({ accessToken: data.accessToken, refreshToken: data.refreshToken })
    authStore.setUser(data.user)
    return data
  },
  register: async (payload: RegisterRequest) => {
    const data = await apiClient.post<AuthResponse>('/auth/register', payload)
    authStore.setTokens({ accessToken: data.accessToken, refreshToken: data.refreshToken })
    authStore.setUser(data.user)
    return data
  },
  refresh: async (refreshToken: string) => {
    const data = await apiClient.post<AuthResponse>('/auth/refresh', { refreshToken })
    authStore.setTokens({ accessToken: data.accessToken, refreshToken: data.refreshToken })
    authStore.setUser(data.user)
    return data
  },
  forgotPassword: async (payload: PasswordResetRequest) => {
    await apiClient.post<void>('/auth/password/forgot', payload)
  },
  resetPassword: async (payload: PasswordUpdateRequest) => {
    await apiClient.post<void>('/auth/password/reset', payload)
  },
  confirmEmail: async (payload: VerifyEmailRequest) => {
    await apiClient.post<void>('/auth/verify', payload)
  },
}
