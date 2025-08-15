// js/ui.js

import { getToken } from './utils/token.js';

// --- DOM 요소 선택 ---
const $authButtons = document.querySelector('.auth-buttons'); // 로그인/회원가입 버튼 그룹
const $userButtons = document.querySelector('.user-buttons'); // 마이페이지/로그아웃 버튼 그룹
const $loginModalOverlay = document.querySelector('.modal-overlay');
const $loginModalCloseBtn = document.querySelector('.modal-close-btn');
const $loginBtn = document.querySelector('.btn-login');

/**
 * 로그인 상태에 따라 헤더의 버튼 UI를 업데이트합니다.
 */
export function updateHeaderUI() {
    const token = getToken();

    if (token) {
        // 로그인 상태: '마이페이지/로그아웃' 버튼 보이기
        $authButtons.style.display = 'none';
        $userButtons.style.display = 'flex';
    } else {
        // 로그아웃 상태: '로그인' 버튼 보이기
        $authButtons.style.display = 'flex';
        $userButtons.style.display = 'none';
    }
}

/**
 * 로그인 모달을 열고 닫는 이벤트 리스너를 초기화합니다.
 */
export function initLoginModal() {
    if (!$loginModalOverlay) return; // 로그인 모달이 없는 페이지일 수 있으므로 예외 처리

    // '로그인' 버튼 클릭 시 모달 열기
    $loginBtn.addEventListener('click', (e) => {
        e.preventDefault();
        $loginModalOverlay.classList.add('open');
    });

    // '닫기' 버튼 클릭 시 모달 닫기
    $loginModalCloseBtn.addEventListener('click', () => {
        $loginModalOverlay.classList.remove('open');
    });

    // 모달 바깥의 어두운 영역 클릭 시 모달 닫기
    $loginModalOverlay.addEventListener('click', (e) => {
        if (e.target === $loginModalOverlay) {
            $loginModalOverlay.classList.remove('open');
        }
    });
}

/**
 * 로그인 모달을 닫습니다. (로그인 성공 시 호출)
 */
export function closeLoginModal() {
    if ($loginModalOverlay) {
        $loginModalOverlay.classList.remove('open');
    }
}
