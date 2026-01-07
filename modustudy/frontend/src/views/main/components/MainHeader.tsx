import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useMenuStore } from '@/store/menuStore'
import { useAuthStore } from '@/store/authStore'
import ssafyLogo from '@/assets/images/ssafy-logo.png'

interface MainHeaderProps {
  height?: string
  onOpenLoginDialog: () => void
  onOpenRegisterDialog: () => void
}

function MainHeader({ height = '70px', onOpenLoginDialog, onOpenRegisterDialog }: MainHeaderProps) {
  const navigate = useNavigate()
  const { menus, activeIndex, setActiveIndex } = useMenuStore()
  const { token, logout } = useAuthStore()
  const [searchValue, setSearchValue] = useState('')
  const [isCollapse, setIsCollapse] = useState(true)

  const menuItems = Object.entries(menus).map(([key, item]) => ({
    key,
    icon: item.icon,
    title: item.name,
    path: item.path
  }))

  const handleMenuSelect = (index: number) => {
    const keys = Object.keys(menus)
    if (keys[index] === 'logout') {
      return
    }
    setActiveIndex(index)
    navigate(menus[keys[index]].path)
    setIsCollapse(true)
  }

  const handleLogoClick = () => {
    setActiveIndex(0)
    navigate('/')
  }

  return (
    <div className="main-header" style={{ height }}>
      <div className="logo-wrapper" onClick={handleLogoClick}>
        <img src={ssafyLogo} alt="SSAFY Logo" className="logo-image" />
      </div>

      {/* Desktop */}
      <div className="hide-on-small">
        <div className="tool-wrapper">
          <div className="search-field">
            <input
              type="text"
              placeholder="검색"
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
            />
          </div>
          <div className="button-wrapper">
            {token ? (
              <button onClick={logout}>로그아웃</button>
            ) : (
              <>
                <button onClick={onOpenRegisterDialog}>회원가입</button>
                <button onClick={onOpenLoginDialog}>로그인</button>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Mobile */}
      <div className="hide-on-big">
        <div className="menu-icon-wrapper" onClick={() => setIsCollapse(!isCollapse)}>
          <i className="icon-menu">☰</i>
        </div>
        {!isCollapse && (
          <div className="mobile-sidebar-wrapper">
            <div className="mobile-sidebar">
              <div className="mobile-sidebar-tool-wrapper">
                <div className="logo-wrapper">
                  <img src={ssafyLogo} alt="SSAFY Logo" className="logo-image" />
                </div>
                {token ? (
                  <button className="mobile-sidebar-btn login-btn" onClick={logout}>
                    로그아웃
                  </button>
                ) : (
                  <>
                    <button className="mobile-sidebar-btn login-btn" onClick={onOpenLoginDialog}>
                      로그인
                    </button>
                    <button className="mobile-sidebar-btn register-btn" onClick={onOpenRegisterDialog}>
                      회원가입
                    </button>
                  </>
                )}
              </div>
              <ul className="menu">
                {menuItems.filter(item => item.key !== 'logout').map((item, index) => (
                  <li
                    key={item.key}
                    className={activeIndex === index ? 'active' : ''}
                    onClick={() => handleMenuSelect(index)}
                  >
                    <span>{item.title}</span>
                  </li>
                ))}
              </ul>
            </div>
            <div className="mobile-sidebar-backdrop" onClick={() => setIsCollapse(true)} />
          </div>
        )}
      </div>
    </div>
  )
}

export default MainHeader
