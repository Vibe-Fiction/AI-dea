/**
 * 마이페이지 모듈
 * URL 파라미터의 userid를 사용해서 사용자 정보와 작성한 소설 목록을 보여주는 페이지
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

        // 소설 표지 이미지 설정 (없으면 기본 이미지 사용)
        const coverImageUrl = novel.coverImageUrl || '/images/2.JPEG';

        article.innerHTML = `
            <img src="${coverImageUrl}"
                 alt="소설 표지"
                 class="novel-cover"
                 onerror="this.src='/images/2.JPEG'; this.onerror=null;">
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

    // 모달 관련 함수들
    const openEditModal = () => {
        console.log('openEditModal 함수 호출됨');
        if (!userData) {
            console.error('userData가 없습니다.');
            return;
        }

        const modal = document.getElementById('edit-profile-modal');

        if (!modal) {
            console.error('edit-profile-modal 요소를 찾을 수 없습니다.');
            return;
        }

        // 텍스트 입력 필드들만 값 설정
        const nicknameInput = document.getElementById('edit-nickname');
        const emailInput = document.getElementById('edit-email');
        const passwordInput = document.getElementById('edit-password');
        const passwordConfirmInput = document.getElementById('edit-password-confirm');

        if (nicknameInput) nicknameInput.value = userData.nickname || '';
        if (emailInput) emailInput.value = userData.email || '';
        if (passwordInput) passwordInput.value = '';
        if (passwordConfirmInput) passwordConfirmInput.value = '';

        // 현재 프로필 이미지 미리보기 설정
        const currentProfilePreview = document.getElementById('current-profile-preview');
        if (currentProfilePreview) {
            const currentImageUrl = userData.profileImageUrl || '/images/2.JPEG';
            currentProfilePreview.src = currentImageUrl;
            currentProfilePreview.onerror = function() {
                this.src = '/images/2.JPEG';
                this.onerror = null;
            };

            // 미리보기 텍스트 초기화
            const previewText = currentProfilePreview.parentElement.querySelector('p small');
            if (previewText) {
                previewText.textContent = '현재 프로필 이미지';
            }
        }

        // 파일 입력 필드는 값 설정하지 않음 (보안상 불가능)
        // document.getElementById('edit-profile-image')는 건드리지 않음

        modal.classList.add('show');
        document.body.style.overflow = 'hidden';
        console.log('모달 열림');
    };

    const closeEditModal = () => {
        const modal = document.getElementById('edit-profile-modal');
        modal.classList.remove('show');
        document.body.style.overflow = '';
    };

    const handleProfileUpdate = async (formData) => {
        try {
            // 저장 버튼 비활성화
            const saveBtn = document.querySelector('.btn-save');
            saveBtn.disabled = true;
            saveBtn.textContent = '저장 중...';

            // FormData 객체 생성 (파일 업로드를 위해)
            const updateData = new FormData();

            // 텍스트 데이터 추가
            updateData.append('nickname', formData.nickname);
            updateData.append('email', formData.email);

            // 비밀번호가 입력된 경우에만 추가
            if (formData.password) {
                updateData.append('password', formData.password);
            }

            // 파일이 선택된 경우에만 추가
            if (formData.profileImage) {
                updateData.append('profileImage', formData.profileImage);
            }

            // 현재 사용자 ID 가져오기
            const userId = getUserIdFromUrl();

            // POST API 호출 (multipart/form-data)
            const response = await fetch(`/api/my-page?userid=${userId}`, {
                method: 'POST',
                body: updateData // FormData는 Content-Type 헤더를 자동 설정
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response.json();
            console.log('프로필 업데이트 성공:', result);

            // 로컬 데이터 업데이트 (비밀번호 제외)
            userData.nickname = formData.nickname;
            userData.email = formData.email;

            // 서버에서 반환된 새 이미지 경로가 있으면 업데이트
            if (result.profileImageUrl) {
                userData.profileImageUrl = result.profileImageUrl;
            }

            // 화면 업데이트
            renderProfile(userData);

            // 모달 닫기
            closeEditModal();

            // 성공 메시지
            alert('프로필이 성공적으로 업데이트되었습니다!');

        } catch (error) {
            console.error('프로필 업데이트 실패:', error);
            alert('프로필 업데이트에 실패했습니다. 다시 시도해주세요.');
        } finally {
            // 저장 버튼 복원
            const saveBtn = document.querySelector('.btn-save');
            if (saveBtn) {
                saveBtn.disabled = false;
                saveBtn.textContent = '저장';
            }
        }
    };

    // 이벤트 바인딩 (이벤트 위임 사용)
    const bindEvents = () => {
        // 전체 document에서 클릭 이벤트를 감지 (이벤트 위임)
        document.addEventListener('click', (e) => {
            // 정보 수정하기 버튼 클릭
            if (e.target.closest('.edit-profile-btn')) {
                console.log('정보 수정하기 버튼 클릭됨 (이벤트 위임)');
                openEditModal();
                return;
            }

            // 모달 닫기 버튼 클릭
            if (e.target.closest('.modal-close')) {
                closeEditModal();
                return;
            }

            // 취소 버튼 클릭
            if (e.target.closest('.btn-cancel')) {
                closeEditModal();
                return;
            }

            // 모달 배경 클릭
            if (e.target.classList.contains('modal')) {
                closeEditModal();
                return;
            }

            // 소설 카드 클릭
            const novelCard = e.target.closest('.novel-card');
            if (novelCard) {
                console.log('소설 카드 클릭됨');
                return;
            }

            // 네비게이션 버튼 클릭
            const navButton = e.target.closest('.btn-nav');
            if (navButton) {
                // 기존 active 클래스 제거
                document.querySelectorAll('.btn-nav').forEach(btn => btn.classList.remove('active'));
                // 클릭된 버튼에 active 클래스 추가
                navButton.classList.add('active');

                const type = navButton.getAttribute('data-type');
                console.log('네비게이션 클릭:', type);
                return;
            }
        });

        // 파일 선택 변경 이벤트 (미리보기 기능)
        document.addEventListener('change', (e) => {
            if (e.target.id === 'edit-profile-image') {
                const file = e.target.files[0];
                const previewImg = document.getElementById('current-profile-preview');

                if (file && previewImg) {
                    // 파일이 이미지인지 확인
                    if (file.type.startsWith('image/')) {
                        const reader = new FileReader();
                        reader.onload = function(e) {
                            previewImg.src = e.target.result;

                            // 미리보기 텍스트 변경
                            const previewText = previewImg.parentElement.querySelector('p small');
                            if (previewText) {
                                previewText.textContent = '프로필 이미지 미리보기';
                            }
                        };
                        reader.readAsDataURL(file);
                    } else {
                        alert('이미지 파일만 선택해주세요.');
                        e.target.value = ''; // 파일 선택 취소
                    }
                }
            }
        });

        // ESC 키로 모달 닫기
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                closeEditModal();
            }
        });

        // 프로필 수정 폼 제출 이벤트
        document.addEventListener('submit', (e) => {
            if (e.target.id === 'edit-profile-form') {
                e.preventDefault();

                const profileImageFile = document.getElementById('edit-profile-image').files[0];

                const formData = {
                    nickname: document.getElementById('edit-nickname').value.trim(),
                    email: document.getElementById('edit-email').value.trim(),
                    password: document.getElementById('edit-password').value,
                    passwordConfirm: document.getElementById('edit-password-confirm').value,
                    profileImage: profileImageFile // 파일 객체
                };

                // 간소화된 유효성 검사

                // 이메일이 입력된 경우에만 형식 검사
                if (formData.email && formData.email.length > 0) {
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    if (!emailRegex.test(formData.email)) {
                        alert('올바른 이메일 형식을 입력해주세요.');
                        return;
                    }
                }

                // 비밀번호가 입력된 경우에만 확인
                if (formData.password || formData.passwordConfirm) {
                    if (formData.password !== formData.passwordConfirm) {
                        alert('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.');
                        return;
                    }
                }

                // 파일 크기 검사 (5MB 제한)
                if (formData.profileImage && formData.profileImage.size > 5 * 1024 * 1024) {
                    alert('파일 크기는 5MB 이하여야 합니다.');
                    return;
                }

                // 파일 형식 검사
                if (formData.profileImage) {
                    const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
                    if (!allowedTypes.includes(formData.profileImage.type)) {
                        alert('JPG, PNG, GIF, WEBP 파일만 업로드 가능합니다.');
                        return;
                    }
                }

                handleProfileUpdate(formData);
            }
        });
    };

    // 데이터 초기화 및 렌더링
    const init = async () => {
        try {
            // 먼저 이벤트 바인딩
            bindEvents();

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
