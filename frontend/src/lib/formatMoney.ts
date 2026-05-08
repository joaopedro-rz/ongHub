export function formatBrl(amount: string | null | undefined) {
  if (amount == null || amount === '') return '—'
  const n = Number(amount)
  if (Number.isNaN(n)) return amount
  return n.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}
