import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const _channel = MethodChannel('apputilx/core');

Future<T?> _invoke<T>(String method, [Map<String, dynamic>? args]) async {
  if (!Platform.isAndroid) return null;
  return _channel.invokeMethod<T>(method, args);
}

enum AppThemeMode { material3, xiaomi }
enum AppDarkMode { system, light, dark }
enum AppLanguage { system, english, arabic }

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  AppThemeMode _themeMode = AppThemeMode.material3;
  AppDarkMode _darkMode = AppDarkMode.system;
  bool _glassEffect = true;
  AppLanguage _language = AppLanguage.system;

  @override
  Widget build(BuildContext context) {
    final isSystemDark =
        MediaQuery.platformBrightnessOf(context) == Brightness.dark;
    final isDark = switch (_darkMode) {
      AppDarkMode.system => isSystemDark,
      AppDarkMode.light => false,
      AppDarkMode.dark => true,
    };

    final isRtl = switch (_language) {
      AppLanguage.system => false,
      AppLanguage.english => false,
      AppLanguage.arabic => true,
    };

    final textDirection = isRtl ? TextDirection.rtl : TextDirection.ltr;

    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'AppUtilX Flutter Demo',
      themeMode: isDark ? ThemeMode.dark : ThemeMode.light,
      builder: (context, child) {
        return Directionality(
          textDirection: textDirection,
          child: child ?? const SizedBox.shrink(),
        );
      },
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF6750A4),
          brightness: Brightness.light,
        ),
        scaffoldBackgroundColor:
            _themeMode == AppThemeMode.xiaomi && _glassEffect
                ? Colors.transparent
                : const Color(0xFFF8FAF8),
      ),
      darkTheme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFFD0BCFF),
          brightness: Brightness.dark,
        ),
        scaffoldBackgroundColor:
            _themeMode == AppThemeMode.xiaomi && _glassEffect
                ? Colors.transparent
                : const Color(0xFF121020),
      ),
      home: DemoPage(
        themeMode: _themeMode,
        darkMode: _darkMode,
        isDark: isDark,
        glassEffect: _glassEffect,
        language: _language,
        isRtl: isRtl,
        onThemeModeChanged: (v) => setState(() => _themeMode = v),
        onDarkModeChanged: (v) => setState(() => _darkMode = v),
        onGlassEffectChanged: (v) => setState(() => _glassEffect = v),
        onLanguageChanged: (v) => setState(() => _language = v),
      ),
    );
  }
}

class DemoPage extends StatefulWidget {
  const DemoPage({
    super.key,
    required this.themeMode,
    required this.darkMode,
    required this.isDark,
    required this.glassEffect,
    required this.language,
    required this.isRtl,
    required this.onThemeModeChanged,
    required this.onDarkModeChanged,
    required this.onGlassEffectChanged,
    required this.onLanguageChanged,
  });

  final AppThemeMode themeMode;
  final AppDarkMode darkMode;
  final bool isDark;
  final bool glassEffect;
  final AppLanguage language;
  final bool isRtl;
  final ValueChanged<AppThemeMode> onThemeModeChanged;
  final ValueChanged<AppDarkMode> onDarkModeChanged;
  final ValueChanged<bool> onGlassEffectChanged;
  final ValueChanged<AppLanguage> onLanguageChanged;

  @override
  State<DemoPage> createState() => _DemoPageState();
}

class _DemoPageState extends State<DemoPage> {
  final _input = TextEditingController(text: 'Hello AppUtilX');

  @override
  void dispose() {
    _input.dispose();
    super.dispose();
  }

