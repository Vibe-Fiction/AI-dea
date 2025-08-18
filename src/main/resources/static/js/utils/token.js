// js/utils/token.js

const TOKEN_KEY = 'vibe_fiction_token';

/**
 * localStorage에 JWT 토큰을 저장합니다.
 * @param {string} token - 서버로부터 받은 JWT 토큰
 */
export function saveToken(token) {
    if (token) {
        localStorage.setItem(TOKEN_KEY, token);
    }
}

/**
 * localStorage에서 JWT 토큰을 가져옵니다.
 * @returns {string | null} - 저장된 토큰 또는 null
 */
export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

/**
 * localStorage에서 JWT 토큰을 삭제합니다. (로그아웃 시 사용)
 */
export function removeToken() {
    localStorage.removeItem(TOKEN_KEY);
}
