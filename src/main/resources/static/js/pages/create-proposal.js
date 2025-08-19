/**
 * @file '다음 챕터 쓰기'(create-proposal.html) 페이지의 모든 UI 상호작용과 API 연동을 처리하는 모듈입니다.
 * @module pages/create-proposal
 * @author 왕택준
 * @since 2025.08.18
 */

import { continueChapterApi, createProposalApi } from '../utils/api.js';

/**
 * '이어쓰기 제안' 페이지의 모든 기능을 캡슐화하는 모듈 함수입니다.
 * @returns {{init: function}} 페이지 초기화를 위한 init 함수를 포함하는 객체.
 */
const createProposalPage = () => {

    // --- DOM 요소 선택 ---
    const form = document.getElementById('createChapterForm');
    const titleInput = document.getElementById('chapter-title');
    const contentTextarea = document.getElementById('chapter-content');
    const aiHelpBtn = document.querySelector('.btn-ai-help');
    const submitBtn = form ? form.querySelector('button[type="submit"]') : null;

    // --- 페이지 상태 관리 ---
    /** @type {string|null} URL 경로에서 추출한 현재 회차의 ID. 유효하지 않으면 null. */
    const chapterId = (() => {
        const pathParts = window.location.pathname.split('/');
        const id = pathParts[pathParts.length - 1];
        return id && !isNaN(parseInt(id)) ? id : null;
    })();

    /**
     * 'AI 도움받기' 버튼의 클릭 이벤트 핸들러입니다.
     * 사용자로부터 받은 지시문을 기반으로 AI에게 이어쓰기 초안을 요청하고, 그 결과를 폼에 채웁니다.
     * @async
     * @param {Event} event - 클릭 이벤트 객체.
     */
    async function handleAiHelpClick(event) {
        event.preventDefault();
        const instruction = prompt("AI에게 어떤 내용을 이어가도록 지시할까요?");
        if (!instruction || instruction.trim() === '') return;

        toggleLoading(true, aiHelpBtn, 'AI 생각 중...');
        try {
            const aiResponse = await continueChapterApi(chapterId, instruction);
            populateFormWithAiData(aiResponse);
            alert('AI 이어쓰기 추천이 완료되었습니다!');
        } catch (error) {
            console.error('AI 이어쓰기 기능 오류:', error);
            alert(`오류가 발생했습니다: ${error.message}`);
        } finally {
            toggleLoading(false, aiHelpBtn, 'AI 도움받기');
        }
    }

    /**
     * '완료'(제안 등록) 폼의 제출 이벤트 핸들러입니다.
     * 현재 폼에 입력된 제목과 내용을 취합하여 이어쓰기 제안 생성 API를 호출합니다.
     * 성공 시, 해당 회차의 투표 페이지로 이동합니다.
     * @async
     * @param {Event} event - 폼 제출 이벤트 객체.
     */
    async function handleFormSubmit(event) {
        event.preventDefault();
        const proposalData = {
            title: titleInput.value,
            content: contentTextarea.value
        };

        toggleLoading(true, submitBtn, '제안 등록 중...');
        try {
            const result = await createProposalApi(chapterId, proposalData);
            alert(`새로운 제안(ID: ${result.proposalId})이 성공적으로 등록되었습니다!`);
            window.location.href = `/vote-page/${chapterId}`;
        } catch (error) {
            console.error('제안 등록 오류:', error);
            alert(`오류가 발생했습니다: ${error.message}`);
        } finally {
            toggleLoading(false, submitBtn, '완료');
        }
    }

    /**
     * 버튼의 로딩 상태를 제어하고 UI를 업데이트하는 헬퍼 함수입니다.
     * 비동기 작업(API 호출) 중에 사용자에게 시각적 피드백을 제공합니다.
     * @param {boolean} isLoading - 로딩 상태 여부.
     * @param {HTMLElement} buttonElement - 상태를 변경할 버튼 요소.
     * @param {string} loadingText - 로딩 중에 표시할 텍스트.
     */
    function toggleLoading(isLoading, buttonElement, loadingText) {
        if (!buttonElement) return;
        buttonElement.disabled = isLoading;
        if (isLoading) {
            buttonElement.dataset.originalHtml = buttonElement.innerHTML;
            buttonElement.innerHTML = `<i class="fas fa-spinner fa-spin"></i> ${loadingText}`;
        } else {
            buttonElement.innerHTML = buttonElement.dataset.originalHtml;
        }
    }

    /**
     * AI 이어쓰기 추천 API의 응답 데이터를 HTML 폼 필드에 채워넣는 헬퍼 함수입니다.
     * @param {object} aiData - AI가 생성한 추천 데이터.
     * @property {string} aiData.suggestedTitle - 추천 소제목.
     * @property {string} aiData.suggestedContent - 추천 내용.
     */
    function populateFormWithAiData(aiData) {
        if (!aiData) return;
        titleInput.value = aiData.suggestedTitle || '';
        contentTextarea.value = aiData.suggestedContent || '';
    }

    /**
     * 페이지의 모든 이벤트 리스너를 바인딩하고, 초기 상태(chapterId 유효성)를 검증하는 초기화 함수입니다.
     * 이 함수는 app.js에 의해 호출되어 페이지 모듈의 실행을 시작합니다.
     */
    const init = () => {
        if (!chapterId) {
            alert('잘못된 접근입니다. 대상 회차 ID를 찾을 수 없습니다.');
            // 폼을 비활성화하거나, 메인 페이지로 리디렉션하는 로직 추가 가능
            if (form) form.style.display = 'none';
            return;
        }

        console.log(`이어쓰기 제안 페이지(${chapterId}번 챕터) 모듈 초기화`);

        if (aiHelpBtn) aiHelpBtn.addEventListener('click', handleAiHelpClick);
        if (form) form.addEventListener('submit', handleFormSubmit);
    };

    return { init };
};

export default createProposalPage;
