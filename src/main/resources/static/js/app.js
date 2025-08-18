/**
 * 우리 앱의 진입점 JS 파일
 */

// 페이지 라우팅 설정
/*
*  module: 해당 경로에 들어오면 실행할 모듈 js
*  requiresAuth: 인증된 사용자만 들어오는 페이지인지 여부
*
* */

import {PAGE_CONFIG} from './config/routes-config.js'
import {authService} from './utils/auth.js';


/**
 *
 * @param requiresAuth - 이 페이지 진입에 로그인이 필요한지 여부
 * @returns true일 경우 페이지 진입 허용, false일 경우 로그인 페이지로 튕겨내기
 */
// 라우트 가드 함수   ============================= 복구예정
const checkRouteAccess = (requiresAuth) => {
    // requiresAuth는 정적으로 페이지 진입이 인증이 필요한지를 표현하는 변수
    // 동적으로는 로그인이 되었을 때 가드처리를 수행하면 안된다.


    if (requiresAuth && !authService.isAuthenticated()){

        // 가드 처리
// 오버레이 표시
        const overlay = document.createElement('div');
        overlay.innerHTML = `
      <div style="
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: #f8f9fa;
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 9999;
      ">
        <div style="text-align: center;">
          <div style="
            width: 40px;
            height: 40px;
            border: 4px solid #e3e3e3;
            border-top: 4px solid #007bff;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin: 0 auto 20px;
          "></div>
          <p style="color: #6c757d; margin: 0; font-family: Arial, sans-serif;">인증 확인 중...</p>
        </div>
      </div>
      <style>
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      </style>
    `;
        document.body.appendChild(overlay);

        // 잠깐 후 얼럿 표시 및 리다이렉트
        // 로그인 모달띄우기
        setTimeout(() => {
            showAlert('로그인이 필요한 페이지입니다. 로그인 페이지로 이동합니다.');
            setTimeout(() => {
                overlay.remove();
                window.location.href = '/login';
            }, 1500);
        }, 800);
        return false;
    }
    return true;
};


// 현재 페이지 확인 함수
const getCurrentPage = () => {
    const path = window.location.pathname;
    return PAGE_CONFIG[path];
};

const App = () => {

    // 외부 모듈 JS파일들을 로드
    // 현재 어떤 페이지에 진입했는지 알아야 그에 맞는 JS를 가져올 수 있음
    const currentPage = getCurrentPage();
    // console.log(`currentPage: ${currentPage}`);

    // 라우트 가드 처리============================= 복구예정
    if(!checkRouteAccess(currentPage.requiresAuth)){
        return;
    }



    // 공통 이벤트 바인딩
    const bindEvents = () => {
        // 로그아웃 이벤트
        document.addEventListener('click',e =>{
            if(e.target.closest('[data-action="logout"]')){
                e.preventDefault();
                // 실제 로그아웃 처리 (localStorage에 있는 로그인 관련 데이터 삭제)
                authService.logout();
            }
        })

    };


    const init = async () => {
        try {
            // header의 인증 UI 업데이트
            authService.updateHeaderUI();

            // 앱의 공통 이벤트 바인딩
            bindEvents();


            const module = await import(`./pages/${currentPage.module}.js`);
            // console.log(module);

            if (module) {
                // default() 함수는 export default 내보낸 함수의 리턴값을 가져온다.
                const component = module.default();
                // console.log(component);
                component.init(); // 서브 모듈 실행
            }

        } catch(error) {
            console.error(`페이지 모듈 ${currentPage.module} 로드 실패!`, error);
        }
    };

    init();
};



// 기본 JavaScript 파일
document.addEventListener('DOMContentLoaded', () => {

    //라우트 가드 체크 (인증되지 않은 사용자가 접근할 수 없는 페이지에 로그인페이지로 리다이렉팅)


    App();
});
