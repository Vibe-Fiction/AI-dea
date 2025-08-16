// 페이지 라우팅 설정
/*
  module: 해당 경로에 들어오면 실행할 모듈 js
  requiresAuth: 인증된 사용자만 들어오는 페이지인지 여부
 */
export const PAGE_CONFIG = {
    '/': {
        module: 'home',
        requiresAuth: false,
    },
    '/my-page': {
        module: 'my-page',
        requiresAuth: false,
    },
    '/my-page/:userid': {
        module: 'my-page',
        requiresAuth: false,
    }
};
