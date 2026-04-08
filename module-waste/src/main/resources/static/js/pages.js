/**
 * 폐기물 순환 추적 시스템 - 페이지 렌더링 모듈
 */

const STATUS_MAP = {
    discharge:   { label: '배출',   icon: 'fa-box-open',     bg: '#fef3c7', color: '#92400e', dot: '#f59e0b' },
    collection:  { label: '수거',   icon: 'fa-truck',        bg: '#dbeafe', color: '#1e40af', dot: '#3b82f6' },
    compression: { label: '압축',   icon: 'fa-compress-alt', bg: '#e0e7ff', color: '#3730a3', dot: '#6366f1' },
    recycling:   { label: '재활용', icon: 'fa-recycle',      bg: '#d1fae5', color: '#065f46', dot: '#10b981' },
    production:  { label: '생산',   icon: 'fa-industry',     bg: '#cffafe', color: '#155e75', dot: '#06b6d4' },
    completed:   { label: '완료',   icon: 'fa-check-circle', bg: '#dcfce7', color: '#166534', dot: '#22c55e' }
};

const STAGE_ORDER = ['discharge','collection','compression','recycling','production','completed'];

const WASTE_TYPES = ['paper','vinyl','general','metal','glass','plastic','food','wood'];

function statusBadge(status) {
    const s = STATUS_MAP[status] || { label: status, bg: '#f1f5f9', color: '#475569' };
    return `<span class="status-badge" style="background:${s.bg};color:${s.color}">
        <i class="fas ${s.icon} mr-1" style="font-size:10px"></i>${s.label}
    </span>`;
}

function formatDate(dt) {
    if (!dt) return '-';
    const d = new Date(dt);
    return d.toLocaleDateString('ko-KR', { year:'numeric', month:'2-digit', day:'2-digit' })
        + ' ' + d.toLocaleTimeString('ko-KR', { hour:'2-digit', minute:'2-digit' });
}

function formatWeight(w) {
    if (w == null) return '-';
    return Number(w).toLocaleString('ko-KR', { minimumFractionDigits: 1, maximumFractionDigits: 2 }) + ' kg';
}

