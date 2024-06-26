import { useCallback, useRef } from 'react'

export const useCreateDebounce = (wait = 400, leading = false) => {
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null)
  const func = useRef<Function | null>(null)

  const _clearTimer = () => {
    timer.current && clearTimeout(timer.current)
    timer.current = null
  }

  return (newFunction: Function, scope?: Function, args?: Array<object>) => {
    // Leading (Call on first)
    if (leading) {
      func.current = newFunction

      // If timer not active, call.
      if (timer.current === null) {
        func.current && func.current.apply(scope, args)
      }

      _clearTimer()
      timer.current = setTimeout(() => _clearTimer(), wait)
    }
    // Default (Call on last)
    else {
      _clearTimer()

      func.current = newFunction

      timer.current = setTimeout(() => {
        func.current && func.current.apply(scope, args)
        _clearTimer()
      }, wait)
    }
  }
}

export const useDebounceFunc = (
  func: (...args: any[]) => void,
  delay = 1000,
  leading = false,
) => {
  const debounce = useCreateDebounce(delay, leading)
  return useCallback((...args: any[]) => debounce(() => func(...args)), [debounce, func])
}
