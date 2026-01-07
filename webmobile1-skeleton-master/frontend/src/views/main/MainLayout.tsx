import { useState } from 'react'
import { Outlet } from 'react-router-dom'
import MainHeader from './components/MainHeader'
import MainSidebar from './components/MainSidebar'
import MainFooter from './components/MainFooter'
import LoginDialog from './components/LoginDialog'
import RegisterDialog from './components/RegisterDialog'
import './main.css'

function MainLayout() {
  const [loginDialogOpen, setLoginDialogOpen] = useState(false)
  const [registerDialogOpen, setRegisterDialogOpen] = useState(false)

  return (
    <div className="main-wrapper">
      <MainHeader
        height="70px"
        onOpenLoginDialog={() => setLoginDialogOpen(true)}
        onOpenRegisterDialog={() => setRegisterDialogOpen(true)}
      />
      <div className="main-container">
        <aside className="sidebar hide-on-small" style={{ width: '240px' }}>
          <MainSidebar width="240px" />
        </aside>
        <main className="main-content">
          <Outlet />
        </main>
      </div>
      <MainFooter height="110px" />
      <LoginDialog
        open={loginDialogOpen}
        onClose={() => setLoginDialogOpen(false)}
      />
      <RegisterDialog
        open={registerDialogOpen}
        onClose={() => setRegisterDialogOpen(false)}
      />
    </div>
  )
}

export default MainLayout
