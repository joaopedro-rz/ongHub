import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import { z } from 'zod'
import { AuthLayout } from '../../../components/shared/AuthLayout'
import { Button } from '../../../components/ui/Button'
import { TextField } from '../../../components/ui/TextField'
import { authApi } from '../services/authApi'
import type { AuthRole } from '../types/auth'
import { useToast } from '../../../components/ui/Toast'
import { applyApiFieldErrors } from '../../../lib/formErrors'

const schema = z.object({
  name: z.string().min(2, 'Informe seu nome completo'),
  email: z.string().email('Email invalido'),
  password: z.string().min(6, 'Minimo de 6 caracteres'),
  role: z.enum(['DONOR', 'VOLUNTEER', 'ONG_MANAGER', 'ADMIN']),
})

type FormValues = z.infer<typeof schema>

const roles: { label: string; value: AuthRole }[] = [
  { label: 'Doador', value: 'DONOR' },
  { label: 'Voluntario', value: 'VOLUNTEER' },
  { label: 'Gestor de ONG', value: 'ONG_MANAGER' },
]

export function RegisterPage() {
  const navigate = useNavigate()
  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { role: 'DONOR' },
  })

  const { showToast } = useToast()

  const mutation = useMutation({
    mutationFn: authApi.register,
    onSuccess: () => {
      showToast('success', 'Cadastro realizado com sucesso')
      navigate('/app', { replace: true })
    },
    onError: (error: unknown) => {
      const applied = applyApiFieldErrors<FormValues>(error, setError)
      if (applied) {
        showToast('error', 'Revise os campos informados')
        return
      }
      const message = error instanceof Error ? error.message : 'Erro ao cadastrar'
      showToast('error', message)
    },
  })

  const onSubmit = (values: FormValues) => {
    mutation.mutate(values)
  }

  return (
    <AuthLayout
      title="Crie sua conta"
      subtitle="Cadastre-se para apoiar causas e participar de campanhas."
      footer={
        <span>
          Ja tem conta?{' '}
          <a className="font-semibold text-brand-600" href="/login">
            Entrar
          </a>
        </span>
      }
    >
      <form className="flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)}>
        <TextField
          label="Nome completo"
          placeholder="Seu nome"
          {...register('name')}
          error={errors.name?.message}
        />
        <TextField
          label="Email"
          type="email"
          placeholder="voce@exemplo.com"
          {...register('email')}
          error={errors.email?.message}
        />
        <TextField
          label="Senha"
          type="password"
          placeholder="Crie uma senha segura"
          {...register('password')}
          error={errors.password?.message}
        />
        <label className="flex flex-col gap-2 text-sm font-medium text-slate-600">
          <span>Perfil</span>
          <select
            className="rounded-lg border border-slate-200 bg-white px-3 py-2 text-slate-900 shadow-sm focus:border-brand-500 focus:outline-none focus:ring-2 focus:ring-brand-100"
            {...register('role')}
          >
            {roles.map((role) => (
              <option key={role.value} value={role.value}>
                {role.label}
              </option>
            ))}
          </select>
          {errors.role?.message ? (
            <span className="text-xs text-danger">{errors.role.message}</span>
          ) : null}
        </label>
        <Button type="submit" isLoading={mutation.isPending}>
          Criar conta
        </Button>
      </form>
    </AuthLayout>
  )
}
