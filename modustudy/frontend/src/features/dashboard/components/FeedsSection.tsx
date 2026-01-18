import { NewsWidget } from './NewsWidget';
import { TrendingWidget } from './TrendingWidget';
import './FeedsSection.css';

export const FeedsSection = () => {
    return (
        <div className="feeds-section">
            <div className="feeds-container">
                <div className="feeds-left">
                    <NewsWidget />
                </div>
                <div className="feeds-right">
                    <TrendingWidget />
                </div>
            </div>
        </div>
    );
};
