import 'package:flutter/material.dart';

void main() {
  runApp(const LeaseFlowApp());
}

class LeaseFlowApp extends StatefulWidget {
  const LeaseFlowApp({super.key});

  @override
  State<LeaseFlowApp> createState() => _LeaseFlowAppState();
}

class _LeaseFlowAppState extends State<LeaseFlowApp> {
  bool _isArabic = false;
  bool _isDarkMode = true;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'LeaseFlow AI',
      debugShowCheckedModeBanner: false,
      locale: _isArabic ? const Locale('ar', 'AE') : const Locale('en', 'US'),
      supportedLocales: const [
        Locale('en', 'US'),
        Locale('ar', 'AE'),
      ],
      themeMode: _isDarkMode ? ThemeMode.dark : ThemeMode.light,
      theme: ThemeData(
        useMaterial3: true,
        brightness: Brightness.light,
        colorSchemeSeed: Colors.blue,
      ),
      darkTheme: ThemeData(
        useMaterial3: true,
        brightness: Brightness.dark,
        colorSchemeSeed: Colors.sky,
        scaffoldBackgroundColor: const Color(0xFF0F172A), // Slate-900
      ),
      home: MainNavigationScreen(
        isArabic: _isArabic,
        isDarkMode: _isDarkMode,
        onLangToggle: () {
          setState(() {
            _isArabic = !_isArabic;
          });
        },
        onThemeToggle: () {
          setState(() {
            _isDarkMode = !_isDarkMode;
          });
        },
      ),
    );
  }
}

class MainNavigationScreen extends StatefulWidget {
  final bool isArabic;
  final bool isDarkMode;
  final VoidCallback onLangToggle;
  final VoidCallback onThemeToggle;

  const MainNavigationScreen({
    super.key,
    required this.isArabic,
    required this.isDarkMode,
    required this.onLangToggle,
    required this.onThemeToggle,
  });

  @override
  State<MainNavigationScreen> createState() => _MainNavigationScreenState();
}

class _MainNavigationScreenState extends State<MainNavigationScreen> {
  int _selectedIndex = 0;
  String _currentRole = "Leasing Executive";

