import React from 'react';
import { Users } from 'lucide-react';

const FriendListMini: React.FC = () => {
    // 임시 데이터
    const friends = [
        { id: 1, name: '김철수', status: 'online' },
        { id: 2, name: '이영희', status: 'away' },
        { id: 3, name: '박지성', status: 'offline' },
    ];

    return (
        <div className="p-4">
            <div className="flex items-center gap-2 mb-4 text-study-blue">
                <Users size={20} />
                <h3 className="font-bold text-sm">친구 목록</h3>
            </div>
            <div className="space-y-3">
                {friends.map(friend => (
                    <div key={friend.id} className="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer">
                        <div className="relative">
                            <div className="w-8 h-8 rounded-full bg-study-blue/10 flex items-center justify-center font-bold text-xs text-study-blue">
                                {friend.name.charAt(0)}
                            </div>
                            <div className={`absolute bottom-0 right-0 w-2.5 h-2.5 rounded-full border-2 border-white ${friend.status === 'online' ? 'bg-green-500' :
                                    friend.status === 'away' ? 'bg-yellow-500' : 'bg-gray-400'
                                }`} />
                        </div>
                        <span className="text-sm font-medium text-gray-700">{friend.name}</span>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default FriendListMini;
