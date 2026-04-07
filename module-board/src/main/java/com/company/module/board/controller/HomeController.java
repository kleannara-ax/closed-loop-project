package com.company.module.board.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 루트 경로 및 헬스체크 컨트롤러
 * - 미리보기/프리뷰 접속 시 플랫폼 안내 페이지 제공
 * - /health 엔드포인트로 서비스 상태 확인
 */
@RestController
public class HomeController {

    @GetMapping(value = "/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "UP");
        result.put("application", "company-platform");
        result.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        return """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Company Platform</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                        background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #334155 100%);
                        color: #e2e8f0;
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }
                    .container {
                        max-width: 800px;
                        width: 90%;
                        padding: 48px;
                        background: rgba(30, 41, 59, 0.8);
                        border: 1px solid rgba(100, 116, 139, 0.3);
                        border-radius: 16px;
                        backdrop-filter: blur(10px);
                    }
                    .header {
                        display: flex;
                        align-items: center;
                        gap: 16px;
                        margin-bottom: 32px;
                    }
                    .logo {
                        width: 48px; height: 48px;
                        background: linear-gradient(135deg, #3b82f6, #8b5cf6);
                        border-radius: 12px;
                        display: flex; align-items: center; justify-content: center;
                        font-size: 24px; font-weight: bold; color: white;
                    }
                    h1 { font-size: 28px; font-weight: 700; color: #f1f5f9; }
                    .subtitle { color: #94a3b8; font-size: 14px; margin-top: 4px; }
                    .badge {
                        display: inline-flex; align-items: center; gap: 6px;
                        background: rgba(34, 197, 94, 0.15);
                        color: #4ade80;
                        padding: 4px 12px; border-radius: 20px;
                        font-size: 13px; font-weight: 500;
                        margin-bottom: 24px;
                    }
                    .badge::before {
                        content: '';
                        width: 8px; height: 8px;
                        background: #4ade80;
                        border-radius: 50%;
                        animation: pulse 2s infinite;
                    }
                    @keyframes pulse {
                        0%, 100% { opacity: 1; }
                        50% { opacity: 0.4; }
                    }
                    .section { margin-bottom: 28px; }
                    .section-title {
                        font-size: 13px; font-weight: 600;
                        color: #64748b; text-transform: uppercase;
                        letter-spacing: 1px; margin-bottom: 12px;
                    }
                    .module-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                        gap: 12px;
                    }
                    .module-card {
                        background: rgba(51, 65, 85, 0.5);
                        border: 1px solid rgba(100, 116, 139, 0.2);
                        border-radius: 10px;
                        padding: 16px;
                        transition: all 0.2s;
                    }
                    .module-card:hover {
                        border-color: rgba(59, 130, 246, 0.5);
                        background: rgba(51, 65, 85, 0.8);
                    }
                    .module-name {
                        font-size: 16px; font-weight: 600;
                        color: #f1f5f9; margin-bottom: 4px;
                    }
                    .module-desc { font-size: 13px; color: #94a3b8; margin-bottom: 10px; }
                    .module-prefix {
                        font-size: 12px; color: #60a5fa;
                        font-family: 'SF Mono', Monaco, monospace;
                        background: rgba(59, 130, 246, 0.1);
                        padding: 2px 8px; border-radius: 4px;
                    }
                    .api-list { list-style: none; }
                    .api-list li {
                        display: flex; align-items: center; gap: 10px;
                        padding: 10px 14px;
                        background: rgba(51, 65, 85, 0.4);
                        border-radius: 8px;
                        margin-bottom: 6px;
                        font-size: 14px;
                        transition: background 0.2s;
                        cursor: pointer;
                    }
                    .api-list li:hover { background: rgba(51, 65, 85, 0.8); }
                    .method {
                        font-size: 11px; font-weight: 700;
                        padding: 2px 8px; border-radius: 4px;
                        font-family: monospace;
                    }
                    .method.get { background: rgba(34, 197, 94, 0.2); color: #4ade80; }
                    .method.post { background: rgba(59, 130, 246, 0.2); color: #60a5fa; }
                    .method.put { background: rgba(245, 158, 11, 0.2); color: #fbbf24; }
                    .method.del { background: rgba(239, 68, 68, 0.2); color: #f87171; }
                    .path {
                        font-family: 'SF Mono', Monaco, monospace;
                        color: #cbd5e1; font-size: 13px;
                    }
                    .tech-stack {
                        display: flex; flex-wrap: wrap; gap: 8px;
                    }
                    .tech-tag {
                        background: rgba(100, 116, 139, 0.2);
                        border: 1px solid rgba(100, 116, 139, 0.3);
                        color: #94a3b8;
                        padding: 4px 12px; border-radius: 6px;
                        font-size: 13px;
                    }
                    .footer {
                        border-top: 1px solid rgba(100, 116, 139, 0.2);
                        padding-top: 20px; margin-top: 8px;
                        color: #64748b; font-size: 13px;
                        text-align: center;
                    }
                    a { color: #60a5fa; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                    #api-result {
                        background: rgba(15, 23, 42, 0.8);
                        border: 1px solid rgba(100, 116, 139, 0.3);
                        border-radius: 8px;
                        padding: 16px;
                        margin-top: 16px;
                        display: none;
                        font-family: 'SF Mono', Monaco, monospace;
                        font-size: 13px;
                        color: #4ade80;
                        max-height: 300px;
                        overflow-y: auto;
                        white-space: pre-wrap;
                        word-break: break-all;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">C</div>
                        <div>
                            <h1>Company Platform</h1>
                            <div class="subtitle">Gradle Multi-Module &middot; Spring Boot 3.x</div>
                        </div>
                    </div>

                    <div class="badge">Service Running</div>

                    <div class="section">
                        <div class="section-title">Modules</div>
                        <div class="module-grid">
                            <div class="module-card">
                                <div class="module-name">Core</div>
                                <div class="module-desc">Security, Exception, Logging</div>
                                <span class="module-prefix">com.company.core</span>
                            </div>
                            <div class="module-card">
                                <div class="module-name">Board</div>
                                <div class="module-desc">Bulletin Board CRUD</div>
                                <span class="module-prefix">/board-api/**</span>
                            </div>
                            <div class="module-card">
                                <div class="module-name">User</div>
                                <div class="module-desc">User Management</div>
                                <span class="module-prefix">/user-api/**</span>
                            </div>
                        </div>
                    </div>

                    <div class="section">
                        <div class="section-title">Board API Endpoints (click to test)</div>
                        <ul class="api-list">
                            <li onclick="callApi('GET', '/board-api/posts')">
                                <span class="method get">GET</span>
                                <span class="path">/board-api/posts</span>
                            </li>
                            <li onclick="callApi('GET', '/board-api/posts/1')">
                                <span class="method get">GET</span>
                                <span class="path">/board-api/posts/1</span>
                            </li>
                            <li onclick="callApi('GET', '/board-api/posts/1/comments')">
                                <span class="method get">GET</span>
                                <span class="path">/board-api/posts/1/comments</span>
                            </li>
                            <li onclick="callApi('GET', '/health')">
                                <span class="method get">GET</span>
                                <span class="path">/health</span>
                            </li>
                        </ul>
                        <div id="api-result"></div>
                    </div>

                    <div class="section">
                        <div class="section-title">Tech Stack</div>
                        <div class="tech-stack">
                            <span class="tech-tag">Spring Boot 3.2.5</span>
                            <span class="tech-tag">Java 17</span>
                            <span class="tech-tag">MariaDB</span>
                            <span class="tech-tag">Spring Security</span>
                            <span class="tech-tag">JPA / Hibernate</span>
                            <span class="tech-tag">Gradle 8.5</span>
                            <span class="tech-tag">Lombok</span>
                        </div>
                    </div>

                    <div class="footer">
                        Company Platform v1.0.0 &middot;
                        <a href="https://github.com/kleannara-ax/closed-loop-project" target="_blank">GitHub Repository</a>
                    </div>
                </div>

                <script>
                    async function callApi(method, path) {
                        const resultEl = document.getElementById('api-result');
                        resultEl.style.display = 'block';
                        resultEl.textContent = 'Loading...';
                        try {
                            const res = await fetch(path, { method });
                            const contentType = res.headers.get('content-type') || '';
                            let body;
                            if (contentType.includes('json')) {
                                body = JSON.stringify(await res.json(), null, 2);
                            } else {
                                body = await res.text();
                            }
                            resultEl.textContent = `${method} ${path}  [${res.status}]\\n\\n${body}`;
                            resultEl.style.color = res.ok ? '#4ade80' : '#f87171';
                        } catch (e) {
                            resultEl.textContent = `Error: ${e.message}`;
                            resultEl.style.color = '#f87171';
                        }
                    }
                </script>
            </body>
            </html>
            """;
    }
}
