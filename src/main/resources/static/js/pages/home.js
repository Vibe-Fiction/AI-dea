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
        if (!novelGrid) return;

        // 초기 로드시에만 기존 콘텐츠 제거 (무한스크롤에서는 추가만)
        if (state.currentPage === 0) {
            novelGrid.innerHTML = '';

            const loadingIndicator = document.createElement('div');
            loadingIndicator.id = 'loading-indicator';
            loadingIndicator.className = 'loading-indicator';
            loadingIndicator.innerHTML = `<div class="spinner"></div><p>소설을 불러오는 중...</p>`;
            loadingIndicator.style.cssText = `text-align: center; padding: 40px; display: none;`;
            novelGrid.parentNode.insertBefore(loadingIndicator, novelGrid.nextSibling);

            const style = document.createElement('style');
            style.textContent = `
                .spinner {
                    border: 4px solid #f3f3f3; border-top: 4px solid #3498db;
                    border-radius: 50%; width: 40px; height: 40px;
                    animation: spin 1s linear infinite; margin: 0 auto 20px;
                }
                @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
                .loading-indicator { color: #666; font-size: 14px; }
            `;
            document.head.appendChild(style);
        }

        // API로부터 소설 목록을 가져오는 함수
        const fetchNovels = async (page = 0, append = false) => {
            if (state.isLoading || !state.hasMoreData) return;
            state.isLoading = true;

            const loadingIndicator = document.getElementById('loading-indicator');
            if (loadingIndicator) loadingIndicator.style.display = 'block';

            try {
                const response = await fetch(`/api/novels?page=${page}&size=8`);
                if (!response.ok) throw new Error(`서버 오류: ${response.status}`);
                const novels = await response.json();

                if (!Array.isArray(novels)) {
                    // ✅ 데이터 형식이 배열이 아닐 경우에도 더 이상 요청하지 않도록 상태 변경
                    state.hasMoreData = false;
                    if (!append) novelGrid.innerHTML = '<p>데이터 형식 오류가 발생했습니다.</p>';
                    return;
                }

                if (novels.length === 0) {
                    state.hasMoreData = false;
                    if (state.totalLoadedNovels === 0) {
                        novelGrid.innerHTML = '<p>표시할 소설이 없습니다.</p>';
                    } else {
                        const endMessage = document.createElement('div');
                        endMessage.className = 'end-message';
                        endMessage.innerHTML = '<p>모든 소설을 불러왔습니다.</p>';
                        endMessage.style.cssText = 'text-align: center; padding: 20px; color: #666;';
                        novelGrid.parentNode.appendChild(endMessage);
                    }
                    // ✅ 데이터가 없으면 자동 로드 로직을 실행하지 않고 바로 종료
                    return;
                }

                if (novels.length < 8) state.hasMoreData = false;
                state.totalLoadedNovels += novels.length;
                renderNovels(novels, append);

                // ✅ [수정] 성공적으로 데이터를 가져온 후에만, 그리고 더 가져올 데이터가 있을 때만 추가 로드를 검사합니다.
                // 이 로직을 finally에서 try의 성공 경로 끝으로 이동합니다.
                setTimeout(() => {
                    if (state.hasMoreData && !state.isLoading && document.body.scrollHeight <= window.innerHeight) {
                        state.currentPage++;
                        fetchNovels(state.currentPage, true);
                    }
                }, 100);

            } catch (error) {
                // ✅ [수정] 오류 발생 시 더 이상 데이터를 요청하지 않도록 상태를 변경하여 무한 루프를 방지합니다.
                state.hasMoreData = false;
                if (!append) novelGrid.innerHTML = '<div class="errortext">아직 등록된 소설이 없습니다.<br> 새로운 소설을 만들어주세요!</div>';
            } finally {
                state.isLoading = false;
                if (loadingIndicator) loadingIndicator.style.display = 'none';

                // ❌ [제거] 이 위치의 자동 로드 로직은 오류 발생 시에도 실행되어 문제를 일으키므로 제거합니다.
            }
        };

        // 소설 목록을 화면에 렌더링하는 함수
        const renderNovels = (novels, append = false) => {
            const novelGrid = document.querySelector('.novel-grid');
            if (!append) novelGrid.innerHTML = '';

            const fragment = document.createDocumentFragment();
            novels.forEach((novel, index) => {
                const novelCard = document.createElement('article');
                novelCard.classList.add('novel-card');
                novelCard.dataset.novelId = novel.novelId;

                const coverImage = novel.coverImageUrl || 'https://placehold.co/400x550/e2e8f0/64748b?text=표지+없음';
                const title = novel.title || '제목 없음';
                const authorName = novel.authorName || '작가 미상';

                novelCard.innerHTML = `
                    <img src="${coverImage}" alt="소설 표지" class="novel-cover" onerror="this.src='https://placehold.co/400x550/e2e8f0/64748b?text=이미지+오류'">
                    <div class="card-content">
                        <h3 class="novel-title">${title}</h3>
                        <p class="novel-author">원작자: ${authorName}</p>
                    </div>
                `;
                fragment.appendChild(novelCard);
            });
            novelGrid.appendChild(fragment);
        };

        // 무한스크롤 이벤트 핸들러
        const handleScroll = () => {
            if (state.isLoading || !state.hasMoreData) return;
            if (window.innerHeight + window.scrollY >= document.body.scrollHeight - 100) {
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
            }, 200);
        };
        window.removeEventListener('scroll', throttledScroll);
        window.addEventListener('scroll', throttledScroll);

        fetchNovels(0, false);
    };

    // 소설 카드 클릭 이벤트를 바인딩하는 함수
    const bindClickEvent = () => {
        const novelGrid = document.querySelector('.novel-grid');
        if (novelGrid) {
            novelGrid.addEventListener('click', (e) => {
                const novelCard = e.target.closest('.novel-card');
                if (novelCard) {
                    const novelId = novelCard.dataset.novelId;
                    if (novelId) {
                        window.location.href = `/chapters?novelId=${novelId}`;
                    }
                }
            });
        }
    };









    // 초기화 함수
    const init = () => {
        // 상태 초기화
        state.currentPage = 0;
        state.isLoading = false;
        state.hasMoreData = true;
        state.totalLoadedNovels = 0;

        updateUI();
        bindClickEvent();
    };

    return {
        init,
    };
};

export default HomePage;
