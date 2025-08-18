// js/app.js

// 각 모듈에서 초기화 함수들을 가져옵니다.
import { initAuth } from './utils/auth.js';
import { initRouter,PAGE_CONFIG } from './config/routes-config.js';
import { updateHeaderUI, initLoginModal } from './utils/ui.js';




// 현재 페이지 확인 함수
const getCurrentPage = () => {
    const path = window.location.pathname;
    return PAGE_CONFIG[path];
};






/**
 * 애플리케이션을 시작하는 메인 함수
 */
function main() {

    // 1. 외부 모듈들을 로드합니다.

    const currentPage = getCurrentPage();
    const init = async () => {
        try {
            // 2. 페이지 로드 시, 먼저 로그인 상태에 따라 헤더 UI를 즉시 업데이트합니다.
            //    이렇게 해야 사용자가 로그인 상태인지 아닌지 바로 알 수 있습니다.
            updateHeaderUI();

            // 3. 로그인 모달의 열고 닫기 기능을 활성화합니다.
            initLoginModal();

            // 4. 회원가입, 로그인, 로그아웃과 같은 인증 관련 기능들을 활성화합니다.
            initAuth();

            // 5. 페이지 이동(라우팅) 기능을 활성화합니다.
            initRouter();


            // 6. 현재 페이지의 모듈을 로드합니다.
            const module = await import(`./pages/${currentPage.module}.js`);
            console.log(module);

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
}




// HTML 문서의 모든 요소가 로드된 후 main 함수를 실행합니다.
// 'DOMContentLoaded'는 CSS, 이미지 등을 기다리지 않고 HTML 구조만 완성되면 바로 실행되어 빠릅니다.
document.addEventListener('DOMContentLoaded',()=> {
    main();
    console.log('Vibe Fiction App Initialized! 🚀');
});
