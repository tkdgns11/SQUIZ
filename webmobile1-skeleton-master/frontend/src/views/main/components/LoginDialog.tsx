import { useState } from 'react'
import { useAuthStore } from '@/store/authStore'

interface LoginDialogProps {
  open: boolean
  onClose: () => void
}

function LoginDialog({ open, onClose }: LoginDialogProps) {
  const [id, setId] = useState('')
  const [password, setPassword] = useState('')
  const [errors, setErrors] = useState({ id: '', password: '' })
  const login = useAuthStore((state) => state.login)

  const validate = () => {
    const newErrors = { id: '', password: '' }
    let valid = true

    if (!id) {
      newErrors.id = 'Please input ID'
      valid = false
    }

    if (!password) {
      newErrors.password = 'Please input password'
      valid = false
    }

    setErrors(newErrors)
    return valid
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (validate()) {
      try {
        await login(id, password)
        handleClose()
      } catch (error) {
        alert('Login failed!')
      }
    }
  }

  const handleClose = () => {
    setId('')
    setPassword('')
    setErrors({ id: '', password: '' })
    onClose()
  }

  if (!open) return null

  return (
    <div className="login-dialog-overlay">
      <div className="login-dialog">
        <div className="login-dialog-header">
          <h3>로그인</h3>
          <button className="close-btn" onClick={handleClose}>&times;</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="id">아이디</label>
            <input
              type="text"
              id="id"
              value={id}
              onChange={(e) => setId(e.target.value)}
              autoComplete="off"
            />
            {errors.id && <span className="error">{errors.id}</span>}
          </div>
          <div className="form-group">
            <label htmlFor="password">비밀번호</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="off"
            />
            {errors.password && <span className="error">{errors.password}</span>}
          </div>
          <div className="dialog-footer">
            <button type="submit" className="btn-primary">로그인</button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default LoginDialog
