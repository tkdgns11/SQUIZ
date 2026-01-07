import { Routes, Route } from 'react-router-dom'
import MainLayout from './views/main/MainLayout'
import Home from './views/home/Home'
import History from './views/history/History'
import ConferenceDetail from './views/conferences/ConferenceDetail'

function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<Home />} />
        <Route path="history" element={<History />} />
        <Route path="conferences/:conferenceId" element={<ConferenceDetail />} />
      </Route>
    </Routes>
  )
}

export default App
