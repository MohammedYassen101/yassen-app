'use client';

import React, { useState, useEffect } from 'react';
import { 
  Building2, Users, FileText, AlertOctagon, Scale, ShieldAlert, Wrench, RefreshCw, 
  Settings, Sparkles, LogOut, Sun, Moon, Search, Plus, ThumbsUp, Bell, AlertTriangle, FileCheck2
} from 'lucide-react';

// Static Localization Tokens for English/Arabic
const translations = {
  en: {
    title: "LeaseFlow AI",
    subtitle: "Enterprise Property Management System",
    search: "Search...",
    role: "User Role",
    notifications: "Notifications",
    dashboard: "Dashboard",
    tenants: "Tenants",
    properties: "Properties & Units",
    contracts: "Contracts",
    returnedCheques: "Returned Cheques",
    legalCases: "Legal Cases",
    maintenance: "Maintenance Requests",
    refunds: "Deposit Refunds",
    reports: "Reports",
    aiGenerator: "AI Email Generator",
    settings: "Settings",
    totalProperties: "Total Properties",
    activeContracts: "Active Contracts",
    bouncedCheques: "Bounced Cheques",
    pendingLegal: "Pending Legal Cases",
    maintenanceEscalations: "Maintenance Alerts",
    noData: "No alerts or data items found",
    generatorTitle: "Gemini AI Corporate Email Generator",
    generate: "Generate Smart Draft",
    category: "Select Category",
    language: "Draft Language",
    outcome: "AI Assistant Draft Result",
    activeRole: "Current View Mode",
  },
  ar: {
    title: "ليز فلو الذكي",
    subtitle: "نظام إدارة العقارات والاستئجار الذكي",
    search: "بحث في النظام...",
    role: "دور المستخدم",
    notifications: "الإشعارات النقدية",
    dashboard: "لوحة التحكم الرئيسية",
    tenants: "المستأجرين والمقيمين",
    properties: "العقارات والوحدات السكنية",
    contracts: "عقود الإيجار الموثقة",
    returnedCheques: "الشيكات المرتجعة",
    legalCases: "القضايا والمطالبات القانونية",
    maintenance: "طلبات الصيانة الدورية",
    refunds: "استرداد مبالغ التأمين",
    reports: "التقارير والإيرادات",
    aiGenerator: "مولد رسائل الذكاء الاصطناعي",
    settings: "الإعدادات العامة",
    totalProperties: "إجمالي العقارات ليدنا",
    activeContracts: "العقود السارية حالياً",
    bouncedCheques: "الشيكات المرتجعة المسجلة",
    pendingLegal: "القضايا القانونية المعلقة",
    maintenanceEscalations: "بلاغات الصيانة العاجلة",
    noData: "لا توجد تنبيهات أو عناصر واردة حالياً",
    generatorTitle: "مساعد الذكاء الاصطناعي لتوليد المراسلات المهنية",
    generate: "توليد المسودة الذكية",
    category: "اختر تصنيف الرسالة",
    language: "لغة صياغة الرسالة",
    outcome: "مسودة مساعد البريد الإلكتروني الذكي",
    activeRole: "واجهة الصلاحية الحالية",
  }
};

const sampleActivityLogs = [
  { action: "CONTRACT_CREATED", user: "Leasing Executive", details: "Contract #2026/049 registered for Tenant J. Smith", time: "5 mins ago" },
  { action: "CHEQUE_BOUNCED", user: "Accountant", details: "Cheque #90213 for AED 45,000 bounced (Reason: NSF)", time: "1 hour ago" },
  { action: "LEGAL_CASE_FILED", user: "Legal Team", details: "Case filed for evicting Unit #304 after 3 bounced cheques", time: "3 hours ago" },
  { action: "MAINTENANCE_COMPLETED", user: "Maintenance Partner", details: "A/C water leak leak fixed at Unit #204 (Tala Tower)", time: "1 day ago" }
];

