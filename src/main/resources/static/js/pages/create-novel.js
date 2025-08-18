/**
 * @file '새 소설 쓰기' 페이지(create-novel.html)의 모든 UI 상호작용과
 *       API 연동을 처리하는 메인 JavaScript 파일입니다.
 * @description 장르 선택/추가, 'AI 와 함께쓰기', '소설 생성하기' 기능을 모두 포함합니다.
 * @author 왕택준 (AI 및 소설 생성 연동)
 * @since 2025.08
 */

import { recommendNovelApi, createNovelApi } from '../utils/api.js';

function main() {
    // --- DOM 요소 선택 ---
    const form = document.getElementById('createNovelForm');
    const novelTitleInput = document.getElementById('novel-title');
    const synopsisTextarea = document.getElementById('novel-synopsis');
    const contentTextarea = document.getElementById('novel-content');
    const aiHelpBtn = document.querySelector('.btn-ai-help');
    const submitBtn = form.querySelector('button[type="submit"]');

    // [리팩토링] 장르 관련 DOM 요소 변경
    const genreSelect = document.getElementById('genre-select');
    const addGenreBtn = document.getElementById('add-genre-btn');
    const selectedGenresContainer = document.getElementById('selected-genres');

    let allGenres = [];
    let selectedGenres = new Set(); // 선택된 장르 Enum 이름 추적

    // --- 장르 선택/추가 로직 ---
    async function fetchGenres() {
        try {
            const response = await fetch('/api/genres');
            if (response.ok) {
                const result = await response.json();
                allGenres = result.data;
                // <select> 태그의 <option>을 채웁니다.
                allGenres.forEach(genre => {
                    const option = document.createElement('option');
                    option.value = genre.name.name; // 서버로 보낼 Enum 이름
                    option.textContent = genre.name.description; // 사용자에게 보여줄 한글 이름
                    genreSelect.appendChild(option);
                });
            }
        } catch (error) {
            console.error('장르 목록을 불러오는 데 실패했습니다:', error);
        }
    }

    function addSelectedGenre() {
        const enumName = genreSelect.value;
        if (!enumName) {
            alert('먼저 목록에서 장르를 선택해주세요.');
            return;
        }

        const description = genreSelect.options[genreSelect.selectedIndex].textContent;

        if (selectedGenres.size >= 3) {
            alert('장르는 최대 3개까지만 선택할 수 있습니다.');
            return;
        }
        if (selectedGenres.has(enumName)) {
            alert('이미 선택된 장르입니다.');
            return;
        }

        selectedGenres.add(enumName);

        const tag = document.createElement('span');
        tag.className = 'genre-tag';
        tag.textContent = description;
        const removeBtn = document.createElement('i');
        removeBtn.className = 'fas fa-times-circle remove-tag-btn';
        removeBtn.onclick = () => {
            selectedGenres.delete(enumName);
            tag.remove();
        };
        tag.appendChild(removeBtn);
        selectedGenresContainer.appendChild(tag);

        genreSelect.selectedIndex = 0; // 선택 후 드롭다운 초기화
    }

    // --- AI 추천 및 소설 생성 로직 ---
    async function handleAiHelpClick(event) {
        event.preventDefault();
        const synopsis = synopsisTextarea.value;
        const genreArray = Array.from(selectedGenres);
        const genre = genreArray.length > 0 ? genreArray[0] : ''; // 선택된 장르 중 첫 번째 것을 사용

        if (!genre || !synopsis) {
            alert('AI의 도움을 받으려면 [장르]를 1개 이상 선택하고 [시놉시스]를 입력해야 합니다.');
            return;
        }
        toggleLoading(true, aiHelpBtn, 'AI 생각 중...');
        try {
            const aiResponse = await recommendNovelApi(genre, synopsis);
            populateFormWithAiData(aiResponse);
            alert('AI 추천이 완료되었습니다!');
        } catch (error) {
            console.error('AI 추천 기능 오류:', error);
            alert(`오류가 발생했습니다: ${error.message}`);
        } finally {
            toggleLoading(false, aiHelpBtn, 'AI 와 함께쓰기');
        }
    }

    async function handleFormSubmit(event) {
        event.preventDefault();

        if (selectedGenres.size === 0) {
            alert('장르를 1개 이상 선택해야 합니다.');
            return;
        }

        const novelData = {
            title: novelTitleInput.value,
            synopsis: synopsisTextarea.value,
            firstChapterTitle: novelTitleInput.value,
            firstChapterContent: contentTextarea.value,
            genres: Array.from(selectedGenres),
            visibility: 'PUBLIC'
        };
        toggleLoading(true, submitBtn, '소설 등록 중...');
        try {
            const result = await createNovelApi(novelData);
            alert(`소설이 성공적으로 생성되었습니다!`);
            window.location.href = `/novels/${result.novelId}`;
        } catch (error) {
            console.error('소설 생성 기능 오류:', error);
            alert(`오류가 발생했습니다: ${error.message}`);
        } finally {
            toggleLoading(false, submitBtn, '소설 생성하기');
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
        novelTitleInput.value = aiData.novelTitle || '';
        contentTextarea.value = aiData.firstChapterContent || '';
    }

    // --- 페이지 초기화 ---
    addGenreBtn.addEventListener('click', addSelectedGenre);
    aiHelpBtn.addEventListener('click', handleAiHelpClick);
    form.addEventListener('submit', handleFormSubmit);
    fetchGenres();
}

document.addEventListener('DOMContentLoaded', main);
