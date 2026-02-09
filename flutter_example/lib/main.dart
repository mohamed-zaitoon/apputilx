import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

const _channel = MethodChannel('apputilx/demo');

Future<T?> _invoke<T>(String method, [Map<String, dynamic>? args]) async {
  if (!Platform.isAndroid) return null;
  return await _channel.invokeMethod<T>(method, args);
}

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'AppUtilx Flutter Demo',
      theme: ThemeData(colorScheme: ColorScheme.fromSeed(seedColor: Colors.teal)),
      home: const DemoPage(),
    );
  }
}

class DemoPage extends StatefulWidget {
  const DemoPage({super.key});

  @override
  State<DemoPage> createState() => _DemoPageState();
}

class _DemoPageState extends State<DemoPage> {
  final _input = TextEditingController(text: "Hello from Flutter");
  String _output = "النتائج ستظهر هنا";

  void _setOutput(String text) => setState(() => _output = text);

  Widget _btn(String text, VoidCallback onTap) =>
      ElevatedButton(onPressed: onTap, child: Text(text));

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('AppUtilx (Android)')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            TextField(
              controller: _input,
              decoration: const InputDecoration(
                labelText: 'نص الإدخال',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            Container(
              padding: const EdgeInsets.all(12),
              color: Colors.grey.shade200,
              child: Text(_output),
            ),
            const SizedBox(height: 12),
            _btn('Toast', () async {
              await _invoke('showToast', {'msg': _input.text});
            }),
            _btn('Open URL', () async {
              await _invoke('openUrl', {'url': 'https://example.com'});
              _setOutput('Opening https://example.com');
            }),
            _btn('Network state', () async {
              final ok = await _invoke<bool>('isConnected');
              _setOutput('isConnected = $ok');
            }),
            _btn('Copy to clipboard', () async {
              await _invoke('copyText', {'text': _input.text});
              _setOutput('Copied');
            }),
            _btn('Read clipboard', () async {
              final text = await _invoke<String>('getClipboard');
              _setOutput('Clipboard: $text');
            }),
            _btn('Vibrate 200ms', () => _invoke('vibrate', {'ms': 200})),
            _btn('Vibrate pattern', () => _invoke('vibratePattern')),
            _btn('Show notification', () => _invoke('showNotification')),
            _btn('Device info', () async {
              _setOutput(await _invoke<String>('deviceInfo') ?? '');
            }),
            _btn('Battery info', () async {
              _setOutput(await _invoke<String>('batteryInfo') ?? '');
            }),
            _btn('Time now', () async {
              _setOutput(await _invoke<String>('timeNow') ?? '');
            }),
            _btn('Validate sample', () async {
              _setOutput('${await _invoke('validate')}');
            }),
            _btn('Storage info', () async {
              _setOutput('${await _invoke('storage')}');
            }),
            _btn('Write file (demo.txt)', () async {
              await _invoke('writeFile', {'text': _input.text});
              _setOutput('Wrote demo.txt');
            }),
            _btn('Read file (demo.txt)', () async {
              _setOutput('${await _invoke('readFile')}');
            }),
            _btn('Delete file (demo.txt)', () async {
              _setOutput('${await _invoke('deleteFile')}');
            }),
            _btn('SHA-256', () async {
              _setOutput('${await _invoke('sha256', {'text': _input.text})}');
            }),
            _btn('Base64 encode/decode', () async {
              _setOutput('${await _invoke('base64', {'text': _input.text})}');
            }),
            _btn('App state', () async {
              _setOutput('${await _invoke('appState')}');
            }),
            _btn('Signatures', () async {
              _setOutput('${await _invoke('signatures')}');
            }),
            _btn('Biometric', () async {
              try {
                _setOutput('${await _invoke('biometric')}');
              } on PlatformException catch (e) {
                _setOutput('Biometric error: ${e.code} ${e.message}');
              }
            }),
            _btn('Open app settings', () => _invoke('openAppSettings')),
          ],
        ),
      ),
    );
  }
}
