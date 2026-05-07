import type { ButtonHTMLAttributes } from 'react'

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: 'primary' | 'ghost'
  isLoading?: boolean
}

const baseStyles =
  'inline-flex items-center justify-center rounded-lg px-4 py-2 text-sm font-semibold transition focus:outline-none focus:ring-2 focus:ring-brand-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60'

const variantStyles: Record<NonNullable<ButtonProps['variant']>, string> = {
  primary: 'bg-brand-600 text-white hover:bg-brand-700',
  ghost: 'bg-white text-slate-600 hover:bg-slate-100',
}

export function Button({
  variant = 'primary',
  className,
  isLoading = false,
  disabled,
  children,
  ...props
}: ButtonProps) {
  return (
    <button
      {...props}
      disabled={disabled || isLoading}
      className={[baseStyles, variantStyles[variant], className]
        .filter(Boolean)
        .join(' ')}
    >
      {isLoading ? (
        <span className="mr-2 inline-flex h-4 w-4 animate-spin rounded-full border-2 border-white/70 border-t-transparent" />
      ) : null}
      {children}
    </button>
  )
}
