import apiClient from '../../../lib/apiClient'

export type VolunteerApplyPayload = {
  skillsNote?: string | null
}

export const volunteerApplyApi = {
  apply: async (opportunityId: number, payload: VolunteerApplyPayload = {}) =>
    apiClient.post(`/volunteer/opportunities/${opportunityId}/applications`, payload),
}
