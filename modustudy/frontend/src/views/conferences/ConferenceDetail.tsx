import { useParams } from 'react-router-dom'

function ConferenceDetail() {
  const { conferenceId } = useParams()

  return (
    <div className="conference-detail-page">
      <h2>Conference Detail</h2>
      <p>Conference ID: {conferenceId}</p>
    </div>
  )
}

export default ConferenceDetail
