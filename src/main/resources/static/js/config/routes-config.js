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


        // 새 소설 쓰기 버튼 클릭 -> 토큰 검증 후 페이지 이동 또는 로그인 모달
        if (target.matches('.create-novel-btn') || target.closest('.create-novel-btn')) {
            const targetUrl="/novels/create";
            e.preventDefault();
            handleCreateNovelClick(targetUrl);
        }

        if(target.matches('.btn-continue-writing')|| target.closest('.btn-continue-writing')){

            const novelId = document.querySelector('.btn-continue-writing').dataset.novelId;


            const targetUrl=`/vote-page?novelId=${novelId}`;
            e.preventDefault();
            handleCreateNovelClick(targetUrl);
        }


    });
}




/**
 * 새 소설 쓰기 버튼 클릭 핸들러
 * 토큰이 있으면 소설 생성 페이지로 이동, 없으면 로그인 모달 표시
 */
function handleCreateNovelClick(targetUrl) {
    // 토큰 확인을 위해 token.js에서 getToken 함수를 가져와야 합니다
    // 하지만 routes-config.js에서 직접 import하면 순환 참조가 발생할 수 있으므로
    // localStorage에서 직접 확인합니다
    const token = localStorage.getItem('vibe_fiction_token');

    if (token) {
        // 토큰이 있으면 버튼에 해당하는 페이지로 이동
        window.location.href = targetUrl;
    } else {
        // 로그인 후 이동할 페이지를 저장
        localStorage.setItem('redirect_after_login', targetUrl);

        // 토큰이 없으면 로그인 모달 표시
        const loginModal = document.querySelector('.modal-overlay');
        if (loginModal) {
            alert('로그인이 필요한 서비스입니다.');
            loginModal.classList.add('open');
        } else {
            // 모달이 없는 경우 알림 후 메인 페이지로 이동
            alert('로그인이 필요한 서비스입니다.');
            window.location.href = '/';
        }
    }
}










export const PAGE_CONFIG = {
    '/': {
        module: 'home',
        requiresAuth: false,
    },
    '/my-page': {
        module: 'my-page',
        requiresAuth: true,
    },

    '/chapters': {
        module: 'chapters-page',
        requiresAuth: false,
    },
    '/novels/create': {
        module: 'create-novel',
        requiresAuth: true,
    },
    '/chapters/create': {
        module: 'create-proposal',
        requiresAuth: true,
    },
    '/vote-page/:id': {
        module: 'vote-page',
        requiresAuth: true,
    },
    '/proposals/create': {
        module: 'create-proposal',
        requiresAuth: true,
    },
    '/signup': {
        module: 'auth',
        requiresAuth: false,
    }


};
