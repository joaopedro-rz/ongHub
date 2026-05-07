import type { ReactNode } from 'react'

type AuthLayoutProps = {
  title: string
  subtitle: string
  children: ReactNode
  footer?: ReactNode
}

export function AuthLayout({ title, subtitle, children, footer }: AuthLayoutProps) {
  return (
    <div className="min-h-screen bg-background px-4 py-10">
      <div className="mx-auto flex w-full max-w-5xl flex-col overflow-hidden rounded-2xl bg-white shadow-card lg:flex-row">
        <div className="flex flex-1 flex-col justify-between bg-brand-600 px-8 py-10 text-white">
          <div>
            <span className="text-sm font-semibold uppercase tracking-wide text-white/80">
              OngHub
            </span>
            <h1 className="mt-4 text-3xl font-semibold leading-tight">{title}</h1>
            <p className="mt-2 text-sm text-white/80">{subtitle}</p>
          </div>
          <p className="mt-8 text-xs text-white/70">
            Conecte doadores, ONGs e voluntarios com seguranca e transparencia.
          </p>
        </div>
        <div className="flex flex-1 flex-col gap-6 px-8 py-10">
          {children}
          {footer ? <div className="text-sm text-slate-500">{footer}</div> : null}
        </div>
      </div>
    </div>
  )
}
