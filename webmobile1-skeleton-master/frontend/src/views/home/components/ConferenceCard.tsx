import placeholderImage from '@/assets/images/placeholder.png'

function ConferenceCard() {
  return (
    <div className="conference-card">
      <div className="conference-thumbnail">
        <img src={placeholderImage} alt="Conference thumbnail" />
      </div>
      <div className="conference-info">
        <h4 className="conference-title">Conference Title</h4>
        <p className="conference-description">Conference description goes here...</p>
      </div>
    </div>
  )
}

export default ConferenceCard
