/**
 * 폐기물 순환 추적 시스템 - API 통신 모듈
 */
const API = {
    BASE: '/waste-api',

    async request(method, path, body) {
        const opts = {
            method,
            headers: { 'Content-Type': 'application/json' }
        };
        if (body) opts.body = JSON.stringify(body);
        const res = await fetch(this.BASE + path, opts);
        if (!res.ok) {
            const err = await res.json().catch(() => ({ message: res.statusText }));
            throw new Error(err.message || `HTTP ${res.status}`);
        }
        return res.json();
    },

    // 추적 목록
    getTrackings(page = 0, size = 10, status, wasteType) {
        let q = `?page=${page}&size=${size}`;
        if (status) q += `&status=${status}`;
        if (wasteType) q += `&wasteType=${wasteType}`;
        return this.request('GET', '/trackings' + q);
    },

    // 추적 상세
    getTracking(id) {
        return this.request('GET', `/trackings/${id}`);
    },

    // 신규 등록
    createTracking(data) {
        return this.request('POST', '/trackings', data);
    },

    // 단계 진행
    advanceStage(data) {
        return this.request('POST', '/trackings/stage', data);
    },

    // 삭제
    deleteTracking(id) {
        return this.request('DELETE', `/trackings/${id}`);
    },

    // 대시보드
    getDashboard() {
        return this.request('GET', '/dashboard');
    },

    // 헬스
    getHealth() {
        return this.request('GET', '/health');
    }
};
