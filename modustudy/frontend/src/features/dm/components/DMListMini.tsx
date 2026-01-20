import React from 'react';
import { MessageSquare } from 'lucide-react';

const DMListMini: React.FC = () => {
    // 임시 데이터
    const dms = [
        { id: 1, name: '부트캠프 그룹', lastMsg: '오늘 스터디 장소 어디인가요?', time: '오후 2:30', unread: true },
        { id: 2, name: '김철수', lastMsg: '네 알겠습니다.', time: '어제', unread: false },
    ];

    return (
        <div className="p-4">
            <div className="flex items-center gap-2 mb-4 text-study-blue">
                <MessageSquare size={20} />
                <h3 className="font-bold text-sm">다이렉트 메시지</h3>
            </div>
            <div className="space-y-1">
                {dms.map(dm => (
                    <div key={dm.id} className="flex flex-col p-2 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer group">
                        <div className="flex justify-between items-start mb-0.5">
                            <span className="text-sm font-bold text-gray-800">{dm.name}</span>
                            <span className="text-[10px] text-gray-400">{dm.time}</span>
                        </div>
                        <div className="flex justify-between items-center">
                            <p className="text-xs text-gray-500 truncate pr-4">{dm.lastMsg}</p>
                            {dm.unread && <div className="w-1.5 h-1.5 bg-red-500 rounded-full flex-shrink-0" />}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default DMListMini;
