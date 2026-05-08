import { useQuery } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { PublicShell } from '../../../components/layout/PublicShell'

import { ngoPublicApi } from '../services/ngoPublicApi'

export function PublicNgoDetailPage() {
  const { id } = useParams()
  const ngoId = Number(id)

  const query = useQuery({
    queryKey: ['ngos', 'public', ngoId],
    queryFn: () => ngoPublicApi.getPublic(ngoId),
    enabled: Number.isFinite(ngoId),
  })

  if (!Number.isFinite(ngoId)) {
    return (
      <PublicShell>
        <p className="p-8 text-center text-sm text-slate-600">ONG invalida.</p>
      </PublicShell>
    )
  }

  const n = query.data

  return (
    <PublicShell>
      <div className="mx-auto max-w-3xl px-4 py-10">
        <Link to="/ongs" className="text-sm font-semibold text-brand-600">
          Voltar
        </Link>
        {query.isLoading ? (
          <p className="mt-6 text-sm text-slate-500">Carregando...</p>
        ) : !n ? (
          <p className="mt-6 text-sm text-slate-600">ONG nao encontrada.</p>
        ) : (
          <article className="mt-6 rounded-xl bg-white p-8 shadow-card">
            <h1 className="font-heading text-3xl font-bold text-slate-900">{n.name}</h1>
            <p className="mt-2 text-sm text-slate-500">Status: {n.status}</p>
            {n.description ? (
              <p className="mt-6 whitespace-pre-wrap text-sm leading-relaxed text-slate-700">{n.description}</p>
            ) : null}
            <dl className="mt-8 grid gap-3 text-sm text-slate-700">
              {n.phone ? (
                <div>
                  <dt className="font-semibold text-slate-900">Telefone</dt>
                  <dd>{n.phone}</dd>
                </div>
              ) : null}
              {n.website ? (
                <div>
                  <dt className="font-semibold text-slate-900">Site</dt>
                  <dd>
                    <a className="text-brand-600 underline" href={n.website} target="_blank" rel="noreferrer">
                      {n.website}
                    </a>
                  </dd>
                </div>
              ) : null}
              {n.email ? (
                <div>
                  <dt className="font-semibold text-slate-900">Email</dt>
                  <dd>{n.email}</dd>
                </div>
              ) : null}
              {n.address ? (
                <div>
                  <dt className="font-semibold text-slate-900">Endereco</dt>
                  <dd>
                    {[n.address.street, n.address.number, n.address.neighborhood, n.address.city, n.address.state]
                      .filter(Boolean)
                      .join(', ')}
                  </dd>
                </div>
              ) : null}
              {n.certifications ? (
                <div>
                  <dt className="font-semibold text-slate-900">Certificacoes</dt>
                  <dd className="whitespace-pre-wrap">{n.certifications}</dd>
                </div>
              ) : null}
            </dl>
            <div className="mt-8">
              <Link
                className="text-sm font-semibold text-brand-600"
                to={`/campanhas?ngo=${n.id}`}
              >
                Ver campanhas desta ONG
              </Link>
            </div>
          </article>
        )}
      </div>
    </PublicShell>
  )
}
