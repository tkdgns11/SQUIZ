import { useNavigate } from 'react-router-dom'
import { useMenuStore } from '@/store/menuStore'

interface MainSidebarProps {
  width?: string
}

function MainSidebar({ width = '240px' }: MainSidebarProps) {
  const navigate = useNavigate()
  const { menus, activeIndex, setActiveIndex } = useMenuStore()

  const menuItems = Object.entries(menus)
    .filter(([key]) => key !== 'logout')
    .map(([key, item]) => ({
      key,
      icon: item.icon,
      title: item.name,
      path: item.path
    }))

  const handleMenuSelect = (index: number) => {
    setActiveIndex(index)
    navigate(menuItems[index].path)
  }

  return (
    <div className="main-sidebar" style={{ width }}>
      <div className="hide-on-small">
        <ul className="menu-vertical">
          {menuItems.map((item, index) => (
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
    </div>
  )
}

export default MainSidebar
