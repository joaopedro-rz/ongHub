import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { Link, useParams } from 'react-router-dom'
import { z } from 'zod'
import { PublicShell } from '../../../components/layout/PublicShell'
import { Button } from '../../../components/ui/Button'
import { Card, CardContent, CardHeader, CardTitle } from '../../../components/ui/Card'
import { TextField } from '../../../components/ui/TextField'
import { useToast } from '../../../components/ui/Toast'
import { authStore } from '../../../lib/authStore'
import { formatBrl } from '../../../lib/formatMoney'
import { donationApi } from '../../donations/services/donationApi'
import { campaignPublicApi } from '../services/campaignPublicApi'

const financialSchema = z.object({
  amount: z.string().min(1, 'Informe o valor'),
  paymentMethod: z.string().optional(),
  proofUrl: z.string().optional(),
  notes: z.string().optional(),
})

type FinancialForm = z.infer<typeof financialSchema>

export function PublicCampaignDetailPage() {
  const { id } = useParams()
  const campaignId = Number(id)
  const { showToast } = useToast()
  const queryClient = useQueryClient()
  const user = authStore.getUser()

  const detailQuery = useQuery({
    queryKey: ['campaigns', 'public', campaignId],
    queryFn: () => campaignPublicApi.get(campaignId),
    enabled: Number.isFinite(campaignId),
  })

  const financialForm = useForm<FinancialForm>({
    resolver: zodResolver(financialSchema),
    defaultValues: { paymentMethod: '', proofUrl: '', notes: '' },
  })

  const donateMutation = useMutation({
    mutationFn: (values: FinancialForm) =>
      donationApi.createFinancial({
        campaignId,
        amount: values.amount.replace(',', '.'),
        paymentMethod: values.paymentMethod || undefined,
        proofUrl: values.proofUrl || undefined,
        notes: values.notes || undefined,
      }),
    onSuccess: () => {
      showToast('success', 'Doacao registrada. Aguarde confirmacao da ONG.')
      financialForm.reset({ paymentMethod: '', proofUrl: '', notes: '', amount: '' })
      void queryClient.invalidateQueries({ queryKey: ['donations', 'me'] })
    },
    onError: (err: unknown) => {
      const msg = err instanceof Error ? err.message : 'Nao foi possivel registrar a doacao'
      showToast('error', msg)
    },
  })

  const canDonate = Boolean(user)

  if (!Number.isFinite(campaignId)) {
    return (
      <PublicShell>
        <p className="p-8 text-center text-sm text-slate-600">Campanha invalida.</p>
      </PublicShell>
    )
  }

  const c = detailQuery.data

  return (
    <PublicShell>
      <div className="mx-auto max-w-4xl px-4 py-10">
        <Link to="/campanhas" className="text-sm font-semibold text-brand-600">
          Voltar para campanhas
        </Link>
        {detailQuery.isLoading ? (
          <p className="mt-6 text-sm text-slate-500">Carregando...</p>
        ) : !c ? (
          <p className="mt-6 text-sm text-slate-600">Campanha nao encontrada.</p>
        ) : (
          <>
            <header className="mt-6">
              <h1 className="font-heading text-3xl font-bold text-slate-900">{c.title}</h1>
              <p className="mt-2 text-sm text-slate-600">
                {c.ngoName} · Meta {formatBrl(c.financialGoal)}
              </p>
              {c.urgent ? (
                <span className="mt-3 inline-block rounded-full bg-amber-100 px-3 py-1 text-xs font-semibold text-amber-800">
                  Urgente
                </span>
              ) : null}
            </header>
            <section className="mt-8 rounded-xl bg-white p-6 shadow-card">
              <h2 className="font-heading text-lg font-semibold text-slate-900">Sobre</h2>
              <p className="mt-3 whitespace-pre-wrap text-sm leading-relaxed text-slate-700">{c.description}</p>
            </section>

            <section className="mt-8 grid gap-6 md:grid-cols-2">
              <Card>
                <CardHeader>
                  <CardTitle>Necessidades (itens)</CardTitle>
                </CardHeader>
                <CardContent>
                  {c.items?.length ? (
                    <ul className="space-y-3 text-sm text-slate-700">
                      {c.items.map((it) => (
                        <li key={it.id}>
                          <span className="font-medium">{it.itemName}</span>
                          <span className="text-slate-500">
                            {' '}
                            — {it.quantityReceived}/{it.quantityNeeded} {it.unit ?? ''}
                          </span>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-sm text-slate-500">Sem itens cadastrados.</p>
                  )}
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Novidades</CardTitle>
                </CardHeader>
                <CardContent>
                  {c.updates?.length ? (
                    <ul className="space-y-4">
                      {c.updates.map((u) => (
                        <li key={u.id} className="text-sm">
                          <p className="font-semibold text-slate-900">{u.title}</p>
                          <p className="mt-1 text-slate-600">{u.body}</p>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-sm text-slate-500">Sem atualizacoes.</p>
                  )}
                </CardContent>
              </Card>
            </section>

            <section className="mt-10">
              <Card>
                <CardHeader>
                  <CardTitle>Doacao financeira</CardTitle>
                </CardHeader>
                <CardContent>
                  {!canDonate ? (
                    <p className="text-sm text-slate-600">
                      <Link className="font-semibold text-brand-600" to="/login">
                        Entre na sua conta
                      </Link>{' '}
                      para registrar uma doacao vinculada ao seu perfil.
                    </p>
                  ) : (
                    <form
                      className="flex max-w-md flex-col gap-4"
                      onSubmit={financialForm.handleSubmit((v) => donateMutation.mutate(v))}
                    >
                      <TextField
                        label="Valor (BRL)"
                        placeholder="120.00"
                        {...financialForm.register('amount')}
                        error={financialForm.formState.errors.amount?.message}
                      />
                      <TextField
                        label="Metodo de pagamento (opcional)"
                        {...financialForm.register('paymentMethod')}
                        error={financialForm.formState.errors.paymentMethod?.message}
                      />
                      <TextField
                        label="URL do comprovante (opcional)"
                        {...financialForm.register('proofUrl')}
                        error={financialForm.formState.errors.proofUrl?.message}
                      />
                      <TextField label="Observacoes (opcional)" {...financialForm.register('notes')} />
                      <Button type="submit" isLoading={donateMutation.isPending}>
                        Registrar doacao
                      </Button>
                    </form>
                  )}
                </CardContent>
              </Card>
            </section>
          </>
        )}
      </div>
    </PublicShell>
  )
}
