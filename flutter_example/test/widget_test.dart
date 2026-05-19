import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:flutter_example/main.dart';

void main() {
  testWidgets('AppUtilx demo renders core controls', (WidgetTester tester) async {
    await tester.pumpWidget(const MyApp());

    expect(find.text('AppUtilx (Android)'), findsOneWidget);
    expect(find.byType(TextField), findsOneWidget);
    expect(find.text('Toast'), findsOneWidget);
    expect(find.text('Network state'), findsOneWidget);
    expect(find.byType(ElevatedButton), findsWidgets);

    await tester.tap(find.text('Network state'));
    await tester.pump();

    expect(find.text('isConnected = null'), findsOneWidget);
  });
}
