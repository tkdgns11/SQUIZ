interface MainFooterProps {
  height?: string
}

function MainFooter({ height = '110px' }: MainFooterProps) {
  return (
    <footer className="main-footer" style={{ height }}>
      <div className="footer-content">
        <p>&copy; 2024 SSAFY. All rights reserved.</p>
      </div>
    </footer>
  )
}

export default MainFooter
