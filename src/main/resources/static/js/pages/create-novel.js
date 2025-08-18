/**
 * @file '새 소설 쓰기' 페이지(create-novel.html)의 모든 UI 상호작용과
 *       API 연동을 처리하는 메인 JavaScript 파일입니다.
 * @description 장르 선택/추가, 'AI 와 함께쓰기', '소설 생성하기' 기능을 모두 포함합니다.
 *
 * @author 왕택준
 * @since 2025.08.18
 */

import { getGenres, recommendNovelApi, createNovelApi } from '../utils/api.js';

/**
 * 페이지의 모든 기능이 시작되는 메인 함수입니다.
 * DOM이 완전히 로드된 후에 호출됩니다.
 */
function main() {
    // --- DOM 요소 참조 ---
    const form = document.getElementById('createNovelForm');
    const novelTitleInput = document.getElementById('novel-title');
    const synopsisTextarea = document.getElementById('novel-synopsis');
    const genreSelect = document.getElementById('genre-select');
    const addGenreBtn = document.getElementById('add-genre-btn');
    const selectedGenresContainer = document.getElementById('selected-genres');
    const contentTextarea = document.getElementById('novel-content');
    const aiHelpBtn = document.querySelector('.btn-ai-help');
    const submitBtn = form.querySelector('button[type="submit"]');
    // HTML에 id="chapter-title" 요소가 없으므로, 이 변수는 null이 됩니다.
    // populateFormWithAiData 함수에서 null 체크 후 사용됩니다.
    const chapterTitleInput = document.getElementById('chapter-title');

    // --- 페이지 상태 관리 변수 ---
    let selectedGenres = new Set(); // 선택된 장르의 Enum 상수명(e.g., "FANTASY")을 저장하는 Set

    /**
     * 페이지 로드 시, 장르 목록 API를 호출하여 드롭다운 메뉴를 동적으로 생성합니다.
     * @async
     */
    async function fetchGenres() {
        try {
            const genres = await getGenres(); // API로부터 [{ code, description }, ...] 형태의 배열을 받습니다.

            genreSelect.innerHTML = '<option value="">-- 장르를 선택해주세요 --</option>'; // 드롭다운 초기화
            genres.forEach(genre => {
                const option = document.createElement('option');
                option.value = genre.code;              // <option>의 value에는 서버와 통신할 Enum 상수명("FANTASY")을 저장합니다.
                option.textContent = genre.description; // 사용자에게는 한글 설명("판타지")을 보여줍니다.
                genreSelect.appendChild(option);
            });
        } catch (error) {
            console.error('장르 불러오기 실패:', error);
            genreSelect.innerHTML = `<option value="">${error.message || '장르 로딩 실패'}</option>`;
        }
    }

    /**
     * 사용자가 드롭다운에서 선택한 장르를 화면에 태그 형태로 추가하는 함수입니다.
     */
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

        selectedGenres.add(enumName); // Set에 Enum 상수명을 추가하여 상태 관리

        const tag = document.createElement('span');
        tag.className = 'genre-tag';
        tag.textContent = description;
        const removeBtn = document.createElement('i');
        removeBtn.className = 'fas fa-times-circle remove-tag-btn';
        removeBtn.onclick = () => {
            selectedGenres.delete(enumName); // Set에서 해당 장르 제거
            tag.remove(); // 화면에서 태그 제거
        };
        tag.appendChild(removeBtn);
        selectedGenresContainer.appendChild(tag);
        genreSelect.selectedIndex = 0; // 선택 후 드롭다운을 기본값으로 되돌림
    }

    /**
     * 'AI 와 함께쓰기' 버튼의 클릭 이벤트 핸들러입니다.
     * @async
     * @param {Event} event - 클릭 이벤트 객체
     */
    async function handleAiHelpClick(event) {
        event.preventDefault();
        const synopsis = synopsisTextarea.value;
        const genreArray = Array.from(selectedGenres);
        const genre = genreArray.length > 0 ? genreArray[0] : ''; // AI 추천에는 선택된 장르 중 첫 번째 것만 사용

        if (!genre || !synopsis) {
            alert('AI의 도움을 받으려면 [장르]를 1개 이상 선택하고 [시놉시스]를 입력해야 합니다.');
            return;
        }

        toggleLoading(true, aiHelpBtn, 'AI 생각 중...');
        try {
            const response = await recommendNovelApi(genre, synopsis);
            populateFormWithAiData(response); // API 응답 데이터로 폼 채우기
            alert('AI 추천이 완료되었습니다!');
        } catch (error) {
            console.error('AI 추천 기능 오류:', error);
            alert(`오류가 발생했습니다: ${error.message}`);
        } finally {
            toggleLoading(false, aiHelpBtn, 'AI 와 함께쓰기');
        }
    }

    /**
     * '소설 생성하기' 폼의 제출(submit) 이벤트 핸들러입니다.
     * @async
     * @param {Event} event - 제출 이벤트 객체
     */
    async function handleFormSubmit(event) {
        event.preventDefault();

        if (selectedGenres.size === 0) {
            alert('장르를 1개 이상 선택해야 합니다.');
            return;
        }

        const novelData = {
            title: novelTitleInput.value,
            synopsis: synopsisTextarea.value,
            // [기획] 1화 제목은 소설의 전체 제목과 동일하게 설정합니다.
            firstChapterTitle: novelTitleInput.value,
            firstChapterContent: contentTextarea.value,
            genres: Array.from(selectedGenres), // Set을 배열로 변환하여 전송
            visibility: 'PUBLIC' // 기본 공개 범위는 PUBLIC
        };

        toggleLoading(true, submitBtn, '소설 등록 중...');
        try {
            const response = await createNovelApi(novelData);
            const resultData = response.data ? response.data : response;
            alert(`소설이 성공적으로 생성되었습니다!`);
            window.location.href = `/novels/${resultData.novelId}`; // 성공 시 생성된 소설 상세 페이지로 이동
        } catch (error) {
            console.error('소설 생성 기능 오류:', error);
            alert(`오류가 발생했습니다: ${error.message}`);
        } finally {
            toggleLoading(false, submitBtn, '소설 생성하기');
        }
    }

    /**
     * 버튼의 로딩 상태를 제어하고 UI를 업데이트하는 헬퍼 함수입니다.
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
     * AI 추천 API의 응답 데이터를 HTML 폼 필드에 채워넣는 헬퍼 함수입니다.
     * @param {object} aiData - AI가 생성한 추천 데이터.
     */
    function populateFormWithAiData(aiData) {
        if (!aiData) return;
        novelTitleInput.value = aiData.novelTitle || '';
        // 1화 제목 필드가 HTML에 존재할 경우에만 값을 채워 넣습니다.
        if (chapterTitleInput) {
            chapterTitleInput.value = aiData.firstChapterTitle || '';
        }
        contentTextarea.value = aiData.firstChapterContent || '';
    }

    // --- 페이지 초기화 및 이벤트 리스너 연결 ---
    addGenreBtn.addEventListener('click', addSelectedGenre);
    aiHelpBtn.addEventListener('click', handleAiHelpClick);
    form.addEventListener('submit', handleFormSubmit);
    fetchGenres(); // 페이지가 로드되면 즉시 장르 목록을 가져옵니다.
}

document.addEventListener('DOMContentLoaded', main);
