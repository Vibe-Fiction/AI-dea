// js/auth.js

import * as api from './utils/api.js';
import { saveToken, removeToken } from './utils/token.js';
import { updateHeaderUI, closeLoginModal } from './ui.js';

// --- 유효성 검사 정규식 (백엔드와 일치) ---
const REGEX = {
    LOGIN_ID: /^[a-zA-Z0-9_]{3,15}$/,
    PASSWORD: /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/,
    NICKNAME: /^[a-zA-Z0-9가-힣]{2,10}$/,
};

// --- 헬퍼 함수 ---

/**
 * API 에러 응답에서 사용자에게 보여줄 메시지를 추출합니다.
 */
function getErrorMessage(errorResponse) {
    if (errorResponse?.validationErrors?.length > 0) {
        return errorResponse.validationErrors[0].message;
    }
    if (errorResponse?.detail) {
        return errorResponse.detail;
    }
    if (errorResponse?.message) {
        return errorResponse.message;
    }
    return '알 수 없는 오류가 발생했습니다.';
}

/**
 * 연속적인 이벤트에 대한 API 호출을 최적화하는 디바운스 함수입니다.
 */
function debounce(callback, delay = 500) {
    let timeoutId;
    return (...args) => {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => {
            callback.apply(null, args);
        }, delay);
    };
}

/**
 * 피드백 메시지를 UI에 표시합니다.
 */
function showFeedback(element, message, isSuccess) {
    element.textContent = message;
    element.className = 'feedback-message';
    if (isSuccess) {
        element.classList.add('success');
    } else {
        element.classList.add('error');
    }
}

// --- 실시간 유효성 검사 핸들러 ---

const handleUsernameCheck = debounce(async (e) => {
    const input = e.target;
    const feedbackEl = document.getElementById('loginId-feedback');
    const loginId = input.value.trim();

    if (loginId.length === 0) {
        feedbackEl.textContent = '';
        return;
    }

    // 1. 클라이언트에서 먼저 형식을 검사합니다.
    if (!REGEX.LOGIN_ID.test(loginId)) {
        showFeedback(feedbackEl, '3~15자의 영문, 숫자, 언더스코어만 사용 가능합니다.', false);
        return; // 형식이 틀리면 서버에 중복 검사 요청을 보내지 않습니다.
    }

    // 2. 형식이 맞을 경우에만 서버에 중복 검사를 요청합니다.
    try {
        const response = await api.checkUsername(loginId);
        showFeedback(feedbackEl, response.message, !response.data);
    } catch (error) {
        showFeedback(feedbackEl, getErrorMessage(error), false);
    }
});

const handleEmailCheck = debounce(async (e) => {
    const input = e.target;
    const feedbackEl = document.getElementById('email-feedback');
    const email = input.value.trim();
    if (email.length === 0) {
        feedbackEl.textContent = '';
        return;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        showFeedback(feedbackEl, '올바른 이메일 형식이 아닙니다.', false);
        return;
    }
    try {
        const response = await api.checkEmail(email);
        showFeedback(feedbackEl, response.message, !response.data);
    } catch (error) {
        showFeedback(feedbackEl, getErrorMessage(error), false);
    }
});

const handleNicknameCheck = debounce(async (e) => {
    const input = e.target;
    const feedbackEl = document.getElementById('nickname-feedback');
    const nickname = input.value.trim();
    if (nickname.length === 0) {
        feedbackEl.textContent = '';
        return;
    }
    if (!REGEX.NICKNAME.test(nickname)) {
        showFeedback(feedbackEl, '닉네임은 2~10자의 영문, 숫자, 한글만 사용 가능합니다.', false);
        return;
    }
    try {
        const response = await api.checkNickname(nickname);
        showFeedback(feedbackEl, response.message, !response.data);
    } catch (error) {
        showFeedback(feedbackEl, getErrorMessage(error), false);
    }
});

function handlePasswordValidation() {
    const password = document.getElementById('password').value;
    const rules = {
        length: document.getElementById('rule-length'),
        letter: document.getElementById('rule-letter'),
        number: document.getElementById('rule-number'),
        special: document.getElementById('rule-special'),
    };

    if (password.length === 0) {
        Object.values(rules).forEach(el => el.classList.remove('fail'));
        handlePasswordCheck();
        return;
    }

    rules.length.classList.toggle('fail', !(password.length >= 8 && password.length <= 16));
    rules.letter.classList.toggle('fail', !/[a-zA-Z]/.test(password));
    rules.number.classList.toggle('fail', !/\d/.test(password));
    rules.special.classList.toggle('fail', !/[@$!%*?&]/.test(password));
    handlePasswordCheck();
}

function handlePasswordCheck() {
    const password = document.getElementById('password').value;
    const confirm = document.getElementById('password-confirm').value;
    const feedbackEl = document.getElementById('password-feedback');
    if (confirm.length === 0) {
        feedbackEl.textContent = '';
        feedbackEl.className = 'feedback-message';
        return;
    }
    showFeedback(feedbackEl, password === confirm ? '비밀번호가 일치합니다.' : '비밀번호가 일치하지 않습니다.', password === confirm);
}

// --- 폼 제출 및 로그아웃 핸들러 ---

async function handleSignUp(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);
    const userData = Object.fromEntries(formData.entries());
    if (userData.password !== userData['password-confirm']) {
        alert('비밀번호가 일치하지 않습니다.');
        return;
    }
    try {
        const response = await api.signUp(userData);
        if (response.success) {
            alert('회원가입에 성공했습니다! 로그인 해주세요.');
            window.location.href = '/';
        }
    } catch (error) {
        alert(`회원가입 실패: ${getErrorMessage(error)}`);
    }
}

async function handleLogin(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);
    const credentials = Object.fromEntries(formData.entries());
    const feedbackEl = document.getElementById('login-feedback');
    feedbackEl.textContent = '';
    try {
        const response = await api.login(credentials);
        if (response.success) {
            saveToken(response.data.token);
            updateHeaderUI();
            closeLoginModal();
            alert('로그인 되었습니다.');
        }
    } catch (error) {
        console.error('로그인 실패:', error);
        feedbackEl.textContent = getErrorMessage(error);
    }
}

function handleLogout(e) {
    e.preventDefault();
    if (confirm('로그아웃 하시겠습니까?')) {
        removeToken();
        updateHeaderUI();
        window.location.reload();
    }
}

// --- 초기화 함수 ---

/**
 * 모든 인증 관련 이벤트 리스너를 등록합니다.
 */
export function initAuth() {
    const $signupForm = document.getElementById('signup-form');
    const $loginForm = document.getElementById('login-form');
    const $logoutBtn = document.querySelector('.btn-logout');

    if ($signupForm) {
        $signupForm.addEventListener('submit', handleSignUp);
        document.getElementById('loginId').addEventListener('input', handleUsernameCheck);
        document.getElementById('email').addEventListener('input', handleEmailCheck);
        document.getElementById('nickname').addEventListener('input', handleNicknameCheck);
        document.getElementById('password').addEventListener('input', handlePasswordValidation);
        document.getElementById('password-confirm').addEventListener('input', handlePasswordCheck);
    }
    if ($loginForm) {
        $loginForm.addEventListener('submit', handleLogin);
    }
    if ($logoutBtn) {
        $logoutBtn.addEventListener('click', handleLogout);
    }
}
