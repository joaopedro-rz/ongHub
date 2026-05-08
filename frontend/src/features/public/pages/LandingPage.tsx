import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { PublicShell } from '../../../components/layout/PublicShell'
import { Card, CardContent } from '../../../components/ui/Card'
import { campaignPublicApi } from '../services/campaignPublicApi'

export function LandingPage() {
  const featured = useQuery({
    queryKey: ['campaigns', 'public', 'featured'],
    queryFn: () => campaignPublicApi.list({ page: 0, size: 3, urgent: true }),
  })

  return (
    <PublicShell>
      <section className="border-b border-slate-200 bg-gradient-to-b from-brand-50 to-background px-4 py-16">
        <div className="mx-auto max-w-6xl">
          <p className="text-sm font-semibold uppercase tracking-wide text-brand-700">Solidariedade digital</p>
          <h1 className="mt-3 font-heading text-4xl font-bold text-slate-900 md:text-5xl">
            Conecte doadores, ONGs e voluntários em um só lugar.
          </h1>
          <p className="mt-4 max-w-2xl text-lg text-slate-600">
            OngHub ajuda organizações a divulgar campanhas, registrar doações e organizar voluntariado com transparência.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <Link
              to="/campanhas"
              className="inline-flex items-center justify-center rounded-lg bg-brand-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-brand-700 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:ring-offset-2"
            >
              Ver campanhas
            </Link>
            <Link
              to="/register"
              className="inline-flex items-center justify-center rounded-lg border border-slate-200 bg-white px-4 py-2 text-sm font-semibold text-slate-800 transition hover:bg-slate-50 focus:outline-none focus:ring-2 focus:ring-brand-500 focus:ring-offset-2"
            >
              Criar conta
            </Link>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-6xl px-4 py-14">
        <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            <h2 className="font-heading text-2xl font-semibold text-slate-900">Campanhas urgentes</h2>
            <p className="mt-1 text-sm text-slate-600">Pedidos que precisam de visibilidade agora.</p>
          </div>
          <Link to="/campanhas" className="text-sm font-semibold text-brand-600">
            Ver todas
          </Link>
        </div>
        <div className="mt-8 grid gap-4 md:grid-cols-3">
          {featured.isLoading ? (
            <p className="text-sm text-slate-500">Carregando...</p>
          ) : featured.data?.content?.length ? (
            featured.data.content.map((c) => (
              <Card key={c.id}>
                <CardContent className="flex flex-col gap-2">
                  <span className="text-xs font-semibold uppercase text-accent">Urgente</span>
                  <h3 className="font-heading text-lg font-semibold text-slate-900">{c.title}</h3>
                  <p className="text-sm text-slate-600">{c.ngoName}</p>
                  <Link to={`/campanhas/${c.id}`} className="mt-2 text-sm font-semibold text-brand-600">
                    Detalhes
                  </Link>
                </CardContent>
              </Card>
            ))
          ) : (
            <p className="text-sm text-slate-500">Nenhuma campanha urgente no momento.</p>
          )}
        </div>
      </section>

      <section className="border-y border-slate-200 bg-white px-4 py-14">
        <div className="mx-auto grid max-w-6xl gap-10 md:grid-cols-3">
          <div>
            <h3 className="font-heading text-lg font-semibold text-slate-900">Para doadores</h3>
            <p className="mt-2 text-sm text-slate-600">
              Acompanhe impacto, histórico e recibos em PDF após a confirmação da ONG.
            </p>
          </div>
          <div>
            <h3 className="font-heading text-lg font-semibold text-slate-900">Para ONGs</h3>
            <p className="mt-2 text-sm text-slate-600">
              Campanhas com metas, voluntariado e relatórios de transparência exportáveis.
            </p>
          </div>
          <div>
            <h3 className="font-heading text-lg font-semibold text-slate-900">Para voluntários</h3>
            <p className="mt-2 text-sm text-slate-600">
              Encontre vagas por organização e registre seu interesse em poucos cliques.
            </p>
          </div>
        </div>
      </section>
    </PublicShell>
  )
}
