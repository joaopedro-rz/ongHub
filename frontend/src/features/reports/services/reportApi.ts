import { getAuthenticatedBlob } from '../../../lib/apiClient'

export const reportApi = {
  transparencyCsv: async (ngoId: number) => getAuthenticatedBlob(`/reports/ngos/${ngoId}/transparency.csv`),

  transparencyPdf: async (ngoId: number) => getAuthenticatedBlob(`/reports/ngos/${ngoId}/transparency.pdf`),
}