// ==================================
// 대시보드 페이지
// ==================================
const Pages = {

async dashboard() {
    const el = document.getElementById('pageContent');
    el.innerHTML = '<div class="flex justify-center py-20"><div class="spinner"></div></div>';

    try {
        const [dash, trackings] = await Promise.all([
            API.getDashboard(),
            API.getTrackings(0, 5)
        ]);

        const completionRate = dash.totalTracking > 0
            ? Math.round((dash.completedTracking / dash.totalTracking) * 100) : 0;

        el.innerHTML = `
        <!-- 통계 카드 -->
        <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4 mb-8">
            <div class="stat-card">
                <div class="flex items-center justify-between mb-3">
                    <span class="text-sm font-medium text-gray-500">전체 추적</span>
                    <div class="w-10 h-10 rounded-xl flex items-center justify-center" style="background:#e0f2fe">
                        <i class="fas fa-clipboard-list" style="color:#0284c7"></i>
                    </div>
                </div>
                <div class="text-3xl font-bold text-gray-800">${dash.totalTracking}</div>
                <div class="text-xs text-gray-400 mt-1">건</div>
            </div>
            <div class="stat-card">
                <div class="flex items-center justify-between mb-3">
                    <span class="text-sm font-medium text-gray-500">진행중</span>
                    <div class="w-10 h-10 rounded-xl flex items-center justify-center" style="background:#fef3c7">
                        <i class="fas fa-spinner" style="color:#d97706"></i>
                    </div>
                </div>
                <div class="text-3xl font-bold text-gray-800">${dash.activeTracking}</div>
                <div class="text-xs text-gray-400 mt-1">건</div>
            </div>
            <div class="stat-card">
                <div class="flex items-center justify-between mb-3">
                    <span class="text-sm font-medium text-gray-500">완료</span>
                    <div class="w-10 h-10 rounded-xl flex items-center justify-center" style="background:#dcfce7">
                        <i class="fas fa-check-circle" style="color:#16a34a"></i>
                    </div>
                </div>
                <div class="text-3xl font-bold text-gray-800">${dash.completedTracking}</div>
                <div class="text-xs text-gray-400 mt-1">건</div>
            </div>
            <div class="stat-card">
                <div class="flex items-center justify-between mb-3">
                    <span class="text-sm font-medium text-gray-500">총 중량</span>
                    <div class="w-10 h-10 rounded-xl flex items-center justify-center" style="background:#e0e7ff">
                        <i class="fas fa-weight-hanging" style="color:#4f46e5"></i>
                    </div>
                </div>
                <div class="text-3xl font-bold text-gray-800">${formatWeight(dash.totalWeightKg)}</div>
            </div>
        </div>

        <!-- 차트 + 완료율 -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
            <div class="stat-card">
                <h3 class="text-sm font-semibold text-gray-600 mb-4">처리 완료율</h3>
                <div class="flex items-center gap-6">
                    <div class="relative w-28 h-28">
                        <canvas id="completionChart"></canvas>
                        <div class="absolute inset-0 flex items-center justify-center">
                            <span class="text-2xl font-bold text-gray-800">${completionRate}%</span>
                        </div>
                    </div>
                    <div class="flex-1">
                        <div class="space-y-3">
                            <div>
                                <div class="flex justify-between text-sm mb-1">
                                    <span class="text-gray-500">완료</span>
                                    <span class="font-semibold text-gray-700">${dash.completedTracking}건</span>
                                </div>
                                <div class="progress-bar">
                                    <div class="progress-fill" style="width:${completionRate}%"></div>
                                </div>
                            </div>
                            <div>
                                <div class="flex justify-between text-sm mb-1">
                                    <span class="text-gray-500">진행중</span>
                                    <span class="font-semibold text-gray-700">${dash.activeTracking}건</span>
                                </div>
                                <div class="progress-bar">
                                    <div class="progress-fill" style="width:${100 - completionRate}%;background:linear-gradient(90deg,#f59e0b,#f97316)"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="stat-card">
                <h3 class="text-sm font-semibold text-gray-600 mb-4">상태 분포</h3>
                <canvas id="statusChart" height="140"></canvas>
            </div>
        </div>

        <!-- 최근 추적 -->
        <div class="stat-card">
            <div class="flex items-center justify-between mb-4">
                <h3 class="text-sm font-semibold text-gray-600">최근 추적</h3>
                <button class="btn btn-outline btn-sm" onclick="App.navigate('list')">
                    전체보기 <i class="fas fa-arrow-right ml-1" style="font-size:10px"></i>
                </button>
            </div>
            <div class="overflow-x-auto">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>추적코드</th>
                            <th>폐기물 유형</th>
                            <th>상태</th>
                            <th>중량</th>
                            <th>등록일</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        ${trackings.content.map(t => `
                        <tr>
                            <td class="font-mono text-sm font-medium text-primary-700">${t.trackingCode}</td>
                            <td>${t.wasteType}</td>
                            <td>${statusBadge(t.status)}</td>
                            <td class="font-medium">${formatWeight(t.weightKg)}</td>
                            <td class="text-sm text-gray-500">${formatDate(t.createdAt)}</td>
                            <td>
                                <button class="btn btn-outline btn-sm" onclick="App.showDetail(${t.trackingId})">
                                    <i class="fas fa-eye"></i>
                                </button>
                            </td>
                        </tr>`).join('')}
                    </tbody>
                </table>
            </div>
        </div>`;

        // 차트 렌더링
        renderCompletionChart(completionRate);
        renderStatusChart(trackings.content);
    } catch (e) {
        el.innerHTML = `<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><p>데이터를 불러올 수 없습니다: ${e.message}</p></div>`;
    }
},

// ==================================
// 추적 목록 페이지
// ==================================
async list() {
    const el = document.getElementById('pageContent');
    el.innerHTML = '<div class="flex justify-center py-20"><div class="spinner"></div></div>';

    try {
        const data = await API.getTrackings(0, 50);
        const items = data.content;

        el.innerHTML = `
        <div class="stat-card mb-6">
            <div class="flex flex-wrap items-center justify-between gap-4 mb-6">
                <div class="flex items-center gap-3">
                    <h3 class="text-lg font-semibold text-gray-800">추적 목록</h3>
                    <span class="text-sm text-gray-400">(${data.totalElements}건)</span>
                </div>
                <div class="flex flex-wrap items-center gap-2">
                    <select id="filterStatus" class="form-input" style="width:auto;padding:6px 12px;font-size:13px" onchange="Pages.filterList()">
                        <option value="">전체 상태</option>
                        ${STAGE_ORDER.map(s => `<option value="${s}">${STATUS_MAP[s].label}</option>`).join('')}
                    </select>
                    <select id="filterType" class="form-input" style="width:auto;padding:6px 12px;font-size:13px" onchange="Pages.filterList()">
                        <option value="">전체 유형</option>
                        ${WASTE_TYPES.map(t => `<option value="${t}">${t}</option>`).join('')}
                    </select>
                    <button class="btn btn-primary btn-sm" onclick="App.navigate('create')">
                        <i class="fas fa-plus"></i> 신규 등록
                    </button>
                </div>
            </div>
            <div class="overflow-x-auto" id="trackingTable">
                ${renderTrackingTable(items)}
            </div>
        </div>`;
    } catch (e) {
        el.innerHTML = `<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><p>${e.message}</p></div>`;
    }
},

async filterList() {
    const status = document.getElementById('filterStatus').value;
    const wasteType = document.getElementById('filterType').value;
    const tableEl = document.getElementById('trackingTable');
    if (!tableEl) return;
    tableEl.innerHTML = '<div class="flex justify-center py-10"><div class="spinner"></div></div>';
    try {
        const data = await API.getTrackings(0, 50, status, wasteType);
        tableEl.innerHTML = renderTrackingTable(data.content);
    } catch(e) {
        tableEl.innerHTML = `<p class="text-red-500 py-4">${e.message}</p>`;
    }
},

// ==================================
// 상세 페이지
// ==================================
async detail(id) {
    const el = document.getElementById('pageContent');
    el.innerHTML = '<div class="flex justify-center py-20"><div class="spinner"></div></div>';

    try {
        const d = await API.getTracking(id);
        const currentIdx = STAGE_ORDER.indexOf(d.status);
        const nextStage = (d.status !== 'completed' && currentIdx < STAGE_ORDER.length - 1)
            ? STAGE_ORDER[currentIdx + 1] : null;

        el.innerHTML = `
        <div class="mb-4">
            <button class="btn btn-outline btn-sm" onclick="App.navigate('list')">
                <i class="fas fa-arrow-left"></i> 목록으로
            </button>
        </div>

        <!-- 기본 정보 -->
        <div class="stat-card mb-6">
            <div class="flex flex-wrap items-start justify-between gap-4 mb-6">
                <div>
                    <div class="flex items-center gap-3 mb-2">
                        <h3 class="text-xl font-bold text-gray-800">${d.trackingCode}</h3>
                        ${statusBadge(d.status)}
                    </div>
                    <p class="text-sm text-gray-400">ID: ${d.trackingId}</p>
                </div>
                <div class="flex gap-2">
                    ${nextStage ? `<button class="btn btn-success btn-sm" onclick="Pages.showStageModal(${d.trackingId}, '${nextStage}')">
                        <i class="fas fa-arrow-right"></i> ${STATUS_MAP[nextStage].label} 단계로
                    </button>` : ''}
                    ${d.status !== 'completed' ? `<button class="btn btn-danger btn-sm" onclick="Pages.confirmDelete(${d.trackingId})">
                        <i class="fas fa-trash"></i>
                    </button>` : ''}
                </div>
            </div>

            <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
                <div><span class="text-xs text-gray-400 block">폐기물 유형</span><span class="font-semibold text-gray-700">${d.wasteType}</span></div>
                <div><span class="text-xs text-gray-400 block">중량</span><span class="font-semibold text-gray-700">${formatWeight(d.weightKg)}</span></div>
                <div><span class="text-xs text-gray-400 block">처리센터</span><span class="font-semibold text-gray-700">${d.centerName || '-'}</span></div>
                <div><span class="text-xs text-gray-400 block">등록일</span><span class="font-semibold text-gray-700">${formatDate(d.createdAt)}</span></div>
            </div>
        </div>

        <!-- 진행 단계 -->
        <div class="stat-card mb-6">
            <h3 class="text-sm font-semibold text-gray-600 mb-4">처리 단계</h3>
            <div class="flex items-center gap-2 overflow-x-auto pb-2">
                ${STAGE_ORDER.map((stage, i) => {
                    const si = STATUS_MAP[stage];
                    const done = STAGE_ORDER.indexOf(d.status) >= i || d.status === 'completed';
                    return `<div class="flex items-center gap-2 flex-shrink-0">
                        <div class="flex flex-col items-center">
                            <div class="w-10 h-10 rounded-full flex items-center justify-center text-sm"
                                 style="background:${done ? si.dot : '#e2e8f0'};color:${done ? '#fff' : '#94a3b8'}">
                                <i class="fas ${si.icon}"></i>
                            </div>
                            <span class="text-xs mt-1 font-medium" style="color:${done ? si.color : '#94a3b8'}">${si.label}</span>
                        </div>
                        ${i < STAGE_ORDER.length - 1 ? `<div class="w-8 h-0.5 mt-[-16px]" style="background:${done && STAGE_ORDER.indexOf(d.status) > i ? si.dot : '#e2e8f0'}"></div>` : ''}
                    </div>`;
                }).join('')}
            </div>
        </div>

        <!-- 타임라인 -->
        <div class="stat-card">
            <h3 class="text-sm font-semibold text-gray-600 mb-4">처리 이력</h3>
            ${d.stages && d.stages.length > 0 ? `
            <div class="space-y-0">
                ${d.stages.map(s => {
                    const si = STATUS_MAP[s.stageType] || { label: s.stageType, dot: '#94a3b8', icon: 'fa-circle' };
                    return `
                    <div class="timeline-item">
                        <div class="timeline-dot" style="background:${si.dot}">
                            <i class="fas ${si.icon}"></i>
                        </div>
                        <div class="bg-gray-50 rounded-xl p-4">
                            <div class="flex flex-wrap items-center justify-between gap-2 mb-2">
                                <span class="font-semibold text-gray-700">${si.label}</span>
                                <span class="text-xs text-gray-400">${formatDate(s.processedAt)}</span>
                            </div>
                            <div class="grid grid-cols-2 sm:grid-cols-4 gap-3 text-sm">
                                <div><span class="text-gray-400">투입</span><br><span class="font-medium">${formatWeight(s.inputWeightKg)}</span></div>
                                <div><span class="text-gray-400">산출</span><br><span class="font-medium">${formatWeight(s.outputWeightKg)}</span></div>
                                <div><span class="text-gray-400">작업자</span><br><span class="font-medium">${s.workerName || '-'}</span></div>
                                <div><span class="text-gray-400">센터</span><br><span class="font-medium">${s.centerName || '-'}</span></div>
                            </div>
                            ${s.memo ? `<p class="text-sm text-gray-500 mt-2"><i class="fas fa-comment-dots mr-1"></i>${s.memo}</p>` : ''}
                        </div>
                    </div>`;
                }).join('')}
            </div>` : '<div class="empty-state"><i class="fas fa-history"></i><p>처리 이력이 없습니다</p></div>'}
        </div>`;
    } catch (e) {
        el.innerHTML = `<div class="empty-state"><i class="fas fa-exclamation-triangle"></i><p>${e.message}</p></div>`;
    }
},

// ==================================
// 신규 등록 페이지
// ==================================
create() {
    const el = document.getElementById('pageContent');
    el.innerHTML = `
    <div class="max-w-2xl mx-auto">
        <div class="stat-card">
            <h3 class="text-lg font-bold text-gray-800 mb-6">
                <i class="fas fa-plus-circle text-primary-500 mr-2"></i>신규 폐기물 등록
            </h3>
            <form id="createForm" onsubmit="Pages.submitCreate(event)" class="space-y-5">
                <div>
                    <label class="form-label">폐기물 유형 *</label>
                    <select name="wasteType" class="form-input" required>
                        <option value="">선택하세요</option>
                        ${WASTE_TYPES.map(t => `<option value="${t}">${t}</option>`).join('')}
                    </select>
                </div>
                <div>
                    <label class="form-label">중량 (kg) *</label>
                    <input type="number" name="weightKg" class="form-input" step="0.01" min="0.01" required placeholder="예: 1500.50">
                </div>
                <div>
                    <label class="form-label">처리센터</label>
                    <input type="text" name="centerName" class="form-input" placeholder="센터명 입력">
                </div>
                <div>
                    <label class="form-label">메모</label>
                    <textarea name="memo" class="form-input" rows="3" placeholder="비고 사항"></textarea>
                </div>
                <div class="flex gap-3 pt-2">
                    <button type="submit" class="btn btn-primary flex-1" id="submitBtn">
                        <i class="fas fa-check"></i> 등록
                    </button>
                    <button type="button" class="btn btn-outline" onclick="App.navigate('list')">취소</button>
                </div>
            </form>
        </div>
    </div>`;
},

async submitCreate(e) {
    e.preventDefault();
    const form = e.target;
    const btn = document.getElementById('submitBtn');
    btn.disabled = true;
    btn.innerHTML = '<div class="spinner"></div> 등록중...';

    try {
        const data = {
            wasteType: form.wasteType.value,
            weightKg: parseFloat(form.weightKg.value),
            centerName: form.centerName.value || null,
            memo: form.memo.value || null
        };
        const result = await API.createTracking(data);
        App.toast('success', `추적 ${result.trackingCode} 등록 완료!`);
        App.navigate('list');
    } catch (e) {
        App.toast('error', e.message);
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-check"></i> 등록';
    }
},

// ==================================
// 단계 진행 모달
// ==================================
showStageModal(trackingId, stageType) {
    const si = STATUS_MAP[stageType];
    const modal = document.getElementById('modal');
    const content = document.getElementById('modalContent');
    content.innerHTML = `
    <div class="p-6">
        <div class="flex items-center justify-between mb-6">
            <h3 class="text-lg font-bold text-gray-800">
                <i class="fas ${si.icon} mr-2" style="color:${si.dot}"></i>${si.label} 단계 진행
            </h3>
            <button onclick="App.closeModal()" class="text-gray-400 hover:text-gray-600">
                <i class="fas fa-times text-xl"></i>
            </button>
        </div>
        <form id="stageForm" onsubmit="Pages.submitStage(event, ${trackingId}, '${stageType}')" class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <label class="form-label">투입 중량 (kg)</label>
                    <input type="number" name="inputWeightKg" class="form-input" step="0.01" min="0">
                </div>
                <div>
                    <label class="form-label">산출 중량 (kg)</label>
                    <input type="number" name="outputWeightKg" class="form-input" step="0.01" min="0">
                </div>
            </div>
            <div>
                <label class="form-label">처리센터</label>
                <input type="text" name="centerName" class="form-input">
            </div>
            <div>
                <label class="form-label">작업자</label>
                <input type="text" name="workerName" class="form-input">
            </div>
            <div>
                <label class="form-label">메모</label>
                <textarea name="memo" class="form-input" rows="2"></textarea>
            </div>
            <div class="flex gap-3 pt-2">
                <button type="submit" class="btn btn-success flex-1" id="stageBtn">
                    <i class="fas fa-arrow-right"></i> ${si.label} 단계 진행
                </button>
                <button type="button" class="btn btn-outline" onclick="App.closeModal()">취소</button>
            </div>
        </form>
    </div>`;
    modal.classList.remove('hidden');
},

async submitStage(e, trackingId, stageType) {
    e.preventDefault();
    const form = e.target;
    const btn = document.getElementById('stageBtn');
    btn.disabled = true;
    btn.innerHTML = '<div class="spinner"></div>';

    try {
        const data = {
            trackingId,
            stageType,
            inputWeightKg: form.inputWeightKg.value ? parseFloat(form.inputWeightKg.value) : null,
            outputWeightKg: form.outputWeightKg.value ? parseFloat(form.outputWeightKg.value) : null,
            centerName: form.centerName.value || null,
            workerName: form.workerName.value || null,
            memo: form.memo.value || null
        };
        await API.advanceStage(data);
        App.closeModal();
        App.toast('success', `${STATUS_MAP[stageType].label} 단계 진행 완료!`);
        Pages.detail(trackingId);
    } catch (e) {
        App.toast('error', e.message);
        btn.disabled = false;
        btn.innerHTML = `<i class="fas fa-arrow-right"></i> 진행`;
    }
},

// ==================================
// 삭제 확인
// ==================================
confirmDelete(id) {
    const modal = document.getElementById('modal');
    const content = document.getElementById('modalContent');
    content.innerHTML = `
    <div class="p-6 text-center">
        <div class="w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4" style="background:#fee2e2">
            <i class="fas fa-exclamation-triangle text-2xl" style="color:#dc2626"></i>
        </div>
        <h3 class="text-lg font-bold text-gray-800 mb-2">삭제 확인</h3>
        <p class="text-sm text-gray-500 mb-6">추적 ID ${id}를 삭제하시겠습니까?<br>이 작업은 되돌릴 수 없습니다.</p>
        <div class="flex gap-3 justify-center">
            <button class="btn btn-danger" onclick="Pages.doDelete(${id})">
                <i class="fas fa-trash"></i> 삭제
            </button>
            <button class="btn btn-outline" onclick="App.closeModal()">취소</button>
        </div>
    </div>`;
    modal.classList.remove('hidden');
},

async doDelete(id) {
    try {
        await API.deleteTracking(id);
        App.closeModal();
        App.toast('success', '삭제 완료');
        App.navigate('list');
    } catch (e) {
        App.toast('error', e.message);
    }
}

}; // end Pages

