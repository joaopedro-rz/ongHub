import { Navigate, Route, Routes } from 'react-router-dom'
import { AppShell } from '../components/layout/AppShell'
import { DashboardPage } from '../features/app/pages/DashboardPage'
import { ForgotPasswordPage } from '../features/auth/pages/ForgotPasswordPage'
import { LoginPage } from '../features/auth/pages/LoginPage'
import { RegisterPage } from '../features/auth/pages/RegisterPage'
import { ResetPasswordPage } from '../features/auth/pages/ResetPasswordPage'
import { VerifyEmailPage } from '../features/auth/pages/VerifyEmailPage'
import { DonationsPage } from '../features/donations/pages/DonationsPage'
import { LandingPage } from '../features/public/pages/LandingPage'
import { PublicCampaignDetailPage } from '../features/public/pages/PublicCampaignDetailPage'
import { PublicCampaignsPage } from '../features/public/pages/PublicCampaignsPage'
import { PublicNgoDetailPage } from '../features/public/pages/PublicNgoDetailPage'
import { PublicNgosPage } from '../features/public/pages/PublicNgosPage'
import { PublicVolunteerDetailPage } from '../features/volunteer/pages/PublicVolunteerDetailPage'
import { PublicVolunteerListPage } from '../features/volunteer/pages/PublicVolunteerListPage'
import { ProtectedRoute } from './ProtectedRoute'

export function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/campanhas" element={<PublicCampaignsPage />} />
      <Route path="/campanhas/:id" element={<PublicCampaignDetailPage />} />
      <Route path="/ongs" element={<PublicNgosPage />} />
      <Route path="/ongs/:id" element={<PublicNgoDetailPage />} />
      <Route path="/vagas" element={<PublicVolunteerListPage />} />
      <Route path="/vagas/:id" element={<PublicVolunteerDetailPage />} />

      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/forgot-password" element={<ForgotPasswordPage />} />
      <Route path="/reset-password" element={<ResetPasswordPage />} />
      <Route path="/verify-email" element={<VerifyEmailPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppShell />}>
          <Route path="/app" element={<DashboardPage />} />
          <Route path="/app/doacoes" element={<DonationsPage />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
