import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { PublicShell } from '../../../components/layout/PublicShell'
import { Card, CardContent } from '../../../components/ui/Card'
import { ngoPublicApi } from '../services/ngoPublicApi'

export function PublicNgosPage() {
  const query = useQuery({
    queryKey: ['ngos', 'public'],
    queryFn: () => ngoPublicApi.list({ page: 0, size: 48 }),
  })

  return (
    <PublicShell>
      <div className="mx-auto max-w-6xl px-4 py-10">
        <h1 className="font-heading text-3xl font-bold text-slate-900">ONGs parceiras</h1>
        <p className="mt-2 max-w-2xl text-sm text-slate-600">
          Organizacoes com perfil publico no OngHub.
        </p>
        <div className="mt-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {query.isLoading ? (
            <p className="text-sm text-slate-500">Carregando...</p>
          ) : query.data?.content?.length ? (
            query.data.content.map((n) => (
              <Card key={n.id}>
                <CardContent className="flex flex-col gap-1">
                  <h2 className="font-heading text-lg font-semibold text-slate-900">{n.name}</h2>
                  <p className="text-xs uppercase tracking-wide text-slate-500">{n.status}</p>
                  <p className="text-sm text-slate-600">
                    {[n.city, n.state].filter(Boolean).join(', ') || 'Localidade nao informada'}
                  </p>
                  <Link className="mt-2 text-sm font-semibold text-brand-600" to={`/ongs/${n.id}`}>
                    Ver perfil
                  </Link>
                </CardContent>
              </Card>
            ))
          ) : (
            <p className="text-sm text-slate-500">Nenhuma ONG encontrada.</p>
          )}
        </div>
      </div>
    </PublicShell>
  )
}
