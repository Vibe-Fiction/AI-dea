/**
 * @file '투표 페이지'(vote-page.html)의 모든 UI 상호작용과 API 연동을 처리하는 모듈입니다.
 * @module pages/vote-page
 * @author 송민재
 * @since 2025.08.19
 */

//  utils/token.js에서 getToken 함수를 가져옵니다.
import { getToken } from '../utils/token.js';

const VotePage = () => {

    // --- DOM 요소 참조 ---
    const proposalsContainer = document.querySelector('.proposal-grid');
    const countdownDisplay = document.getElementById('countdown-display');

    // 모달 관련 DOM 요소를 참조합니다.
    const votingModalContainer = document.querySelector('.voting-modal-container');
    const votingModalOverlay = document.querySelector('.voting-modal-overlay');
    const votingModalCloseBtn = document.querySelector('.voting-modal-close-btn');

    // 이어쓰기 버튼 DOM 요소 참조
    const continueWritingBtn = document.querySelector('.btn-continue-writing-start');

    // --- 페이지 상태 관리 ---
    let timerInterval = null;
    // 제안 데이터를 proposalId로 빠르게 찾기 위해 Map에 저장합니다.
    const proposalsMap = new Map();
    // 1위 제안의 chapterId 대신 소설의 최신 chapterId를 저장할 변수
    let latestChapterId = null;

    // URL 경로에서 novelId를 추출하는 로직
    const novelId = (() => {
        const urlParams = new URLSearchParams(window.location.search);
        const id = urlParams.get('novelId');
        return id && !isNaN(parseInt(id)) ? id : null;
    })();

    /**
     * 투표 마감 처리 API
     * @param {number} novelId 투표를 마감할 소설의 ID
     */
    const finalizeVoting = async (novelId) => {
        const token = getToken();

        if (!token) {
            console.error('인증 토큰이 없습니다. 로그인 상태를 확인해 주세요.');
            return;
        }

        try {
            const response = await fetch(`/api/vote/finalize/${novelId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                console.log('투표 마감 처리가 성공적으로 완료되었습니다.');
                alert('투표가 마감되었습니다. 다음 챕터가 곧 생성됩니다.');
                // 성공적으로 마감 처리 후 페이지를 새로고침하거나 필요한 UI 업데이트를 진행할 수 있습니다.
            } else {
                const errorMessage = await response.text();
                console.error('투표 마감 처리 실패:', errorMessage);
                alert(`투표 마감 처리 중 오류가 발생했습니다: ${errorMessage}`);
            }
        } catch (error) {
            console.error('API 호출 중 오류 발생:', error);
            alert('투표 마감 처리 중 네트워크 오류가 발생했습니다.');
        }
    };

    /**
     * @description 투표 API를 호출하여 투표를 처리합니다.
     * @param {number} proposalId 투표할 제안의 ID
     */
    async function doVote(proposalId) {
        // 로컬 스토리지 등에서 JWT 토큰을 가져옵니다.
        const token = getToken();

        // 토큰이 없으면 투표를 진행하지 않고 알림을 띄웁니다.
        if (!token) {
            alert('로그인이 필요합니다.');
            return;
        }

        try {
            const response = await fetch('/api/vote/do', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    // Authorization 헤더에 Bearer 토큰을 추가합니다.
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ proposalId: proposalId })
            });

            console.log('API 응답 객체:', response);

            if (response.ok) {
                alert('투표가 성공적으로 완료되었습니다!');
                window.location.reload();
            } else {
                const errorText = await response.text();
                console.log('API 오류 응답 본문:', errorText);

                alert(`투표 실패: ${errorText}`);
            }
        } catch (error) {
            console.error('투표 요청 중 오류 발생:', error);
            alert('투표 요청 중 문제가 발생했습니다.');
        }
    }

    let initialChapterCount = 0;

    /**
     * @description 투표 제안 데이터를 가져와 화면에 렌더링합니다.
     * 실제로는 fetch API를 통해 서버와 통신합니다.
     */
    async function loadProposals() {
        if (!novelId) {
            console.error('소설 ID를 찾을 수 없습니다.');
            proposalsContainer.innerHTML = '<p>투표 제안을 불러올 수 없습니다. 잘못된 접근입니다.</p>';
            return;
        }

        // getToken() 함수를 호출하여 토큰을 가져오고 token 변수에 할당합니다.
        const token = getToken();

        try {
            // novelId를 사용하여 API 호출 경로를 동적으로 변경합니다.
            const apiUrl = `/api/vote/novels/${novelId}/proposals`;
            // [추가] 헤더에 Authorization을 포함시킵니다.
            const headers = {
                'Content-Type': 'application/json',
            };
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }

            const response = await fetch(apiUrl, {
                method: 'GET',
                headers: headers
            });

            if (!response.ok) {
                //  403 에러일 경우 구체적인 메시지를 표시
                if (response.status === 403) {
                    alert('투표 제안을 불러올 권한이 없습니다. 로그인 상태를 확인해 주세요.');
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            //  API 응답을 JSON 객체로 파싱합니다. 이 객체는 { data: { proposals: [...], deadlineInfo: {...} }, message: "..." } 형태입니다.
            const responseData = await response.json();

            //  파싱한 JSON에서 'data' 필드를 추출하고, 그 안에서 'proposals'와 'latestChapterId'를 바로 추출합니다.
            const { proposals, deadlineInfo, latestChapterId: apiLatestChapterId } = responseData.data;

            // apiLatestChapterId가 유효한 값인지 확인 후, 전역 변수인 latestChapterId에 할당합니다.
            if (apiLatestChapterId) {
                latestChapterId = apiLatestChapterId;
            }


            // 투표 수(voteCount)를 기준으로 내림차순 정렬합니다.
            proposals.sort((a, b) => b.voteCount - a.voteCount);



            // 기존에 표시된 내용을 지웁니다. 이 코드를 제안을 렌더링하기 전에 위치시키는 것이 중요합니다.
            proposalsContainer.innerHTML = '';

            // 제안 목록이 비어있는 경우, 사용자에게 안내 메시지를 표시합니다.
            if (proposals.length === 0) {
                proposalsContainer.innerHTML = '<p>아직 등록된 투표 제안이 없습니다.</p>';
            }

            // 제안 목록을 순회하며 각 항목을 HTML 요소로 생성하고 DOM에 추가합니다.
            proposals.forEach((proposal, index) => {
                const proposalItem = createProposalElement(proposal, index + 1);
                proposalsContainer.appendChild(proposalItem);
                proposalsMap.set(proposal.proposalId, proposal);
            });

            // 11. 마감 시간이 존재하는 경우, 카운트다운 타이머를 시작합니다.
            // 제안 목록의 유무와 관계없이 마감 시간이 존재하면 타이머를 시작합니다.
            const deadlineTime = deadlineInfo.closingTime;
            if (deadlineTime) {
                startCountdown(deadlineTime);
            } else {
                console.error('마감 시간 정보를 찾을 수 없습니다.');
            }

        } catch (error) {
            // API 호출이나 데이터 처리 중 오류가 발생하면, 사용자에게 알리고 콘솔에 에러를 기록합니다.
            console.error('제안 데이터를 로드하는 데 실패했습니다:', error);
            proposalsContainer.innerHTML = '<p>제안을 불러오는 중 오류가 발생했습니다. 다시 시도해 주세요.</p>';
        }
    }


    /**
     * @description 마감 시간을 기준으로 카운트다운 타이머를 시작합니다.
     * @param {string} deadlineTime - ISO 8601 형식의 마감 시간 문자열 (예: '2025-08-23T10:00:00Z')
     */
    function startCountdown(deadlineTime) {
        // ✅ [수정] 문자열을 수동으로 파싱하여 안정적인 Date 객체 생성
        const parts = deadlineTime.split(/[- :]/);
        const deadline = new Date(
            parts[0], // Year
            parts[1] - 1, // Month (0부터 시작하므로 1을 빼줍니다)
            parts[2], // Day
            parts[3], // Hour
            parts[4], // Minute
            parts[5]  // Second
        );

        // let timerInterval = null;

        const updateTimer = () => {
            const now = new Date();
            const distance = deadline.getTime() - now.getTime();

            if (distance < 0) {
                clearInterval(timerInterval);
                countdownDisplay.textContent = "투표가 마감되었습니다.";
                // ✅ [추가] 마감 시 이어쓰기 버튼을 비활성화하고 스타일을 변경
                handleVotingEnd();
                return;
            }

            const hours = Math.floor(distance / (1000 * 60 * 60));
            const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((distance % (1000 * 60)) / 1000);

            const formattedTime = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
            countdownDisplay.textContent = formattedTime;
        };



        /**
        * 투표 마감 시 실행되는 함수.
        * 새로운 챕터가 생성되었는지 폴링 방식으로 확인합니다.
        */
        const handleVotingEnd = async () => {
            countdownDisplay.textContent = "투표가 마감되었습니다. 결과를 반영하고 있습니다...";

            // 모든 투표 버튼과 이어쓰기 버튼 비활성화
            document.querySelectorAll('.btn-vote').forEach(btn => btn.disabled = true);
            if (continueWritingBtn) {
                continueWritingBtn.disabled = true;
                continueWritingBtn.style.opacity = '0.5';
                continueWritingBtn.style.cursor = 'not-allowed';
            }

            // ✅ [추가] 투표 마감 API를 호출하여 백엔드에 결과 반영을 요청
            try {
                // API 엔드포인트는 백엔드에서 구현해야 합니다.
                // 예: '/api/vote/finalize'
                const token = getToken();
                if (!token) {
                    alert('로그인이 필요합니다. 투표 결과를 반영할 수 없습니다.');
                    return;
                }

                const response = await fetch('/api/vote/finalize', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({ novelId: novelId })
                });

                if (response.ok) {
                    alert('투표 결과가 성공적으로 반영되었습니다. 페이지를 새로고침합니다.');
                    window.location.reload();
                } else {
                    const errorText = await response.text();
                    console.error('투표 결과 반영 실패:', errorText);
                    alert(`투표 결과 반영 실패: ${errorText}`);
                }
            } catch (error) {
                console.error('투표 결과 반영 요청 중 오류 발생:', error);
                alert('투표 결과 반영 중 문제가 발생했습니다.');
            }
        };

        /**
         * @description 새로운 챕터가 시작될 때 이어쓰기 버튼을 활성화하고 클릭 이벤트를 추가하는 함수
         */
            // ✅ [추가] 새로운 챕터가 시작될 때 버튼을 활성화하는 함수
        const handleNewChapterStart = () => {
                if (continueWritingBtn) {
                    // 버튼의 비활성화 상태와 스타일을 원래대로 되돌립니다.
                    continueWritingBtn.disabled = false;
                    continueWritingBtn.style.opacity = '1';
                    continueWritingBtn.style.cursor = 'pointer';

                    // 이전에 추가되었을 수 있는 경고 핸들러를 제거합니다.
                    const alertHandler = () => {
                        alert('투표가 마감되어 새로운 챕터를 작성할 수 없습니다.');
                    };
                    continueWritingBtn.removeEventListener('click', alertHandler);

                    // 원래의 이어쓰기 로직을 다시 연결합니다.
                    // (init 함수에서 이미 한 번 추가하므로, 여기서는 한 번만 추가하도록 로직을 조정해야 중복 추가를 방지할 수 있습니다.)
                    // 안전하게 이벤트를 재연결하는 방법:
                    continueWritingBtn.removeEventListener('click', handleContinueWriting);
                    continueWritingBtn.addEventListener('click', handleContinueWriting);
                }
            };

        if (timerInterval) {
            clearInterval(timerInterval);
        }
        timerInterval = setInterval(updateTimer, 1000);

        updateTimer();
    }
    /**
     * 서버에 새로운 챕터 생성 상태를 확인하는 API를 호출하는 함수.
     * @param {string} novelId - 현재 소설 ID
     * @returns {Promise<boolean>} 새로운 챕터가 생성되었는지 여부
     */
    async function checkNewChapterStatus(novelId) {
        // 1. 토큰 가져오기
        const token = getToken();
        if (!token) {
            // 토큰이 없으면 권한 에러를 발생시킬 수 있습니다.
            console.error('인증 토큰이 없습니다. 로그인이 필요합니다.');
            return false;
        }
        try {
            const apiUrl = `/api/novel/${novelId}`;

            // 2. 요청 헤더에 Authorization 토큰 추가
            const response = await fetch(apiUrl, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                // 서버 응답이 200번대가 아닐 경우 에러 발생
                const errorText = await response.text();
                throw new Error(`챕터 상태 확인 실패: ${response.status} - ${errorText}`);
            }

            const data = await response.json();
            return data.hasNewChapter;
        } catch (error) {
            console.error('새 챕터 상태 확인 중 오류 발생:', error);
            return false;
        }
    }

    /**
     * @description 투표 제안 항목을 생성하는 헬퍼 함수
     * @param {object} proposal - 제안 데이터 객체
     * @param {number} rank - 순위
     * @returns {HTMLElement} 생성된 제안 항목 DOM 요소
     */
    function createProposalElement(proposal, rank) {
        const proposalArticle = document.createElement('article');
        proposalArticle.classList.add('proposal-card');
        proposalArticle.dataset.proposalId = proposal.proposalId;


        // 새로운 HTML 구조로 템플릿 리터럴을 변경합니다.
        proposalArticle.innerHTML = `
        <div class="card-content">
            <h3 class="proposal-title">${proposal.proposalTitle}</h3>
            <div class="rank-container">
                <span class="rank-large">${rank <= 3 ? `${rank}위` : ''}</span>
            </div>
            <div class="proposal-actions-row">
                <div class="vote-info">
                    <i class="fas fa-vote-yea"></i>
                    <span class="vote-count">${proposal.voteCount}</span>
                </div>
                <button class="btn-vote">투표하기</button>
            </div>
        </div>
        `;

        // 4. `proposalArticle` 전체에 클릭 이벤트를 추가하여 모달을 엽니다.
        proposalArticle.addEventListener('click', (event) => {
            // 투표 버튼 클릭 시에는 모달을 열지 않습니다.
            if (event.target.closest('.btn-vote')) {
                return;
            }
            openVotingModal(proposal);
        });

        // 5. 투표하기 버튼에 대한 별도의 클릭 이벤트 리스너를 추가합니다.
        const voteButton = proposalArticle.querySelector('.btn-vote');
        voteButton.addEventListener('click', (event) => {
            event.stopPropagation(); // 부모 요소로의 이벤트 전파를 막습니다.
            openVotingModal(proposal);
        });

        return proposalArticle;
    }

    /**
     * @description 투표 모달을 열고 데이터를 채워 넣습니다.
     * @param {object} proposalData - 투표 제안 데이터
     */
    const openVotingModal = (proposalData) => {
        // 1. 모달 제목을 '다음 챕터에 투표하기'로 설정
        votingModalContainer.querySelector('.voting-modal-novel-title').textContent = proposalData.novelName;
        // 2. API에서 받은 'proposalTitle'을 챕터 제목에 설정
        votingModalContainer.querySelector('.voting-chapter-title').textContent = proposalData.proposalTitle;
        // 3. API에서 받은 'proposalAuthor'를 참여자 이름에 설정
        votingModalContainer.querySelector('.voting-author-name').textContent = `by ${proposalData.proposalAuthor}`;
        // 4. API에서 받은 'voteCount'를 득표수에 설정
        votingModalContainer.querySelector('.voting-score').textContent = `현재 득표수: ${proposalData.voteCount}`;
        // 5. API에서 받은 'ProposalContent'를 내용에 설정
        votingModalContainer.querySelector('.voting-modal-story-content').textContent = proposalData.proposalContent;
        // 모달이 열릴 때 투표 완료 버튼을 찾고 이벤트 리스너를 추가
        const voteConfirmBtn = votingModalContainer.querySelector('.btn-vote');

        if (voteConfirmBtn) {
            voteConfirmBtn.onclick = () => {
                console.log('투표 완료 버튼 클릭됨'); // 디버깅을 위한 로그
                doVote(proposalData.proposalId);
            };
        } else {
            console.error("투표 완료 버튼을 찾을 수 없습니다.");
        }

        votingModalContainer.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    };


    const closeModal = () => {
        votingModalContainer.style.display = 'none';
        document.body.style.overflow = '';
    };

    /**
     * @description '이어쓰기' 버튼 클릭 시 페이지를 이동시킵니다.
     * @param {Event} event - 클릭 이벤트 객체
     */
    const handleContinueWriting = (event) => {
        event.preventDefault();
        if (latestChapterId) {
            history.pushState({}, '', `/proposals/create?chapterId=${latestChapterId}`);
            window.location.reload();
        } else {
            console.error('1위 제안의 chapterId를 찾을 수 없습니다.');
            alert('이어쓸 챕터를 찾을 수 없습니다.');
        }
    };


    // 초기화 함수
    const init = async () => {
        // URL에서 novelId를 가져온 후 loadProposals를 호출
        loadProposals();

        // 페이지 로드 시 최신 챕터 ID를 가져옴
        const novelId = (() => {
            const urlParams = new URLSearchParams(window.location.search);
            const id = urlParams.get('novelId');
            return id && !isNaN(parseInt(id)) ? id : null;
        })();

        if (novelId) {
            try {
                const response = await fetch(`/api/novels/${novelId}`);
                if (response.ok) {
                    const novelData = await response.json();
                    const chapters = novelData.chapters;
                    if (chapters && chapters.length > 0) {
                        latestChapterId = chapters[chapters.length - 1].chapterId;
                    }
                }
            } catch (error) {
                console.error("최신 챕터 ID를 가져오는 중 오류 발생:", error);
            }
        }

        // 투표 모달 닫기
        votingModalCloseBtn.addEventListener('click', closeModal);
        votingModalOverlay.addEventListener('click', closeModal);


        // 이어쓰기 버튼 이벤트 리스너 추가
        if(continueWritingBtn) {
            continueWritingBtn.addEventListener('click', handleContinueWriting);
        }
    };

    return {
        init,
    };
};

// 모듈 내보내기
export default VotePage;
