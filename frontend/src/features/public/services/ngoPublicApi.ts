import apiClient from '../../../lib/apiClient'
import type { Page } from '../../../types/api'
import type { NgoDetail, NgoSummary } from '../../../types/models'

export type NgoPublicParams = {
  page?: number
  size?: number
  categoryId?: number
  search?: string
}

export const ngoPublicApi = {
  list: async (params: NgoPublicParams = {}) => {
    const search = new URLSearchParams()
    search.set('page', String(params.page ?? 0))
    search.set('size', String(params.size ?? 12))
    if (params.categoryId != null) search.set('categoryId', String(params.categoryId))
    if (params.search) search.set('search', params.search)
    return apiClient.get<Page<NgoSummary>>(`/ngos/public?${search.toString()}`)
  },

  getPublic: async (id: number) => {
    return apiClient.get<NgoDetail>(`/ngos/${id}/public`)
  },
}
