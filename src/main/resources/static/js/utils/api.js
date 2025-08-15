// js/utils/api.js

import { getToken } from './token.js';

const BASE_URL = ''; // API 서버와 같은 도메인이므로 비워둡니다.

/**
 * API 요청을 보내는 범용 함수
 * @param {string} endpoint - 요청 엔드포인트 (예: '/api/auth/login')
 * @param {object} options - fetch 옵션 객체 (method, headers, body 등)
 * @returns {Promise<any>} - 서버의 JSON 응답 데이터
 */
async function request(endpoint, options = {}) {
    const url = `${BASE_URL}${endpoint}`;

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    const token = getToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers,
    };

    try {
        const response = await fetch(url, config);
        const data = await response.json();

        if (!response.ok) {
            // 서버가 보낸 에러 응답 전체를 reject하여 catch 블록에서 처리하도록 합니다.
            return Promise.reject(data);
        }

        return data;
    } catch (error) {
        console.error('API 요청 중 네트워크 오류 발생:', error);
        return Promise.reject({ success: false, message: '서버와 통신할 수 없습니다.' });
    }
}

// --- 인증 관련 API 함수들 ---

export const signUp = (userData) => {
    return request('/api/auth/signup', {
        method: 'POST',
        body: JSON.stringify(userData),
    });
};

export const login = (credentials) => {
    return request('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify(credentials),
    });
};

export const checkUsername = (loginId) => {
    return request(`/api/auth/check-username?loginId=${loginId}`);
};

export const checkEmail = (email) => {
    return request(`/api/auth/check-email?email=${email}`);
};

export const checkNickname = (nickname) => {
    return request(`/api/auth/check-nickname?nickname=${nickname}`);
};
