/**
 * @file '투표 페이지'(vote-page.html)의 모든 UI 상호작용과 API 연동을 처리하는 모듈입니다.
 * @module pages/vote-page
 * @author Gemini
 * @since 2025.08.19
 */

// API 호출을 위한 유틸리티 함수를 import할 수 있습니다.
// 예: import { getProposals, voteApi } from '../utils/api.js';

const VotePage = () => {

    // --- DOM 요소 참조 ---
    const proposalsContainer = document.querySelector('.proposal-grid');
    const countdownDisplay = document.getElementById('countdown-display');

    // ✅ [추가] 모달 관련 DOM 요소를 참조합니다.
    const votingModalContainer = document.querySelector('.voting-modal-container');
    const votingModalOverlay = document.querySelector('.voting-modal-overlay');
    const votingModalCloseBtn = document.querySelector('.voting-modal-close-btn');

    // ✅ [추가] 이어쓰기 버튼 DOM 요소 참조
    const continueWritingBtn = document.querySelector('.btn-continue-writing');

    // --- 페이지 상태 관리 ---
    let timerInterval = null;
    // ✅ [추가] 제안 데이터를 proposalId로 빠르게 찾기 위해 Map에 저장합니다.
    const proposalsMap = new Map();
    // ✅ [추가] 1위 제안의 chapterId를 저장할 변수
    let topProposalChapterId = null;

    // ✅ [추가] URL 경로에서 novelId를 추출하는 로직
    const novelId = (() => {
        const urlParams = new URLSearchParams(window.location.search);
        const id = urlParams.get('novelId');
        return id && !isNaN(parseInt(id)) ? id : null;
    })();

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

        try {
            // ✅ [수정] novelId를 사용하여 API 호출 경로를 동적으로 변경합니다.
            // fetch(`/api/proposals/${novelId}`)

            // 현재는 임시 데이터로 구현합니다.
            const response = await new Promise(resolve => setTimeout(() => {
                resolve({
                    ok: true,
                    json: () => Promise.resolve([
                        {
                            proposalId: 101,
                            chapterTitle: '미래에서 온 편지',
                            authorName: '김민준',
                            voteCount: 15,
                            chapterId: 'chap101',
                            content: '빛의 속도로 달려가는 기차 안에서...'
                        },
                        {
                            proposalId: 102,
                            chapterTitle: '시간의 틈새',
                            authorName: '이서연',
                            voteCount: 12,
                            chapterId: 'chap102',
                            content: '고대 유적지에서 발견된 이상한 거울은...'
                        },
                        {
                            proposalId: 103,
                            chapterTitle: '다른 차원의 문',
                            authorName: '박지훈',
                            voteCount: 8,
                            chapterId: 'chap103',
                            content: '낡은 도서관의 비밀 통로를 열자...'
                        },
                    ])
                });
            }, 500));

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const proposals = await response.json();
            proposals.sort((a, b) => b.voteCount - a.voteCount); // 투표 수 기준으로 정렬
            topProposalChapterId = proposals.length > 0 ? proposals[0].chapterId : null;

            // ✅ [수정] 이 코드가 먼저 실행되어 기존 콘텐츠를 비웁니다.
            proposalsContainer.innerHTML = '';

            if (proposals.length === 0) {
                proposalsContainer.innerHTML = '<p>아직 등록된 투표 제안이 없습니다.</p>';
                return;
            }

            proposals.forEach((proposal, index) => {
                const proposalItem = createProposalElement(proposal, index + 1);
                proposalsContainer.appendChild(proposalItem);
                proposalsMap.set(proposal.proposalId, proposal);
            });

        } catch (error) {
            console.error('제안 데이터를 로드하는 데 실패했습니다:', error);
            proposalsContainer.innerHTML = '<p>제안을 불러오는 중 오류가 발생했습니다. 다시 시도해 주세요.</p>';
        }
    }

    /**
     * @description 투표 제안 항목을 생성하는 헬퍼 함수
     * @param {object} proposal - 제안 데이터 객체
     * @param {number} rank - 순위
     * @returns {HTMLElement} 생성된 제안 항목 DOM 요소
     */
    function createProposalElement(proposal, rank) {
        // 1. article 요소를 생성하고, CSS에 정의된 'proposal-card' 클래스를 추가합니다.
        const proposalArticle = document.createElement('article');
        proposalArticle.classList.add('proposal-card');

        // 2. data-proposal-id 속성을 추가하여 나중에 클릭 이벤트에서 해당 제안을 식별할 수 있도록 합니다.
        proposalArticle.dataset.proposalId = proposal.proposalId;

        // 3. 템플릿 리터럴을 사용하여 카드의 전체 HTML 구조를 작성합니다.
        proposalArticle.innerHTML = `
        <div class="proposal-card-header">
            <h4 class="proposal-title">${proposal.chapterTitle}</h4>
            <span class="proposal-rank">${rank <= 3 ? `${rank}위` : ''}</span>

            <div class="proposal-meta">
                <span><i class="fas fa-user-circle"></i> ${proposal.authorName}</span>
            </div>
        </div>
        <div class="proposal-content-snippet">
            <p>${proposal.content.substring(0, 100)}...</p>
        </div>
        <div class="proposal-card-footer">
            <div class="proposal-info">
                <span class="proposal-score"><i class="fas fa-vote-yea"></i> ${proposal.voteCount}</span>
            </div>
            <button class="btn-vote">투표하기</button>
        </div>
    `;

        // 4. `proposalArticle` 전체에 클릭 이벤트를 추가하여 모달을 엽니다.
        //    이벤트 버블링을 이용하여 `btn-vote` 클릭 시 중복 실행을 막습니다.
        proposalArticle.addEventListener('click', (event) => {
            if (event.target.closest('.btn-vote')) {
                return;
            }
            openVotingModal(proposal);
        });

        // 5. 투표하기 버튼에 대한 별도의 클릭 이벤트 리스너를 추가합니다.
        const voteButton = proposalArticle.querySelector('.btn-vote');
        voteButton.addEventListener('click', () => {
            openVotingModal(proposal);
        });

        return proposalArticle;
    }

    /**
     * @description 투표 모달을 열고 데이터를 채워 넣습니다.
     * @param {object} proposalData - 투표 제안 데이터
     */
    const openVotingModal = (proposalData) => {
        votingModalContainer.querySelector('.voting-modal-title').textContent = '다음 챕터에 투표하기';
        votingModalContainer.querySelector('.voting-chapter-title').textContent = proposalData.chapterTitle;
        votingModalContainer.querySelector('.voting-author-name').textContent = `by ${proposalData.authorName}`;
        votingModalContainer.querySelector('.voting-score').textContent = `현재 점수: ${proposalData.voteCount}`;
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
        if (topProposalChapterId) {
            // ✅ [수정] History API를 사용하여 페이지를 이동합니다.
            // URL에 1위 제안의 chapterId를 포함하여 전달합니다.
            history.pushState({}, '', `/create-proposal/${topProposalChapterId}`);
            window.location.reload(); // 페이지 강제 새로고침
        } else {
            console.error('1위 제안의 chapterId를 찾을 수 없습니다.');
            alert('이어쓸 챕터를 찾을 수 없습니다.');
        }
    };


    // 초기화 함수
    const init = () => {
        // ✅ [수정] URL에서 novelId를 가져온 후 loadProposals를 호출합니다.
        loadProposals();

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
