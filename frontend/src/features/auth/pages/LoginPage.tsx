import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'
import { z } from 'zod'
import { AuthLayout } from '../../../components/shared/AuthLayout'
import { Button } from '../../../components/ui/Button'
import { TextField } from '../../../components/ui/TextField'
import { authApi } from '../services/authApi'
import { useToast } from '../../../components/ui/Toast'
import { applyApiFieldErrors } from '../../../lib/formErrors'

const schema = z.object({
  email: z.string().email('Email invalido'),
  password: z.string().min(6, 'Minimo de 6 caracteres'),
})

type FormValues = z.infer<typeof schema>

export function LoginPage() {
  const navigate = useNavigate()
  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm<FormValues>({ resolver: zodResolver(schema) })

  const { showToast } = useToast()

  const mutation = useMutation({
    mutationFn: authApi.login,
    onSuccess: () => {
      showToast('success', 'Login realizado com sucesso')
      navigate('/app', { replace: true })
    },
    onError: (error: unknown) => {
      const applied = applyApiFieldErrors<FormValues>(error, setError)
      if (applied) {
        showToast('error', 'Revise os campos informados')
        return
      }
      const message = error instanceof Error ? error.message : 'Erro ao fazer login'
      showToast('error', message)
    },
  })

  const onSubmit = (values: FormValues) => {
    mutation.mutate(values)
  }

  return (
    <AuthLayout
      title="Acesse sua conta"
      subtitle="Entre para acompanhar campanhas, doacoes e voluntariado."
      footer={
        <span>
          Ainda nao tem conta?{' '}
          <a className="font-semibold text-brand-600" href="/register">
            Crie agora
          </a>
        </span>
      }
    >
      <form className="flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)}>
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
          placeholder="Sua senha"
          {...register('password')}
          error={errors.password?.message}
        />
        <div className="flex items-center justify-between text-sm">
          <a className="text-brand-600" href="/forgot-password">
            Esqueci minha senha
          </a>
        </div>
        <Button type="submit" isLoading={mutation.isPending}>
          Entrar
        </Button>
      </form>
    </AuthLayout>
  )
}
