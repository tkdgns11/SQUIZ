import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { StartPage } from '../features/start/StartPage';
import { Dashboard } from '../features/dashboard';
import { TestSidebarPage } from '../features/test/TestSidebarPage';

export const AppRouter = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/startpage" element={<StartPage />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/test-sidebar" element={<TestSidebarPage />} />
                <Route path="/" element={
                    <div style={{ textAlign: "center", marginTop: "50px" }}>
                        <h1>Home</h1>
                        <a href="/startpage" className="text-study-blue hover:underline">Go to Start Page</a>
                        <br />
                        <a href="/dashboard" className="text-study-blue hover:underline">Go to Dashboard</a>
                        <br />
                        <a href="/test-sidebar" className="text-study-blue hover:underline">Test Sidebar</a>
                    </div>
                } />
            </Routes>
        </BrowserRouter>
    );
};
