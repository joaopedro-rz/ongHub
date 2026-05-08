import type { ReactNode } from 'react'
import { Link } from 'react-router-dom'

export function PublicHeader() {
  return (
    <header className="sticky top-0 z-20 border-b border-slate-200 bg-white/90 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-3">
        <Link to="/" className="font-heading text-lg font-semibold text-brand-600">
          OngHub
        </Link>
        <nav className="hidden items-center gap-6 text-sm font-medium text-slate-600 md:flex" aria-label="Principal">
          <Link to="/campanhas" className="hover:text-brand-600">
            Campanhas
          </Link>
          <Link to="/ongs" className="hover:text-brand-600">
            ONGs
          </Link>
          <Link to="/vagas" className="hover:text-brand-600">
            Voluntariado
          </Link>
          <Link to="/login" className="rounded-lg bg-brand-500 px-3 py-2 text-white hover:bg-brand-600">
            Entrar
          </Link>
        </nav>
        <Link to="/login" className="md:hidden rounded-lg bg-brand-500 px-3 py-2 text-sm font-semibold text-white">
          Entrar
        </Link>
      </div>
    </header>
  )
}

export function PublicFooter() {
  return (
    <footer className="border-t border-slate-200 bg-white py-10 text-center text-sm text-slate-500">
      <p>OngHub — plataforma para ONGs, campanhas e voluntariado.</p>
      <p className="mt-2">
        <Link to="/login" className="text-brand-600">
          Acesse sua conta
        </Link>
      </p>
    </footer>
  )
}

export function PublicShell({ children }: { children: ReactNode }) {
  return (
    <div className="flex min-h-screen flex-col bg-background">
      <PublicHeader />
      <main className="flex-1">{children}</main>
      <PublicFooter />
    </div>
  )
}

