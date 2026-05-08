import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ToastProvider } from '../../../components/ui/Toast'
import { authStore } from '../../../lib/authStore'
import { LoginPage } from './LoginPage'

const loginMock = vi.fn(async (payload: { email: string; password: string }, _meta?: unknown) => {
  authStore.setTokens({ accessToken: 'a', refreshToken: 'r' })
  authStore.setUser({
    id: 1,
    name: 'Test',
    email: payload.email,
    phone: null,
    profileImageUrl: null,
    enabled: true,
    roles: ['DONOR'],
  })
  return {
    accessToken: 'a',
    refreshToken: 'r',
    user: {
      id: 1,
      name: 'Test',
      email: payload.email,
      phone: null,
      profileImageUrl: null,
      enabled: true,
      roles: ['DONOR'],
    },
  }
})

vi.mock('../services/authApi', () => ({
  authApi: {
    login: (payload: unknown, context: unknown) =>
      loginMock(payload as { email: string; password: string }, context),
  },
}))

describe('LoginPage', () => {
  beforeEach(() => {
    loginMock.mockClear()
    authStore.clear()
  })

  it('submits credentials and navigates to app', async () => {
    const qc = new QueryClient({ defaultOptions: { queries: { retry: false }, mutations: { retry: false } } })
    const user = userEvent.setup()

    render(
      <QueryClientProvider client={qc}>
        <ToastProvider>
          <MemoryRouter initialEntries={['/login']}>
            <Routes>
              <Route path="/login" element={<LoginPage />} />
              <Route path="/app" element={<div>Inside app</div>} />
            </Routes>
          </MemoryRouter>
        </ToastProvider>
      </QueryClientProvider>,
    )

    await user.type(screen.getByLabelText(/email/i), 't@t.com')
    await user.type(screen.getByLabelText(/senha/i), 'secret12')
    await user.click(screen.getByRole('button', { name: /entrar/i }))

    await waitFor(() => {
      expect(loginMock).toHaveBeenCalledWith(
        { email: 't@t.com', password: 'secret12' },
        expect.any(Object),
      )
    })

    await screen.findByText('Inside app')
    const raw = sessionStorage.getItem('onghub_user')
    expect(raw).toBeTruthy()
    expect(JSON.parse(raw as string).email).toBe('t@t.com')
  })
})
