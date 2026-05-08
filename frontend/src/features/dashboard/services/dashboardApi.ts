import apiClient from '../../../lib/apiClient'
import type { AdminDashboard, DonorDashboard, NgoDashboard } from '../../../types/models'

export const dashboardApi = {
  admin: async () => apiClient.get<AdminDashboard>('/dashboard/admin'),

  ngo: async (ngoId: number) => apiClient.get<NgoDashboard>(`/dashboard/ngo/${ngoId}`),

  donor: async () => apiClient.get<DonorDashboard>('/dashboard/donor'),
}
