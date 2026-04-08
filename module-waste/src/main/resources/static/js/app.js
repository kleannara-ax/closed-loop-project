/**
 * 폐기물 순환 추적 시스템 - 앱 라우터 & 초기화
 */
const App = {
    currentPage: 'dashboard',

    init() {
        // 해시 기반 라우팅
        window.addEventListener('hashchange', () => this.route());
        this.route();
    },

    route() {
        const hash = location.hash.slice(1) || 'dashboard';
        const [page, ...params] = hash.split('/');

        switch (page) {
            case 'dashboard':
                this.setPage('dashboard', '대시보드');
                Pages.dashboard();
                break;
            case 'list':
                this.setPage('list', '추적 목록');
                Pages.list();
                break;
            case 'create':
                this.setPage('create', '신규 등록');
                Pages.create();
                break;
            case 'detail':
                this.setPage('list', '추적 상세');
                Pages.detail(parseInt(params[0]));
                break;
            default:
                this.setPage('dashboard', '대시보드');
                Pages.dashboard();
        }
    },

    navigate(page) {
        location.hash = page;
    },

    showDetail(id) {
        location.hash = `detail/${id}`;
    },

    setPage(page, title) {
        this.currentPage = page;
        document.getElementById('pageTitle').textContent = title;

        // 네비게이션 활성화
        document.querySelectorAll('#sideNav .nav-item').forEach(el => {
            el.classList.toggle('active', el.dataset.page === page);
        });

        // 모바일 사이드바 닫기
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('overlay');
        sidebar.classList.add('-translate-x-full');
        overlay.classList.add('hidden');
    },

    closeModal() {
        document.getElementById('modal').classList.add('hidden');
    },

    toast(type, message) {
        // 기존 토스트 제거
        document.querySelectorAll('.toast').forEach(t => t.remove());

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `<i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'} mr-2"></i>${message}`;
        document.body.appendChild(toast);

        requestAnimationFrame(() => toast.classList.add('show'));
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
};

// 사이드바 토글
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('overlay');
    sidebar.classList.toggle('-translate-x-full');
    overlay.classList.toggle('hidden');
}

// 모달 외부 클릭 닫기
document.getElementById('modal').addEventListener('click', function(e) {
    if (e.target === this) App.closeModal();
});

// 앱 시작
App.init();
