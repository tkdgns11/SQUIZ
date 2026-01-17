/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'study-bg': '#FFF7DD',    // 배경 & 사이드바
        'study-blue': '#80A1BA',  // 메인 포인트 (신뢰)
        'study-teal': '#91C4C3',  // 보조 포인트 (퀴즈)
        'study-green': '#B4DEBD', // 액센트 (스터디/완료)
        'study-text': '#454A4F',  // 기본 텍스트
      },
      borderRadius: {
        'google': '12px',         // 구글스러운 라운드 값
      },
    },
  },
  plugins: [],
}

