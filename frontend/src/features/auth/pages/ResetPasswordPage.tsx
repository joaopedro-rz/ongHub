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

const schema = z
  .object({
    token: z.string().min(10, 'Token invalido'),
    password: z.string().min(6, 'Minimo de 6 caracteres'),
    confirmPassword: z.string().min(6, 'Minimo de 6 caracteres'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'As senhas nao conferem',
    path: ['confirmPassword'],
  })

type FormValues = z.infer<typeof schema>

export function ResetPasswordPage() {
  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm<FormValues>({ resolver: zodResolver(schema) })

  const { showToast } = useToast()

  const mutation = useMutation({
    mutationFn: authApi.resetPassword,
    onSuccess: () => {
      showToast('success', 'Senha atualizada com sucesso')
    },
    onError: (error: unknown) => {
      const applied = applyApiFieldErrors<FormValues>(error, setError)
      if (applied) {
        showToast('error', 'Revise os campos informados')
        return
      }
      const message = error instanceof Error ? error.message : 'Erro ao atualizar senha'
      showToast('error', message)
    },
  })

  const onSubmit = (values: FormValues) => {
    mutation.mutate({ token: values.token, password: values.password })
  }

  return (
    <AuthLayout
      title="Defina uma nova senha"
      subtitle="Escolha uma senha forte para proteger sua conta."
    >
      <form className="flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)}>
        <TextField
          label="Token"
          placeholder="Cole o token recebido"
          {...register('token')}
          error={errors.token?.message}
        />
        <TextField
          label="Nova senha"
          type="password"
          placeholder="Nova senha"
          {...register('password')}
          error={errors.password?.message}
        />
        <TextField
          label="Confirmar senha"
          type="password"
          placeholder="Repita a senha"
          {...register('confirmPassword')}
          error={errors.confirmPassword?.message}
        />
        <Button type="submit" isLoading={mutation.isPending}>
          Atualizar senha
        </Button>
      </form>
    </AuthLayout>
  )
}
