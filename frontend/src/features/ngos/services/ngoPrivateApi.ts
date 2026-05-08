import apiClient from '../../../lib/apiClient'
import type { Page } from '../../../types/api'
import type { NgoSummary } from '../../../types/models'

export const ngoPrivateApi = {
  /** Authenticated listing; filter by manager email for ONG_MANAGER dashboards. */
  listManaged: async (managerEmail: string, page = 0, size = 10) => {
    const q = new URLSearchParams({
      managerEmail,
      page: String(page),
      size: String(size),
    })
    return apiClient.get<Page<NgoSummary>>(`/ngos?${q.toString()}`)
  },

  /** Broader listing for ADMIN report picker. */
  listAll: async (page = 0, size = 50) => {
    const q = new URLSearchParams({ page: String(page), size: String(size) })
    return apiClient.get<Page<NgoSummary>>(`/ngos?${q.toString()}`)
  },
}
