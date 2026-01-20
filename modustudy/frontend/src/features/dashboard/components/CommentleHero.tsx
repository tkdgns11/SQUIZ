import React from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import '../styles/CommentleHero.css';

const CommentleHero: React.FC = () => {
    const navigate = useNavigate();

    return (
        <section className="commentle-hero">
            <motion.div
                className="hero-content"
                initial={{ opacity: 0, x: -20 }}
                whileInView={{ opacity: 1, x: 0 }}
                viewport={{ once: true }}
                transition={{ duration: 0.6 }}
            >
                <div className="badge-new">NEW</div>
                <h2 className="hero-title">
                    오늘의 CS 지식,<br />
                    <span className="text-highlight-accent">꼬멘틀 CS ver</span>
                </h2>
                <p className="hero-description">
                    매일 하나씩 배달되는 컴퓨터 사이언스 퀴즈!<br />
                    친구들과 함께 용어를 맞추고 지식을 공유해보세요.
                </p>
                <div className="hero-actions">
                </div>
            </motion.div>

            <motion.div
                className="hero-animation-side"
                initial={{ opacity: 0, scale: 0.8 }}
                whileInView={{ opacity: 1, scale: 1 }}
                viewport={{ once: true }}
                transition={{ duration: 0.8 }}
            >
                <div className="quiz-grid-preview">
                    {[...Array(9)].map((_, i) => (
                        <div key={i} className={`grid-box box-${(i % 3) + 1} delay-${i}`}>
                            <span className="material-icons">
                                {i === 4 ? 'question_mark' : 'code'}
                            </span>
                        </div>
                    ))}
                </div>
                <div className="floating-term term-1">Binary Tree</div>
                <div className="floating-term term-2">Recursion</div>
                <div className="floating-term term-3">Deadlock</div>
            </motion.div>

            <button
                className="btn-hero-corner"
                onClick={() => navigate('/commentle')}
            >
                퀴즈 도전하기
            </button>
        </section>
    );
};

export default CommentleHero;