// ==================================
// 헬퍼 함수
// ==================================
function renderTrackingTable(items) {
    if (!items || items.length === 0) {
        return '<div class="empty-state"><i class="fas fa-inbox"></i><p>데이터가 없습니다</p></div>';
    }
    return `
    <table class="data-table">
        <thead>
            <tr>
                <th>추적코드</th>
                <th>유형</th>
                <th>상태</th>
                <th>중량</th>
                <th>처리센터</th>
                <th>등록일</th>
                <th>관리</th>
            </tr>
        </thead>
        <tbody>
            ${items.map(t => `
            <tr>
                <td class="font-mono text-sm font-medium" style="color:#0369a1">${t.trackingCode}</td>
                <td>${t.wasteType}</td>
                <td>${statusBadge(t.status)}</td>
                <td class="font-medium">${formatWeight(t.weightKg)}</td>
                <td class="text-sm text-gray-500">${t.centerName || '-'}</td>
                <td class="text-sm text-gray-500">${formatDate(t.createdAt)}</td>
                <td>
                    <div class="flex gap-1">
                        <button class="btn btn-outline btn-sm" onclick="App.showDetail(${t.trackingId})" title="상세">
                            <i class="fas fa-eye"></i>
                        </button>
                        ${t.status !== 'completed' ? `<button class="btn btn-sm" style="background:#fee2e2;color:#dc2626" onclick="Pages.confirmDelete(${t.trackingId})" title="삭제">
                            <i class="fas fa-trash"></i>
                        </button>` : ''}
                    </div>
                </td>
            </tr>`).join('')}
        </tbody>
    </table>`;
}

function renderCompletionChart(rate) {
    const ctx = document.getElementById('completionChart');
    if (!ctx) return;
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            datasets: [{
                data: [rate, 100 - rate],
                backgroundColor: ['#22c55e', '#e2e8f0'],
                borderWidth: 0
            }]
        },
        options: {
            cutout: '80%',
            responsive: true,
            maintainAspectRatio: true,
            plugins: { legend: { display: false }, tooltip: { enabled: false } }
        }
    });
}

function renderStatusChart(items) {
    const ctx = document.getElementById('statusChart');
    if (!ctx) return;
    const counts = {};
    items.forEach(t => { counts[t.status] = (counts[t.status] || 0) + 1; });
    const labels = Object.keys(counts).map(k => (STATUS_MAP[k] || {}).label || k);
    const data = Object.values(counts);
    const colors = Object.keys(counts).map(k => (STATUS_MAP[k] || {}).dot || '#94a3b8');

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{ data, backgroundColor: colors, borderRadius: 8, barThickness: 32 }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { beginAtZero: true, ticks: { stepSize: 1 }, grid: { color: '#f1f5f9' } },
                x: { grid: { display: false } }
            }
        }
    });
}
