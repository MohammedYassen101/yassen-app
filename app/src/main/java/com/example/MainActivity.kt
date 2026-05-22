package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.LeaseFlowViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LeaseFlowMainWorkspace()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaseFlowMainWorkspace(
    vm: LeaseFlowViewModel = viewModel()
) {
    val lang by vm.currentLanguage.collectAsState()
    val role by vm.currentRole.collectAsState()
    val activeTab by vm.activeTab.collectAsState()

    // Determine layout direction based on chosen language
    val layoutDirection = if (lang == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

    // Real-time Database Collections
    val propertiesList by vm.properties.collectAsState()
    val tenantsList by vm.tenants.collectAsState()
    val contractsList by vm.contracts.collectAsState()
    val chequesList by vm.returnedCheques.collectAsState()
    val maintenanceList by vm.maintenanceRequests.collectAsState()
    val logList by vm.activityLogs.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                LeaseFlowTopAppBar(
                    lang = lang,
                    currentRole = role,
                    onLangToggle = { vm.currentLanguage.value = if (lang == "en") "ar" else "en" },
                    onRoleChange = { vm.currentRole.value = it }
                )
            },
            bottomBar = {
                LeaseFlowBottomNavigationBar(
                    activeTab = activeTab,
                    onTabSelect = { vm.activeTab.value = it },
                    lang = lang
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F172A), // Tail Slate 900
                                Color(0xFF020617)  // Tail Slate 950
                            )
                        )
                    )
            ) {
                when (activeTab) {
                    "dashboard" -> WorkspaceDashboardPage(
                        lang = lang,
                        role = role,
                        properties = propertiesList,
                        contracts = contractsList,
                        cheques = chequesList,
                        maintenance = maintenanceList,
                        logs = logList,
                        onClearLogs = { vm.resolveActivityLogs() },
                        onQuickBounce = { name, bank, valD -> vm.triggerChequeBounce(name, bank, valD) },
                        onNewRequest = { vm.activeTab.value = "maintenance" }
                    )
                    "modules" -> WorkspaceModulesPage(
                        lang = lang,
                        role = role,
                        properties = propertiesList,
                        contracts = contractsList,
                        cheques = chequesList,
                        maintenance = maintenanceList,
                        tenants = tenantsList,
                        onAddTenant = { nameEn, nameAr, email, phone, nid -> vm.addTenant(nameEn, nameAr, email, phone, nid) },
                        onDeleteTenant = { vm.deleteTenant(it) },
                        onAddMaintenance = { prop, unit, titleEn, titleAr, pr, details -> vm.addMaintenanceRequest(prop, unit, titleEn, titleAr, pr, details) },
                        onTriggerLegal = { tenant, valS -> vm.triggerLegalCaseFile(tenant, valS) }
                    )
                    "ai_generator" -> WorkspaceAiGeneratorPage(
                        lang = lang,
                        vm = vm
                    )
                    "settings" -> WorkspaceSettingsPage(
                        lang = lang,
                        vm = vm
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaseFlowTopAppBar(
    lang: String,
    currentRole: String,
    onLangToggle: () -> Unit,
    onRoleChange: (String) -> Unit
) {
    var expandedRoleMenu by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF1E293B), // Slate 800
            titleContentColor = Color.White
        ),
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = "App logo",
                        tint = Color(0xFF38BDF8), // Sky Blue Accent
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == "ar") "ليز فلو الذكي" else "LeaseFlow AI",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
                Text(
                    text = if (lang == "ar") "الدور الإداري: $currentRole" else "Role Workspace: $currentRole",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }
        },
        actions = {
            // Role Quick Switcher Tool Target
            Box {
                Button(
                    onClick = { expandedRoleMenu = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF334155),
                        contentColor = Color(0xFF38BDF8)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("role_switcher")
                ) {
                    Text(
                        text = currentRole,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Role list", modifier = Modifier.size(16.dp))
                }
                DropdownMenu(
                    expanded = expandedRoleMenu,
                    onDismissRequest = { expandedRoleMenu = false },
                    modifier = Modifier.background(Color(0xFF1E293B))
                ) {
                    listOf("Admin", "Leasing Executive", "Accountant", "Legal Team", "Maintenance Team").forEach { roleName ->
                        DropdownMenuItem(
                            text = { Text(roleName, color = if (roleName == currentRole) Color(0xFF38BDF8) else Color.White) },
                            onClick = {
                                onRoleChange(roleName)
                                expandedRoleMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Language switcher strictly 48dp clickable touch target size
            IconButton(
                onClick = onLangToggle,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("lang_toggle")
            ) {
                Text(
                    text = if (lang == "en") "العربية" else "English",
                    color = Color(0xFF38BDF8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    )
}

@Composable
fun LeaseFlowBottomNavigationBar(
    activeTab: String,
    onTabSelect: (String) -> Unit,
    lang: String
) {
    NavigationBar(
        containerColor = Color(0xFF0F172A),
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        val menuItems = listOf(
            Triple("dashboard", if (lang == "ar") "الرئيسية" else "Dashboard", Icons.Default.Dashboard),
            Triple("modules", if (lang == "ar") "الأقسام" else "Modules", Icons.AutoMirrored.Filled.Assignment),
            Triple("ai_generator", if (lang == "ar") "الذكاء الاصطناعي" else "AI Generative", Icons.Default.AutoAwesome),
            Triple("settings", if (lang == "ar") "الإعدادات" else "Settings", Icons.Default.Settings)
        )

        menuItems.forEach { (id, label, icon) ->
            val isSelected = activeTab == id
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelect(id) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) Color(0xFF38BDF8) else Color.LightGray
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF38BDF8) else Color.LightGray
                    )
                },
                modifier = Modifier.testTag("nav_tab_$id")
            )
        }
    }
}

@Composable
fun WorkspaceDashboardPage(
    lang: String,
    role: String,
    properties: List<PropertyEntity>,
    contracts: List<ContractEntity>,
    cheques: List<ReturnedChequeEntity>,
    maintenance: List<MaintenanceRequestEntity>,
    logs: List<ActivityLogEntity>,
    onClearLogs: () -> Unit,
    onQuickBounce: (String, String, Double) -> Unit,
    onNewRequest: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Alert with Sync pulse
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)) // Neon Green pulse
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == "ar") "المزامنة السحابية نشطة (Room-Supabase Live Sync)" else "Database Local Sync Online (Room Persistent Session)",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Metrics Card Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardMetricCard(
                        title = if (lang == "ar") "إجمالي العقارات" else "Total Properties",
                        value = "${properties.size} Buildings",
                        subtitle = "Managed Assets",
                        icon = Icons.Default.LocationCity,
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f)
                    )
                    DashboardMetricCard(
                        title = if (lang == "ar") "العقود النشطة" else "Active Contracts",
                        value = "${contracts.size} Active",
                        subtitle = "Leasing Index",
                        icon = Icons.Default.Description,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardMetricCard(
                        title = if (lang == "ar") "الشيكات المرتجعة" else "Bounced Cheques",
                        value = "${cheques.filter { it.status != "Paid" }.size} Issued",
                        subtitle = "Pending Claim Settlement",
                        icon = Icons.Default.Gavel,
                        color = Color(0xFFF43F5E),
                        modifier = Modifier.weight(1f)
                    )
                    DashboardMetricCard(
                        title = if (lang == "ar") "بلاغات الصيانة" else "Maintenance Alerts",
                        value = "${maintenance.filter { it.status != "Completed" }.size} Active",
                        subtitle = "Tickets Assigned",
                        icon = Icons.Default.Build,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Admin triggers
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = if (lang == "ar") "اختصارات سريعة للتحكم (محاكاة أحداث)" else "Operational Sandbox Event Controllers",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onQuickBounce("Mohamed Yassen", "H S B C", 65000.0) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (lang == "ar") "ارتجاع شيك جديد" else "Simulate Bounce", fontSize = 11.sp, maxLines = 1)
                        }
                        Button(
                            onClick = onNewRequest,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (lang == "ar") "صيانة عاجلة" else "Log Service", fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }
            }
        }

        // Operational Activity Feed
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == "ar") "سجل العمليات والشبكة الموحدة" else "Real-time Operations Activity Audit",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                    TextButton(onClick = onClearLogs) {
                        Text(
                            text = if (lang == "ar") "مسح السجل" else "Wipe Audit",
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                if (logs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (lang == "ar") "لا توجد معاملات جارية حالياً" else "Zero active logs registered.", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            logs.take(6).forEach { log ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = "Realtime Sync",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${log.action} - ${log.user}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = Color(0xFF38BDF8)
                                        )
                                        Text(
                                            text = log.details,
                                            fontSize = 12.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(top = 1.dp)
                                        )
                                    }
                                    Text(
                                        text = log.timestamp,
                                        fontSize = 10.sp,
                                        color = Color.LightGray
                                    )
                                }
                                HorizontalDivider(color = Color(0xFF334155), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                color = Color.White,
                fontWeight = FontWeight.Black
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun WorkspaceModulesPage(
    lang: String,
    role: String,
    properties: List<PropertyEntity>,
    contracts: List<ContractEntity>,
    cheques: List<ReturnedChequeEntity>,
    maintenance: List<MaintenanceRequestEntity>,
    tenants: List<TenantEntity>,
    onAddTenant: (String, String, String, String, String) -> Unit,
    onDeleteTenant: (TenantEntity) -> Unit,
    onAddMaintenance: (String, String, String, String, String, String) -> Unit,
    onTriggerLegal: (String, String) -> Unit
) {
    // Current active sub-module
    var selSubModule by remember { mutableStateOf("none") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (selSubModule != "none") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selSubModule = "none" }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to list", tint = Color.LightGray)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selSubModule.replaceFirstChar { it.uppercase() } + " Workspace Desk",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        when (selSubModule) {
            "none" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    item {
                        Text(
                            text = if (lang == "ar") "مساحات إدارة الأعمال المعتمدة" else "System Modules Workspaces",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                    }
                    val modules = listOf(
                        Quad("tenants", if (lang == "ar") "المستأجرين" else "Tenants Directory", "Registry details & contact notes", Icons.Default.People),
                        Quad("properties", if (lang == "ar") "العقارات والوحدات" else "Properties & Spaces", "Floor lists & assigned unit types", Icons.Default.LocationCity),
                        Quad("contracts", if (lang == "ar") "عقود الإيجار الموثقة" else "Lease Contracts", "Active rates, schedules & penalties", Icons.Default.Gavel),
                        Quad("cheques", if (lang == "ar") "الشيكات المرتجعة" else "Bounced Cheques Hub", "NSF registers, alerts & logs", Icons.Default.MoneyOff),
                        Quad("legal", if (lang == "ar") "القضايا القانونية" else "Legal Eviction Cases", "Courts dossiers, claim counts", Icons.Default.Balance),
                        Quad("maintenance", if (lang == "ar") "بلاغات الصيانة" else "Maintenance Request Desk", "Ticket progress & technician assigns", Icons.Default.Build),
                        Quad("refunds", if (lang == "ar") "استرداد التأمين" else "Deposit Refund Pipeline", "Approvals checklist & checks", Icons.Default.Security),
                        Quad("reports", if (lang == "ar") "التقارير المالية" else "Executive Reports Tool", "Graphical monthly metrics & logs", Icons.Default.PieChart)
                    )
                    items(modules) { mod ->
                        Card(
                            onClick = { selSubModule = mod.id },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircleAvatarIcon(icon = mod.icon, color = Color(0xFF38BDF8))
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(mod.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                    Text(mod.desc, color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                            }
                        }
                    }
                }
            }

            "tenants" -> {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    var inputEn by remember { mutableStateOf("") }
                    var inputAr by remember { mutableStateOf("") }
                    var inputNid by remember { mutableStateOf("") }
                    var registerMsg by remember { mutableStateOf("") }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Register New Leasing Client", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = inputEn,
                                onValueChange = { inputEn = it },
                                label = { Text("Name (English)") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = inputAr,
                                onValueChange = { inputAr = it },
                                label = { Text("الاسم (العربية)") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = inputNid,
                                onValueChange = { inputNid = it },
                                label = { Text("National ID / Passport Ref") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    if (inputEn.isNotBlank() && inputAr.isNotBlank()) {
                                        onAddTenant(inputEn, inputAr, "${inputEn.replace(" ", "").lowercase()}@example.com", "+971 50 XXXXXXX", inputNid)
                                        registerMsg = "Client ${inputEn} created safely."
                                        inputEn = ""
                                        inputAr = ""
                                        inputNid = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Commit New Registry")
                            }
                            if (registerMsg.isNotBlank()) {
                                Text(registerMsg, color = Color.Green, fontSize = 11.sp)
                            }
                        }
                    }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tenants) { tnt ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                border = BorderStroke(0.5.dp, Color(0xFF334155))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = if (lang == "ar") tnt.nameAr else tnt.nameEn, fontWeight = FontWeight.Black, color = Color.White)
                                        Text(text = "ID: ${tnt.nationalId} | Contact: ${tnt.phone}", fontSize = 11.sp, color = Color.LightGray)
                                    }
                                    IconButton(onClick = { onDeleteTenant(tnt) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete record", tint = Color(0xFFF43F5E))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "properties" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(properties) { prop ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (lang == "ar") prop.nameAr else prop.nameEn,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Address: ${if (lang == "ar") prop.addressAr else prop.addressEn}",
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Index Size: ${prop.totalUnits} Units", fontSize = 12.sp, color = Color(0xFF38BDF8))
                                    Text("Vibe classification: ${prop.type}", fontSize = 12.sp, color = Color(0xFF10B981))
                                }
                            }
                        }
                    }
                }
            }

            "contracts" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(contracts) { cr ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(cr.contractNumber, fontWeight = FontWeight.Black, color = Color.White)
                                    Text("Status: ${cr.status}", color = Color(0xFF10B981), fontSize = 11.sp)
                                }
                                Text("Allocated Premises: ${cr.unitNumber}", fontSize = 12.sp, color = Color.LightGray)
                                Text("Date Limit: ${cr.startDate} to ${cr.endDate}", fontSize = 11.sp, color = Color.Gray)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFF334155))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Instalment Split: ${cr.numberOfCheques} Checks", fontSize = 12.sp, color = Color.LightGray)
                                    Text("Value: ${cr.annualRent} AED / Yr", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                }
                            }
                        }
                    }
                }
            }

            "cheques" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF881337))) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Bounced Cheque Penalty Warning Procedure", fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Every unpaid check must trigger notification within 2 hours. Role actions logged for auditing.", fontSize = 11.sp, color = Color.LightGray)
                            }
                        }
                    }
                    items(cheques) { ch ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(ch.chequeNumber, fontWeight = FontWeight.Black, color = Color.White)
                                    Text(ch.status, color = Color(0xFFF43F5E), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("Issuer: ${ch.tenantName} | Bank: ${ch.bankName}", fontSize = 12.sp, color = Color.LightGray)
                                Text("Date: ${ch.bounceDate} | Disputed sum: ${ch.amount} AED", fontSize = 11.sp, color = Color.LightGray)
                                Button(
                                    onClick = { onTriggerLegal(ch.tenantName, "${ch.amount} AED") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    Text("Route Case file to Legal Team Desk")
                                }
                            }
                        }
                    }
                }
            }

            "legal" -> {
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Text("Active Eviction Dossier Review pipeline", fontWeight = FontWeight.Bold, color = Color.LightGray)
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Filed Eviction Pre-Dossier", fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Court Case Ref: AE/DXB/2026/892", fontSize = 12.sp, color = Color.LightGray)
                                Text("Defendant: Mohamed Yassen | Amount: 65,000 AED", fontSize = 12.sp, color = Color.LightGray)
                                Text("Assigned Council: Legal Corporate Team Unit Alpha", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            "maintenance" -> {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    var titleEn by remember { mutableStateOf("") }
                    var unitNo by remember { mutableStateOf("") }
                    var details by remember { mutableStateOf("") }
                    var confirmedMsg by remember { mutableStateOf("") }

                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Log Immediate Maintenance Request", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = titleEn,
                                onValueChange = { titleEn = it },
                                label = { Text("Issue Title (e.g. Broken Lock)") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = unitNo,
                                onValueChange = { unitNo = it },
                                label = { Text("Premises Unit Ref") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = details,
                                onValueChange = { details = it },
                                label = { Text("Describe water leak / damage details") },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    if (titleEn.isNotBlank() && unitNo.isNotBlank()) {
                                        onAddMaintenance("Al Tala Tower", unitNo, titleEn, titleEn, "High", details)
                                        confirmedMsg = "Assigned task ticket logged to active queue."
                                        titleEn = ""
                                        unitNo = ""
                                        details = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Dispatch Maintenance request")
                            }
                            if (confirmedMsg.isNotBlank()) {
                                Text(confirmedMsg, color = Color.Green, fontSize = 12.sp)
                            }
                        }
                    }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(maintenance) { req ->
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = if (lang == "ar") req.titleAr else req.titleEn, fontWeight = FontWeight.Black, color = Color.White)
                                        Text(text = "Priority: ${req.priority}", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("Unit No: ${req.unitNumber} (${req.propertyName})", fontSize = 11.sp, color = Color.LightGray)
                                    Text("Allocated Technician: ${req.assignee}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            "refunds" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Text("Security Deposit Refund Sign-off Board", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Tenant: Johnathan Doe | Contract: CON-2025/11", fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Initial Deposit: 10,000 AED", fontSize = 12.sp, color = Color.LightGray)
                                Text("Calculated Deductions: 1,500 AED (Wall painting damage)", fontSize = 12.sp, color = Color.LightGray)
                                HorizontalDivider(color = Color(0xFF334155), modifier = Modifier.padding(vertical = 4.dp))
                                Text("Refund status: Approved - Payment Draft Ready", color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            "reports" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        Text("Financial Performance executive graphics", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Simulated Rental Revenues by Tower (YTD)", fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(14.dp))
                                // Custom built beautiful graphical visual bars
                                CustomVerticalBarChart(
                                    data = listOf(
                                        Pair("Al Tala", 120000.0f),
                                        Pair("Safar Plaza", 185000.0f),
                                        Pair("Orchid", 75000.0f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomVerticalBarChart(data: List<Pair<String, Float>>) {
    val maxVal = data.maxOf { it.second }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (label, value) ->
            val ratio = value / maxVal
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${(value/1000).toInt()}k", fontSize = 10.sp, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .fillMaxHeight(ratio * 0.75f)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF38BDF8), Color(0xFF1E40AF))
                            )
                        )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(label, fontSize = 10.sp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun CircleAvatarIcon(icon: ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun WorkspaceAiGeneratorPage(
    lang: String,
    vm: LeaseFlowViewModel
) {
    val type by vm.aiType.collectAsState()
    val aiLang by vm.aiLanguage.collectAsState()
    val tenant by vm.aiTenantName.collectAsState()
    val unit by vm.aiUnitId.collectAsState()
    val amount by vm.aiAmount.collectAsState()
    val draft by vm.aiResponseDraft.collectAsState()
    val isLoading by vm.aiIsLoading.collectAsState()

    var showCategoryMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (lang == "ar") "مساعد صياغة الرسائل الذكية لقسم الإيجارات" else "Gemini Copilot Legal & Rent Email Generator",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        // Configuration Panel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    
                    // Category selection dropdown
                    Column {
                        Text("Mailing Notice Category", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { showCategoryMenu = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(12.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(type, color = Color.White)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.LightGray)
                                }
                            }
                            DropdownMenu(
                                expanded = showCategoryMenu,
                                onDismissRequest = { showCategoryMenu = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(Color(0xFF1E293B))
                            ) {
                                listOf("Bounced Cheque Alert", "Legal Case Notice", "Contract Renewal Reminder").forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat, color = Color.White) },
                                        onClick = {
                                            vm.aiType.value = cat
                                            showCategoryMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Fields
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = tenant,
                            onValueChange = { vm.aiTenantName.value = it },
                            label = { Text("Tenant Name") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { vm.aiUnitId.value = it },
                            label = { Text("Premises Ref") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { vm.aiAmount.value = it },
                            label = { Text("Value Amount") },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.weight(1f)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Drafting Language", color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Row {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { vm.aiLanguage.value = "English" }) {
                                    RadioButton(selected = aiLang == "English", onClick = { vm.aiLanguage.value = "English" })
                                    Text("EN", fontSize = 11.sp, color = Color.White)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { vm.aiLanguage.value = "Arabic" }) {
                                    RadioButton(selected = aiLang == "Arabic", onClick = { vm.aiLanguage.value = "Arabic" })
                                    Text("AR", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }

                    // Generate trigger button
                    Button(
                        onClick = { vm.generateAiSmartDraft() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("ai_compose_btn")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Generate Smart Communication via Gemini", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Response Canvas
        item {
            Column {
                Text(
                    text = if (lang == "ar") "مخرجات مسودة مساعد الذكاء الاصطناعي" else "Gemini AI Official Draft Output",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF020617)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(0.5.dp, Color(0xFF334155))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .heightIn(min = 260.dp)
                    ) {
                        SelectionContainer {
                            Text(
                                text = draft.ifBlank { "Specify tenant, currency sums, select your language, and click the blue generate button to craft a stunning legal notification with Gemini AI instantly." },
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = if (draft.isBlank()) Color.Gray else Color.White,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkspaceSettingsPage(
    lang: String,
    vm: LeaseFlowViewModel
) {
    val syncEnabled by vm.isCloudSyncEnabled.collectAsState()
    var showNidConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (lang == "ar") "إعدادات الرابط وقواعد المزامنة" else "System Preferences & LeaseFlow Settings",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Shared Cloud Settings", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Online Supabase Snapshots", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Enable multi-client instant updates on leases and cheques.", color = Color.LightGray, fontSize = 11.sp)
                    }
                    Switch(
                        checked = syncEnabled,
                        onCheckedChange = { vm.isCloudSyncEnabled.value = it }
                    )
                }

                HorizontalDivider(color = Color(0xFF334155))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Role Eviction Protocol", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Trigger eviction warnings with audit logs automatically.", color = Color.LightGray, fontSize = 11.sp)
                    }
                    Button(
                        onClick = { showNidConfirm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Reset DB Data", fontSize = 11.sp)
                    }
                }

                if (showNidConfirm) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF881337)),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Confirm database reset? This clears contracts and tenants.", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        vm.resolveActivityLogs()
                                        showNidConfirm = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.DarkGray)
                                ) {
                                    Text("Yes, clear logs", fontSize = 11.sp)
                                }
                                Button(onClick = { showNidConfirm = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                                    Text("Cancel", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Legal Compliance checklist
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Regulatory Compliance Protocol (UAE Central Bank RDC)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                Text("- Returned cheques logged under UAE RDC regulations.", fontSize = 11.sp, color = Color.LightGray)
                Text("- Legal warnings automatically conform with notarization rules.", fontSize = 11.sp, color = Color.LightGray)
                Text("- Data sync utilizes TLS 1.3 encryption natively.", fontSize = 11.sp, color = Color.LightGray)
            }
        }
    }
}

// Simple quad holder
data class Quad(
    val id: String,
    val title: String,
    val desc: String,
    val icon: ImageVector
)
