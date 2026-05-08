import type { UserResponse } from '../features/auth/types/auth'

type AuthTokens = {
  accessToken: string
  refreshToken: string
}

const REFRESH_TOKEN_KEY = 'onghub_refresh_token'
const USER_KEY = 'onghub_user'

let accessToken: string | null = null

export const authStore = {
  getAccessToken: () => accessToken,
  setAccessToken: (token: string | null) => {
    accessToken = token
  },
  getRefreshToken: () => localStorage.getItem(REFRESH_TOKEN_KEY),
  setTokens: ({ accessToken: newAccessToken, refreshToken }: AuthTokens) => {
    accessToken = newAccessToken
    // WARNING: localStorage is not fully secure; prefer httpOnly cookies in production.
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
  },
  getUser: (): UserResponse | null => {
    const raw = sessionStorage.getItem(USER_KEY)
    if (!raw) {
      return null
    }
    try {
      return JSON.parse(raw) as UserResponse
    } catch {
      return null
    }
  },
  setUser: (user: UserResponse | null) => {
    if (user) {
      sessionStorage.setItem(USER_KEY, JSON.stringify(user))
    } else {
      sessionStorage.removeItem(USER_KEY)
    }
  },
  isAuthenticated: () => Boolean(accessToken || localStorage.getItem(REFRESH_TOKEN_KEY)),
  clear: () => {
    accessToken = null
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    sessionStorage.removeItem(USER_KEY)
  },
}
