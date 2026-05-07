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
  token: z.string().min(10, 'Token invalido'),
})

type FormValues = z.infer<typeof schema>

export function VerifyEmailPage() {
  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm<FormValues>({ resolver: zodResolver(schema) })

  const { showToast } = useToast()

  const mutation = useMutation({
    mutationFn: authApi.confirmEmail,
    onSuccess: () => {
      showToast('success', 'Email confirmado com sucesso')
    },
    onError: (error: unknown) => {
      const applied = applyApiFieldErrors<FormValues>(error, setError)
      if (applied) {
        showToast('error', 'Revise os campos informados')
        return
      }
      const message =
        error instanceof Error ? error.message : 'Erro ao confirmar email'
      showToast('error', message)
    },
  })

  const onSubmit = (values: FormValues) => {
    mutation.mutate(values)
  }

  return (
    <AuthLayout
      title="Confirme seu email"
      subtitle="Valide o token enviado para liberar sua conta."
    >
      <form className="flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)}>
        <TextField
          label="Token"
          placeholder="Cole o token recebido"
          {...register('token')}
          error={errors.token?.message}
        />
        <Button type="submit" isLoading={mutation.isPending}>
          Confirmar email
        </Button>
      </form>
    </AuthLayout>
  )
}
