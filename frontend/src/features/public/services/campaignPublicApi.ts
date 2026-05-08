import apiClient from '../../../lib/apiClient'
import type { Page } from '../../../types/api'
import type { CampaignDetail, CampaignSummary } from '../../../types/models'

export type CampaignPublicParams = {
  page?: number
  size?: number
  ngoId?: number
  category?: string
  urgent?: boolean
  search?: string
}

export const campaignPublicApi = {
  list: async (params: CampaignPublicParams = {}) => {
    const search = new URLSearchParams()
    search.set('page', String(params.page ?? 0))
    search.set('size', String(params.size ?? 12))
    if (params.ngoId != null) search.set('ngoId', String(params.ngoId))
    if (params.category) search.set('category', params.category)
    if (params.urgent != null) search.set('urgent', String(params.urgent))
    if (params.search) search.set('search', params.search)
    return apiClient.get<Page<CampaignSummary>>(`/campaigns/public?${search.toString()}`)
  },

  get: async (id: number) => {
    return apiClient.get<CampaignDetail>(`/campaigns/public/${id}`)
  },
}
