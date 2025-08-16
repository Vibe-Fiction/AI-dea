/**
 * 마이페이지 모듈
 * URL 경로의 userid를 사용해서 사용자 정보와 작성한 소설 목록을 보여주는 페이지
 */
const MyPage = () => {
    let userData = null;

    // URL 파라미터에서 userid 가져오기
    const getUserIdFromUrl = () => {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('userid') || '1'; // 기본값 1
    };

    // API에서 사용자 데이터를 가져오는 함수
    const fetchUserData = async (userId) => {
        try {
            console.log(`API 호출: /api/my-page?userid=${userId}`);

            const response = await fetch(`/api/my-page?userid=${userId}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status} - ${response.statusText}`);
            }

            const data = await response.json();
            console.log('API 응답 데이터:', data);
            return data;
        } catch (error) {
            console.error('사용자 데이터 가져오기 실패:', error);
            throw error;
        }
    };

    // 날짜 포맷팅 함수
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    };

    // 프로필 섹션 렌더링 (기존 HTML 구조 활용)
    const renderProfile = (user) => {
        // 기존 HTML 요소들 찾기
        const profileImage = document.querySelector('.profile-image');
        const userNickname = document.querySelector('.user-nickname');
        const userStats = document.querySelector('.user-stats');

        if (profileImage) {
            // 프로필 이미지 설정 (없으면 2.JPEG 사용)
            const imageUrl = user.profileImageUrl || '/images/2.JPEG';
            profileImage.src = imageUrl;
            profileImage.alt = `${user.nickname} 프로필 사진`;

            // 이미지 로드 실패 시 기본 이미지로 변경
            profileImage.onerror = function() {
                this.src = '/images/2.JPEG';
                this.onerror = null; // 무한 루프 방지
            };
        }

        if (userNickname) {
            userNickname.textContent = `필명: ${user.nickname}`;
        }

        if (userStats) {
            userStats.innerHTML = `
                <span class="stat-item">
                    <i class="fas fa-calendar userstat"></i>
                    <span class="userstat">가입일: ${formatDate(user.createdAt)}</span>
                </span>
                <span class="stat-item">
                    <i class="fas fa-book userstat"></i>
                    <span class="userstat">작품 수: ${user.novels ? user.novels.length : 0}편</span>
                </span>
            `;
        }
    };

    // 소설 카드 렌더링 (기존 CSS 구조에 맞춤)
    const renderNovelCard = (novel) => {
        const article = document.createElement('article');


        article.className = 'novel-card';
        article.dataset.novelId = novel.novelId;
        article.innerHTML = `
            <img src="${novel.coverImageUrl || '/images/default-cover.webp'}" alt="소설 표지" class="novel-cover">
            <div class="card-content">
                <h3 class="novel-title">${novel.title}</h3>
            </div>
        `;
        return article;
    };

    // 소설 목록 섹션 렌더링 (기존 HTML 구조 활용)
    const renderNovels = (novels) => {
        const novelGrid = document.querySelector('#novel-grid'); // id로 찾기

        if (!novelGrid) {
            console.error('novel-grid 요소를 찾을 수 없습니다.');
            return;
        }

        // 기존 내용 초기화
        novelGrid.innerHTML = '';

        if (!novels || novels.length === 0) {
            novelGrid.innerHTML = '<p>작성한 소설이 없습니다.</p>';
            return;
        }

        // 각 소설에 대해 카드 생성 및 추가
        novels.forEach(novel => {
            const novelCard = renderNovelCard(novel);
            novelGrid.appendChild(novelCard);
        });
    };

    // 로딩 상태 표시
    const showLoading = () => {
        const novelGrid = document.querySelector('#novel-grid');
        if (novelGrid) {
            novelGrid.innerHTML = `
                <div style="text-align: center; padding: 2rem;">
                    <p>사용자 정보를 불러오는 중...</p>
                </div>
            `;
        }
    };

    // 에러 상태 표시
    const showError = (message) => {
        const novelGrid = document.querySelector('#novel-grid');
        if (novelGrid) {
            novelGrid.innerHTML = `
                <div style="text-align: center; padding: 2rem; color: red;">
                    <h3>오류가 발생했습니다</h3>
                    <p>${message}</p>
                    <button onclick="location.reload()">다시 시도</button>
                </div>
            `;
        }
    };

    // 메인 렌더링 함수
    const render = () => {
        if (!userData) {
            showError('사용자 데이터가 없습니다.');
            return;
        }

        // 프로필 렌더링
        renderProfile(userData);

        // 소설 목록 렌더링
        renderNovels(userData.novels);
    };

    // 이벤트 바인딩
    const bindEvents = () => {
        // 소설 카드 클릭 이벤트
        const novelGrid = document.querySelector('#novel-grid');
        if (novelGrid) {
            novelGrid.addEventListener('click', (e) => {
                const novelCard = e.target.closest('.novel-card');
                if (novelCard) {
                    console.log('소설 카드 클릭됨');
                }
            });
        }

        // 네비게이션 버튼 이벤트
        const navButtons = document.querySelectorAll('.btn-nav');
        navButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                // 기존 active 클래스 제거
                navButtons.forEach(btn => btn.classList.remove('active'));
                // 클릭된 버튼에 active 클래스 추가
                e.target.classList.add('active');

                const type = e.target.getAttribute('data-type');
                console.log('네비게이션 클릭:', type);
                // 나중에 각 타입별 필터링 구현
            });
        });
    };

    // 데이터 초기화 및 렌더링
    const init = async () => {
        try {
            showLoading();

            // URL 파라미터에서 userid 가져오기
            const userId = getUserIdFromUrl();
            console.log('URL 파라미터에서 가져온 userId:', userId);

            if (!userId) {
                throw new Error('사용자 ID가 제공되지 않았습니다. URL을 /my-page?userid=1 형태로 접속해주세요.');
            }

            // API에서 사용자 데이터 가져오기
            userData = await fetchUserData(userId);

            // 페이지 렌더링
            render();

            // 이벤트 바인딩
            bindEvents();

        } catch (error) {
            console.error('마이페이지 초기화 실패:', error);
            showError(error.message);
        }
    };

    return {
        init
    };
};

export default MyPage;
