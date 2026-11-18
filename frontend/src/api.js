const API_BASE = import.meta.env.VITE_API_BASE_URL || ''

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  })
  const body = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(body.error || `Request failed (${response.status})`)
  return body
}

export const api = {
  dashboard: () => request('/api/dashboard'),
  students: () => request('/api/students'),
  internships: () => request('/api/internships'),
  allocate: (topKPerInternship = 20) => request('/api/matching/allocate', {
    method: 'POST', body: JSON.stringify({ topKPerInternship })
  }),
  shortlist: (internshipId, topK = 5) => request('/api/matching/shortlist', {
    method: 'POST', body: JSON.stringify({ internshipId, topK })
  }),
  benchmark: () => request('/api/benchmark', { method: 'POST', body: '{}' })
}
