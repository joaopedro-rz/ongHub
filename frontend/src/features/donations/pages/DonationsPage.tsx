import { useMutation, useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import { Button } from '../../../components/ui/Button'
import { Card, CardContent, CardHeader, CardTitle } from '../../../components/ui/Card'
import { useToast } from '../../../components/ui/Toast'
import { triggerBlobDownload } from '../../../lib/download'
import { formatBrl } from '../../../lib/formatMoney'
import type { ApiError } from '../../../types/api'
import { donationApi } from '../../donations/services/donationApi'

function errorMessage(err: unknown, fallback: string) {
  if (err && typeof err === 'object' && 'message' in err && typeof (err as ApiError).message === 'string') {
    return (err as ApiError).message
  }
  if (err instanceof Error) return err.message
  return fallback
}

export function DonationsPage() {
  const { showToast } = useToast()
  const [receiptLoadingId, setReceiptLoadingId] = useState<number | null>(null)

  const listQuery = useQuery({
    queryKey: ['donations', 'me'],
    queryFn: () => donationApi.mine(0, 40),
  })

  const receiptMutation = useMutation({
    mutationFn: async (id: number) => {
      setReceiptLoadingId(id)
      try {
        return { blob: await donationApi.downloadReceipt(id), id }
      } finally {
        setReceiptLoadingId(null)
      }
    },
    onSuccess: ({ blob, id }) => {
      triggerBlobDownload(blob, `recibo-doacao-${id}.pdf`)
      showToast('success', 'Recibo baixado')
    },
    onError: (e) => showToast('error', errorMessage(e, 'Não foi possível baixar o recibo')),
  })

  const rows = listQuery.data?.content ?? []

  return (
    <div className="mx-auto flex max-w-5xl flex-col gap-6">
      <header>
        <h1 className="font-heading text-2xl font-bold text-slate-900">Minhas doações</h1>
        <p className="mt-1 text-sm text-slate-600">Histórico e recibos em PDF após confirmação.</p>
      </header>

      <Card>
        <CardHeader>
          <CardTitle>Registros</CardTitle>
        </CardHeader>
        <CardContent>
          {listQuery.isLoading ? (
            <p className="text-sm text-slate-500">Carregando...</p>
          ) : rows.length === 0 ? (
            <p className="text-sm text-slate-600">Você ainda não possui doações registradas.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full text-left text-sm text-slate-700">
                <thead className="border-b border-slate-200 text-xs uppercase text-slate-500">
                  <tr>
                    <th className="py-2 pr-4">Campanha</th>
                    <th className="py-2 pr-4">Tipo</th>
                    <th className="py-2 pr-4">Status</th>
                    <th className="py-2 pr-4">Valor / Material</th>
                    <th className="py-2">Recibo</th>
                  </tr>
                </thead>
                <tbody>
                  {rows.map((d) => (
                    <tr key={d.id} className="border-b border-slate-100">
                      <td className="py-3 pr-4 font-medium text-slate-900">{d.campaignTitle ?? '—'}</td>
                      <td className="py-3 pr-4">{d.donationType}</td>
                      <td className="py-3 pr-4">{d.status}</td>
                      <td className="py-3 pr-4">
                        {d.donationType === 'FINANCIAL' ? formatBrl(d.amount) : d.materialDescription ?? '—'}
                      </td>
                      <td className="py-3">
                        {d.status === 'CONFIRMED' ? (
                          <Button
                            type="button"
                            variant="ghost"
                            className="!px-2 !py-1 text-xs"
                            isLoading={receiptLoadingId === d.id}
                            onClick={() => receiptMutation.mutate(d.id)}
                          >
                            PDF
                          </Button>
                        ) : (
                          <span className="text-xs text-slate-400">—</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
