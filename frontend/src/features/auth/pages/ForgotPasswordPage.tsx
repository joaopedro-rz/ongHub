import { zodResolver } from '@hookform/resolvers/zod'
import { useMutation } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { AuthLayout } from '../../../components/shared/AuthLayout'
import { Button } from '../../../components/ui/Button'
import { TextField } from '../../../components/ui/TextField'
import { authApi } from '../services/authApi'
import { useToast } from '../../../components/ui/Toast'
import { applyApiFieldErrors } from '../../../lib/formErrors'

const schema = z.object({
  email: z.string().email('Email invalido'),
})

type FormValues = z.infer<typeof schema>

export function ForgotPasswordPage() {
  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm<FormValues>({ resolver: zodResolver(schema) })

  const { showToast } = useToast()

  const mutation = useMutation({
    mutationFn: authApi.forgotPassword,
    onSuccess: () => {
      showToast('success', 'Email de recuperacao enviado')
    },
    onError: (error: unknown) => {
      const applied = applyApiFieldErrors<FormValues>(error, setError)
      if (applied) {
        showToast('error', 'Revise os campos informados')
        return
      }
      const message = error instanceof Error ? error.message : 'Erro ao solicitar recuperacao'
      showToast('error', message)
    },
  })

  const onSubmit = (values: FormValues) => {
    mutation.mutate(values)
  }

  return (
    <AuthLayout
      title="Recupere sua senha"
      subtitle="Enviaremos um link para voce redefinir sua senha."
      footer={
        <span>
          Lembrou da senha?{' '}
          <a className="font-semibold text-brand-600" href="/login">
            Voltar para login
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
        <Button type="submit" isLoading={mutation.isPending}>
          Enviar link
        </Button>
      </form>
    </AuthLayout>
  )
}
