import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation, useQuery } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { Link, useParams } from 'react-router-dom'
import { z } from 'zod'
import { PublicShell } from '../../../components/layout/PublicShell'
import { Button } from '../../../components/ui/Button'
import { Card, CardContent, CardHeader, CardTitle } from '../../../components/ui/Card'
import { TextField } from '../../../components/ui/TextField'
import { useToast } from '../../../components/ui/Toast'
import { authStore } from '../../../lib/authStore'
import { volunteerApplyApi } from '../services/volunteerApplyApi'
import { volunteerPublicApi } from '../services/volunteerPublicApi'

const applySchema = z.object({
  skillsNote: z.string().optional(),
})

type ApplyForm = z.infer<typeof applySchema>

export function PublicVolunteerDetailPage() {
  const { id } = useParams()
  const oppId = Number(id)
  const { showToast } = useToast()
  const user = authStore.getUser()

  const detailQuery = useQuery({
    queryKey: ['volunteer', 'public', oppId],
    queryFn: () => volunteerPublicApi.get(oppId),
    enabled: Number.isFinite(oppId),
  })

  const form = useForm<ApplyForm>({ resolver: zodResolver(applySchema) })

  const applyMutation = useMutation({
    mutationFn: (values: ApplyForm) => volunteerApplyApi.apply(oppId, { skillsNote: values.skillsNote || null }),
    onSuccess: () => {
      showToast('success', 'Inscricao enviada.')
      form.reset()
    },
    onError: (err: unknown) => {
      const msg = err instanceof Error ? err.message : 'Falha ao inscrever'
      showToast('error', msg)
    },
  })

  if (!Number.isFinite(oppId)) {
    return (
      <PublicShell>
        <p className="p-8 text-center text-sm text-slate-600">Vaga invalida.</p>
      </PublicShell>
    )
  }

  const d = detailQuery.data

  return (
    <PublicShell>
      <div className="mx-auto max-w-3xl px-4 py-10">
        <Link to="/vagas" className="text-sm font-semibold text-brand-600">
          Voltar
        </Link>
        {detailQuery.isLoading ? (
          <p className="mt-6 text-sm text-slate-500">Carregando...</p>
        ) : !d ? (
          <p className="mt-6 text-sm text-slate-600">Vaga nao encontrada.</p>
        ) : (
          <>
            <header className="mt-6">
              <h1 className="font-heading text-3xl font-bold text-slate-900">{d.summary.title}</h1>
              <p className="mt-2 text-sm text-slate-600">
                {d.summary.ngoName} · {d.summary.startDate} → {d.summary.endDate}
              </p>
            </header>
            <section className="mt-8 rounded-xl bg-white p-6 shadow-card">
              <h2 className="font-heading text-lg font-semibold text-slate-900">Descricao</h2>
              <p className="mt-3 whitespace-pre-wrap text-sm text-slate-700">{d.description ?? '—'}</p>
              {d.skillsRequired ? (
                <p className="mt-4 text-sm text-slate-700">
                  <span className="font-semibold">Habilidades:</span> {d.skillsRequired}
                </p>
              ) : null}
              {d.hoursPerWeek != null ? (
                <p className="mt-2 text-sm text-slate-700">
                  <span className="font-semibold">Horas/semana:</span> {d.hoursPerWeek}
                </p>
              ) : null}
            </section>

            <Card className="mt-8">
              <CardHeader>
                <CardTitle>Escalas publicadas</CardTitle>
              </CardHeader>
              <CardContent>
                {d.schedules?.length ? (
                  <ul className="space-y-2 text-sm text-slate-700">
                    {d.schedules.map((s) => (
                      <li key={s.id}>
                        <span className="font-medium">{s.title ?? 'Turno'}</span>
                        <span className="text-slate-500">
                          {' '}
                          {s.slotStart} → {s.slotEnd}
                        </span>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-sm text-slate-500">Sem escalas cadastradas.</p>
                )}
              </CardContent>
            </Card>

            <Card className="mt-8">
              <CardHeader>
                <CardTitle>Inscricao</CardTitle>
              </CardHeader>
              <CardContent>
                {!user ? (
                  <p className="text-sm text-slate-600">
                    <Link className="font-semibold text-brand-600" to="/login">
                      Faca login
                    </Link>{' '}
                    para se candidatar.
                  </p>
                ) : (
                  <form className="flex max-w-lg flex-col gap-4" onSubmit={form.handleSubmit((v) => applyMutation.mutate(v))}>
                    <TextField
                      label="Conte suas habilidades e disponibilidade (opcional)"
                      {...form.register('skillsNote')}
                    />
                    <Button type="submit" isLoading={applyMutation.isPending}>
                      Enviar inscricao
                    </Button>
                  </form>
                )}
              </CardContent>
            </Card>
          </>
        )}
      </div>
    </PublicShell>
  )
}
