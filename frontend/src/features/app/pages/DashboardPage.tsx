import { useMutation, useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import { Button } from '../../../components/ui/Button'
import { Card, CardContent, CardHeader, CardTitle } from '../../../components/ui/Card'
import { useToast } from '../../../components/ui/Toast'
import { authStore } from '../../../lib/authStore'
import { triggerBlobDownload } from '../../../lib/download'
import { formatBrl } from '../../../lib/formatMoney'
import type { ApiError } from '../../../types/api'
import type { AdminDashboard, DonorDashboard, NgoDashboard } from '../../../types/models'
import { AdminDashboardChart, DonorDashboardChart, NgoDashboardChart } from '../../dashboard/components/DashboardCharts'
import { dashboardApi } from '../../dashboard/services/dashboardApi'
import { ngoPrivateApi } from '../../ngos/services/ngoPrivateApi'
import { reportApi } from '../../reports/services/reportApi'

function errorMessage(err: unknown, fallback: string) {
  if (err && typeof err === 'object' && 'message' in err && typeof (err as ApiError).message === 'string') {
    return (err as ApiError).message
  }
  if (err instanceof Error) return err.message
  return fallback
}

export function DashboardPage() {
  const user = authStore.getUser()
  const roles = user?.roles ?? []
  const isAdmin = roles.includes('ADMIN')
  const isManager = roles.includes('ONG_MANAGER')
  const { showToast } = useToast()

  const managedNgosQuery = useQuery({
    queryKey: ['ngos', 'managed', user?.email],
    queryFn: () => ngoPrivateApi.listManaged(user!.email!, 0, 5),
    enabled: Boolean(isManager && user?.email),
  })

  const adminNgosPickQuery = useQuery({
    queryKey: ['ngos', 'admin', 'pick'],
    queryFn: () => ngoPrivateApi.listAll(0, 100),
    enabled: isAdmin,
  })

  const managedNgoId = managedNgosQuery.data?.content?.[0]?.id

  const [adminPickOverride, setAdminPickOverride] = useState<number | null>(null)

  const fallbackAdminNgoId = adminNgosPickQuery.data?.content?.[0]?.id ?? null

  const adminSelectedNgoId = adminPickOverride ?? fallbackAdminNgoId

  const transparencyNgoId = isAdmin ? adminSelectedNgoId : (managedNgoId ?? null)

  const adminDashboardQuery = useQuery<AdminDashboard>({
    queryKey: ['dashboard', 'admin'],
    queryFn: dashboardApi.admin,
    enabled: isAdmin,
  })

  const ngoDashboardQuery = useQuery<NgoDashboard>({
    queryKey: ['dashboard', 'ngo', managedNgoId],
    queryFn: () => dashboardApi.ngo(managedNgoId!),
    enabled: Boolean(isManager && managedNgoId),
  })

  const donorDashboardQuery = useQuery<DonorDashboard>({
    queryKey: ['dashboard', 'donor'],
    queryFn: dashboardApi.donor,
    enabled: !isAdmin && !(isManager && managedNgoId),
  })

  const csvMutation = useMutation({
    mutationFn: async () => {
      if (!transparencyNgoId) throw new Error('Selecione uma ONG')
      return reportApi.transparencyCsv(transparencyNgoId)
    },
    onSuccess: (blob) => {
      triggerBlobDownload(blob, 'transparency.csv')
      showToast('success', 'CSV gerado')
    },
    onError: (e) => showToast('error', errorMessage(e, 'Falha ao exportar CSV')),
  })

  const pdfMutation = useMutation({
    mutationFn: async () => {
      if (!transparencyNgoId) throw new Error('Selecione uma ONG')
      return reportApi.transparencyPdf(transparencyNgoId)
    },
    onSuccess: (blob) => {
      triggerBlobDownload(blob, 'transparency.pdf')
      showToast('success', 'PDF gerado')
    },
    onError: (e) => showToast('error', errorMessage(e, 'Falha ao exportar PDF')),
  })

  const adminOptions = adminNgosPickQuery.data?.content ?? []

  if (!user) {
    return <p className="text-sm text-slate-600">Carregando perfil...</p>
  }

  return (
    <div className="mx-auto flex max-w-5xl flex-col gap-8">
      <header>
        <h1 className="font-heading text-2xl font-bold text-slate-900">Painel</h1>
        <p className="mt-1 text-sm text-slate-600">
          Olá, <span className="font-semibold text-slate-800">{user.name}</span>.
        </p>
      </header>

      {isAdmin ? (
        <Card>
          <CardHeader>
            <CardTitle>Visão administrativa</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            {adminDashboardQuery.isLoading ? (
              <p className="text-sm text-slate-500">Carregando indicadores...</p>
            ) : adminDashboardQuery.data ? (
              <>
                <AdminDashboardChart data={adminDashboardQuery.data} />
                <dl className="grid gap-3 text-sm text-slate-700 sm:grid-cols-2">
                  <div>
                    <dt className="text-slate-500">ONGs</dt>
                    <dd className="text-lg font-semibold">{adminDashboardQuery.data.ngos}</dd>
                  </div>
                  <div>
                    <dt className="text-slate-500">Doações registradas</dt>
                    <dd className="text-lg font-semibold">{adminDashboardQuery.data.donations}</dd>
                  </div>
                </dl>
              </>
            ) : (
              <p className="text-sm text-danger">Não foi possível carregar o painel admin.</p>
            )}
          </CardContent>
        </Card>
      ) : null}

      {isManager ? (
        <Card>
          <CardHeader>
            <CardTitle>ONG que você gerencia</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            {managedNgosQuery.isLoading ? (
              <p className="text-sm text-slate-500">Carregando ONGs...</p>
            ) : !managedNgoId ? (
              <p className="text-sm text-slate-600">
                Nenhuma ONG vinculada ao seu usuário. Cadastre uma organização pela API ou fluxo de gestão.
              </p>
            ) : ngoDashboardQuery.data ? (
              <>
                <NgoDashboardChart data={ngoDashboardQuery.data} />
                <p className="text-sm text-slate-700">
                  Total financeiro confirmado:{' '}
                  <span className="font-semibold">{formatBrl(ngoDashboardQuery.data.confirmedFinancialTotal)}</span>
                </p>
              </>
            ) : (
              <p className="text-sm text-slate-500">Carregando painel da ONG...</p>
            )}
          </CardContent>
        </Card>
      ) : null}

      {!isAdmin && !(isManager && managedNgoId) ? (
        <Card>
          <CardHeader>
            <CardTitle>Seu impacto como apoiador</CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            {donorDashboardQuery.isLoading ? (
              <p className="text-sm text-slate-500">Carregando...</p>
            ) : donorDashboardQuery.data ? (
              <>
                <DonorDashboardChart data={donorDashboardQuery.data} />
                <p className="text-sm text-slate-700">
                  Total confirmado em doações financeiras:{' '}
                  <span className="font-semibold">{formatBrl(donorDashboardQuery.data.confirmedFinancialTotal)}</span>
                </p>
              </>
            ) : (
              <p className="text-sm text-danger">Não foi possível carregar seu painel.</p>
            )}
          </CardContent>
        </Card>
      ) : null}

      {(isAdmin || isManager) && transparencyNgoId ? (
        <Card>
          <CardHeader>
            <CardTitle>Relatório de transparência</CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col gap-4">
            {isAdmin ? (
              <label className="flex flex-col gap-2 text-sm font-medium text-slate-700">
                ONG alvo
                <select
                  className="rounded-lg border border-slate-200 px-3 py-2 text-slate-900"
                  value={adminSelectedNgoId ?? ''}
                  onChange={(e) => setAdminPickOverride(Number(e.target.value))}
                >
                  {adminOptions.map((n) => (
                    <option key={n.id} value={n.id}>
                      {n.name}
                    </option>
                  ))}
                </select>
              </label>
            ) : null}
            <div className="flex flex-wrap gap-3">
              <Button type="button" variant="secondary" isLoading={csvMutation.isPending} onClick={() => csvMutation.mutate()}>
                Baixar CSV
              </Button>
              <Button type="button" variant="secondary" isLoading={pdfMutation.isPending} onClick={() => pdfMutation.mutate()}>
                Baixar PDF
              </Button>
            </div>
          </CardContent>
        </Card>
      ) : null}
    </div>
  )
}
