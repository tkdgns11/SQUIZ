import { Link } from 'react-router-dom';
import './TrendingWidget.css';

// 인기 게시글 아이템 인터페이스 정의
interface TrendingPost {
    id: number;
    rank: number;
    title: string;
    author: string;
    category: '스터디' | '프로젝트' | '자유' | 'Q&A';
    viewCount: number;
    timeAgo: string;
}

// 스터디/취준생 커뮤니티 인기글 데이터
const mockTrendingPosts: TrendingPost[] = [
    {
        id: 1, rank: 1, title: "[서울] 리액트 네이티브 6개월 프로젝트 팀원 구합니다!",
        author: "코딩하는토끼", category: "프로젝트", viewCount: 1240, timeAgo: "30분 전"
    },
    {
        id: 2, rank: 2, title: "알고리즘 코테 스터디 (백준 골드 이상) 모집합니다",
        author: "알골조아", category: "스터디", viewCount: 856, timeAgo: "1시간 전"
    },
    {
        id: 3, rank: 3, title: "비전공자 개발자 취업 성공 후기 및 로드맵 공유",
        author: "열정기획자", category: "자유", viewCount: 2432, timeAgo: "2시간 전"
    },
    {
        id: 4, rank: 4, title: "Next.js 14 App Router 서버 액션 질문있습니다!",
        author: "주니어개발자", category: "Q&A", viewCount: 420, timeAgo: "3시간 전"
    },
    {
        id: 5, rank: 5, title: "현직 선배님들, 포커스 온 면접 팁 부탁드려요",
        author: "취준생101", category: "자유", viewCount: 515, timeAgo: "5시간 전"
    },
    {
        id: 6, rank: 6, title: "CS 기초 다지기 스터디 상시 모집 중",
        author: "정보처리기사", category: "스터디", viewCount: 380, timeAgo: "8시간 전"
    },
    {
        id: 7, rank: 7, title: "파이썬 데이터 분석 사이드 프로젝트 하실 분!",
        author: "데이터덕후", category: "프로젝트", viewCount: 290, timeAgo: "12시간 전"
    }
];

export const TrendingWidget = () => {
    return (
        <div className="trending-widget">
            <div className="widget-header">
                <h3>Trending Topics</h3>
                <Link to="/recruitment" className="view-more">View All →</Link>
            </div>

            <div className="trending-posts-container">
                <div className="posts-minimal-list">
                    {mockTrendingPosts.map(post => (
                        <a
                            key={post.id}
                            href="#"
                            className={`post-card-item rank-${post.rank} ${post.rank <= 3 ? 'is-top-rank' : ''}`}
                        >
                            <div className="post-card-content">
                                <div className="post-rank-wrapper">
                                    <div className="post-rank">{post.rank}</div>

                                    <div className="post-main-info">
                                        <div className="post-meta">
                                            <span className={`post-category cat-${post.category === '스터디' ? 'study' : post.category === '프로젝트' ? 'proj' : 'etc'}`}>
                                                {post.category}
                                            </span>
                                            <span className="post-author">{post.author}</span>
                                        </div>

                                        <h4 className="post-title">{post.title}</h4>
                                    </div>

                                    <div className="post-stats-minimal">
                                        <span className="view-count">
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="view-icon">
                                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                                <circle cx="12" cy="12" r="3"></circle>
                                            </svg>
                                            {post.viewCount}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </a>
                    ))}
                </div>
            </div>
        </div>
    );
};
