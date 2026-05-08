import { LayoutDashboard, HeartHandshake, LogOut } from 'lucide-react'
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'
import { navLinkClass } from './navLinkClass'
import { authStore } from '../../lib/authStore'

const shellNavClass =
  'flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition focus:outline-none focus:ring-2 focus:ring-brand-500'

export function AppShell() {
  const navigate = useNavigate()
  const user = authStore.getUser()

  const logout = () => {
    authStore.clear()
    navigate('/login', { replace: true })
  }

  return (
    <div className="min-h-screen bg-background pb-20 md:flex md:pb-0">
      <aside className="hidden w-56 shrink-0 border-r border-slate-200 bg-white md:block">
        <div className="flex h-full flex-col gap-6 p-4">
          <Link to="/" className="font-heading text-lg font-semibold text-brand-600">
            OngHub
          </Link>
          <nav className="flex flex-col gap-1" aria-label="Painel">
            <NavLink to="/app" end className={({ isActive }) => `${shellNavClass} ${navLinkClass(isActive)}`}>
              <LayoutDashboard className="h-4 w-4" aria-hidden />
              Painel
            </NavLink>
            <NavLink to="/app/doacoes" className={({ isActive }) => `${shellNavClass} ${navLinkClass(isActive)}`}>
              <HeartHandshake className="h-4 w-4" aria-hidden />
              Doações
            </NavLink>
          </nav>
          <div className="mt-auto space-y-3 border-t border-slate-100 pt-4 text-xs text-slate-600">
            <p className="truncate font-medium text-slate-900">{user?.name ?? 'Conta'}</p>
            <button
              type="button"
              onClick={logout}
              className="flex w-full items-center gap-2 rounded-lg px-3 py-2 text-left text-sm font-semibold text-slate-700 hover:bg-slate-50 focus:outline-none focus:ring-2 focus:ring-brand-500"
            >
              <LogOut className="h-4 w-4" aria-hidden />
              Sair
            </button>
          </div>
        </div>
      </aside>
      <div className="flex min-h-screen flex-1 flex-col">
        <header className="flex items-center justify-between border-b border-slate-200 bg-white px-4 py-3 md:hidden">
          <Link to="/app" className="font-heading text-lg font-semibold text-brand-600">
            OngHub
          </Link>
          <button
            type="button"
            onClick={logout}
            className="text-xs font-semibold text-slate-600 underline-offset-2 hover:underline"
          >
            Sair
          </button>
        </header>
        <div className="flex-1 p-4 md:p-8">
          <Outlet />
        </div>
      </div>
      <nav
        className="fixed bottom-0 left-0 right-0 flex justify-around border-t border-slate-200 bg-white px-2 py-2 md:hidden"
        aria-label="Painel mobile"
      >
        <NavLink
          to="/app"
          className={({ isActive }) =>
            `flex flex-col items-center gap-1 px-3 py-1 text-xs font-semibold ${isActive ? 'text-brand-700' : 'text-slate-500'}`
          }
          end
        >
          <LayoutDashboard className="h-5 w-5" aria-hidden />
          Painel
        </NavLink>
        <NavLink
          to="/app/doacoes"
          className={({ isActive }) =>
            `flex flex-col items-center gap-1 px-3 py-1 text-xs font-semibold ${isActive ? 'text-brand-700' : 'text-slate-500'}`
          }
        >
          <HeartHandshake className="h-5 w-5" aria-hidden />
          Doações
        </NavLink>
      </nav>
    </div>
  )
}
