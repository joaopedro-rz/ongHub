import apiClient from '../../../lib/apiClient'
import type { Page } from '../../../types/api'
import type { OpportunityDetailPublic, OpportunitySummary } from '../../../types/models'

export const volunteerPublicApi = {
  list: async (page = 0, size = 12, ngoId?: number) => {
    const q = new URLSearchParams({ page: String(page), size: String(size) })
    if (ngoId != null) q.set('ngoId', String(ngoId))
    return apiClient.get<Page<OpportunitySummary>>(`/volunteer/opportunities/public?${q.toString()}`)
  },

  get: async (id: number) => {
    return apiClient.get<OpportunityDetailPublic>(`/volunteer/opportunities/public/${id}`)
  },
}
