package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GeminiClient
import com.example.api.GenerateContentRequest
import com.example.api.Part
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LeaseFlowViewModel(application: Application) : AndroidViewModel(application) {
    private val database = LeaseFlowDatabase.getInstance(application)
    private val dao = database.dao

    // UI Configuration States
    val currentRole = MutableStateFlow("Admin") // Admin, Leasing Executive, Accountant, Legal Team, Maintenance Team
    val currentLanguage = MutableStateFlow("en") // en, ar
    val activeTab = MutableStateFlow("dashboard") // dashboard, tenants, properties, contracts, cheques, legal, maintenance, refunds, reports, ai_generator, settings
    
    // Live Data Streams (from Room Database)
    val properties = dao.getAllPropertiesFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val tenants = dao.getAllTenantsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val contracts = dao.getAllContractsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val returnedCheques = dao.getAllReturnedChequesFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val maintenanceRequests = dao.getAllMaintenanceRequestsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activityLogs = dao.getAllActivityLogsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Simulated Remote Cloud Sync State
    val isCloudSyncEnabled = MutableStateFlow(true)

    // AI Email Generator state
    val aiType = MutableStateFlow("Bounced Cheque Alert")
    val aiLanguage = MutableStateFlow("English")
    val aiTenantName = MutableStateFlow("Mohamed Yassen")
    val aiUnitId = MutableStateFlow("Unit #102, Al Tala Tower")
    val aiAmount = MutableStateFlow("50,000 AED")
    val aiResponseDraft = MutableStateFlow("")
    val aiIsLoading = MutableStateFlow(false)

    init {
        // Automatically populate DB with initial sample data if properties in the DB are empty
        viewModelScope.launch {
            try {
                // Collect directly from the room flow to bypass the empty stateflow placeholder
                val currentProps = dao.getAllPropertiesFlow().first()
                if (currentProps.isEmpty()) {
                    primeDatabaseSampleData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun primeDatabaseSampleData() {
        // 1. Prime Properties
        dao.insertProperty(PropertyEntity("P01", "Al Tala Tower", "برج الطلا", "Abu Dhabi Marina", "مارينا أبو ظبي", 42, "Residential", "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=500&auto=format&fit=crop&q=60"))
        dao.insertProperty(PropertyEntity("P02", "Safar Plaza", "صفار بلازا", "Sheikh Zayed Rd, Dubai", "شارع الشيخ زايد، دبي", 50, "Commercial", "https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=500&auto=format&fit=crop&q=60"))
        dao.insertProperty(PropertyEntity("P03", "Orchid Heights", "أوركيد هايتس", "Downtown Dubai", "وسط مدينة دبي", 24, "Mixed", "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=500&auto=format&fit=crop&q=60"))

        // 2. Prime Tenants
        dao.insertTenant(TenantEntity("T01", "Mohamed Yassen", "محمد ياسين", "mohamed1699yassen@gmail.com", "+971 50 123 4567", "784-2000-1234567-1", "Active"))
        dao.insertTenant(TenantEntity("T02", "Amira Salem", "أميرة سالم", "amira@example.com", "+971 50 987 6543", "784-1995-7654321-2", "Active"))
        dao.insertTenant(TenantEntity("T03", "Johnathan Doe", "جون دو", "john.doe@example.com", "+971 54 555 1212", "784-1988-5551212-3", "Pending"))

        // 3. Prime Contracts
        dao.insertContract(ContractEntity("C01", "CON-2026/049", "T01", "P01", "Unit #102", "2026-01-01", "2027-01-01", 120000.00, 4, 10000.00, "Active"))
        dao.insertContract(ContractEntity("C02", "CON-2026/050", "T02", "P02", "Suite 404", "2026-03-01", "2027-03-01", 150000.00, 1, 15000.00, "Active"))

        // 4. Prime Bounced Cheques
        dao.insertReturnedCheque(ReturnedChequeEntity("CH01", "C01", "Mohamed Yassen", "CHQ-890214", "Emirates NBD", 50000.00, "2026-05-10", "Pending Follow-up", "https://images.unsplash.com/photo-1554415707-6e8cfc93fe23?w=500&auto=format&fit=crop&q=60"))
        dao.insertReturnedCheque(ReturnedChequeEntity("CH02", "C02", "Amira Salem", "CHQ-341029", "First Abu Dhabi Bank", 37500.00, "2026-05-18", "Paid", ""))

        // 5. Prime Maintenance Requests
        dao.insertMaintenanceRequest(MaintenanceRequestEntity("M01", "Al Tala Tower", "Unit #102", "Air Conditioning Water Leak", "تسرب مياه المكيف", "High", "In Progress", "A/C in master bedroom dripping water onto carpet.", "Maintenance Partner Group A"))
        dao.insertMaintenanceRequest(MaintenanceRequestEntity("M02", "Safar Plaza", "Suite 404", "Main entry door lock replacement", "تبديل قفل الباب الرئيسي", "Low", "New", "Keycard scanner is failing intermittent authorization.", "Locksmith Pros Ltd"))

        // 6. Log Initial Activity
        dao.insertActivityLog(ActivityLogEntity(
            action = "SYSTEM_INITIALIZATION",
            user = "LeaseFlow AI System",
            details = "Primed sqlite baseline with properties, contracts, and demo accounts.",
            timestamp = getNowString()
        ))
    }

    private fun getNowString(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    fun addTenant(nameEn: String, nameAr: String, email: String, phone: String, nationalId: String) {
        viewModelScope.launch {
            val id = "T" + System.currentTimeMillis().toString().takeLast(4)
            val tenant = TenantEntity(id, nameEn, nameAr, email, phone, nationalId, "Active")
            dao.insertTenant(tenant)
            
            dao.insertActivityLog(ActivityLogEntity(
                action = "TENANT_ADDED",
                user = currentRole.value,
                details = "Registered tenant ${nameEn} with reference ID $id.",
                timestamp = getNowString()
            ))
        }
    }

    fun deleteTenant(tenant: TenantEntity) {
        viewModelScope.launch {
            dao.deleteTenant(tenant)
            dao.insertActivityLog(ActivityLogEntity(
                action = "TENANT_DELETED",
                user = currentRole.value,
                details = "Deleted tenant registry of ${tenant.nameEn}.",
                timestamp = getNowString()
            ))
        }
    }

    fun addMaintenanceRequest(property: String, unit: String, titleEn: String, titleAr: String, priority: String, details: String) {
        viewModelScope.launch {
            val id = "M" + System.currentTimeMillis().toString().takeLast(4)
            val req = MaintenanceRequestEntity(id, property, unit, titleEn, titleAr, priority, "New", details, "Unassigned")
            dao.insertMaintenanceRequest(req)

            dao.insertActivityLog(ActivityLogEntity(
                action = "MAINTENANCE_CREATED",
                user = currentRole.value,
                details = "New $priority priority ticket created for unit $unit.",
                timestamp = getNowString()
            ))
        }
    }

    fun triggerLegalCaseFile(tenantName: String, value: String) {
        viewModelScope.launch {
            dao.insertActivityLog(ActivityLogEntity(
                action = "LEGAL_CASE_FILED",
                user = "Legal Team",
                details = "Eviction litigation dossier created for tenant $tenantName. Disputed amount: $value.",
                timestamp = getNowString()
            ))
        }
    }

    fun triggerChequeBounce(tenantName: String, bank: String, amount: Double) {
        viewModelScope.launch {
            val id = "CH" + System.currentTimeMillis().toString().takeLast(3)
            val num = "CHQ-" + (100000..999999).random().toString()
            dao.insertReturnedCheque(ReturnedChequeEntity(id, "C01", tenantName, num, bank, amount, getNowString(), "Pending Follow-up", ""))

            dao.insertActivityLog(ActivityLogEntity(
                action = "CHEQUE_BOUNCED",
                user = currentRole.value,
                details = "Captured bounced cheque $num for $amount AED belonging to $tenantName.",
                timestamp = getNowString()
            ))
        }
    }

    fun resolveActivityLogs() {
        viewModelScope.launch {
            dao.clearLogs()
            dao.insertActivityLog(ActivityLogEntity(
                action = "LOGS_CLEARED",
                user = currentRole.value,
                details = "Wiped activity log stream from terminal.",
                timestamp = getNowString()
            ))
        }
    }

    // Call real Gemini API REST endpoints!
    fun generateAiSmartDraft() {
        viewModelScope.launch {
            aiIsLoading.value = true
            val prompt = """
                You are LeaseFlow AI, an enterprise smart real estate assistant.
                Generate a highly professional property lease communication email.
                Category of communication: ${aiType.value}
                Language requested: ${aiLanguage.value}
                Tenant Name: ${aiTenantName.value}
                Premises Reference: ${aiUnitId.value}
                Outstanding Value or Amount: ${aiAmount.value}
                
                Guidelines:
                - Keep the tone formal, direct, and polite.
                - If the language is Arabic, ensure flawless grammar and perfect business style.
                - Include placeholders for signature.
                - Address the issue specifically (e.g., if returned cheque, explain that it must be paid within 48 hours to avoid evictions or legal blockades).
            """.trimIndent()

            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                // Return immediate beautifully styled placeholder draft from custom library
                aiResponseDraft.value = generateMockBilingualDraft(
                    type = aiType.value,
                    lang = aiLanguage.value,
                    tenant = aiTenantName.value,
                    unit = aiUnitId.value,
                    amount = aiAmount.value
                )
                aiIsLoading.value = false
                return@launch
            }

            try {
                val requestBody = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )
                val response = GeminiClient.service.generateContent(apiKey, requestBody)
                val generatedText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                aiResponseDraft.value = generatedText ?: "Error: Did not receive expected response structure from Gemini API. Please review parameters."
            } catch (e: Exception) {
                aiResponseDraft.value = """
                    [DIRECT REST CONNECTION ERROR]
                    ${e.localizedMessage}
                    
                    --- RECOVERABLE LOCAL TEMPLATE DRAFT GENERATED ---
                    
                    ${generateMockBilingualDraft(aiType.value, aiLanguage.value, aiTenantName.value, aiUnitId.value, aiAmount.value)}
                """.trimIndent()
            } finally {
                aiIsLoading.value = false
            }
        }
    }

    private fun generateMockBilingualDraft(type: String, lang: String, tenant: String, unit: String, amount: String): String {
        return if (lang == "English") {
            when (type) {
                "Bounced Cheque Alert" -> """
                    Subject: LeaseFlow - OFFICIAL NOTICE OF DEMAND - Bounced Rent Cheque: $unit
                    
                    Dear $tenant,
                    
                    We are writing from the Accounts and Corporate Administration of LeaseFlow AI regarding your rental unit located at $unit.
                    
                    Our bank reported that your cheque for the lease rent payment in the amount of $amount was returned unsuccessful due to: INSUFFICIENT FUNDS.
                    
                    Please be advised that you are required to settle this outstanding amount of $amount via wire transfer, manager's cheque, or cash payment within forty-eight (48) hours of this notice. Failure to do so will result in immediate lease termination, filing of eviction proceedings to the municipal rent tribunal, and standard default penalties.
                    
                    Please upload the settlement receipt directly in the LeaseFlow app or email us at collections@leaseflow.ai.
                    
                    Sincerely,
                    Accounts & Receivables Division
                    LeaseFlow Real Estate
                """.trimIndent()
                
                "Legal Case Notice" -> """
                    Subject: URGENT: Pre-Litigation Legal Notice / Lease Eviction Alert - Unit: $unit
                    
                    Dear $tenant,
                    
                    This is an official communication from LeaseFlow Legal Affairs in respect of your current lease of $unit.
                    
                    Despite multiple notifications from our accounts team, the outstanding amount of $amount remains unpaid. Consequently, we have initiated the drafting of an eviction case file to the Rental Disputes Center.
                    
                    To halt court registration, you must complete full settlement of the overdue sums immediately. Once filed, full dispute resolution fees and judicial costs will be added to your balance.
                    
                    Regards,
                    Legal Counsel Unit
                    LeaseFlow AI
                """.trimIndent()
                
                else -> """
                    Subject: LeaseFlow Notice - Lease Rent Renewal Status: Unit $unit
                    
                    Dear $tenant,
                    
                    We trust this letter finds you well. 
                    
                    Your current leasing agreement for $unit is scheduled for automatic renewal. The calculated annual lease value is set to $amount.
                    
                    Please reply to confirm if you consent to renewal or wish to file a transition request.
                    
                    Sincerely,
                    Leasing Relations Office
                    LeaseFlow AI
                """.trimIndent()
            }
        } else {
            when (type) {
                "Bounced Cheque Alert" -> """
                    الموضوع: إخطار سداد رسمي عاجل - شيك مرتجع للوحدة: $unit
                    
                    عزيزي المستأجر $tenant الموقر،
                    
                    نخاطبكم من الدائرة المالية لشركة ليز فلو للذكاء الاصطناعي بشأن الوحدة السكنية رقم $unit.
                    
                    أبلغنا المصرف بأن الشيك المقدم للتحصيل بقيمة $amount درهم قد تم إرجاعه دون صرف لعدم كفاية الرصيد في الحساب المالي.
                    
                    بناءً عليه، يرجى تسديد المبلغ المذكور فوراً وبحد أقصى ثمانية وأربعين (48) ساعة من تاريخ هذا الإخطار عن طريق التحويل البنكي المباشر أو شيك مدير لتلافي إلغاء العقد المبرم وبدء تحريك دعوى الإخلاء القانونية.
                    
                    بعد السداد، يرجى رفع إيصال الدفع عبر تطبيق الهاتف لتوثيقه تلقائياً.
                    
                    مع التقدير والتحية،
                    قسم الحسابات العقارية
                    ليز فلو الذكي
                """.trimIndent()
                
                "Legal Case Notice" -> """
                    الموضوع: إنذار قانوني نهائي قبل الشروع في دعوى فض المنازعات - الوحدة: $unit
                    
                    عزيزي المستأجر $tenant الموقر،
                    
                    نود إشعاركم رسمياً من الدائرة القانونية لشركة ليز فلو العقارية بشأن تأخر سداد المتأخرات البالغة $amount درهم.
                    
                    من المؤسف أن نبلغكم بأننا شرعنا بالفعل في إعداد ملف القضية لتقديمه إلى مركز فض المنازعات الإيجارية لغرض المطالبة بالإخلاء الفوري وسداد المطالبات الإيجارية المتأخرة.
                    
                    لتفادي تسجيل القضية وفرض الرسوم القضائية، يرجى إجراء السداد الفوري واستيفاء المستحقات قبل الموعد.
                    
                    مع الاحترام،
                    الشؤون القانونية والمطالبات
                    ليز فلو الذكي
                """.trimIndent()
                
                else -> """
                    الموضوع: تذكير عقد الإيجار وتجديد الرخص الإيجارية - الوحدة: $unit
                    
                    عزيزي المستأجر $tenant الموقر،
                    
                    نأمل أن تكونوا بخير.
                    
                    يرجى العلم بأن عقدكم الحالي للوحدة $unit يقترب من موعد انتهائه. القيمة الإيجارية السنوية المحددة للعام القادم هي $amount درهم.
                    
                    يرجى التواصل معنا لتأكيد الرغبة في التجديد وصياغة اتفاقية الملحق الجديد.
                    
                    شاكرين تعاونكم،
                    مكتب علاقات المستأجرين
                    ليز فلو الذكي
                """.trimIndent()
            }
        }
    }
}
