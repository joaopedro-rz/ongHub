import { useQuery } from '@tanstack/react-query'
import { Link, useSearchParams } from 'react-router-dom'
import { PublicShell } from '../../../components/layout/PublicShell'
import { Card, CardContent } from '../../../components/ui/Card'
import { formatBrl } from '../../../lib/formatMoney'
import { campaignPublicApi } from '../services/campaignPublicApi'

export function PublicCampaignsPage() {
  const [params] = useSearchParams()
  const ngoParam = params.get('ngo')
  const ngoId = ngoParam ? Number(ngoParam) : undefined

  const query = useQuery({
    queryKey: ['campaigns', 'public', 'list', ngoId],
    queryFn: () =>
      campaignPublicApi.list({
        page: 0,
        size: 24,
        ngoId: Number.isFinite(ngoId) ? ngoId : undefined,
      }),
  })

  return (
    <PublicShell>
      <div className="mx-auto max-w-6xl px-4 py-10">
        <h1 className="font-heading text-3xl font-bold text-slate-900">Campanhas</h1>
        <p className="mt-2 max-w-2xl text-sm text-slate-600">
          Campanhas publicadas por ONGs verificadas. Use os detalhes para acompanhar metas e necessidades.
        </p>
        <div className="mt-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {query.isLoading ? (
            <p className="text-sm text-slate-500">Carregando campanhas...</p>
          ) : query.data?.content?.length ? (
            query.data.content.map((c) => (
              <Card key={c.id}>
                <CardContent className="flex flex-col gap-2">
                  <div className="flex items-start justify-between gap-2">
                    <h2 className="font-heading text-lg font-semibold text-slate-900">{c.title}</h2>
                    {c.urgent ? (
                      <span className="shrink-0 rounded-full bg-amber-100 px-2 py-0.5 text-xs font-semibold text-amber-800">
                        Urgente
                      </span>
                    ) : null}
                  </div>
                  <p className="text-sm text-slate-600">{c.ngoName}</p>
                  <p className="text-xs text-slate-500">
                    Meta {formatBrl(c.financialGoal)} · {c.status}
                  </p>
                  <Link to={`/campanhas/${c.id}`} className="mt-1 text-sm font-semibold text-brand-600">
                    Abrir campanha
                  </Link>
                </CardContent>
              </Card>
            ))
          ) : (
            <p className="text-sm text-slate-500">Nenhuma campanha encontrada.</p>
          )}
        </div>
      </div>
    </PublicShell>
  )
}
