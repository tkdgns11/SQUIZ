import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Info, Settings } from 'lucide-react';
import StudyListContainer from './StudyListContainer';
import { MainLayout } from '@/layouts/MainLayout';
import './styles/StudyCreatePage.css';

const StudyCreatePage: React.FC = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        topic: '알고리즘',
        difficulty: 'BEGINNER',
        meetingType: 'ONLINE',
        maxMembers: 4,
        scheduleDays: [],
        studyType: 'PLANNED'
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleOptionToggle = (name: string, value: string) => {
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        console.log('Form submitted:', formData);
        // TODO: API 연결
        alert('스터디가 생성되었습니다! (모의)');
        navigate('/study');
    };

    return (
        <MainLayout>
            <StudyListContainer>
                <div className="study-create-container">
                    <header className="study-create-header">
                        <button className="btn-back" onClick={() => navigate(-1)}>
                            <ChevronLeft size={20} />
                            <span>뒤로가기</span>
                        </button>
                        <h1>새로운 스터디 시작하기</h1>
                        <p>함께 성장할 팀원을 모집해보세요.</p>
                    </header>

                    <form className="study-create-card" onSubmit={handleSubmit}>
                        {/* 기본 정보 스택 */}
                        <div className="create-form-section">
                            <h2 className="section-title">
                                <Info size={20} />
                                기본 정보
                            </h2>
                            <div className="form-grid">
                                <div className="form-group full-width">
                                    <label htmlFor="name">스터디 이름</label>
                                    <input
                                        type="text"
                                        id="name"
                                        name="name"
                                        className="form-input"
                                        placeholder="모두가 이해하기 쉬운 이름을 지어주세요"
                                        required
                                        value={formData.name}
                                        onChange={handleChange}
                                    />
                                </div>
                                <div className="form-group full-width">
                                    <label htmlFor="description">스터디 설명</label>
                                    <textarea
                                        id="description"
                                        name="description"
                                        className="form-textarea"
                                        placeholder="스터디의 목표와 진행 방식에 대해 자세히 적어주세요"
                                        required
                                        value={formData.description}
                                        onChange={handleChange}
                                    />
                                </div>
                            </div>
                        </div>

                        {/* 상세 설정 섹션 */}
                        <div className="create-form-section">
                            <h2 className="section-title">
                                <Settings size={20} />
                                상세 설정
                            </h2>
                            <div className="form-grid">
                                <div className="form-group">
                                    <label>주제</label>
                                    <select
                                        name="topic"
                                        className="form-select"
                                        value={formData.topic}
                                        onChange={handleChange}
                                    >
                                        <option value="알고리즘">알고리즘</option>
                                        <option value="프론트엔드">프론트엔드</option>
                                        <option value="백엔드">백엔드</option>
                                        <option value="데이터베이스">데이터베이스</option>
                                        <option value="CS">CS 지식</option>
                                        <option value="안드로이드">안드로이드</option>
                                        <option value="iOS">iOS</option>
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label>권장 난이도</label>
                                    <div className="option-group">
                                        <button
                                            type="button"
                                            className={`option-button ${formData.difficulty === 'BEGINNER' ? 'active' : ''}`}
                                            onClick={() => handleOptionToggle('difficulty', 'BEGINNER')}
                                        >
                                            입문
                                        </button>
                                        <button
                                            type="button"
                                            className={`option-button ${formData.difficulty === 'INTERMEDIATE' ? 'active' : ''}`}
                                            onClick={() => handleOptionToggle('difficulty', 'INTERMEDIATE')}
                                        >
                                            중급
                                        </button>
                                        <button
                                            type="button"
                                            className={`option-button ${formData.difficulty === 'ADVANCED' ? 'active' : ''}`}
                                            onClick={() => handleOptionToggle('difficulty', 'ADVANCED')}
                                        >
                                            고급
                                        </button>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label>진행 방식</label>
                                    <div className="option-group">
                                        <button
                                            type="button"
                                            className={`option-button ${formData.meetingType === 'ONLINE' ? 'active' : ''}`}
                                            onClick={() => handleOptionToggle('meetingType', 'ONLINE')}
                                        >
                                            온라인
                                        </button>
                                        <button
                                            type="button"
                                            className={`option-button ${formData.meetingType === 'OFFLINE' ? 'active' : ''}`}
                                            onClick={() => handleOptionToggle('meetingType', 'OFFLINE')}
                                        >
                                            오프라인
                                        </button>
                                        <button
                                            type="button"
                                            className={`option-button ${formData.meetingType === 'HYBRID' ? 'active' : ''}`}
                                            onClick={() => handleOptionToggle('meetingType', 'HYBRID')}
                                        >
                                            혼합
                                        </button>
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label>최대 인원</label>
                                    <input
                                        type="number"
                                        name="maxMembers"
                                        min="2"
                                        max="50"
                                        className="form-input"
                                        value={formData.maxMembers}
                                        onChange={handleChange}
                                    />
                                </div>
                            </div>
                        </div>

                        {/* 버튼 영역 */}
                        <div className="form-actions">
                            <button type="button" className="btn-cancel" onClick={() => navigate(-1)}>
                                취소
                            </button>
                            <button type="submit" className="btn-submit">
                                개설하기
                            </button>
                        </div>
                    </form>
                </div>
            </StudyListContainer>
        </MainLayout>
    );
};

export default StudyCreatePage;
