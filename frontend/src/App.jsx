import { useEffect, useMemo, useState } from 'react'
import {
  Activity, ArrowDownRight, ArrowUpRight, Award, BarChart3, BriefcaseBusiness,
  CheckCircle2, ChevronDown, CircleHelp, Clock3, Code2, Database, Download,
  Filter, GraduationCap, Layers3, Network, Play, RefreshCw, Search, ShieldCheck,
  Sparkles, Users, XCircle
} from 'lucide-react'
import { api } from './api'

const money = (n) => Number(n || 0).toLocaleString('en-IN')
const scoreClass = (score) => score >= 80 ? 'high' : score >= 60 ? 'medium' : 'low'

function StatCard({ icon: Icon, label, value, foot, tone = 'blue' }) {
  return <div className="stat-card">
    <div className={`stat-icon ${tone}`}><Icon size={19} /></div>
    <div className="stat-label">{label}</div>
    <div className="stat-value">{value ?? '—'}</div>
    <div className="stat-foot">{foot}</div>
  </div>
}

function App() {
  const [dashboard, setDashboard] = useState(null)
  const [students, setStudents] = useState([])
  const [internships, setInternships] = useState([])
  const [allocation, setAllocation] = useState(null)
  const [benchmark, setBenchmark] = useState(null)
  const [shortlist, setShortlist] = useState(null)
  const [selectedInternship, setSelectedInternship] = useState('')
  const [topK, setTopK] = useState(20)
  const [loading, setLoading] = useState(false)
  const [benchmarkLoading, setBenchmarkLoading] = useState(false)
  const [error, setError] = useState('')
  const [activeTab, setActiveTab] = useState('overview')
  const [query, setQuery] = useState('')

  async function loadData() {
    setError('')
    try {
      const [d, s, i] = await Promise.all([api.dashboard(), api.students(), api.internships()])
      setDashboard(d); setStudents(s); setInternships(i)
      if (!selectedInternship && i.length) setSelectedInternship(i[0].id)
    } catch (e) {
      setError(`${e.message}. Is the Java backend running?`)
    }
  }

  useEffect(() => { loadData() }, [])

  async function runAllocation() {
    setLoading(true); setError('')
    try {
      const result = await api.allocate(Number(topK))
      setAllocation(result); setActiveTab('allocations')
    } catch (e) { setError(e.message) }
    finally { setLoading(false) }
  }

  async function runShortlist(internshipId = selectedInternship) {
    if (!internshipId) return
    setLoading(true); setError('')
    try {
      setShortlist(await api.shortlist(internshipId, 5))
      setActiveTab('shortlist')
    } catch (e) { setError(e.message) }
    finally { setLoading(false) }
  }

  async function runBenchmark() {
    setBenchmarkLoading(true); setError('')
    try { setBenchmark(await api.benchmark()); setActiveTab('benchmark') }
    catch (e) { setError(e.message) }
    finally { setBenchmarkLoading(false) }
  }

  const filteredStudents = useMemo(() => students.filter(s =>
    `${s.name} ${s.id} ${s.skills.join(' ')}`.toLowerCase().includes(query.toLowerCase())
  ), [students, query])

  const placedRate = dashboard?.totalSeats
    ? Math.round(((allocation?.placements || 0) / dashboard.totalSeats) * 100) : 0

  return <div className="app-shell">
    <aside className="sidebar">
      <div className="brand">
        <div className="brand-mark"><Layers3 size={21} /></div>
        <div><strong>skillbridge<span>.</span></strong><small>PLACEMENT INTELLIGENCE</small></div>
      </div>
      <div className="workspace-label">WORKSPACE</div>
      <nav>
        <button className={activeTab === 'overview' ? 'nav-item active' : 'nav-item'} onClick={() => setActiveTab('overview')}><BarChart3 size={17}/> Overview</button>
        <button className={activeTab === 'students' ? 'nav-item active' : 'nav-item'} onClick={() => setActiveTab('students')}><Users size={17}/> Students <span>{students.length}</span></button>
        <button className={activeTab === 'internships' ? 'nav-item active' : 'nav-item'} onClick={() => setActiveTab('internships')}><BriefcaseBusiness size={17}/> Internships <span>{internships.length}</span></button>
        <button className={activeTab === 'shortlist' ? 'nav-item active' : 'nav-item'} onClick={() => setActiveTab('shortlist')}><Filter size={17}/> Shortlisting</button>
        <button className={activeTab === 'allocations' ? 'nav-item active' : 'nav-item'} onClick={() => setActiveTab('allocations')}><Network size={17}/> Allocations</button>
        <button className={activeTab === 'benchmark' ? 'nav-item active' : 'nav-item'} onClick={() => setActiveTab('benchmark')}><Activity size={17}/> Performance lab</button>
      </nav>
      <div className="sidebar-bottom">
        <div className="engine-status"><span className="status-dot"></span><div><b>Algorithm engine</b><small>Java backend connected</small></div></div>
        <div className="profile"><div className="avatar">SB</div><div><b>SkillBridge Admin</b><small>Demo workspace</small></div><ChevronDown size={15}/></div>
      </div>
    </aside>

    <main className="main">
      <header className="topbar">
        <div className="breadcrumb">Workspace <span>/</span> <b>{activeTab === 'overview' ? 'Overview' : activeTab[0].toUpperCase() + activeTab.slice(1)}</b></div>
        <div className="top-actions"><span className="live-pill"><i></i> DEMO ENVIRONMENT</span><button className="icon-button" onClick={loadData} title="Refresh data"><RefreshCw size={17}/></button><div className="avatar small">SB</div></div>
      </header>

      <div className="content">
        {error && <div className="alert"><XCircle size={17}/><span>{error}</span><button onClick={() => setError('')}><XCircle size={15}/></button></div>}
        <section className="page-heading">
          <div><div className="eyebrow">PLACEMENT OPERATIONS / 2026–27</div><h1>{activeTab === 'overview' ? 'Good afternoon, Admin' : activeTab === 'benchmark' ? 'Performance lab' : activeTab === 'allocations' ? 'Placement allocations' : activeTab === 'shortlist' ? 'Candidate shortlisting' : activeTab === 'students' ? 'Student directory' : 'Internship opportunities'}</h1>
            <p>{activeTab === 'overview' ? 'A clear view of talent, opportunities and optimized placement outcomes.' : 'Explore the data and algorithm-driven decisions behind your placement workflow.'}</p></div>
          <div className="heading-actions"><button className="button secondary" onClick={loadData}><RefreshCw size={16}/> Refresh</button><button className="button primary" onClick={runAllocation} disabled={loading}><Play size={16}/>{loading ? 'Running…' : 'Run allocation'}</button></div>
        </section>

        {activeTab === 'overview' && <>
          <div className="hero-banner">
            <div className="hero-copy"><div className="hero-kicker"><Sparkles size={14}/> ALGORITHM-POWERED MATCHING</div><h2>Better matches.<br/><em>Smarter placements.</em></h2><p>Use randomized selection and maximum-flow optimization to shortlist talent and allocate limited internship seats.</p><button className="hero-button" onClick={runAllocation} disabled={loading}><Play size={15}/>{loading ? 'Processing…' : 'Optimize placements'} <ArrowUpRight size={15}/></button></div>
            <div className="hero-visual"><div className="orbit orbit-one"></div><div className="orbit orbit-two"></div><div className="hero-node node-a"><GraduationCap size={20}/><span>Students</span></div><div className="hero-node node-b"><BriefcaseBusiness size={20}/><span>Roles</span></div><div className="hero-core"><Network size={29}/></div><div className="hero-caption">MAX-FLOW ENGINE <b>ACTIVE</b></div></div>
          </div>
          <div className="section-title"><div><h3>Placement overview</h3><p>Current demo dataset at a glance</p></div><span className="muted-tag">SYNTHETIC DATA</span></div>
          <div className="stats-grid">
            <StatCard icon={Users} label="Registered students" value={dashboard?.students ?? '—'} foot="Profiles in demo dataset" tone="blue"/>
            <StatCard icon={BriefcaseBusiness} label="Open opportunities" value={dashboard?.internships ?? '—'} foot="Across partner companies" tone="violet"/>
            <StatCard icon={Award} label="Available seats" value={dashboard?.totalSeats ?? '—'} foot="Total internship capacity" tone="amber"/>
            <StatCard icon={CheckCircle2} label="Placements optimized" value={allocation?.placements ?? '—'} foot={allocation ? `${placedRate}% of available seats filled` : 'Run allocation to calculate'} tone="green"/>
          </div>
          <div className="two-column">
            <div className="panel">
              <div className="panel-heading"><div><h3>Algorithm pipeline</h3><p>How SkillBridge reaches an allocation</p></div><span className="tiny-status">2 ALGORITHMS</span></div>
              <div className="pipeline">
                <div className="pipeline-item"><div className="pipeline-icon purple"><Filter size={17}/></div><div><b>Randomized Quickselect</b><small>Finds the kth score cutoff for each role</small></div><span className="unit-chip">UNIT 6</span></div>
                <div className="pipeline-line"></div>
                <div className="pipeline-item"><div className="pipeline-icon teal"><Network size={17}/></div><div><b>Edmonds–Karp maximum flow</b><small>Maximizes feasible placements under seat limits</small></div><span className="unit-chip">UNIT 5</span></div>
                <div className="pipeline-line"></div>
                <div className="pipeline-item"><div className="pipeline-icon orange"><BarChart3 size={17}/></div><div><b>Results & evaluation</b><small>Assignments, vacancies and performance evidence</small></div><span className="unit-chip">REPORT</span></div>
              </div>
            </div>
            <div className="panel">
              <div className="panel-heading"><div><h3>Quick actions</h3><p>Run a focused algorithm operation</p></div><CircleHelp size={17} className="subtle-icon"/></div>
              <div className="quick-action"><div className="quick-icon"><Filter size={18}/></div><div className="quick-text"><b>Shortlist candidates</b><small>Run Quickselect for a chosen internship</small></div><select value={selectedInternship} onChange={e => setSelectedInternship(e.target.value)}>{internships.map(i => <option key={i.id} value={i.id}>{i.company}</option>)}</select><button className="square-button" onClick={runShortlist}><ArrowUpRight size={17}/></button></div>
              <div className="quick-action"><div className="quick-icon teal-bg"><Activity size={18}/></div><div className="quick-text"><b>Benchmark algorithms</b><small>Compare selection and allocation approaches</small></div><button className="text-button" onClick={runBenchmark} disabled={benchmarkLoading}>{benchmarkLoading ? 'Running…' : 'Run test'} <ArrowUpRight size={15}/></button></div>
              <div className="note-box"><ShieldCheck size={17}/><span>Eligibility and seat-capacity constraints are enforced by the Java backend.</span></div>
            </div>
          </div>
          <div className="panel table-panel">
            <div className="panel-heading"><div><h3>Internship opportunities</h3><p>Roles available in the current dataset</p></div><button className="link-button" onClick={() => setActiveTab('internships')}>View all <ArrowUpRight size={15}/></button></div>
            <InternshipTable internships={internships}/>
          </div>
        </>}

        {activeTab === 'students' && <div className="panel table-panel"><div className="panel-heading"><div><h3>Student profiles</h3><p>{students.length} synthetic candidate records</p></div><div className="search-box"><Search size={16}/><input value={query} onChange={e => setQuery(e.target.value)} placeholder="Search students or skills"/></div></div><div className="table-wrap"><table><thead><tr><th>STUDENT</th><th>CGPA</th><th>SKILLS</th><th>PREFERENCES</th></tr></thead><tbody>{filteredStudents.map(s => <tr key={s.id}><td><div className="person-cell"><div className="table-avatar">{s.name.split(' ').map(x => x[0]).slice(0,2).join('')}</div><div><b>{s.name}</b><small>{s.id}</small></div></div></td><td><b>{s.cgpa.toFixed(1)}</b></td><td><div className="skill-list">{s.skills.slice(0,4).map(skill => <span key={skill}>{skill}</span>)}</div></td><td>{s.preferredInternshipIds.length} roles</td></tr>)}</tbody></table></div></div>}

        {activeTab === 'internships' && <div className="internship-grid">{internships.map(job => <div className="panel job-card" key={job.id}><div className="job-top"><div className="company-mark">{job.company.slice(0,1)}</div><span className="open-tag">OPEN</span></div><div className="job-company">{job.company}</div><h3>{job.title}</h3><div className="job-meta"><span><Users size={14}/> {job.seats} seats</span><span><GraduationCap size={14}/> Min CGPA {job.minimumCgpa.toFixed(1)}</span></div><div className="skill-list">{job.requiredSkills.map(s => <span key={s}>{s}</span>)}</div><button className="button secondary full" onClick={() => {setSelectedInternship(job.id); runShortlist(job.id)}}>Find candidates <ArrowUpRight size={15}/></button></div>)}</div>}

        {activeTab === 'shortlist' && <div className="panel table-panel"><div className="panel-heading"><div><h3>Quickselect shortlist</h3><p>Top candidates by compatibility score, subject to minimum CGPA</p></div><div className="inline-controls"><select value={selectedInternship} onChange={e => setSelectedInternship(e.target.value)}>{internships.map(i => <option key={i.id} value={i.id}>{i.company} — {i.title}</option>)}</select><button className="button primary" onClick={runShortlist} disabled={loading}><Play size={15}/> Run Quickselect</button></div></div>{shortlist ? <><div className="result-strip"><div><small>ELIGIBLE POOL</small><b>{shortlist.eligibleCandidates}</b></div><div><small>SHORTLISTED</small><b>{shortlist.returnedCandidates}</b></div><div><small>KTH SCORE CUTOFF</small><b>{shortlist.cutoffScore?.toFixed(2) ?? '—'}</b></div><div><small>ALGORITHM</small><b>{shortlist.algorithm}</b></div></div><CandidateTable candidates={shortlist.candidates}/></> : <EmptyState text="Choose an internship and run Quickselect to see its top five eligible candidates."/>}</div>}

        {activeTab === 'allocations' && <div className="panel table-panel"><div className="panel-heading"><div><h3>Maximum-flow allocation</h3><p>One internship per student, with seat capacities enforced</p></div><div className="inline-controls"><label>Top K / role <input type="number" min="1" max="1000" value={topK} onChange={e => setTopK(Math.max(1, Number(e.target.value)))}/></label><button className="button primary" onClick={runAllocation} disabled={loading}><Play size={15}/>{loading ? 'Calculating…' : 'Recalculate'}</button></div></div>{allocation ? <><div className="result-strip"><div><small>PLACEMENTS</small><b>{allocation.placements}</b></div><div><small>UNMATCHED</small><b>{allocation.unmatchedStudents}</b></div><div><small>UNFILLED SEATS</small><b>{allocation.unfilledSeats}</b></div><div><small>SHORTLIST EDGES</small><b>{allocation.totalEligibleShortlistEdges}</b></div></div><div className="algorithm-note"><Network size={17}/><span>{allocation.optimizationNote}</span></div><div className="table-wrap"><table><thead><tr><th>STUDENT</th><th>INTERNSHIP</th><th>COMPANY</th><th>COMPATIBILITY</th><th>STATUS</th></tr></thead><tbody>{allocation.assignments.map((a, idx) => <tr key={`${a.studentId}-${a.internshipId}`}><td><div className="person-cell"><div className="table-avatar">{a.studentName.split(' ').map(x=>x[0]).slice(0,2).join('')}</div><div><b>{a.studentName}</b><small>{a.studentId}</small></div></div></td><td>{a.internshipTitle}</td><td>{a.company}</td><td><Score score={a.compatibilityScore}/></td><td><span className="assigned-tag"><CheckCircle2 size={13}/> Assigned</span></td></tr>)}</tbody></table></div><div className="unmatched"><b>Unmatched students:</b> {allocation.unmatchedStudentIds.length ? allocation.unmatchedStudentIds.join(', ') : 'None'}</div><div className="unmatched"><b>Internships with vacancies:</b> {allocation.internshipsWithVacancies.length ? allocation.internshipsWithVacancies.join(', ') : 'None'}</div></> : <EmptyState text="Run the allocation algorithm to calculate feasible placements."/>}</div>}

        {activeTab === 'benchmark' && <div className="panel table-panel"><div className="panel-heading"><div><h3>Algorithm performance lab</h3><p>Empirical runtime measurements on generated datasets</p></div><button className="button primary" onClick={runBenchmark} disabled={benchmarkLoading}><Play size={15}/>{benchmarkLoading ? 'Benchmarking…' : 'Run benchmark'}</button></div>{benchmark ? <><div className="algorithm-note"><Clock3 size={17}/><span>{benchmark.note}</span></div><div className="table-wrap"><table><thead><tr><th>CANDIDATES</th><th>QUICKSELECT</th><th>FULL SORT</th><th>QUICKSELECT CUTOFF</th><th>SORT CUTOFF</th><th>MAX FLOW</th><th>GREEDY</th><th>FLOW TIME</th></tr></thead><tbody>{benchmark.results.map(r => <tr key={r.candidates}><td><b>{money(r.candidates)}</b></td><td>{r.quickselectMs} ms</td><td>{r.fullSortMs} ms</td><td>{r.quickselectCutoff}</td><td>{r.sortCutoff}</td><td>{r.maximumFlowPlacements}</td><td>{r.greedyPlacements}</td><td>{r.flowMs} ms</td></tr>)}</tbody></table></div><div className="benchmark-cards">{benchmark.results.map(r => <div className="benchmark-card" key={r.candidates}><div><b>{money(r.candidates)} candidates</b><small>Selection runtime</small></div><div className="bar-row"><span>Quickselect</span><div className="bar-track"><i style={{width: `${Math.max(3, Math.min(100, r.quickselectMs / Math.max(r.quickselectMs,r.fullSortMs,0.01)*100))}%`}}/></div><b>{r.quickselectMs} ms</b></div><div className="bar-row"><span>Full sort</span><div className="bar-track"><i className="bar-sort" style={{width: `${Math.max(3, Math.min(100, r.fullSortMs / Math.max(r.quickselectMs,r.fullSortMs,0.01)*100))}%`}}/></div><b>{r.fullSortMs} ms</b></div></div>)}</div></> : <EmptyState text="Run the benchmark to compare Quickselect with sorting and inspect maximum-flow execution."/>}</div>}

        <footer className="footer"><span>© 2026 SkillBridge AI · DAA CIA 3 Project</span><span><Code2 size={13}/> Java algorithms · React interface · Synthetic data</span></footer>
      </div>
    </main>
  </div>
}

