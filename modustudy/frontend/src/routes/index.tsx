import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { StartPage } from '../features/start/StartPage';
import { Dashboard } from '../features/dashboard';

export const AppRouter = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/startpage" element={<StartPage />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/" element={
                    <div style={{ textAlign: "center", marginTop: "50px" }}>
                        <h1>Home</h1>
                        <a href="/startpage">Go to Start Page</a>
                    </div>
                } />
            </Routes>
        </BrowserRouter>
    );
};
