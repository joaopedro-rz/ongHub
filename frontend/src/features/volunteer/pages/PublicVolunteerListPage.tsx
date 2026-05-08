import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { PublicShell } from '../../../components/layout/PublicShell'
import { Card, CardContent } from '../../../components/ui/Card'
import { volunteerPublicApi } from '../../volunteer/services/volunteerPublicApi'

export function PublicVolunteerListPage() {
  const query = useQuery({
    queryKey: ['volunteer', 'public'],
    queryFn: () => volunteerPublicApi.list(0, 24),
  })

  return (
    <PublicShell>
      <div className="mx-auto max-w-6xl px-4 py-10">
        <h1 className="font-heading text-3xl font-bold text-slate-900">Vagas de voluntariado</h1>
        <p className="mt-2 max-w-2xl text-sm text-slate-600">
          Inscricoes ficam disponíveis após login. Veja detalhes de cada vaga.
        </p>
        <div className="mt-8 grid gap-4 md:grid-cols-2">
          {query.isLoading ? (
            <p className="text-sm text-slate-500">Carregando...</p>
          ) : query.data?.content?.length ? (
            query.data.content.map((o) => (
              <Card key={o.id}>
                <CardContent className="flex flex-col gap-2">
                  <h2 className="font-heading text-lg font-semibold text-slate-900">{o.title}</h2>
                  <p className="text-sm text-slate-600">{o.ngoName}</p>
                  <p className="text-xs text-slate-500">
                    Vagas: {o.slotsAvailable} · {o.startDate} → {o.endDate}
                  </p>
                  <Link className="mt-1 text-sm font-semibold text-brand-600" to={`/vagas/${o.id}`}>
                    Detalhes e inscricao
                  </Link>
                </CardContent>
              </Card>
            ))
          ) : (
            <p className="text-sm text-slate-500">Nenhuma vaga publicada.</p>
          )}
        </div>
      </div>
    </PublicShell>
  )
}
