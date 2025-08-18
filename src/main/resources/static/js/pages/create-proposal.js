/**
 * @file '다음 챕터 쓰기'(create-proposal.html) 페이지의 모든 UI 상호작용과
 *       API 연동을 처리하는 메인 JavaScript 파일입니다.
 * @author 왕택준
 * @since 2025.08.18
 */

import { continueChapterApi, createProposalApi } from '../utils/api.js';

function main() {
    // --- DOM 요소 선택 ---
    const form = document.getElementById('createChapterForm');
    const titleInput = document.getElementById('chapter-title');
    const contentTextarea = document.getElementById('chapter-content');
    const aiHelpBtn = document.querySelector('.btn-ai-help');
    const submitBtn = form.querySelector('button[type="submit"]');

    // URL 경로에서 chapterId를 동적으로 추출 (예: /proposals/create/1)
    const pathParts = window.location.pathname.split('/');
    const chapterId = pathParts[pathParts.length - 1];

    if (!chapterId || isNaN(parseInt(chapterId))) {
        alert('잘못된 접근입니다. 대상 회차 ID를 찾을 수 없습니다.');
        return;
    }

    // --- 이벤트 리스너 바인딩 ---
    aiHelpBtn.addEventListener('click', handleAiHelpClick);
    form.addEventListener('submit', handleFormSubmit);

    // --- 이벤트 핸들러 함수 ---
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
            window.location.href = `/vote-page/${chapterId}`; // 성공 시 투표 페이지로 이동
        } catch (error) {
            console.error('제안 등록 오류:', error);
            alert(`오류가 발생했습니다: ${error.message}`);
        } finally {
            toggleLoading(false, submitBtn, '완료');
        }
    }

    // --- 헬퍼 함수 ---
    function toggleLoading(isLoading, buttonElement, loadingText) {
        if (!buttonElement) return;
        const icon = buttonElement.querySelector('i');
        buttonElement.disabled = isLoading;
        if (isLoading) {
            buttonElement.dataset.originalHtml = buttonElement.innerHTML;
            buttonElement.innerHTML = `<i class="fas fa-spinner fa-spin"></i> ${loadingText}`;
        } else {
            buttonElement.innerHTML = buttonElement.dataset.originalHtml;
        }
    }

    function populateFormWithAiData(aiData) {
        if (!aiData) return;
        titleInput.value = aiData.suggestedTitle || '';
        contentTextarea.value = aiData.suggestedContent || '';
    }
}

document.addEventListener('DOMContentLoaded', main);
