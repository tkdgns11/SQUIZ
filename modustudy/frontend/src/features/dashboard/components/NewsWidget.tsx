import { useState } from 'react';
import './NewsWidget.css';

// 뉴스 아이템 인터페이스 정의
interface NewsItem {
    id: number;
    title: string;
    description: string;
    source: string;
    date: string;
    thumbnail: string;
    tags: string[];
    url: string;
}

// Mock 데이터 (4개)
const mockNews: NewsItem[] = [
    {
        id: 1,
        title: "[새로나왔] 1월 2주차 신상 리스트",
        description: "블랙베리를 닮은 '커뮤니케이터'",
        source: "디에디트",
        date: "2026.01.16",
        thumbnail: "https://images.unsplash.com/photo-1550009158-9ebf69173e03?auto=format&fit=crop&q=80&w=600&h=400",
        tags: ["일반", "스타트업", "마케팅 인사이트"],
        url: "#"
    },
    {
        id: 2,
        title: "2026년 주목해야 할 기술 트렌드 10가지",
        description: "AI에서 양자 컴퓨팅까지, 미래를 바꿀 기술들",
        source: "테크크런치",
        date: "2026.01.15",
        thumbnail: "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?auto=format&fit=crop&q=80&w=600&h=400",
        tags: ["기술", "미래", "AI"],
        url: "#"
    },
    {
        id: 3,
        title: "모던 웹 개발의 새로운 표준: React 19",
        description: "더 똑똑해진 컴파일러와 리액트 서버 컴포넌트",
        source: "코드인사이드",
        date: "2026.01.14",
        thumbnail: "https://images.unsplash.com/photo-1633356122544-f134324a6cee?auto=format&fit=crop&q=80&w=600&h=400",
        tags: ["개발", "웹", "React"],
        url: "#"
    },
    {
        id: 4,
        title: "디자인 시스템을 구축하는 완벽한 가이드",
        description: "효율적인 협업을 위한 피그마와 스토리북 활용법",
        source: "디자인로그",
        date: "2026.01.13",
        thumbnail: "https://images.unsplash.com/photo-1586717791821-3f44a563dc4c?auto=format&fit=crop&q=80&w=600&h=400",
        tags: ["디자인", "가이드", "UI/UX"],
        url: "#"
    }
];

export const NewsWidget = () => {
    // 현재 표시 중인 뉴스 인덱스 상태 관리
    const [currentIndex, setCurrentIndex] = useState(0);

    // 다음 슬라이드로 이동
    const nextSlide = () => {
        setCurrentIndex((prev) => (prev + 1) % mockNews.length);
    };

    // 이전 슬라이드로 이동
    const prevSlide = () => {
        setCurrentIndex((prev) => (prev - 1 + mockNews.length) % mockNews.length);
    };

    return (
        <div className="news-widget">
            {/* 헤더 섹션: 타이틀 및 전체보기 링크 */}
            <div className="widget-header">
                <h3>IT News</h3>
                <a href="#" className="view-more">View All →</a>
            </div>

            {/* 스택 카드 컨테이너 */}
            <div className="news-stack-container">
                {/* 좌측 넘기기 버튼 */}
                <button className="nav-btn-side prev" onClick={prevSlide} aria-label="이전 뉴스">
                    <span className="arrow">&lt;</span>
                </button>

                {/* 뉴스 카드 매핑 */}
                {mockNews.map((news, index) => {
                    // 현재 인덱스를 기준으로 상대적 위치 계산
                    let position = index - currentIndex;
                    if (position < 0) position += mockNews.length;

                    // 애니메이션 로직: 
                    // 0 = 맨 앞 활성 카드
                    // 1, 2 = 뒤에 쌓여있는 카드들
                    // 마지막 데이터 = 방금 왼쪽으로 사라진 카드 (fly-out 효과)
                    const isExiting = position === mockNews.length - 1;
                    const isVisible = position <= 2;

                    // 레이아웃 공간 최적화를 위해 회전 및 이동 값 조정
                    const rotation = position * 3;
                    const translateX = position * 15;
                    const translateY = position * 8;
                    const scale = 1 - position * 0.03;

                    return (
                        <div
                            key={news.id}
                            className={`news-stack-card pos-${position} ${isVisible ? 'active' : ''} ${isExiting ? 'exiting' : ''}`}
                            style={{
                                zIndex: isExiting ? 15 : 10 - position,
                                transform: isExiting
                                    ? `translateX(-150%) rotate(-20deg) scale(0.9)`
                                    : `translateX(${translateX}px) translateY(${translateY}px) rotate(${rotation}deg) scale(${scale})`,
                                opacity: isExiting ? 0 : (position === 0 ? 1 : position === 1 ? 0.85 : 0.4),
                                visibility: isVisible || isExiting ? 'visible' : 'hidden',
                                pointerEvents: position === 0 ? 'auto' : 'none',
                            }}
                        >
                            <a href={news.url} className="news-card-link">
                                {/* 카드 상단이미지 영역 */}
                                <div className="card-image">
                                    <img src={news.thumbnail} alt={news.title} />
                                    {/* 뒤쪽 카드들을 위한 어두운 그라데이션 오버레이 */}
                                    {position > 0 && <div className="stack-gradient-overlay" />}
                                    <div className="card-badge">the edit</div>
                                </div>

                                {/* 카드 텍스트 콘텐츠 영역 */}
                                <div className="card-content">
                                    <div className="card-meta">
                                        <span className="source">{news.source}</span>
                                        <span className="divider">|</span>
                                        <span className="date">{news.date}</span>
                                    </div>
                                    <h4 className="card-title">{news.title}</h4>
                                    <p className="card-desc">{news.description}</p>

                                    {/* 카드 하단 태그 및 액션 버튼 */}
                                    <div className="card-footer">
                                        <div className="card-tags">
                                            {news.tags.map(tag => <span key={tag} className="tag">{tag}</span>)}
                                        </div>
                                        <div className="card-actions">
                                            <button className="action-btn">❤</button>
                                            <button className="action-btn">⋮</button>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </div>
                    );
                })}

                {/* 우측 넘기기 버튼 */}
                <button className="nav-btn-side next" onClick={nextSlide} aria-label="다음 뉴스">
                    <span className="arrow">&gt;</span>
                </button>
            </div>
        </div>
    );
};
