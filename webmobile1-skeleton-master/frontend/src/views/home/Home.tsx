import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import ConferenceCard from './components/ConferenceCard'

function Home() {
  const navigate = useNavigate()
  const [count, setCount] = useState(12)

  const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
    const { scrollTop, scrollHeight, clientHeight } = e.currentTarget
    if (scrollTop + clientHeight >= scrollHeight - 10) {
      setCount((prev) => prev + 4)
    }
  }

  const handleClickConference = (id: number) => {
    navigate(`/conferences/${id}`)
  }

  return (
    <div className="infinite-list" onScroll={handleScroll}>
      <ul className="infinite-list-content">
        {Array.from({ length: count }, (_, i) => i + 1).map((i) => (
          <li
            key={i}
            className="infinite-list-item"
            onClick={() => handleClickConference(i)}
          >
            <ConferenceCard />
          </li>
        ))}
      </ul>
    </div>
  )
}

export default Home
