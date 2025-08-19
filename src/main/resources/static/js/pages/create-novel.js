/**
 * @file '새 소설 쓰기' 페이지(create-novel.html)의 모든 UI 상호작용과 API 연동을 처리하는 모듈입니다.
 * @module pages/create-novel
 * @author 왕택준
 * @since 2025.08.18
 */

import { getGenres, recommendNovelApi, createNovelApi } from '../utils/api.js';

/**
 * '새 소설 쓰기' 페이지의 모든 기능을 캡슐화하는 모듈 함수입니다.
 * @returns {{init: function}} 페이지 초기화를 위한 init 함수를 포함하는 객체.
 */
const createNovelPage = () => {

    // --- DOM 요소 참조 ---
    const form = document.getElementById('createNovelForm');
    const novelTitleInput = document.getElementById('novel-title');
    const synopsisTextarea = document.getElementById('novel-synopsis');
    const genreSelect = document.getElementById('genre-select');
    const addGenreBtn = document.getElementById('add-genre-btn');
    const selectedGenresContainer = document.getElementById('selected-genres');
    const contentTextarea = document.getElementById('novel-content');
    const aiHelpBtn = document.querySelector('.btn-ai-help');
    const submitBtn = form ? form.querySelector('button[type="submit"]') : null;
    /** @type {?HTMLElement} HTML에 존재하지 않을 수 있는 선택적 DOM 요소 */
    const chapterTitleInput = document.getElementById('chapter-title');

    // --- 페이지 상태 관리 ---
    /** @type {Set<string>} 선택된 장르의 Enum 상수명(e.g., "FANTASY")을 저장하는 Set */
    let selectedGenres = new Set();

    /**
     * 페이지 로드 시, 장르 목록 API를 호출하여 드롭다운 메뉴를 동적으로 생성합니다.
     * 서버로부터 장르 목록을 받아와 `<select>` 요소의 `<option>`으로 채웁니다.
     * @async
     */
    async function fetchGenres() {
        try {
            const genres = await getGenres();
            genreSelect.innerHTML = '<option value="">-- 장르를 선택해주세요 --</option>';
            genres.forEach(genre => {
                const option = document.createElement('option');
                option.value = genre.code;
                option.textContent = genre.description;
                genreSelect.appendChild(option);
            });
        } catch (error) {
            console.error('장르 불러오기 실패:', error);
            genreSelect.innerHTML = `<option value="">${error.message || '장르 로딩 실패'}</option>`;
        }
    }

    /**
     * 사용자가 드롭다운에서 선택한 장르를 화면에 태그 형태로 추가하고,
     * 내부 상태(`selectedGenres`)에 저장합니다.
     * 최대 3개의 장르만 선택 가능하며, 중복 선택은 방지됩니다.
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

        genreSelect.selectedIndex = 0;
    }

    /**
     * 'AI 와 함께쓰기' 버튼의 클릭 이벤트 핸들러입니다.
     * 현재 입력된 시놉시스와 선택된 장르를 기반으로 AI에게 소설 초안을 요청하고,
     * 그 결과를 현재 폼에 채워넣습니다.
     * @async
     * @param {Event} event - 클릭 이벤트 객체.
     */
    async function handleAiHelpClick(event) {
        event.preventDefault();
        const synopsis = synopsisTextarea.value;
        const genreArray = Array.from(selectedGenres);
        const genre = genreArray.length > 0 ? genreArray[0] : '';

        if (!genre || !synopsis) {
            alert('AI의 도움을 받으려면 [장르]를 1개 이상 선택하고 [시놉시스]를 입력해야 합니다.');
            return;
        }

        toggleLoading(true, aiHelpBtn, 'AI 생각 중...');
        try {
            const response = await recommendNovelApi(genre, synopsis);
            populateFormWithAiData(response);
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
     * 현재 폼에 입력된 모든 정보를 취합하여 소설 생성 API를 호출합니다.
     * 성공 시, 생성된 소설의 상세 페이지로 이동합니다.
     * @async
     * @param {Event} event - 폼 제출 이벤트 객체.
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
            firstChapterTitle: novelTitleInput.value,
            firstChapterContent: contentTextarea.value,
            genres: Array.from(selectedGenres),
            visibility: 'PUBLIC'
        };

        toggleLoading(true, submitBtn, '소설 등록 중...');
        try {
            const response = await createNovelApi(novelData);
            const resultData = response.data ? response.data : response;
            alert(`소설이 성공적으로 생성되었습니다!`);
            window.location.href = `/chapters?novelId=${resultData.novelId}`;
        } catch (error) {
            console.error('소설 생성 기능 오류:', error);
            alert(`오류가 발생했습니다: ${error.message}`);
        } finally {
            toggleLoading(false, submitBtn, '소설 생성하기');
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
     * AI 추천 API의 응답 데이터를 HTML 폼 필드에 채워넣는 헬퍼 함수입니다.
     * @param {object} aiData - AI가 생성한 추천 데이터.
     * @property {string} aiData.novelTitle - 추천 소설 제목.
     * @property {string} aiData.firstChapterTitle - 추천 1화 제목.
     * @property {string} aiData.firstChapterContent - 추천 1화 내용.
     */
    function populateFormWithAiData(aiData) {
        if (!aiData) return;
        novelTitleInput.value = aiData.novelTitle || '';
        if (chapterTitleInput) {
            chapterTitleInput.value = aiData.firstChapterTitle || '';
        }
        contentTextarea.value = aiData.firstChapterContent || '';
    }

    /**
     * 페이지의 모든 이벤트 리스너를 바인딩하고, 초기 데이터를 로드하는 초기화 함수입니다.
     * 이 함수는 app.js에 의해 호출되어 페이지 모듈의 실행을 시작합니다.
     */
    const init = () => {
        console.log("새 소설 쓰기 페이지 모듈 초기화");
        if (addGenreBtn) addGenreBtn.addEventListener('click', addSelectedGenre);
        if (aiHelpBtn) aiHelpBtn.addEventListener('click', handleAiHelpClick);
        if (form) form.addEventListener('submit', handleFormSubmit);
        fetchGenres();
    };

    return { init };
};

export default createNovelPage;