  @override
  Widget build(BuildContext context) {
    // Localization labels dictionary
    final labels = widget.isArabic
        ? {
            'dashboard': 'لوحة التحكم',
            'tenants': 'المستأجرين',
            'contracts': 'العقود',
            'properties': 'العقارات والوحدات',
            'returnedCheques': 'الشيكات المرتجعة',
            'maintenance': 'طلبات الصيانة',
            'aiGenerator': 'مولد الإيميل بالذكاء الاصطناعي',
            'legal': 'المطالبات القانونية',
            'role': 'دور المستخدم الحالي',
          }
        : {
            'dashboard': 'Dashboard',
            'tenants': 'Tenants',
            'contracts': 'Contracts',
            'properties': 'Properties & Units',
            'returnedCheques': 'Bounced Cheques',
            'maintenance': 'Maintenance',
            'aiGenerator': 'AI Generator',
            'legal': 'Legal Cases',
            'role': 'User Role',
          };

    final List<Widget> screens = [
      _buildDashboard(labels),
      _buildTenants(labels),
      _buildReturnedCheques(labels),
      _buildAiGenerator(labels),
    ];

    return Directionality(
      textDirection: widget.isArabic ? TextDirection.rtl : TextDirection.ltr,
      child: Scaffold(
        appBar: AppBar(
          title: Row(
            children: [
              const Icon(Icons.business, color: Colors.skyAccent, size: 28),
              const SizedBox(width: 8),
              Text(
                widget.isArabic ? 'ليز فلو الذكي' : 'LeaseFlow AI',
                style: const TextStyle(fontWeight: FontWeight.black, letterSpacing: -0.5),
              ),
            ],
          ),
          elevation: 2,
          actions: [
            // User Role Selector
            DropdownButton<String>(
              value: _currentRole,
              underline: const SizedBox(),
              icon: const Icon(Icons.arrow_drop_down, color: Colors.skyAccent),
              onChanged: (String? newValue) {
                if (newValue != null) {
                  setState(() {
                    _currentRole = newValue;
                  });
                }
              },
              items: <String>[
                'Admin',
                'Leasing Executive',
                'Accountant',
                'Legal Team',
                'Maintenance Team'
              ].map<DropdownMenuItem<String>>((String value) {
                return DropdownMenuItem<String>(
                  value: value,
                  child: Text(
                    value,
                    style: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold),
                  ),
                );
              }).toList(),
            ),
            IconButton(
              onPressed: widget.onLangToggle,
              icon: Text(widget.isArabic ? 'EN' : 'عربي',
                  style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13, color: Colors.skyAccent)),
            ),
            IconButton(
              onPressed: widget.onThemeToggle,
              icon: Icon(widget.isDarkMode ? Icons.light_mode : Icons.dark_mode, color: Colors.amberAccent),
            ),
          ],
        ),
        body: IndexedStack(
          index: _selectedIndex,
          children: screens,
        ),
        bottomNavigationBar: NavigationBar(
          selectedIndex: _selectedIndex,
          onDestinationSelected: (int index) {
            setState(() {
              _selectedIndex = index;
            });
          },
          destinations: [
            NavigationDestination(
              icon: const Icon(Icons.dashboard_outlined),
              selectedIcon: const Icon(Icons.dashboard, color: Colors.skyAccent),
              label: labels['dashboard']!,
            ),
            NavigationDestination(
              icon: const Icon(Icons.people_alt_outlined),
              selectedIcon: const Icon(Icons.people_alt, color: Colors.skyAccent),
              label: labels['tenants']!,
            ),
            NavigationDestination(
              icon: const Icon(Icons.money_off_outlined),
              selectedIcon: const Icon(Icons.money_off, color: Colors.skyAccent),
              label: labels['returnedCheques']!,
            ),
            NavigationDestination(
              icon: const Icon(Icons.psychology_outlined),
              selectedIcon: const Icon(Icons.psychology, color: Colors.skyAccent),
              label: labels['aiGenerator']!,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDashboard(Map<String, String> labels) {
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        // Welcome and Status banner
        Card(
          color: Theme.of(context).colorScheme.primaryContainer.withOpacity(0.2),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            widget: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    const Icon(Icons.lock_clock, color: Colors.skyAccent),
                    const SizedBox(width: 8),
                    Text(
                      widget.isArabic ? 'المزامنة السحابية نشطة' : 'Cloud Sync Connected',
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 13),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                Text(
                  widget.isArabic 
                      ? 'مرحباً بك في ليز فلو العقاري. واجهة الصلاحية الحالية: $_currentRole'
                      : 'Welcome back safely. Current Operational Role: $_currentRole',
                  style: const TextStyle(fontSize: 12),
                )
              ],
            ),
          ),
        ),
        const SizedBox(height: 20),
        // Metrics Grid
        GridView.count(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          crossAxisCount: 2,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
          childAspectRatio: 1.5,
          children: [
            _buildStatCard(labels['properties']!, "48 Units", Icons.apartment, Colors.sky),
            _buildStatCard(labels['contracts']!, "112 Active", Icons.gavel, Colors.emerald),
            _buildStatCard(labels['returnedCheques']!, "4 Cheques", Icons.warning_amber, Colors.rose),
            _buildStatCard(labels['maintenance']!, "3 Open", Icons.construction, Colors.orange),
          ],
        ),
        const SizedBox(height: 24),
        Text(
          widget.isArabic ? 'آخر التنبيهات والعمليات المزامنة' : 'Real-time System Audit Stream',
          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 8),
        _buildLogTile("CONTRACT_CREATED", "Contract #2026/049 registered", "5m ago"),
        _buildLogTile("CHEQUE_BOUNCED", "Cheque #90213 bounced (NSF)", "1h ago"),
        _buildLogTile("MAINTENANCE_LOGGED", "Water leak issue at Tala Tower Unit #304", "1d ago"),
      ],
    );
  }

  Widget _buildStatCard(String title, String value, IconData icon, Color color) {
    return Card(
      elevation: 1,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
        child: Row(
          children: [
            CircleAvatar(
              backgroundColor: color.withOpacity(0.15),
              child: Icon(icon, color: color, size: 20),
            ),
            const SizedBox(width: 10),
            Expanded(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(title, style: const TextStyle(fontSize: 10, color: Colors.grey, fontWeight: FontWeight.bold), overflow: TextOverflow.ellipsis),
                  Text(value, style: const TextStyle(fontSize: 14, fontWeight: FontWeight.black)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildLogTile(String action, String details, String time) {
    return ListTile(
      leading: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(color: Colors.sky.withOpacity(0.1), borderRadius: BorderRadius.circular(10)),
        child: const Icon(Icons.sync, color: Colors.sky, size: 16),
      ),
      title: Text(action, style: const TextStyle(fontSize: 11, fontWeight: FontWeight.bold, color: Colors.grey)),
      subtitle: Text(details, style: const TextStyle(fontSize: 13, fontWeight: FontWeight.bold)),
      trailing: Text(time, style: const TextStyle(fontSize: 10, color: Colors.grey)),
    );
  }

  Widget _buildTenants(Map<String, String> labels) {
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        SearchBar(
          hintText: widget.isArabic ? "البحث عن مستأجر..." : "Search tenant name/phone...",
          leading: const Icon(Icons.search),
        ),
        const SizedBox(height: 16),
        _buildTenantCard("Mohamed Yassen", "mohamed@example.com", "Active", "+971 50 123 4567"),
        _buildTenantCard("Amira Salem", "amira@example.com", "Active", "+971 50 987 6543"),
      ],
    );
  }

  Widget _buildTenantCard(String name, String email, String status, String phone) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: ListTile(
        title: Text(name, style: const TextStyle(fontWeight: FontWeight.bold)),
        subtitle: Text("$email\n$phone"),
        isThreeLine: true,
        trailing: Container(
          padding: const EdgeInsets.all(6),
          decoration: BoxDecoration(color: Colors.emerald.withOpacity(0.2), borderRadius: BorderRadius.circular(4)),
          child: Text(status, style: const TextStyle(color: Colors.green, fontSize: 10, fontWeight: FontWeight.bold)),
        ),
      ),
    );
  }

  Widget _buildReturnedCheques(Map<String, String> labels) {
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.between,
          children: [
            Text(labels['returnedCheques']!, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            ElevatedButton.icon(
              onPressed: () {},
              icon: const Icon(Icons.camera_alt),
              label: Text(widget.isArabic ? "رفع الشيك" : "Upload Cheque Image"),
            )
          ],
        ),
        const SizedBox(height: 16),
        _buildChequeCard("Cheque #90213", "NSF (Insufficient Funds)", "50,000 AED", "Mohamed Yassen", "Pending Legal Action"),
      ],
    );
  }

  Widget _buildChequeCard(String num, String code, String val, String tenant, String status) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.between,
              children: [
                Text(num, style: const TextStyle(fontWeight: FontWeight.black, fontSize: 16)),
                Text(val, style: const TextStyle(color: Colors.rose, fontWeight: FontWeight.bold)),
              ],
            ),
            const SizedBox(height: 8),
            Text("Tenant: $tenant", style: const TextStyle(fontWeight: FontWeight.bold)),
            Text("Issue code: $code"),
            const Divider(),
            Text("Stage: $status", style: const TextStyle(color: Colors.orange, fontWeight: FontWeight.bold, fontSize: 12)),
          ],
        ),
      ),
    );
  }

  Widget _buildAiGenerator(Map<String, String> labels) {
    final types = ["Bounced Cheque Alert", "Legal evictions", "Renewal Reminders"];
    String selectedType = types[0];
    TextEditingController tnt = TextEditingController(text: "Mohamed Yassen");
    TextEditingController amt = TextEditingController(text: "50,050 AED");
    String preview = "Click generate below to compile Gemini mail in chosen format...";

    return StatefulBuilder(
      builder: (context, setWidgetState) {
        return ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Text(widget.isArabic ? "مولد الرسائل الموثق" : "Gemini Generative Email Hub", style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            Card(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    TextField(
                      controller: tnt,
                      decoration: const InputDecoration(labelText: "Tenant Name"),
                    ),
                    const SizedBox(height: 10),
                    TextField(
                      controller: amt,
                      decoration: const InputDecoration(labelText: "Amount"),
                    ),
                    const SizedBox(height: 14),
                    ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.sky,
                        foregroundColor: Colors.white,
                        minimumSize: const Size.fromHeight(48),
                      ),
                      onPressed: () {
                        setWidgetState(() {
                          preview = widget.isArabic
                              ? "إشعار رسمي عاجل\n\nعزيزي ${tnt.text} الموقر،\n\nنود إخطاركم بأن الشيك البالغ قيمته ${amt.text} قد تم إرجاعه من قبل البنك المسحوب عليه. يرجى سداد القيمة الإيجارية خلال 48 ساعة لتلافي الإجراءات القضائية.\n\nمع التحية،\nإدارة الممتلكات."
                              : "Urgent Official Notice: Returned Cheque\n\nDear ${tnt.text},\n\nWe refer to your lease agreement. Your recent rent cheque in the amount of ${amt.text} was returned unpaid by your bank.\n\nPlease settle this outstanding balance immediately to avoid lease termination actions.\n\nSincerely,\nProperty Operations.";
                        });
                      },
                      child: const Text("Generate Smart Draft", style: TextStyle(fontWeight: FontWeight.black)),
                    )
                  ],
                ),
              ),
            ),
            const SizedBox(height: 20),
            const Text("AI Resulting Output", style: TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.black12,
                border: Border.all(color: Colors.white12),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                preview,
                style: const TextStyle(fontFamily: 'monospace', fontSize: 13, height: 1.4),
              ),
            )
          ],
        );
      },
    );
  }
}
