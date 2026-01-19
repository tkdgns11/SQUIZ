import React from 'react';
import { MainLayout } from '@/layouts/MainLayout';

export const StudyPage: React.FC = () => {
    return (
        <MainLayout>
            <div className="space-y-6">
                <div className="flex flex-col gap-2">
                    <h1 className="text-3xl font-bold text-study-text">스터디 관리</h1>
                    <p className="text-study-text/60">참여 중인 스터디와 스터디 일정을 확인하세요.</p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {/* Placeholder content */}
                    <div className="bg-white p-6 rounded-google shadow-sm border border-study-blue/10 h-40 flex items-center justify-center text-study-text/40">
                        준비 중인 서비스입니다.
                    </div>
                </div>
            </div>
        </MainLayout>
    );
};
