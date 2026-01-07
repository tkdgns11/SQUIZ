import { useState } from 'react'
import axios from 'axios'

interface RegisterDialogProps {
  open: boolean
  onClose: () => void
}

function RegisterDialog({ open, onClose }: RegisterDialogProps) {
  const [id, setId] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [errors, setErrors] = useState({ id: '', password: '', name: '' })

  const validate = () => {
    const newErrors = { id: '', password: '', name: '' }
    let valid = true

    if (!id) {
      newErrors.id = '아이디를 입력해주세요'
      valid = false
    }

    if (!password) {
      newErrors.password = '비밀번호를 입력해주세요'
      valid = false
    }

    if (!name) {
      newErrors.name = '이름을 입력해주세요'
      valid = false
    }

    setErrors(newErrors)
    return valid
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (validate()) {
      try {
        await axios.post('/api/v1/users', { id, password, name })
        alert('회원가입이 완료되었습니다!')
        handleClose()
      } catch (error) {
        alert('회원가입에 실패했습니다.')
      }
    }
  }

  const handleClose = () => {
    setId('')
    setPassword('')
    setName('')
    setErrors({ id: '', password: '', name: '' })
    onClose()
  }

  if (!open) return null

  return (
    <div className="login-dialog-overlay">
      <div className="login-dialog">
        <div className="login-dialog-header">
          <h3>회원가입</h3>
          <button className="close-btn" onClick={handleClose}>&times;</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="register-id">아이디</label>
            <input
              type="text"
              id="register-id"
              value={id}
              onChange={(e) => setId(e.target.value)}
              autoComplete="off"
            />
            {errors.id && <span className="error">{errors.id}</span>}
          </div>
          <div className="form-group">
            <label htmlFor="register-name">이름</label>
            <input
              type="text"
              id="register-name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              autoComplete="off"
            />
            {errors.name && <span className="error">{errors.name}</span>}
          </div>
          <div className="form-group">
            <label htmlFor="register-password">비밀번호</label>
            <input
              type="password"
              id="register-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="off"
            />
            {errors.password && <span className="error">{errors.password}</span>}
          </div>
          <div className="dialog-footer">
            <button type="submit" className="btn-primary">가입하기</button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default RegisterDialog
