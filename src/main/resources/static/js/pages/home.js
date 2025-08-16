const HomePage = () => {
    // 상태 관리
    const state = {
        user: null,
        currentPage: 0,
        isLoading: false,
        hasMoreData: true,
        totalLoadedNovels: 0
    };

    /**
     * DOM 요소를 업데이트하고 API에서 데이터를 가져와 렌더링하는 함수
     */
    const updateUI = () => {


        const novelGrid = document.querySelector('.novel-grid');

        if (!novelGrid) {

            return;
        }

        // 초기 로드시에만 기존 콘텐츠 제거 (무한스크롤에서는 추가만)
        if (state.currentPage === 0) {

            novelGrid.innerHTML = '';

            // 로딩 인디케이터 추가
            const loadingIndicator = document.createElement('div');
            loadingIndicator.id = 'loading-indicator';
            loadingIndicator.className = 'loading-indicator';
            loadingIndicator.innerHTML = `
                <div class="spinner"></div>
                <p>소설을 불러오는 중...</p>
            `;
            loadingIndicator.style.cssText = `
                text-align: center;
                padding: 40px;
                display: none;
            `;
            novelGrid.parentNode.insertBefore(loadingIndicator, novelGrid.nextSibling);

            // CSS 스타일 추가
            const style = document.createElement('style');
            style.textContent = `
                .spinner {
                    border: 4px solid #f3f3f3;
                    border-top: 4px solid #3498db;
                    border-radius: 50%;
                    width: 40px;
                    height: 40px;
                    animation: spin 1s linear infinite;
                    margin: 0 auto 20px;
                }
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
                .loading-indicator {
                    color: #666;
                    font-size: 14px;
                }
            `;
            document.head.appendChild(style);
        }

        // API로부터 소설 목록을 가져오는 함수
        const fetchNovels = async (page = 0, append = false) => {
            // 로딩 중이거나 더 이상 데이터가 없으면 중단
            if (state.isLoading || !state.hasMoreData) {
                return;
            }

            state.isLoading = true;

            // 로딩 인디케이터 표시
            const loadingIndicator = document.getElementById('loading-indicator');
            if (loadingIndicator) {
                loadingIndicator.style.display = 'block';
            }

            try {
                const response = await fetch(`/api/novels?page=${page}&size=8`);

                if (!response.ok) {
                    throw new Error(`서버 오류: ${response.status}`);
                }

                const novels = await response.json();

                if (!Array.isArray(novels)) {

                    if (!append) {
                        novelGrid.innerHTML = '<p>데이터 형식 오류가 발생했습니다.</p>';
                    }
                    return;
                }

                // 데이터가 없거나 8개 미만이면 더 이상 로드할 데이터가 없음
                if (novels.length === 0) {
                    state.hasMoreData = false;


                    if (state.totalLoadedNovels === 0) {
                        novelGrid.innerHTML = '<p>표시할 소설이 없습니다.</p>';
                    } else {
                        // 마지막 페이지 메시지 추가
                        const endMessage = document.createElement('div');
                        endMessage.className = 'end-message';
                        endMessage.innerHTML = '<p>모든 소설을 불러왔습니다.</p>';
                        endMessage.style.cssText = 'text-align: center; padding: 20px; color: #666;';
                        novelGrid.parentNode.appendChild(endMessage);
                    }
                    return;
                }

                if (novels.length < 8) {
                    state.hasMoreData = false;
                }

                state.totalLoadedNovels += novels.length;


                renderNovels(novels, append);

            } catch (error) {

                if (!append) {
                    novelGrid.innerHTML = '<p>소설 목록을 불러오는 중 오류가 발생했습니다.</p>';
                }
            } finally {
                state.isLoading = false;

                // 로딩 인디케이터 숨김
                const loadingIndicator = document.getElementById('loading-indicator');
                if (loadingIndicator) {
                    loadingIndicator.style.display = 'none';
                }


            }
        };

        // 소설 목록을 화면에 렌더링하는 함수
        const renderNovels = (novels, append = false) => {


            const novelGrid = document.querySelector('.novel-grid');

            if (!append) {
                novelGrid.innerHTML = '';
            }

            const fragment = document.createDocumentFragment();

            novels.forEach((novel, index) => {
                const novelCard = document.createElement('article');
                novelCard.classList.add('novel-card');

                novelCard.dataset.novelId = novel.novelId;

                // 기본값 설정으로 안전한 렌더링
                const coverImage = novel.coverImageUrl || 'https://placehold.co/400x550/e2e8f0/64748b?text=표지+없음';
                const title = novel.title || '제목 없음';
                const authorName = novel.authorName || '작가 미상';

                novelCard.innerHTML = `
                    <img src="${coverImage}" alt="소설 표지" class="novel-cover"
                         onerror="this.src='https://placehold.co/400x550/e2e8f0/64748b?text=이미지+오류'">
                    <div class="card-content">
                        <h3 class="novel-title">${title}</h3>
                        <p class="novel-author">원작자: ${authorName}</p>
                    </div>
                `;

                // 새로 추가되는 카드에 페이드인 효과 (선택사항)
                if (append) {
                    novelCard.style.cssText = 'opacity: 0; transform: translateY(20px); transition: all 0.3s ease;';
                    setTimeout(() => {
                        novelCard.style.cssText = 'opacity: 1; transform: translateY(0); transition: all 0.3s ease;';
                    }, index * 100); // 순차적 애니메이션
                }

                fragment.appendChild(novelCard);
            });

            novelGrid.appendChild(fragment);

        };

        // 무한스크롤 이벤트 핸들러
        const handleScroll = () => {
            // 쓰로틀링을 위한 디바운싱
            if (state.isLoading || !state.hasMoreData) return;

            const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
            const scrollPercentage = (scrollTop + clientHeight) / scrollHeight;

            // 90% 스크롤 시 다음 페이지 로드
            if (scrollPercentage >= 0.9) {

                state.currentPage++;
                fetchNovels(state.currentPage, true);
            }
        };

        // 스크롤 이벤트 리스너 등록 (쓰로틀링 적용)
        let scrollTimeout;
        const throttledScroll = () => {
            if (scrollTimeout) return;
            scrollTimeout = setTimeout(() => {
                handleScroll();
                scrollTimeout = null;
            }, 200); // 200ms 쓰로틀링
        };

        // 기존 이벤트 리스너 제거 후 새로 등록
        window.removeEventListener('scroll', throttledScroll);
        window.addEventListener('scroll', throttledScroll);



        fetchNovels(0, false);
    };

    // 초기화 함수
    const init = () => {

        // 상태 초기화
        state.currentPage = 0;
        state.isLoading = false;
        state.hasMoreData = true;
        state.totalLoadedNovels = 0;

        updateUI();
    };

    return {
        init,
    };
};

export default HomePage;
