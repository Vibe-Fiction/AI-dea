// 인증 관련 모듈
const TOKEN = 'token';
const USER = 'user';

export const authService = {

    // 인증 상태를 체크하는 함수
    checkAuthStatus() {
        // 토큰을 가져와봄
        const token = localStorage.getItem('token');
        // 로그인한 유저정보 가져오기
        const user = JSON.parse(localStorage.getItem('user') || '{}');

        if (token && user) { // 로그인 한것
            return { isAuthenticated: true, user };
        } else {
            return { isAuthenticated: false, user: null };
        }
    },

    // 헤더의 UI를 업데이트하는 함수
    updateHeaderUI() {
        const { isAuthenticated, user } = this.checkAuthStatus();

        const $authButtons = document.querySelector('.auth-buttons');   // 클래스 선택자 사용
        const $userButtons = document.querySelector('.user-buttons');   // 클래스 선택자 사용

        if (isAuthenticated) { // 로그인된 경우
            if ($authButtons) $authButtons.style.display = 'none';      // 로그인 버튼 숨김
            if ($userButtons) $userButtons.style.display = 'block';     // 사용자 메뉴 표시
        } else { // 로그인 안된 경우
            if ($authButtons) $authButtons.style.display = 'block';     // 로그인 버튼 표시
            if ($userButtons) $userButtons.style.display = 'none';      // 사용자 메뉴 숨김
        }
    },

    login(token, user) {
        localStorage.setItem(TOKEN, token);
        localStorage.setItem(USER, user);
    },

    // 로그아웃 처리
    logout() {
        localStorage.removeItem(TOKEN);
        localStorage.removeItem(USER);
        window.location.href = '/';
    }
    ,
    // 단순히 로그인 했는지만 확인하는 함수
    isAuthenticated() {
        return this.checkAuthStatus().isAuthenticated;
    },

    // 토큰 가져오기
    getToken() {
        return localStorage.getItem(TOKEN);
    },



};
