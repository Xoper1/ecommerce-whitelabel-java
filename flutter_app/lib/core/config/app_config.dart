import 'dart:io' show Platform;
import 'package:flutter/foundation.dart' show kIsWeb;

class AppConfig {
  // Ajusta automaticamente para 10.0.2.2 no Emulador Android e localhost no Chrome/Windows
  static String get baseUrl {
    if (!kIsWeb && Platform.isAndroid) {
      return 'http://10.0.2.2:3000';
    }
    return 'http://localhost:3000';
  }

  // Loja padrão (cliente3.local = Multi Store com fornecedores BR + EU)
  static String clientDomain = 'cliente3.local';

  // Storage keys
  static const String tokenKey = 'auth_token';
  static const String userKey = 'user_data';
  static const String clientConfigKey = 'client_config';

  static String getCurrentDomain() {
    return clientDomain;
  }

  static void setClientDomain(String domain) {
    clientDomain = domain;
  }

  // Domain mappings for testing
  static const Map<String, String> domainMappings = {
    'devnology.com:8000': 'cliente1.local',
    'in8.com:8000': 'cliente2.local',
    'localhost:8080': 'cliente3.local',
  };

  static String getDomainFromUrl(String url) {
    final uri = Uri.parse(url);
    final hostWithPort = '${uri.host}:${uri.port}';
    return domainMappings[hostWithPort] ?? clientDomain;
  }
}