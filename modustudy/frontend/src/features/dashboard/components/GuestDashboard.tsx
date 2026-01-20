import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { FeedsSection, CommentleHero } from './';
import { mockStudies } from '../../study/mockData';
import { Users, Star, MessageSquare, Video } from 'lucide-react';
import '../styles/GuestDashboard.css';

const BANNER_DATA = [
    {
        id: 1,
        title: "나에게 꼭 맞는 스터디 그룹 찾기",
        description: "IT, 언어, 취업 등 다양한 카테고리의 스터디원들과 소통하고 함께 성장하세요.",
        color: "var(--color-primary)",
        bgGradient: "linear-gradient(135deg, #f8faff 0%, #eef2ff 100%)",
        btnText: "스터디 탐색하기",
        path: "/study",
        type: 'STUDY_LIST'
    },
    {
        id: 2,
        title: "오늘의 퀴즈 챔피언은 누구?",
        description: "매일 열리는 실시간 CS 퀴즈 대결에 참여하고 나의 지식을 실전에서 겨뤄보세요.",
        color: "#9333ea",
        bgGradient: "linear-gradient(135deg, #f3e8ff 0%, #e0e7ff 100%)",
        btnText: "퀴즈 대회 보기",
        path: "/commentle",
        type: 'QUIZ'
    },
    {
        id: 3,
        title: "어디서든 함께하는 라이브 미팅",
        description: "화면 공유와 음성 채팅이 결합된 강력한 실시간 학습 환경을 지금 바로 경험하세요.",
        color: "#0ea5e9",
        bgGradient: "linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%)",
        btnText: "미팅룸 만들기",
        path: "/login",
        type: 'MEETING'
    }
];

const MiniStudyCard = ({ study }: { study: typeof mockStudies[0] }) => (
    <div className="mini-study-card">
        <div className="mini-card-header">
            <span className="mini-badge">{study.topic}</span>
            <span className="mini-status">{study.status === 'RECRUITING' ? '모집중' : '진행중'}</span>
        </div>
        <h4 className="mini-card-title">{study.name}</h4>
        <div className="mini-card-footer">
            <div className="mini-info">
                <Users size={12} />
                <span>{study.currentMembers}/{study.maxMembers}</span>
            </div>
            <div className="mini-leader">
                <Star size={12} fill="currentColor" />
                <span>{study.leader.leaderRating.toFixed(1)}</span>
            </div>
        </div>
    </div>
);

