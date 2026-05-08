import apiClient, { getAuthenticatedBlob } from '../../../lib/apiClient'
import type { Page } from '../../../types/api'
import type { DonationRow } from '../../../types/models'

export type FinancialDonationPayload = {
  campaignId: number
  amount: string
  paymentMethod?: string
  proofUrl?: string
  notes?: string
}

export type MaterialDonationPayload = {
  campaignId: number
  materialDescription: string
  quantity: number
  campaignItemId?: number | null
  proofUrl?: string
  notes?: string
}

export const donationApi = {
  createFinancial: async (body: FinancialDonationPayload) =>
    apiClient.post<DonationRow>('/donations/financial', body),

  createMaterial: async (body: MaterialDonationPayload) =>
    apiClient.post<DonationRow>('/donations/material', body),

  mine: async (page = 0, size = 20) => {
    const q = new URLSearchParams({ page: String(page), size: String(size) })
    return apiClient.get<Page<DonationRow>>(`/donations/me?${q.toString()}`)
  },

  downloadReceipt: async (donationId: number) => getAuthenticatedBlob(`/donations/${donationId}/receipt.pdf`),
}