  void _showResultDialog(String title, String rawContent) {
    // Format text lines into bullet points
    final content = rawContent.split('\n').map((line) {
      if (line.contains('=')) {
        return line.split(' ').map((p) => p.contains('=') ? '• ${p.replaceAll('=', ': ')}' : p).join('\n');
      }
      return '• $line';
    }).join('\n');

    if (widget.themeMode == AppThemeMode.material3) {
      showDialog(
        context: context,
        builder: (ctx) => AlertDialog(
          title: Text(title),
          content: SelectableText(
            content,
            style: const TextStyle(fontFamily: 'monospace', fontSize: 13, height: 1.5),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(ctx),
              child: const Text('OK'),
            ),
          ],
        ),
      );
    } else {
      // Xiaomi Bottom Sheet Dialog
      showModalBottomSheet(
        context: context,
        backgroundColor: Colors.transparent,
        builder: (ctx) => _GlassPanel(
          isDark: widget.isDark,
          enabled: widget.glassEffect,
          padding: const EdgeInsets.all(20),
          child: SafeArea(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Text(
                  title,
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 12),
                SelectableText(
                  content,
                  style: const TextStyle(fontSize: 14, height: 1.5),
                ),
                const SizedBox(height: 16),
                FilledButton(
                  onPressed: () => Navigator.pop(ctx),
                  child: const Text('OK'),
                ),
              ],
            ),
          ),
        ),
      );
    }
  }

  void _openSettings() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (ctx) => StatefulBuilder(
        builder: (context, setModalState) {
          return _GlassPanel(
            isDark: widget.isDark,
            enabled: widget.themeMode == AppThemeMode.xiaomi && widget.glassEffect,
            padding: const EdgeInsets.all(20),
            child: SafeArea(
              child: Padding(
                padding: EdgeInsets.only(
                  bottom: MediaQuery.of(context).viewInsets.bottom,
                ),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          widget.isRtl ? 'الإعدادات' : 'Settings',
                          style: const TextStyle(
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        IconButton(
                          icon: const Icon(Icons.close),
                          onPressed: () => Navigator.pop(ctx),
                        ),
                      ],
                    ),
                    const Divider(),
                    ListTile(
                      title: Text(widget.isRtl ? 'الثيم' : 'Theme'),
                      trailing: DropdownButton<AppThemeMode>(
                        value: widget.themeMode,
                        items: [
                          DropdownMenuItem(
                            value: AppThemeMode.material3,
                            child: Text(
                              widget.isRtl ? 'Material 3' : 'Material 3',
                            ),
                          ),
                          DropdownMenuItem(
                            value: AppThemeMode.xiaomi,
                            child: Text(
                              widget.isRtl ? 'شاومي (HyperOS)' : 'Xiaomi (HyperOS)',
                            ),
                          ),
                        ],
                        onChanged: (v) {
                          if (v != null) {
                            widget.onThemeModeChanged(v);
                            setModalState(() {});
                          }
                        },
                      ),
                    ),
                    ListTile(
                      title: Text(widget.isRtl ? 'الوضع الداكن' : 'Dark Mode'),
                      trailing: DropdownButton<AppDarkMode>(
                        value: widget.darkMode,
                        items: [
                          DropdownMenuItem(
                            value: AppDarkMode.system,
                            child: Text(widget.isRtl ? 'النظام' : 'System'),
                          ),
                          DropdownMenuItem(
                            value: AppDarkMode.light,
                            child: Text(widget.isRtl ? 'فاتح' : 'Light'),
                          ),
                          DropdownMenuItem(
                            value: AppDarkMode.dark,
                            child: Text(widget.isRtl ? 'داكن' : 'Dark'),
                          ),
                        ],
                        onChanged: (v) {
                          if (v != null) {
                            widget.onDarkModeChanged(v);
                            setModalState(() {});
                          }
                        },
                      ),
                    ),
                    if (widget.themeMode == AppThemeMode.xiaomi)
                      SwitchListTile(
                        title: Text(
                          widget.isRtl ? 'تأثير الزجاج السائل' : 'Liquid Glass Effect',
                        ),
                        value: widget.glassEffect,
                        onChanged: (v) {
                          widget.onGlassEffectChanged(v);
                          setModalState(() {});
                        },
                      ),
                    ListTile(
                      title: Text(widget.isRtl ? 'اللغة' : 'Language'),
                      trailing: DropdownButton<AppLanguage>(
                        value: widget.language,
                        items: [
                          DropdownMenuItem(
                            value: AppLanguage.system,
                            child: Text(widget.isRtl ? 'النظام' : 'System'),
                          ),
                          DropdownMenuItem(
                            value: AppLanguage.english,
                            child: Text(widget.isRtl ? 'English' : 'English'),
                          ),
                          DropdownMenuItem(
                            value: AppLanguage.arabic,
                            child: Text(widget.isRtl ? 'العربية' : 'Arabic'),
                          ),
                        ],
                        onChanged: (v) {
                          if (v != null) {
                            widget.onLanguageChanged(v);
                            setModalState(() {});
                          }
                        },
                      ),
                    ),
                  ],
                ),
              ),
            ),
          );
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final isGlass =
        widget.themeMode == AppThemeMode.xiaomi && widget.glassEffect;

    Widget body = SafeArea(
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Top Bar
            _GlassPanel(
              isDark: widget.isDark,
              enabled: isGlass,
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    widget.isRtl ? 'تجربة AppUtilX' : 'AppUtilX Demo',
                    style: const TextStyle(
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  IconButton(
                    icon: const Icon(Icons.settings),
                    onPressed: _openSettings,
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),

            // Input Field Card
            _GlassPanel(
              isDark: widget.isDark,
              enabled: isGlass,
              padding: const EdgeInsets.all(12),
              child: TextField(
                controller: _input,
                decoration: InputDecoration(
                  labelText: widget.isRtl ? 'نص الإدخال' : 'Input text',
                  border: const OutlineInputBorder(),
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Section: Network
            _SectionTitle(widget.isRtl ? 'الشبكة' : 'Network'),
            _GlassPanel(
              isDark: widget.isDark,
              enabled: isGlass,
              child: Column(
                children: [
                  _ActionTile(
                    title: widget.isRtl ? 'تفاصيل الاتصال' : 'Connection Details',
                    onTap: () async {
                      final res = await _invoke<String>('networkState');
                      if (res != null) _showResultDialog(widget.isRtl ? 'تفاصيل الاتصال' : 'Connection Details', res);
                    },
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'نوع الاتصال والقیاس' : 'Network Transport',
                    onTap: () async {
                      final res = await _invoke<String>('networkTransport');
                      if (res != null) _showResultDialog(widget.isRtl ? 'نوع الاتصال والقیاس' : 'Network Transport', res);
                    },
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Section: Intents (Execute directly without dialog)
            _SectionTitle(widget.isRtl ? 'المقاصد (Intents)' : 'Intents'),
            _GlassPanel(
              isDark: widget.isDark,
              enabled: isGlass,
              child: Column(
                children: [
                  _ActionTile(
                    title: widget.isRtl ? 'فتح رابط' : 'Open URL',
                    onTap: () => _invoke('openUrl', {'url': 'https://apputilx.mohamedzaitoon.com'}),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'فتح إعدادات التطبيق' : 'Open App Settings',
                    onTap: () => _invoke('openAppSettings'),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'فتح محادثة واتساب' : 'Open WhatsApp',
                    onTap: () => _invoke('openWhatsApp', {'text': _input.text}),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'اتصال بالرقم' : 'Dial Number',
                    onTap: () => _invoke('dial'),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'إرسال رسالة SMS' : 'Send SMS',
                    onTap: () => _invoke('sendSms', {'text': _input.text}),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'إرسال بريد إلكتروني' : 'Send Email',
                    onTap: () => _invoke('sendEmail', {'text': _input.text}),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'مشاركة نص' : 'Share Text',
                    onTap: () => _invoke('shareText', {'text': _input.text}),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'فتح الخريطة' : 'Open Map',
                    onTap: () => _invoke('openMap'),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'متجر متجر بلاي' : 'Open Play Store',
                    onTap: () => _invoke('openPlayStore'),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Section: Clipboard & Screen
            _SectionTitle(widget.isRtl ? 'الحافظة والاهتزاز والمنع' : 'Clipboard & Screen'),
            _GlassPanel(
              isDark: widget.isDark,
              enabled: isGlass,
              child: Column(
                children: [
                  _ActionTile(
                    title: widget.isRtl ? 'نسخ النص' : 'Copy Text',
                    onTap: () => _invoke('copyText', {'text': _input.text}),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'لصق النص' : 'Paste Text',
                    onTap: () async {
                      final text = await _invoke<String>('getClipboard');
                      if (text != null) _showResultDialog(widget.isRtl ? 'محتوى الحافظة' : 'Clipboard Text', text);
                    },
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'اهتزاز سريع (200ms)' : 'Vibrate (200ms)',
                    onTap: () => _invoke('vibrate', {'ms': 200}),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'نمط اهتزاز' : 'Vibrate Pattern',
                    onTap: () => _invoke('vibratePattern'),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'حظر تصوير الشاشة' : 'Block Screen Capture',
                    onTap: () => _invoke('blockCapture'),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'إلغاء حظر تصوير الشاشة' : 'Unblock Screen Capture',
                    onTap: () => _invoke('unblockCapture'),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Section: Notifications & Keyboard
            _SectionTitle(widget.isRtl ? 'الإشعارات ولـوحة المفاتيح' : 'Notifications & Keyboard'),
            _GlassPanel(
              isDark: widget.isDark,
              enabled: isGlass,
              child: Column(
                children: [
                  _ActionTile(
                    title: widget.isRtl ? 'عرض إشعار' : 'Show Notification',
                    onTap: () => _invoke('showNotification', {'text': _input.text}),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'إلغاء الإشعارات' : 'Cancel Notifications',
                    onTap: () => _invoke('cancelNotifications'),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'إخفاء لوحة المفاتيح' : 'Hide Keyboard',
                    onTap: () => _invoke('hideKeyboard'),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Section: Device & App Info
            _SectionTitle(widget.isRtl ? 'الجهاز والتطبيق' : 'Device & App'),
            _GlassPanel(
              isDark: widget.isDark,
              enabled: isGlass,
              child: Column(
                children: [
                  _ActionTile(
                    title: widget.isRtl ? 'معلومات الجهاز' : 'Device Info',
                    onTap: () async {
                      final res = await _invoke<String>('deviceInfo');
                      if (res != null) _showResultDialog(widget.isRtl ? 'معلومات الجهاز' : 'Device Info', res);
                    },
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'معلومات التطبيق' : 'App Info',
                    onTap: () async {
                      final res = await _invoke<String>('appInfo');
                      if (res != null) _showResultDialog(widget.isRtl ? 'معلومات التطبيق' : 'App Info', res);
                    },
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'معلومات البطارية' : 'Battery Info',
                    onTap: () async {
                      final res = await _invoke<String>('batteryInfo');
                      if (res != null) _showResultDialog(widget.isRtl ? 'معلومات البطارية' : 'Battery Info', res);
                    },
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'حالة التطبيق' : 'App State',
                    onTap: () async {
                      final res = await _invoke<String>('appState');
                      if (res != null) _showResultDialog(widget.isRtl ? 'حالة التطبيق' : 'App State', res);
                    },
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Section: Data & Storage
            _SectionTitle(widget.isRtl ? 'البيانات والتخزين' : 'Data & Storage'),
            _GlassPanel(
              isDark: widget.isDark,
              enabled: isGlass,
              child: Column(
                children: [
                  _ActionTile(
                    title: widget.isRtl ? 'مساعدو الوقت' : 'Time Helpers',
                    onTap: () async {
                      final res = await _invoke<String>('timeNow');
                      if (res != null) _showResultDialog(widget.isRtl ? 'مساعدو الوقت' : 'Time Helpers', res);
                    },
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'مساعدو التحقق' : 'Validation',
                    onTap: () async {
                      final res = await _invoke<String>('validate');
                      if (res != null) _showResultDialog(widget.isRtl ? 'مساعدو التحقق' : 'Validation', res);
                    },
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'مساعدو التخزين' : 'Storage Info',
                    onTap: () async {
                      final res = await _invoke<String>('storage');
                      if (res != null) _showResultDialog(widget.isRtl ? 'مساعدو التخزين' : 'Storage Info', res);
                    },
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'كتابة ملف' : 'Write File',
                    onTap: () => _invoke('writeFile', {'text': _input.text}),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'قراءة ملف' : 'Read File',
                    onTap: () async {
                      final res = await _invoke<String>('readFile');
                      if (res != null) _showResultDialog(widget.isRtl ? 'قراءة ملف' : 'Read File', res);
                    },
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'حذف ملف' : 'Delete File',
                    onTap: () => _invoke('deleteFile'),
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'تشفير SHA-256' : 'SHA-256',
                    onTap: () async {
                      final res = await _invoke<String>('sha256', {'text': _input.text});
                      if (res != null) _showResultDialog('SHA-256', res);
                    },
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'تشفير Base64' : 'Base64',
                    onTap: () async {
                      final res = await _invoke<String>('base64', {'text': _input.text});
                      if (res != null) _showResultDialog('Base64', res);
                    },
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Section: Security
            _SectionTitle(widget.isRtl ? 'الأمان والبصمة' : 'Security & Biometric'),
            _GlassPanel(
              isDark: widget.isDark,
              enabled: isGlass,
              child: Column(
                children: [
                  _ActionTile(
                    title: widget.isRtl ? 'التوقيعات الرقمية' : 'App Signatures',
                    onTap: () async {
                      final res = await _invoke<String>('signatures');
                      if (res != null) _showResultDialog(widget.isRtl ? 'التوقيعات الرقمية' : 'App Signatures', res);
                    },
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'المصادقة بالبصمة' : 'Biometric Auth',
                    onTap: () async {
                      try {
                        final res = await _invoke<String>('biometric');
                        if (res != null) _showResultDialog(widget.isRtl ? 'البصمة' : 'Biometric', res);
                      } on PlatformException catch (e) {
                        _showResultDialog(widget.isRtl ? 'خطأ في البصمة' : 'Biometric Error', '${e.code}: ${e.message}');
                      }
                    },
                  ),
                  const Divider(height: 1),
                  _ActionTile(
                    title: widget.isRtl ? 'سجل الأخطاء (Logger)' : 'Logger',
                    onTap: () => _invoke('logger'),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
          ],
        ),
      ),
    );

    if (isGlass) {
      body = Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: widget.isDark
                ? const [
                    Color(0xFF121215),
                    Color(0xFF1A1A1E),
                    Color(0xFF0D0D10),
                  ]
                : const [
                    Color(0xFFEDEFF3),
                    Color(0xFFF4F6F9),
                    Color(0xFFE8EBF0),
                  ],
          ),
        ),
        child: body,
      );
    }

    return Scaffold(body: body);
  }
}

class _SectionTitle extends StatelessWidget {
  const _SectionTitle(this.title);

  final String title;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(left: 4, top: 8, bottom: 6),
      child: Text(
        title,
        style: TextStyle(
          fontSize: 15,
          fontWeight: FontWeight.bold,
          color: Theme.of(context).colorScheme.primary,
        ),
      ),
    );
  }
}

class _ActionTile extends StatelessWidget {
  const _ActionTile({required this.title, required this.onTap});

  final String title;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(
        title,
        style: const TextStyle(fontWeight: FontWeight.w500),
      ),
      trailing: const Icon(Icons.chevron_right, size: 20),
      onTap: onTap,
    );
  }
}

class _GlassPanel extends StatelessWidget {
  const _GlassPanel({
    required this.child,
    required this.isDark,
    this.enabled = true,
    this.padding = EdgeInsets.zero,
  });

  final Widget child;
  final bool isDark;
  final bool enabled;
  final EdgeInsetsGeometry padding;

  @override
  Widget build(BuildContext context) {
    if (!enabled) {
      return Card(
        margin: EdgeInsets.zero,
        child: Padding(padding: padding, child: child),
      );
    }

    final bgColors = isDark
        ? [
            const Color(0x2DFFFFFF),
            const Color(0x0EFFFFFF),
            const Color(0x1CFFFFFF),
          ]
        : [
            const Color(0xF0FFFFFF),
            const Color(0x88FFFFFF),
            const Color(0xD0FFFFFF),
          ];

    final borderColor = isDark ? const Color(0x80FFFFFF) : const Color(0xCCFFFFFF);

    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: isDark ? const Color(0xCC000000) : const Color(0x30000000),
            blurRadius: 10,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(16),
        child: Container(
          padding: padding,
          decoration: BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: bgColors,
            ),
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: borderColor, width: 1.5),
          ),
          child: child,
        ),
      ),
    );
  }
}
