import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import type { AdminDashboard, DonorDashboard, NgoDashboard } from '../../../types/models'

export function AdminDashboardChart({ data }: { data: AdminDashboard }) {
  const chartData = [
    { name: 'ONGs', valor: data.ngos },
    { name: 'Usuários', valor: data.users },
    { name: 'Campanhas', valor: data.campaigns },
    { name: 'Doações', valor: data.donations },
    { name: 'Inscrições vol.', valor: data.volunteerApplications },
  ]

  return (
    <div className="h-72 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={chartData} margin={{ top: 8, right: 8, left: 0, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
          <XAxis dataKey="name" tick={{ fontSize: 11 }} />
          <YAxis allowDecimals={false} tick={{ fontSize: 11 }} />
          <Tooltip formatter={(value) => [Number(value ?? 0), 'Total']} />
          <Legend />
          <Bar dataKey="valor" name="Quantidade" fill="#059669" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}

export function DonorDashboardChart({ data }: { data: DonorDashboard }) {
  const chartData = [
    { name: 'Doações registradas', valor: data.donationsCount },
    { name: 'ONGs apoiadas', valor: data.supportedNgos },
  ]

  return (
    <div className="h-64 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={chartData} layout="vertical" margin={{ top: 8, right: 16, left: 8, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
          <XAxis type="number" allowDecimals={false} />
          <YAxis type="category" dataKey="name" width={140} tick={{ fontSize: 11 }} />
          <Tooltip formatter={(value) => [Number(value ?? 0), '']} />
          <Bar dataKey="valor" fill="#047857" radius={[0, 4, 4, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}

export function NgoDashboardChart({ data }: { data: NgoDashboard }) {
  const chartData = [
    { name: 'Doações', valor: data.donationsCount },
    { name: 'Campanhas ativas', valor: data.activeCampaigns },
    { name: 'Voluntários', valor: data.approvedVolunteers },
  ]

  return (
    <div className="h-64 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
          <XAxis dataKey="name" tick={{ fontSize: 11 }} />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Bar dataKey="valor" fill="#475569" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}