function Score({ score }) {
  return <div className="score-cell"><div className="score-track"><i className={scoreClass(score)} style={{width: `${score}%`}}/></div><b>{score.toFixed(1)}</b></div>
}

function CandidateTable({ candidates }) {
  return <div className="table-wrap"><table><thead><tr><th>CANDIDATE</th><th>CGPA</th><th>SKILL SCORE</th><th>PREFERENCE</th><th>COMPATIBILITY</th></tr></thead><tbody>{candidates.map(c => <tr key={c.student.id}><td><div className="person-cell"><div className="table-avatar">{c.student.name.split(' ').map(x=>x[0]).slice(0,2).join('')}</div><div><b>{c.student.name}</b><small>{c.student.id}</small></div></div></td><td>{c.student.cgpa.toFixed(1)}</td><td>{c.skillScore.toFixed(1)}</td><td>{c.preferenceScore.toFixed(0)}</td><td><Score score={c.score}/></td></tr>)}</tbody></table></div>
}

function InternshipTable({ internships }) {
  return <div className="table-wrap"><table><thead><tr><th>COMPANY / ROLE</th><th>REQUIRED SKILLS</th><th>MIN CGPA</th><th>SEATS</th><th>STATUS</th></tr></thead><tbody>{internships.map(i => <tr key={i.id}><td><div className="person-cell"><div className="company-mark small-mark">{i.company[0]}</div><div><b>{i.title}</b><small>{i.company} · {i.id}</small></div></div></td><td><div className="skill-list">{i.requiredSkills.slice(0,3).map(s => <span key={s}>{s}</span>)}</div></td><td>{i.minimumCgpa.toFixed(1)}</td><td><b>{i.seats}</b></td><td><span className="open-tag">OPEN</span></td></tr>)}</tbody></table></div>
}

function EmptyState({ text }) {
  return <div className="empty-state"><div><Network size={24}/></div><b>Ready when you are</b><p>{text}</p></div>
}

export default App
