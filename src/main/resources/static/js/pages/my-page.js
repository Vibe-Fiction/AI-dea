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
            // 프로필 이미지 설정 (로컬 파일 경로 또는 기본 이미지 사용)
            let imageUrl = user.profileImageUrl;

            // 로컬 파일 경로인 경우 file:// 프로토콜 추가
            if (imageUrl && !imageUrl.startsWith('http') && !imageUrl.startsWith('/')) {
                imageUrl = `file://${imageUrl}`;
            } else if (!imageUrl) {
                imageUrl = '/images/2.JPEG';
            }

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
        const currentPasswordInput = document.getElementById('edit-current-password');
        const passwordInput = document.getElementById('edit-password');
        const passwordConfirmInput = document.getElementById('edit-password-confirm');

        if (nicknameInput) nicknameInput.value = userData.nickname || '';
        if (emailInput) emailInput.value = userData.email || '';
        if (currentPasswordInput) currentPasswordInput.value = '';
        if (passwordInput) passwordInput.value = '';
        if (passwordConfirmInput) passwordConfirmInput.value = '';

        // 현재 프로필 이미지 미리보기 설정
        const currentProfilePreview = document.getElementById('current-profile-preview');
        if (currentProfilePreview) {
            let currentImageUrl = userData.profileImageUrl;

            // 로컬 파일 경로인 경우 file:// 프로토콜 추가
            if (currentImageUrl && !currentImageUrl.startsWith('http') && !currentImageUrl.startsWith('/')) {
                currentImageUrl = `file://${currentImageUrl}`;
            } else if (!currentImageUrl) {
                currentImageUrl = '/images/2.JPEG';
            }

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

        modal.classList.add('show');
        document.body.style.overflow = 'hidden';
        console.log('모달 열림');
    };

    const closeEditModal = () => {
        const modal = document.getElementById('edit-profile-modal');
        modal.classList.remove('show');
        document.body.style.overflow = '';
    };

    const handleProfileUpdate = async (changedFields) => {
        try {
            // 저장 버튼 비활성화
            const saveBtn = document.querySelector('.btn-save');
            saveBtn.disabled = true;
            saveBtn.textContent = '저장 중...';

            // 사용자 ID 가져오기
            const userId = getUserIdFromUrl();

            // FormData 객체 생성 (변경된 필드만 포함)
            const updateData = new FormData();

            // 변경된 텍스트 데이터만 추가
            if (changedFields.nickname) {
                updateData.append('nickname', changedFields.nickname);
            }
            if (changedFields.email) {
                updateData.append('email', changedFields.email);
            }
            if (changedFields.password) {
                updateData.append('password', changedFields.password);
                // 현재 비밀번호도 함께 전송 (필수)
                updateData.append('currentPassword', changedFields.currentPassword);
            }

            // 파일이 변경된 경우에만 추가
            if (changedFields.profileImage) {
                updateData.append('profileImage', changedFields.profileImage);
            }

            // POST API 호출 - userid를 쿼리 파라미터로 전송
            const response = await fetch(`/api/my-page?userid=${userId}`, {
                method: 'POST',
                body: updateData
            });

            if (!response.ok) {
                // 디버깅: 응답 내용 확인
                console.log('응답 상태:', response.status);

                const responseText = await response.text();
                console.log('응답 원본:', responseText);

                let errorData;
                try {
                    errorData = JSON.parse(responseText);
                    console.log('파싱된 에러 데이터:', errorData);
                } catch (e) {
                    console.error('JSON 파싱 실패:', e);
                    if (response.status === 401) {
                        alert('현재 비밀번호가 올바르지 않습니다.');
                        document.getElementById('edit-current-password').focus();
                        document.getElementById('edit-current-password').select();
                    } else {
                        alert('서버 오류가 발생했습니다.');
                    }
                    return;
                }

                // 서버에서 보낸 에러 메시지 처리
                if (response.status === 401) {
                    // 비밀번호 관련 에러 처리
                    if (errorData.message && errorData.message.includes('현재 비밀번호')) {
                        alert('현재 비밀번호가 올바르지 않습니다.');
                        document.getElementById('edit-current-password').focus();
                        document.getElementById('edit-current-password').select();
                    } else if (errorData.detail) {
                        alert(errorData.detail);
                    } else if (errorData.message) {
                        alert(errorData.message);
                    } else {
                        alert('입력된 정보를 확인해주세요.');
                    }
                    return;
                } else {
                    alert('업데이트에 실패했습니다.');
                    return;
                }
            }

            console.log('프로필 업데이트 성공');

            // 업데이트된 사용자 정보 다시 가져오기
            userData = await fetchUserData(userId);

            // 화면 업데이트
            renderProfile(userData);

            // 모달 닫기
            closeEditModal();

            // 성공 메시지 (변경된 필드 표시)
            const changedFieldNames = Object.keys(changedFields)
                .filter(key => key !== 'currentPassword') // 현재 비밀번호는 표시하지 않음
                .map(field => {
                    switch(field) {
                        case 'nickname': return '닉네임';
                        case 'email': return '이메일';
                        case 'password': return '비밀번호';
                        case 'profileImage': return '프로필 이미지';
                        default: return field;
                    }
                });
            alert(`${changedFieldNames.join(', ')}이(가) 성공적으로 업데이트되었습니다!`);

        } catch (error) {
            console.error('프로필 업데이트 실패:', error);
            alert('네트워크 오류가 발생했습니다. 인터넷 연결을 확인해주세요.');
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
                                previewText.textContent = '바뀔 프로필 이미지 미리보기';
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

                // 현재 입력된 값들
                const currentValues = {
                    nickname: document.getElementById('edit-nickname').value.trim(),
                    email: document.getElementById('edit-email').value.trim(),
                    currentPassword: document.getElementById('edit-current-password').value,
                    password: document.getElementById('edit-password').value,
                    passwordConfirm: document.getElementById('edit-password-confirm').value,
                    profileImage: profileImageFile
                };

                // 변경된 필드만 찾아내기
                const changedFields = {};

                // 닉네임 변경 확인
                if (currentValues.nickname !== userData.nickname) {
                    changedFields.nickname = currentValues.nickname;
                }

                // 이메일 변경 확인
                if (currentValues.email !== userData.email) {
                    changedFields.email = currentValues.email;
                }

                // 비밀번호는 입력되었으면 변경된 것으로 간주
                if (currentValues.password) {
                    changedFields.password = currentValues.password;
                    changedFields.currentPassword = currentValues.currentPassword;
                }

                // 프로필 이미지는 파일이 선택되었으면 변경된 것으로 간주
                if (currentValues.profileImage) {
                    changedFields.profileImage = currentValues.profileImage;
                }

                // 변경된 내용이 없으면 저장하지 않음
                if (Object.keys(changedFields).length === 0) {
                    alert('변경된 내용이 없습니다.');
                    return;
                }

                console.log('변경된 필드들:', Object.keys(changedFields));

                // 클라이언트 측 유효성 검사

                // 이메일이 변경된 경우에만 형식 검사
                if (changedFields.email) {
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    if (!emailRegex.test(changedFields.email)) {
                        alert('올바른 이메일 형식을 입력해주세요.');
                        return;
                    }
                }

                // 비밀번호가 입력된 경우에만 확인
                if (changedFields.password) {
                    // 현재 비밀번호 입력 확인
                    if (!currentValues.currentPassword) {
                        alert('비밀번호 변경을 위해 현재 비밀번호를 입력해주세요.');
                        document.getElementById('edit-current-password').focus();
                        return;
                    }

                    // 새 비밀번호와 확인 비밀번호 일치 확인
                    if (currentValues.password !== currentValues.passwordConfirm) {
                        alert('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.');
                        document.getElementById('edit-password-confirm').focus();
                        document.getElementById('edit-password-confirm').select();
                        return;
                    }

                    // 비밀번호 길이 확인
                    if (currentValues.password.length < 6) {
                        alert('비밀번호는 6자 이상이어야 합니다.');
                        document.getElementById('edit-password').focus();
                        return;
                    }
                }

                // 파일 크기 검사 (5MB 제한)
                if (changedFields.profileImage && changedFields.profileImage.size > 5 * 1024 * 1024) {
                    alert('파일 크기는 5MB 이하여야 합니다.');
                    return;
                }

                // 파일 형식 검사
                if (changedFields.profileImage) {
                    const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
                    if (!allowedTypes.includes(changedFields.profileImage.type)) {
                        alert('JPG, PNG, GIF, WEBP 파일만 업로드 가능합니다.');
                        return;
                    }
                }

                handleProfileUpdate(changedFields);
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
