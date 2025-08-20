const ChaptersPage = () => {

    // 2. 비동기 함수로 소설과 챕터 데이터를 불러옵니다.
    async function loadNovelAndChapters() {
        try {
            // ✅ [수정] URL 쿼리 파라미터에서 novelId를 추출합니다.
            // 예: /chapters?novelId=1 -> "1"
            const urlParams = new URLSearchParams(window.location.search);
            const novelId = urlParams.get('novelId');

            if (!novelId) {
                console.error('URL 파라미터에서 novelId를 찾을 수 없습니다.');
                const mainContent = document.querySelector('.main-content');
                mainContent.innerHTML = '<h2>잘못된 접근입니다.</h2><p>소설 ID가 필요합니다.</p>';
                return;
            }

            // Promise.all을 사용해 소설 정보와 챕터 목록을 병렬로 동시에 요청합니다.
            const [novelResponse, chaptersResponse] = await Promise.all([
                fetch(`/api/novels/${novelId}`),
                // ChapterControllerSH의 주소에 맞게 API 엔드포인트를 사용합니다.
                fetch(`/api/chapters/${novelId}`)
            ]);

            if (!novelResponse.ok || !chaptersResponse.ok) {
                if (!novelResponse.ok) console.error(`소설 정보 로드 실패: ${novelResponse.status}`);
                if (!chaptersResponse.ok) console.error(`챕터 정보 로드 실패: ${chaptersResponse.status}`);
                throw new Error('데이터를 불러오는 데 실패했습니다.');
            }

            // ✅ [수정] '현재 진행 중인 이어쓰기' 버튼에 클릭 이벤트 리스너를 추가합니다.
            const continueWritingBtn = document.querySelector('.btn-continue-writing');
            if (continueWritingBtn) {
                continueWritingBtn.addEventListener('click', (event) => {
                    event.preventDefault();
                    const novelId = new URLSearchParams(window.location.search).get('novelId');
                    if (novelId) {
                        window.location.href = `/vote-page?novelId=${novelId}`;
                    } else {
                        alert('소설 정보를 찾을 수 없습니다.');
                    }
                });
            }

            const novel = await novelResponse.json();
            const chapters = await chaptersResponse.json();

            // 3. 받아온 데이터로 화면을 렌더링합니다.
            renderNovelDetails(novel);
            renderChapterList(chapters);

            // 4. 동적으로 생성된 챕터 목록에 모달 이벤트를 연결합니다.
            initializeModalEventListeners();

        } catch (error) {
            console.error('페이지 렌더링 중 오류 발생:', error);
            const mainContent = document.querySelector('.main-content');
            mainContent.innerHTML = '<h2>페이지를 불러올 수 없습니다.</h2><p>나중에 다시 시도해주세요.</p>';
        }
    }

    // 소설 상세 정보를 렌더링하는 함수
    function renderNovelDetails(novel) {
        document.querySelector('.novel-title').textContent = novel.title;
        document.querySelector('.author-info').innerHTML = `<i class="fas fa-user-circle"></i> ${novel.authorName}`;
        document.querySelector('.synopsis p').textContent = novel.synopsis;
        document.querySelector('.novel-detail-cover').src = novel.coverImageUrl || 'https://placehold.co/400x550/e2e8f0/64748b?text=No+Image';
        const continueButton = document.querySelector('.btn-continue-writing');
        continueButton.dataset.novelId = `${novel.novelId}`;

        const hashtagList = document.querySelector('.hashtag-list');
        hashtagList.innerHTML = '';
        novel.genres.forEach(genre => {
            const hashtag = document.createElement('span');
            hashtag.className = 'hashtag';
            hashtag.textContent = `#${genre}`;
            hashtagList.appendChild(hashtag);
        });
    }

    // 챕터 목록을 렌더링하는 함수
    function renderChapterList(chapters) {
        const chapterList = document.querySelector('.chapter-list');
        const chapterCountHeader = document.querySelector('.chapter-list-header h3');

        chapterList.innerHTML = '';
        chapterCountHeader.textContent = `총 ${chapters.length}화`;

        if (chapters.length === 0) {
            chapterList.innerHTML = '<li style="text-align: center; padding: 2rem;">등록된 챕터가 없습니다.</li>';
            return;
        }

        chapters.forEach((chapter, index) => {
            const li = document.createElement('li');
            li.className = 'chapter-item';
            li.setAttribute('data-chapter-index', index);
            li.setAttribute('data-chapter-content', chapter.content);

            li.innerHTML = `
                <div class="chapter-info">
                    <span class="chapter-num">${chapter.chapterNumber}화</span>
                    <span class="chapter-title">${chapter.title}</span>
                    <span class="chapter-author">by ${chapter.author}</span>
                </div>
            `;
            chapterList.appendChild(li);
        });
    }

    // 동적으로 생성된 요소들에 모달 이벤트를 바인딩하는 함수
    function initializeModalEventListeners() {
        const chapterItems = document.querySelectorAll('.chapter-item');
        const modalContainer = document.querySelector('.fiction-modal-container');
        const modalCloseBtn = document.querySelector('.fiction-modal-close-btn');
        const modalOverlay = document.querySelector('.fiction-modal-overlay');
        const novelTitleEl = document.querySelector('.novel-title');
        const prevBtn = document.querySelector('.fiction-nav-btn.prev-btn');
        const nextBtn = document.querySelector('.fiction-nav-btn.next-btn');
        let currentChapterIndex = -1;

        const openModal = (chapterEl) => {
            const novelTitle = novelTitleEl.textContent;
            const chapterNum = chapterEl.querySelector('.chapter-num').textContent;
            const chapterTitle = chapterEl.querySelector('.chapter-title').textContent;
            const chapterAuthor = chapterEl.querySelector('.chapter-author').textContent;
            const storyContent = chapterEl.getAttribute('data-chapter-content');

            currentChapterIndex = parseInt(chapterEl.getAttribute('data-chapter-index'));

            modalContainer.querySelector('.fiction-modal-novel-title').textContent = novelTitle;
            modalContainer.querySelector('.fiction-modal-chapter-num').textContent = chapterNum;
            modalContainer.querySelector('.fiction-modal-chapter-title').textContent = chapterTitle;
            modalContainer.querySelector('.fiction-modal-chapter-author').textContent = chapterAuthor;
            modalContainer.querySelector('.fiction-modal-story-content').innerHTML = `<p>${storyContent.replace(/\n/g, '</p><p>')}</p>`;
            modalContainer.style.display = 'flex';
            document.body.style.overflow = 'hidden';

            prevBtn.disabled = currentChapterIndex === 0;
            nextBtn.disabled = currentChapterIndex === chapterItems.length - 1;
        };

        const closeModal = () => {
            modalContainer.style.display = 'none';
            document.body.style.overflow = '';
            currentChapterIndex = -1;
        };

        const changeChapter = (direction) => {
            const newIndex = direction === 'next' ? currentChapterIndex + 1 : currentChapterIndex - 1;
            if (newIndex >= 0 && newIndex < chapterItems.length) {
                openModal(chapterItems[newIndex]);
            }
        };

        chapterItems.forEach(item => {
            item.addEventListener('click', () => openModal(item));
        });

        modalCloseBtn.addEventListener('click', closeModal);
        modalOverlay.addEventListener('click', closeModal);
        prevBtn.addEventListener('click', () => changeChapter('prev'));
        nextBtn.addEventListener('click', () => changeChapter('next'));
    }

    // 초기화 함수
    const init = () => {
        loadNovelAndChapters();
    };

    return {
        init,
    };
};

export default ChaptersPage;