const GuestDashboard: React.FC = () => {
    const navigate = useNavigate();
    const [currentIndex, setCurrentIndex] = useState(0);
    const [direction, setDirection] = useState(0);

    const nextSlide = () => {
        setDirection(1);
        setCurrentIndex((prev) => (prev + 1) % BANNER_DATA.length);
    };

    const prevSlide = () => {
        setDirection(-1);
        setCurrentIndex((prev) => (prev - 1 + BANNER_DATA.length) % BANNER_DATA.length);
    };

    useEffect(() => {
        const timer = setInterval(nextSlide, 6000);
        return () => clearInterval(timer);
    }, [currentIndex]);

    const variants = {
        enter: (direction: number) => ({
            x: direction > 0 ? 500 : -500,
            opacity: 0
        }),
        center: {
            zIndex: 1,
            x: 0,
            opacity: 1
        },
        exit: (direction: number) => ({
            zIndex: 0,
            x: direction < 0 ? 500 : -500,
            opacity: 0
        })
    };

    const currentBanner = BANNER_DATA[currentIndex];

    const renderVisual = () => {
        switch (currentBanner.type) {
            case 'STUDY_LIST':
                return (
                    <div className="visual-study-list">
                        {mockStudies.slice(0, 3).map((study, idx) => (
                            <motion.div
                                key={study.id}
                                initial={{ opacity: 0, y: 20 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: 0.3 + idx * 0.1 }}
                                className={`mini-card-wrapper pos-${idx}`}
                            >
                                <MiniStudyCard study={study} />
                            </motion.div>
                        ))}
                    </div>
                );
            case 'QUIZ':
                return (
                    <div className="visual-quiz">
                        <motion.div
                            className="quiz-preview-card"
                            animate={{ rotate: [0, -2, 2, 0] }}
                            transition={{ repeat: Infinity, duration: 4 }}
                        >
                            <div className="quiz-card-header">
                                <MessageSquare size={24} />
                                <span>오늘의 CS 퀴즈</span>
                            </div>
                            <div className="quiz-card-body">
                                <div className="quiz-question">"REST API의 무상태성(Stateless)이란?"</div>
                                <div className="quiz-options">
                                    <div className="option">A. 서버에 상태 정보 저장 안함</div>
                                    <div className="option">B. 클라이언트와 연결 유지 안함</div>
                                </div>
                            </div>
                        </motion.div>
                    </div>
                );
            case 'MEETING':
                return (
                    <div className="visual-meeting">
                        <div className="meeting-orbit">
                            {[...Array(5)].map((_, i) => (
                                <motion.div
                                    key={i}
                                    className={`meeting-avatar avatar-${i}`}
                                    animate={{
                                        scale: [1, 1.1, 1],
                                        opacity: [0.7, 1, 0.7]
                                    }}
                                    transition={{
                                        repeat: Infinity,
                                        duration: 3,
                                        delay: i * 0.5
                                    }}
                                >
                                    <Users size={20} />
                                </motion.div>
                            ))}
                            <motion.div
                                className="meeting-center"
                                animate={{ scale: [0.95, 1.05, 0.95] }}
                                transition={{ repeat: Infinity, duration: 2 }}
                            >
                                <Video size={40} color="white" />
                            </motion.div>
                        </div>
                    </div>
                );
            default:
                return null;
        }
    };

    return (
        <div className="guest-dashboard">
            <section className="guest-hero-container">
                <AnimatePresence initial={false} custom={direction} mode="wait">
                    <motion.div
                        key={currentIndex}
                        custom={direction}
                        variants={variants}
                        initial="enter"
                        animate="center"
                        exit="exit"
                        transition={{
                            x: { type: "tween", ease: "easeOut", duration: 0.5 },
                            opacity: { duration: 0.3 }
                        }}
                        className="guest-hero"
                        style={{ background: currentBanner.bgGradient }}
                    >
                        <div className="hero-content">
                            <motion.div
                                initial={{ opacity: 0, y: 15 }}
                                animate={{ opacity: 1, y: 0 }}
                                transition={{ delay: 0.2 }}
                            >
                                <h1 className="hero-title">
                                    {currentBanner.title}
                                </h1>
                                <p className="hero-description">
                                    {currentBanner.description}
                                </p>
                                <div className="hero-actions">
                                    <button
                                        className="btn-hero-primary"
                                        style={{ backgroundColor: currentBanner.color }}
                                        onClick={() => navigate(currentBanner.path)}
                                    >
                                        {currentBanner.btnText}
                                    </button>
                                    {currentIndex === 0 && (
                                        <button
                                            className="btn-hero-secondary"
                                            onClick={() => navigate('/login')}
                                        >
                                            무료로 시작하기
                                        </button>
                                    )}
                                </div>
                            </motion.div>
                        </div>

                        <div className="hero-visual">
                            {renderVisual()}
                        </div>
                    </motion.div>
                </AnimatePresence>

                <button className="carousel-nav-btn prev" onClick={prevSlide} aria-label="Previous slide">
                    <span className="material-icons">chevron_left</span>
                </button>
                <button className="carousel-nav-btn next" onClick={nextSlide} aria-label="Next slide">
                    <span className="material-icons">chevron_right</span>
                </button>

                <div className="carousel-indicators">
                    {BANNER_DATA.map((_, index) => (
                        <button
                            key={index}
                            className={`dot ${index === currentIndex ? 'active' : ''}`}
                            onClick={() => {
                                setDirection(index > currentIndex ? 1 : -1);
                                setCurrentIndex(index);
                            }}
                            aria-label={`Go to slide ${index + 1}`}
                        />
                    ))}
                </div>
            </section>

            <CommentleHero />

            <div className="guest-content-grid">
                <div className="guest-main-col">
                    <section className="guest-section">
                        <div className="section-header">
                            <h2>실시간 스터디 피드</h2>
                            <button className="btn-text" onClick={() => navigate('/study')}>전체 보기</button>
                        </div>
                        <FeedsSection />
                    </section>
                </div>
            </div>
        </div>
    );
};

export default GuestDashboard;
