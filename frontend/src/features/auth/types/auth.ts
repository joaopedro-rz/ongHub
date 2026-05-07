export type AuthRole = 'ADMIN' | 'ONG_MANAGER' | 'DONOR' | 'VOLUNTEER'

export type UserResponse = {
  id: number
  name: string
  email: string
  phone: string | null
  profileImageUrl: string | null
  enabled: boolean
  roles: string[]
}

export type AuthResponse = {
  accessToken: string
  refreshToken: string
  user: UserResponse
}

export type LoginRequest = {
  email: string
  password: string
}

export type RegisterRequest = {
  name: string
  email: string
  password: string
  role: AuthRole
}

export type PasswordResetRequest = {
  email: string
}

export type PasswordUpdateRequest = {
  token: string
  password: string
}

export type VerifyEmailRequest = {
  token: string
}
