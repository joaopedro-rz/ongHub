/** Shared active-state classes for sidebar / nav links in the shell */
export function navLinkClass(active: boolean) {
  return active ? 'text-brand-700 bg-brand-50' : 'text-slate-600 hover:bg-slate-50'
}
