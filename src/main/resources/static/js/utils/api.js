/**
 * @file Vibe Fiction 백엔드 API와의 모든 통신을 담당하는 공용 모듈입니다.
 * @description 모든 API 호출을 request() 함수로 추상화하여 코드 중복을 최소화하고,
 *              인증 토큰 및 에러 처리를 중앙에서 관리합니다.
 * @author 고동현 (request 함수 및 인증 API 원안), 왕택준 (기능 API 추가)
 * @since 2025.08
 */

// token.js 모듈에서 getToken 함수를 가져옵니다.
import { getToken } from './token.js';

const BASE_URL = ''; // API 서버와 같은 도메인이므로 비워둡니다.

/**
 * fetch API의 Response 객체를 안전하게 JSON으로 파싱하는 헬퍼 함수입니다.
 * <p>
 * 이 함수는 다음과 같은 예외 상황들을 방어적으로 처리하여, `response.json()` 호출 시
 * 발생할 수 있는 런타임 에러를 방지합니다.
 * <ul>
 *   <li>응답 본문(body)이 아예 없는 경우 (e.g., HTTP 204 No Content)</li>
 *   <li>응답의 Content-Type 헤더가 'application/json'이 아닌 경우</li>
 *   <li>Content-Type은 JSON이지만, 실제 본문 내용이 깨진 JSON이라 파싱에 실패하는 경우</li>
 * </ul>
 *
 * @param {Response} response - fetch API로부터 받은 원본 응답(Response) 객체.
 * @returns {Promise<Object|null>} 성공적으로 파싱된 JSON 객체. 본문이 없으면 null.
 *          JSON이 아니거나 파싱에 실패하면, 디버깅을 위해 원본 텍스트를 담은 객체를 반환합니다.
 *          (e.g., { error: 'Invalid JSON', raw: '...' })
 */
async function safeJson(response) {
    if (response.status === 204 || response.status === 205) {
        return null;
    }

    const contentType = response.headers.get('content-type') || '';
    const responseText = await response.text();

    if (!responseText) {
        return null;
    }

    if (contentType.includes('application/json')) {
        try {
            return JSON.parse(responseText);
        } catch (error) {
            console.error("JSON 파싱 실패:", error);
            return { error: 'Invalid JSON', raw: responseText };
        }
    }

    return { error: 'Not a JSON response', raw: responseText };
}


/**
 * 프로젝트의 모든 백엔드 API 요청을 처리하는 범용 래퍼(Wrapper) 함수입니다.
 * <p>
 * 이 함수는 fetch API를 기반으로 하며, 다음과 같은 공통 로직을 추상화하여 제공합니다.
 * <ul>
 *   <li>BASE_URL 자동 적용</li>
 *   <li>'Content-Type' 헤더 기본 설정</li>
 *   <li>로컬 스토리지의 JWT 토큰을 'Authorization' 헤더에 자동으로 추가</li>
 *   <li>{@link safeJson}을 통한 안정적인 응답 파싱</li>
 *   <li>성공/실패 응답에 대한 일관된 처리 및 에러 전파</li>
 * </ul>
 *
 * @param {string} endpoint - 요청할 API의 엔드포인트 (예: '/api/genres').
 * @param {object} [options={}] - fetch API에 전달할 옵션 객체 (method, body 등).
 * @returns {Promise<any>} 성공 시, 서버가 보낸 ApiResponse 형식의 JSON 객체 전체를 반환합니다.
 * @throws {Promise<Object>} 실패 시, 에러 정보를 담은 객체를 reject합니다.
 */
async function request(endpoint, options = {}) {
    const url = `${BASE_URL}${endpoint}`;
    const headers = { 'Content-Type': 'application/json', ...options.headers, };
    const token = getToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    const config = { ...options, headers };

    try {
        const response = await fetch(url, config);
        const data = await safeJson(response);

        if (!response.ok) {
            const errorMessage = data?.message || data?.detail || `HTTP 에러! 상태 코드: ${response.status}`;
            return Promise.reject({ success: false, message: errorMessage, status: response.status, raw: data?.raw });
        }
        return data;
    } catch (error) {
        console.error('API 요청 중 네트워크 오류 발생:', error);
        return Promise.reject({ success: false, message: '서버와 통신할 수 없습니다.' });
    }
}

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

/**
 * 장르 목록을 조회합니다. (인증 불필요)
 * @author 왕택준
 * @returns {Promise<Object>} 장르 목록 데이터 (ApiResponse의 data 부분).
 */
export const getGenres = () => {
    // request 함수는 토큰이 없으면 그냥 안 보내므로, 그대로 사용해도 됩니다.
    // 성공 시 ApiResponse({ success, message, data })를 반환합니다.
    return request('/api/genres');
};


/**
 * AI에게 새로운 소설 초안을 추천받습니다. (인증 필요)
 * @author 왕택준
 * @param {string} genre - 사용자가 입력한 장르.
 * @param {string} synopsis - 사용자가 입력한 시놉시스.
 * @returns {Promise<Object>} AI가 생성한 추천 데이터 (ApiResponse의 data 부분).
 */
export const recommendNovelApi = (genre, synopsis) => {
    return request('/api/ai/novels/recommend', {
        method: 'POST',
        body: JSON.stringify({ genre, synopsis }),
    }).then(response => response.data);
};

/**
 * 새로운 소설을 생성합니다. (인증 필요)
 * @author 왕택준
 * @param {Object} novelData - 생성할 소설 데이터.
 * @returns {Promise<Object>} 생성된 소설 정보 (ApiResponse의 data 부분).
 */
export const createNovelApi = (novelData) => {
    return request('/api/novels', {
        method: 'POST',
        body: JSON.stringify(novelData),
    }).then(response => response.data);
};

/**
 * AI에게 이어쓰기 초안을 추천받습니다. (인증 필요)
 * @author 왕택준
 * @param {number|string} chapterId - 이어쓰기의 기준이 될 회차 ID.
 * @param {string} instruction - AI에게 전달할 구체적인 지시사항.
 * @returns {Promise<Object>} AI가 생성한 추천 데이터 (ApiResponse의 data 부분).
 */
export const continueChapterApi = (chapterId, instruction) => {
    return request(`/api/ai/chapters/${chapterId}/continue`, {
        method: 'POST',
        body: JSON.stringify({ instruction }),
    }).then(response => response.data);
};

/**
 * 새로운 이어쓰기 제안을 생성합니다. (인증 필요)
 * @author 왕택준
 * @param {number|string} chapterId - 제안을 등록할 대상 회차 ID.
 * @param {Object} proposalData - 생성할 제안 데이터.
 * @returns {Promise<Object>} 생성된 제안 정보 (ApiResponse의 data 부분).
 */
export const createProposalApi = (chapterId, proposalData) => {
    return request(`/api/chapters/${chapterId}/proposals`, {
        method: 'POST',
        body: JSON.stringify(proposalData),
    }).then(response => response.data);
};

