/**
 * 페이지 이동 관련 이벤트 리스너를 초기화합니다.
 */
export function initRouter() {
    // 위임(delegation)을 사용하여 document 전체에서 클릭 이벤트를 감지합니다.
    // 이렇게 하면 페이지가 동적으로 변경되어도 이벤트 리스너가 계속 작동합니다.
    document.addEventListener('click', (e) => {
        const target = e.target;

        // --- 네비게이션 처리 ---
        // 로고 클릭 -> 홈으로
        if (target.matches('.logo')) {
            e.preventDefault();
            window.location.href = '/';
        }

        // 로그인 모달의 '회원가입' 버튼 클릭 -> 회원가입 페이지로
        if (target.matches('.btn-signup')) {
            window.location.href = '/signup';
        }
    });
}