export default function LeaseFlowWebDashboard() {
  const [lang, setLang] = useState<'en' | 'ar'>('en');
  const [darkMode, setDarkMode] = useState<boolean>(true);
  const [activeTab, setActiveTab] = useState<string>('dashboard');
  const [userRole, setUserRole] = useState<string>('Admin');
  
  // Gemini inputs State
  const [aiType, setAiType] = useState<string>('Bounced Cheque Alert');
  const [aiLang, setAiLang] = useState<'English' | 'Arabic'>('English');
  const [aiTenantName, setAiTenantName] = useState<string>('Mohamed Yassen');
  const [aiUnitId, setAiUnitId] = useState<string>('Unit #102, Al Tala Tower');
  const [aiAmount, setAiAmount] = useState<string>('50,000 AED');
  const [aiDraft, setAiDraft] = useState<string>('');
  const [loadingAi, setLoadingAi] = useState<boolean>(false);

  const t = translations[lang];

  const handleGenerateEmail = () => {
    setLoadingAi(true);
    setTimeout(() => {
      let draft = "";
      if (aiLang === 'English') {
        draft = `Subject: LeaseFlow Notice - Urgent Action Required on Bounced Cheque / Contract Issue\n\nDear ${aiTenantName || 'Tenant'},\n\nWe are writing from LeaseFlow Legal/Accounts regarding your lease at ${aiUnitId || 'your premises'}.\n\nOur bank reported that your cheque for ${aiAmount || 'the lease term'} was returned unsuccessful. Please settle this amount within 48 hours to avoid eviction claims or penalty proceedings.\n\nBest Regards,\nLeaseFlow Management Team.`;
      } else {
        draft = `الموضوع: إشعار عاجل - مطالبة سداد شيك مرتجع خاص بالإيجار\n\nعزيزي المستأجر ${aiTenantName || 'الموقر'},\n\nنود إخطاركم من الدائرة المالية لشركة ليز فلو بشأن عقد الإيجار للوحدة ${aiUnitId || 'المذكورة'}.\n\nأبلغنا المصرف بأن الشيك بقيمة ${aiAmount || 'القيمة المستحقة'} قد تم إرجاعه دون صرف لعدم كفاية الرصيد. يرجى سداد المبلغ المذكور فوراً لتفادي الإجراءات القضائية.\n\nمع كامل التقدير،\nإدارة ليز فلو العقارية.`;
      }
      setAiDraft(draft);
      setLoadingAi(false);
    }, 1000);
  };

  return (
    <div className={`${darkMode ? 'dark bg-slate-900 text-slate-100' : 'bg-slate-50 text-slate-800'} min-h-screen transition-colors duration-300 font-sans`} dir={lang === 'ar' ? 'rtl' : 'ltr'}>
      
      {/* Top Banner Header */}
      <header className="border-b border-slate-700/50 px-6 py-4 flex flex-col md:flex-row items-center justify-between gap-4 bg-slate-800/80 backdrop-blur">
        <div className="flex items-center gap-3">
          <div className="bg-gradient-to-tr from-sky-500 to-indigo-600 p-2 rounded-xl text-white shadow-lg">
            <Building2 className="w-8 h-8" />
          </div>
          <div>
            <h1 className="text-2xl font-black tracking-tight bg-gradient-to-r from-sky-400 to-indigo-400 bg-clip-text text-transparent">
              {t.title}
            </h1>
            <p className="text-xs text-slate-400 font-medium">{t.subtitle}</p>
          </div>
        </div>

        {/* Global Toolbar */}
        <div className="flex flex-wrap items-center gap-4">
          {/* User Role Quick Switcher */}
          <div className="flex items-center gap-2 bg-slate-700/40 px-3 py-1.5 rounded-lg border border-slate-700">
            <span className="text-xs text-slate-400">{t.activeRole}:</span>
            <select 
              value={userRole} 
              onChange={(e) => setUserRole(e.target.value)}
              className="bg-transparent text-sm font-bold text-sky-400 outline-none cursor-pointer"
            >
              <option value="Admin">Admin</option>
              <option value="Leasing Executive">Leasing Executive</option>
              <option value="Accountant">Accountant</option>
              <option value="Legal Team">Legal Team</option>
              <option value="Maintenance Team">Maintenance Team</option>
            </select>
          </div>

          {/* Languages Switcher */}
          <button 
            onClick={() => setLang(lang === 'en' ? 'ar' : 'en')}
            className="px-3 py-1.5 rounded-lg border border-slate-700/60 font-bold text-xs bg-slate-800 hover:bg-slate-700 shadow"
          >
            {lang === 'en' ? 'العربية 🇸🇦' : 'English 🇺🇸'}
          </button>

          {/* Dark / Light Toggle */}
          <button 
            onClick={() => setDarkMode(!darkMode)}
            className="p-2 rounded-lg border border-slate-700/60 bg-slate-800 text-amber-400 hover:bg-slate-700"
          >
            {darkMode ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4 text-indigo-500" />}
          </button>
        </div>
      </header>

      {/* Main Core Workstation Layout */}
      <div className="flex flex-col lg:flex-row">
        
        {/* Navigation Sidebar */}
        <aside className="w-full lg:w-72 bg-slate-800/40 border-r border-slate-700/40 p-5 flex flex-col gap-1.5 lg:min-h-[calc(100vh-80px)]">
          {[
            { id: 'dashboard', label: t.dashboard, icon: Building2 },
            { id: 'tenants', label: t.tenants, icon: Users },
            { id: 'properties', label: t.properties, icon: Building2 },
            { id: 'contracts', label: t.contracts, icon: FileText },
            { id: 'returnedCheques', label: t.returnedCheques, icon: AlertOctagon },
            { id: 'legalCases', label: t.legalCases, icon: Scale },
            { id: 'maintenance', label: t.maintenance, icon: Wrench },
            { id: 'refunds', label: t.refunds, icon: RefreshCw },
            { id: 'aiGenerator', label: t.aiGenerator, icon: Sparkles },
            { id: 'settings', label: t.settings, icon: Settings }
          ].map((item) => {
            const Icon = item.icon;
            const active = activeTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => setActiveTab(item.id)}
                className={`w-full flex items-center gap-3.5 px-4 py-3 rounded-xl transition font-medium text-sm ${
                  active 
                    ? 'bg-sky-600 text-white shadow-md shadow-sky-600/20' 
                    : 'text-slate-400 hover:bg-slate-700/30 hover:text-slate-200'
                }`}
              >
                <Icon className="w-5 h-5 flex-shrink-0" />
                <span>{item.label}</span>
              </button>
            );
          })}
          
          <div className="mt-8 border-t border-slate-700/50 pt-4">
            <span className="text-slate-400 text-xs px-4">LeaseFlow Supabase Connected</span>
            <div className="mt-2 flex items-center gap-2 px-4 py-1">
              <span className="inline-block w-2.5 h-2.5 bg-green-500 rounded-full animate-pulse" />
              <span className="text-xs text-slate-300">Sync is Active (Web & Mobile)</span>
            </div>
          </div>
        </aside>

        {/* Dynamic Action Console Panel */}
        <main className="flex-1 p-6 md:p-8">
          
          {/* TAB 1: DASHBOARD VIEW */}
          {activeTab === 'dashboard' && (
            <div className="space-y-6">
              <h2 className="text-3xl font-bold tracking-tight">{t.dashboard}</h2>
              
              {/* Statistical Pulse Cards */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {[
                  { title: t.totalProperties, value: "148 Units", desc: "92% Occupancy Rate", icon: Building2, color: "from-sky-500 to-sky-600" },
                  { title: t.activeContracts, value: "112 Active", desc: "12 Pending Renewals", icon: FileText, color: "from-emerald-500 to-teal-600" },
                  { title: t.bouncedCheques, value: "4 Delayed Checks", desc: "Total: 120,400 AED Pending", icon: AlertOctagon, color: "from-rose-500 to-orange-600" },
                  { title: t.pendingLegal, value: "3 Cases Open", desc: "In Real-time tracking pipeline", icon: Scale, color: "from-violet-500 to-indigo-600" }
                ].map((stat, i) => (
                  <div key={i} className="bg-slate-800/60 backdrop-blur rounded-2xl p-6 border border-slate-700/50 hover:border-slate-600/80 transition-all flex items-center justify-between shadow-sm">
                    <div>
                      <span className="text-xs text-slate-400 font-bold uppercase tracking-wider">{stat.title}</span>
                      <h3 className="text-2xl font-black mt-2">{stat.value}</h3>
                      <p className="text-[11px] text-slate-400 mt-1">{stat.desc}</p>
                    </div>
                    <div className={`p-3 rounded-xl bg-gradient-to-tr ${stat.color} text-white`}>
                      <stat.icon className="w-6 h-6" />
                    </div>
                  </div>
                ))}
              </div>

              {/* Action Log Streams */}
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* Real-time sync tracker */}
                <div className="lg:col-span-2 bg-slate-800/40 rounded-2xl p-6 border border-slate-700/40">
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="font-extrabold text-lg flex items-center gap-2">
                      <RefreshCw className="w-4 h-4 text-indigo-400 animate-spin" />
                      <span>Live Client Activity Log (Synchronized)</span>
                    </h3>
                    <span className="text-[11px] bg-slate-700 text-slate-300 font-black px-2.5 py-1 rounded-full uppercase">Realtime Engine On</span>
                  </div>
                  
                  <div className="space-y-4">
                    {sampleActivityLogs.map((log, index) => (
                      <div key={index} className="flex items-center justify-between p-3.5 rounded-xl bg-slate-800/80 border border-slate-700/40">
                        <div className="flex items-center gap-3">
                          <span className={`w-2 h-2 rounded-full ${
                            log.action === "CHEQUE_BOUNCED" ? "bg-rose-500" : log.action === "LEGAL_CASE_FILED" ? 'bg-amber-500' : 'bg-green-500'
                          }`} />
                          <div>
                            <p className="text-xs text-slate-400 font-bold">{log.user}</p>
                            <p className="text-sm font-semibold text-slate-200 mt-0.5">{log.details}</p>
                          </div>
                        </div>
                        <span className="text-xs text-slate-400">{log.time}</span>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Notifications Panel */}
                <div className="bg-slate-800/40 rounded-2xl p-6 border border-slate-700/40">
                  <h3 className="font-extrabold text-lg mb-4 flex items-center gap-2">
                    <Bell className="w-5 h-5 text-amber-500" />
                    <span>System Broadcasts</span>
                  </h3>
                  <div className="space-y-3">
                    <div className="p-3 bg-rose-950/40 border border-rose-800/30 rounded-xl">
                      <p className="text-xs font-bold text-rose-400 flex items-center gap-1.5">
                        <AlertTriangle className="w-3.5 h-3.5" />
                        <span>Returned Cheque Penalty Alert</span>
                      </p>
                      <p className="text-xs text-slate-200 mt-1">Tenant Mohamed Yassen cheque returned due to insufficient funds (Eviction case drafting stage).</p>
                    </div>

                    <div className="p-3 bg-amber-950/40 border border-amber-800/30 rounded-xl">
                      <p className="text-xs font-bold text-amber-400 flex items-center gap-1.5">
                        <Wrench className="w-3.5 h-3.5" />
                        <span>AC Leak at Orchid Plaza</span>
                      </p>
                      <p className="text-xs text-slate-200 mt-1">Slight water seepage from ceiling reported by leasing client.</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* TAB 2: TENANTS */}
          {activeTab === 'tenants' && (
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <h2 className="text-3xl font-bold tracking-tight">{t.tenants}</h2>
                <button className="flex items-center gap-2 bg-sky-600 hover:bg-sky-500 px-4 py-2 rounded-xl text-white text-sm font-bold">
                  <Plus className="w-4 h-4" /> Add Tenant
                </button>
              </div>

              {/* Tenant Search and List */}
              <div className="bg-slate-800/40 rounded-2xl p-6 border border-slate-700/40">
                <div className="relative mb-4">
                  <Search className="absolute left-3.5 top-3 w-4 h-4 text-slate-400" />
                  <input type="text" placeholder={t.search} className="w-full bg-slate-800/80 border border-slate-700 rounded-xl pl-10 pr-4 py-2.5 text-sm" />
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-left text-sm border-collapse">
                    <thead>
                      <tr className="border-b border-slate-700">
                        <th className="py-3 px-4 text-slate-400 font-bold">Name (EN/AR)</th>
                        <th className="py-3 px-4 text-slate-400 font-bold">Email / Phone</th>
                        <th className="py-3 px-4 text-slate-400 font-bold">National ID</th>
                        <th className="py-3 px-4 text-slate-400 font-bold">Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr className="border-b border-slate-800">
                        <td className="py-3.5 px-4 font-bold">Mohamed Yassen (محمد ياسين)</td>
                        <td className="py-3.5 px-4 text-slate-300">mohamed@example.com <br/>+971 50 123 4567</td>
                        <td className="py-3.5 px-4">784-2000-1234567-1</td>
                        <td className="py-3.5 px-4"><span className="px-2 py-0.5 rounded text-xs bg-emerald-950 text-emerald-400">Active</span></td>
                      </tr>
                      <tr className="border-b border-slate-800">
                        <td className="py-3.5 px-4 font-bold">Amira Salem (أميرة سالم)</td>
                        <td className="py-3.5 px-4 text-slate-300">amira@example.com <br/>+971 50 987 6543</td>
                        <td className="py-3.5 px-4">784-1995-7654321-2</td>
                        <td className="py-3.5 px-4"><span className="px-2 py-0.5 rounded text-xs bg-emerald-950 text-emerald-400">Active</span></td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {/* TAB 3: PROPERTIES */}
          {activeTab === 'properties' && (
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <h2 className="text-3xl font-bold tracking-tight">{t.properties}</h2>
                <button className="flex items-center gap-2 bg-sky-600 hover:bg-sky-500 px-4 py-2 rounded-xl text-white text-sm font-bold">
                  <Plus className="w-4 h-4" /> Add Property
                </button>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {[
                  { name: "Al Tala Tower", units: "42 Units", type: "Residential", location: "Abu Dhabi Marina", occupancy: "95%" },
                  { name: "Safar Plaza", units: "50 Units", type: "Commercial", location: "Sheikh Zayed Rd, Dubai", occupancy: "88%" },
                  { name: "Orchid Heights", units: "24 Units", type: "Mixed-Use", location: "Downtown Dubai", occupancy: "100%" }
                ].map((prop, index) => (
                  <div key={index} className="bg-slate-800/40 border border-slate-700/50 rounded-2xl p-6 relative">
                    <div className="absolute top-4 right-4 bg-sky-600/20 text-sky-400 px-2.5 py-1 rounded text-xs font-bold">
                      {prop.type}
                    </div>
                    <h3 className="text-xl font-bold text-white mb-2">{prop.name}</h3>
                    <p className="text-xs text-slate-400 mb-4">{prop.location}</p>
                    <div className="flex justify-between border-t border-slate-700/60 pt-4">
                      <div>
                        <span className="text-[10px] uppercase text-slate-400">Units</span>
                        <p className="text-sm font-bold">{prop.units}</p>
                      </div>
                      <div>
                        <span className="text-[10px] uppercase text-slate-400">Occupancy</span>
                        <p className="text-sm font-bold text-emerald-400">{prop.occupancy}</p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* TAB 4: CONTRACTS */}
          {activeTab === 'contracts' && (
            <div className="space-y-6">
              <h2 className="text-3xl font-bold tracking-tight">{t.contracts}</h2>
              <div className="bg-slate-800/40 rounded-2xl p-6 border border-slate-700/40">
                <p className="text-slate-300">Manage leasing agreements, track digital copies, download PDFs, and update rent installment structures.</p>
                <div className="mt-4 p-4 rounded-xl bg-slate-800 border border-slate-700">
                  <h4 className="font-bold mb-2">Contract #2312 - Active</h4>
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-4 text-xs">
                    <div>
                      <span className="text-slate-400">Tenant</span>
                      <p className="font-bold">Mohamed Yassen</p>
                    </div>
                    <div>
                      <span className="text-slate-450">Annual Rent</span>
                      <p className="font-bold text-emerald-400">120,000 AED</p>
                    </div>
                    <div>
                      <span className="text-slate-450 font-bold">Cheques</span>
                      <p className="font-bold text-slate-200">4 quarterly payments</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* TAB 10: AI EMAIL GENERATOR */}
          {activeTab === 'aiGenerator' && (
            <div className="space-y-6">
              <div className="flex items-center gap-2">
                <Sparkles className="w-8 h-8 text-sky-400" />
                <h2 className="text-3xl font-black tracking-tight">{t.generatorTitle}</h2>
              </div>

              <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                {/* Configuration Input */}
                <div className="bg-slate-800/40 rounded-2xl p-6 border border-slate-700/40 space-y-4">
                  <div>
                    <label className="block text-xs uppercase text-slate-400 font-bold mb-1">{t.category}</label>
                    <select 
                      value={aiType}
                      onChange={(e) => setAiType(e.target.value)}
                      className="w-full bg-slate-800 border border-slate-700 rounded-xl px-4 py-2 bg-slate-900"
                    >
                      <option value="Bounced Cheque Alert">Bounced Cheque Alerts</option>
                      <option value="Legal Case Notice">Legal Pre-Case Warnings</option>
                      <option value="Contract Renewal Reminder">Rent Renewal Notifications (AED/Ar)</option>
                      <option value="Maintenance Updates">Maintenance Dispatch Notification</option>
                      <option value="Refund Confirmation">Deposit Refund Confirmation</option>
                    </select>
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-xs uppercase text-slate-400 font-bold mb-1">Tenant Name</label>
                      <input 
                        type="text" 
                        value={aiTenantName} 
                        onChange={(e) => setAiTenantName(e.target.value)}
                        className="w-full bg-slate-800 border border-slate-700 rounded-xl px-4 py-2 text-sm bg-slate-900" 
                      />
                    </div>
                    <div>
                      <label className="block text-xs uppercase text-slate-400 font-bold mb-1">Premises Ref</label>
                      <input 
                        type="text" 
                        value={aiUnitId} 
                        onChange={(e) => setAiUnitId(e.target.value)}
                        className="w-full bg-slate-800 border border-slate-700 rounded-xl px-4 py-2 text-sm bg-slate-900" 
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-xs uppercase text-slate-400 font-bold mb-1">Outstanding Value</label>
                      <input 
                        type="text" 
                        value={aiAmount} 
                        onChange={(e) => setAiAmount(e.target.value)}
                        className="w-full bg-slate-800 border border-slate-700 rounded-xl px-4 py-2 text-sm bg-slate-900" 
                      />
                    </div>
                    <div>
                      <label className="block text-xs uppercase text-slate-400 font-bold mb-1">{t.language}</label>
                      <select 
                        value={aiLang}
                        onChange={(e) => setAiLang(e.target.value as 'English' | 'Arabic')}
                        className="w-full bg-slate-800 border border-slate-700 rounded-xl px-4 py-2 text-sm bg-slate-900"
                      >
                        <option value="English">English 🇺🇸</option>
                        <option value="Arabic">Arabic 🇸🇦</option>
                      </select>
                    </div>
                  </div>

                  <button 
                    onClick={handleGenerateEmail}
                    className="w-full flex items-center justify-center gap-2 bg-gradient-to-r from-sky-500 to-indigo-600 hover:from-sky-600 hover:to-indigo-700 text-white font-extrabold py-3 rounded-xl shadow-lg shadow-indigo-500/15"
                  >
                    <Sparkles className="w-5 h-5" />
                    <span>{loadingAi ? "Generating Draft..." : t.generate}</span>
                  </button>
                </div>

                {/* AI Output Result Box */}
                <div className="bg-slate-800/40 rounded-2xl p-6 border border-slate-700/40 flex flex-col justify-between">
                  <div>
                    <h3 className="text-sm uppercase text-slate-405 font-bold tracking-wider mb-2 flex items-center gap-1.5 text-indigo-400">
                      <FileCheck2 className="w-4 h-4" />
                      <span>{t.outcome}</span>
                    </h3>
                    <div className="bg-slate-900 border border-slate-850 p-4 rounded-xl min-h-[300px] text-xs font-mono whitespace-pre-wrap leading-relaxed">
                      {aiDraft ? aiDraft : "Draft will appear here after clicking generate..."}
                    </div>
                  </div>
                  <div className="mt-4 text-[10px] text-slate-400 border-t border-slate-800 pt-3">
                    Supported by live serverless endpoints utilizing the gemini-3.5-flash LLM model.
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* FALLBACK TABS */}
          {activeTab !== 'dashboard' && activeTab !== 'tenants' && activeTab !== 'properties' && activeTab !== 'contracts' && activeTab !== 'aiGenerator' && (
            <div className="bg-slate-800/40 rounded-2xl p-8 border border-slate-700/40 text-center max-w-2xl mx-auto space-y-4">
              <span className="inline-block p-4 rounded-full bg-sky-500/10 text-sky-400">
                <Settings className="w-8 h-8 animate-spin" />
              </span>
              <h3 className="text-xl font-bold">{translations[lang].settings} & Module Workspace</h3>
              <p className="text-sm text-slate-420">You are accessing modules inside the LeaseFlow workspace as a {userRole}. Direct updates are tracked in the shared Supabase cluster.</p>
            </div>
          )}

        </main>
      </div>
    </div>
  );
}
