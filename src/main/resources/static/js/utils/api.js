/**
 * @file Vibe Fiction 백엔드 API와의 모든 통신을 담당하는 공용 모듈입니다.
 * @description 이 모듈은 fetch API를 기반으로 하며, 인증 토큰 관리, 공통 헤더 생성,
 *              표준화된 응답 처리 등 API 호출에 필요한 모든 기능을 캡슐화합니다.
 *              다른 JavaScript 파일에서는 이 모듈의 함수들을 import하여 사용합니다.
 * @author 왕택준
 * @since 2025.08.18
 */

const BASE_URL = 'http://localhost:9009';

/**
 * 로컬 스토리지에서 현재 로그인된 사용자의 JWT 토큰을 가져옵니다.
 * @returns {string|null} 저장된 accessToken 문자열 또는 토큰이 없을 경우 null.
 */
function getToken() {
    return localStorage.getItem('accessToken');
}

/**
 * 인증이 필요한 API 요청을 위해 'Authorization' 헤더가 포함된
 * 공통 HTTP 헤더 객체를 생성합니다.
 * @returns {Headers} 'Content-Type'과 'Authorization'이 설정된 Headers 객체.
 * @throws {Error} 로컬 스토리지에 토큰이 없을 경우 에러를 발생시킵니다.
 */
function getAuthHeaders() {
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');

    const token = getToken();
    if (!token) {
        alert('오류: 로그인이 필요한 기능입니다.');
        throw new Error('인증 토큰이 없습니다. API 호출을 중단합니다.');
    }
    headers.append('Authorization', `Bearer ${token}`);
    return headers;
}

/**
 * fetch API의 응답(Response) 객체를 처리하는 공통 헬퍼 함수입니다.
 * @param {Response} response - fetch API로부터 받은 원본 응답 객체.
 * @returns {Promise<any>} 성공 시, ApiResponse의 'data' 객체.
 * @throws {Error} API 호출 실패 시, 서버가 보낸 에러 메시지를 포함한 에러.
 */
async function handleResponse(response) {
    const responseJson = await response.json();
    if (!response.ok) {
        const errorMessage = responseJson.detail || responseJson.message || `HTTP 에러! 상태 코드: ${response.status}`;
        throw new Error(errorMessage);
    }
    return responseJson.data;
}

/**
 * AI에게 새로운 소설 초안을 추천받습니다.
 * @param {string} genre - 사용자가 입력한 장르.
 * @param {string} synopsis - 사용자가 입력한 시놉시스.
 * @returns {Promise<Object>} AI가 생성한 추천 데이터 (novelTitle, firstChapterContent 등).
 */
export async function recommendNovelApi(genre, synopsis) {
    const response = await fetch(`${BASE_URL}/api/ai/novels/recommend`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ genre, synopsis })
    });
    return handleResponse(response);
}

/**
 * 새로운 소설을 생성합니다.
 * @param {Object} novelData - 생성할 소설 데이터.
 * @returns {Promise<Object>} 생성된 소설 정보 (novelId, firstChapterId).
 */
export async function createNovelApi(novelData) {
    const response = await fetch(`${BASE_URL}/api/novels`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(novelData)
    });
    return handleResponse(response);
}

/**
 * AI에게 이어쓰기 초안을 추천받습니다.
 * @param {number|string} chapterId - 이어쓰기의 기준이 될 회차 ID.
 * @param {string} instruction - AI에게 전달할 구체적인 지시사항.
 * @returns {Promise<Object>} AI가 생성한 추천 데이터 (suggestedTitle, suggestedContent).
 */
export async function continueChapterApi(chapterId, instruction) {
    const response = await fetch(`${BASE_URL}/api/ai/chapters/${chapterId}/continue`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ instruction })
    });
    return handleResponse(response);
}

/**
 * 새로운 이어쓰기 제안을 생성합니다.
 * @param {number|string} chapterId - 제안을 등록할 대상 회차 ID.
 * @param {Object} proposalData - 생성할 제안 데이터.
 * @returns {Promise<Object>} 생성된 제안 정보 (proposalId).
 */
export async function createProposalApi(chapterId, proposalData) {
    const response = await fetch(`${BASE_URL}/api/chapters/${chapterId}/proposals`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(proposalData)
    });
    return handleResponse(response);
}
