type AuthTokens = {
  accessToken: string
  refreshToken: string
}

const REFRESH_TOKEN_KEY = 'onghub_refresh_token'

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
  isAuthenticated: () => Boolean(accessToken || localStorage.getItem(REFRESH_TOKEN_KEY)),
  clear: () => {
    accessToken = null
    localStorage.removeItem(REFRESH_TOKEN_KEY)
  },
}
