# SkillBridge AI
**Intelligent Internship Matching & Placement Optimization — DAA CIA 3**

A runnable full-stack reference implementation using:
- Java 21 + Spring Boot REST API
- Original Java implementations of Randomized Quickselect and Edmonds–Karp maximum flow
- React + Vite frontend
- Synthetic in-memory demo data (no database or API keys required)
- JUnit algorithm tests
- Docker Compose deployment

> Important academic note: the faculty brief requires teams of 2–3. Individual submissions require explicit faculty approval. This project is an implementation starting point; review, understand, test, and adapt it before submission. It does not guarantee a particular grade.

## What the algorithms actually do

1. **Randomized Quickselect (Unit 6 / CO5):** for each internship, selects the kth-largest compatibility score and returns a deterministic top-k shortlist. The selected shortlist is sorted for display; the full candidate list is not fully sorted to find the cutoff. Expected selection time is O(n), worst case O(n²), plus O(k log k) to order the selected results.
2. **Edmonds–Karp (Unit 5):** builds a source → student → internship → sink flow network. It maximizes the *number* of feasible placements subject to one placement per student, eligibility, and seat capacity. Complexity O(VE²).

**Important limitation:** maximum flow maximizes placement count, not total compatibility score. Quickselect limits the candidate edges considered for each internship. The allocation is optimal only for that constructed shortlist graph, not necessarily for all possible students or maximum total score. Increase `topKPerInternship` to include more candidates.

Compatibility score (0–100):
- Skill overlap: 70 points × matched required skills / required skills
- Explicit internship preference: 20 points
- Academic score: 10 points × CGPA / 10

Minimum CGPA is a hard eligibility constraint. Skill overlap is scored, not a hard all-skills requirement.

## Requirements

- Java 21+
- Maven 3.9+
- Node.js 20+
- npm 10+
- Docker + Docker Compose (optional deployment route)

## Run locally

### 1. Backend

```bash
cd backend
mvn test
mvn spring-boot:run
```

Backend: http://localhost:8080  
Health: http://localhost:8080/actuator/health  
API docs are described below; no Swagger dependency is needed.

### 2. Frontend

In another terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend: http://localhost:5173

The Vite development server proxies `/api` to `http://localhost:8080`.

## Run the full stack with Docker

From the repository root:

```bash
docker compose up --build
```

Open http://localhost:8088

The frontend container serves the built React app and reverse-proxies `/api` to the backend container. This is the simplest single-origin deployment.

Stop:

```bash
docker compose down
```

## API

### `GET /api/dashboard`
Returns counts and a short product description.

### `GET /api/students`
Returns demo students.

### `GET /api/internships`
Returns demo internships.

### `POST /api/matching/shortlist`
Request:

```json
{
  "internshipId": "INT-101",
  "topK": 5
}
```

Returns eligible candidates ranked by compatibility score and the Quickselect threshold.

### `POST /api/matching/allocate`
Request:

```json
{
  "topKPerInternship": 20
}
```

Returns maximum-cardinality feasible placements from the shortlisted candidate graph, unmatched students, and unfilled seats.

### `POST /api/benchmark`
Runs reproducible synthetic experiments at 100, 500, and 1000 candidates. Reports measured times for Quickselect vs sorting and Edmonds–Karp vs greedy allocation. Timings vary by machine and JVM warm-up; do not present one run as a universal performance claim.

## Deployment

### Docker-capable host / VPS

1. Push this folder to a Git repository.
2. Install Docker Engine and Docker Compose on the host.
3. Clone the repository.
4. Run `docker compose up --build -d`.
5. Open port `8088` (or change the host-side port mapping in `docker-compose.yml`).
6. For a public production deployment, put HTTPS/TLS and a domain in front of the service using your host's reverse proxy.

### Render (separate services)

A `render.yaml` blueprint is included as a starting point. The frontend's API base URL is configured for `https://skillbridge-api.onrender.com`; if Render assigns a different backend URL, update `VITE_API_BASE_URL` in the frontend Static Site environment and redeploy the frontend. The backend allows browser requests from any origin in this demo; restrict CORS to your deployed frontend domain before production use.

## Data and persistence

The backend starts with synthetic demo records on each restart. Data added in this demo is not persisted. No real student personal data is included. Before using real data, add authentication, authorization, secure persistence, privacy controls, audit logs, and institution approval.

## Tests and evidence

```bash
cd backend
mvn test
```

The test suite includes boundary and duplicate-value cases for Quickselect and a known max-flow example for Edmonds–Karp. Add at least five documented correctness cases per assessed algorithm and capture benchmark results for your final report, as required by your course brief.

## Repository structure

```text
SkillBridge-AI/
├── backend/                 # Spring Boot + Java algorithms + JUnit
├── frontend/                # React UI
├── docker-compose.yml
├── render.yaml
└── README.md
```

## Academic integrity / viva preparation

Do not submit code you cannot explain. Be ready to explain:
- Quickselect partition invariant, randomized pivot, expected vs worst-case complexity.
- Flow network construction, residual edges, BFS augmenting paths, and why Edmonds–Karp is O(VE²).
- Why max-flow maximizes placement count but not total score.
- How ties, empty lists, invalid capacities, ineligible students, and impossible allocations are handled.
