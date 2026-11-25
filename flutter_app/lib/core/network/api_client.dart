import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../config/app_config.dart';

class ApiClient {
  late final Dio _dio;
  final FlutterSecureStorage _storage = const FlutterSecureStorage();

  ApiClient() {
    _dio = Dio(
      BaseOptions(
        baseUrl: AppConfig.baseUrl,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        connectTimeout: const Duration(seconds: 30),
        receiveTimeout: const Duration(seconds: 30),
      ),
    );

    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          // Atualiza o tenant dinamicamente a cada requisição
          options.headers['X-Client-Domain'] = AppConfig.clientDomain;

          // Adiciona o token JWT se existir
          final token = await _storage.read(key: AppConfig.tokenKey);
          if (token != null && token.isNotEmpty) {
            options.headers['Authorization'] = 'Bearer $token';
          }
          return handler.next(options);
        },
        onError: (error, handler) async {
          // Limpa sessão apenas se der 401 fora das rotas públicas de auth
          final path = error.requestOptions.path;
          if (error.response?.statusCode == 401 &&
              !path.contains('/auth/login') &&
              !path.contains('/auth/register')) {
            await _storage.delete(key: AppConfig.tokenKey);
            await _storage.delete(key: AppConfig.userKey);
          }
          return handler.next(error);
        },
      ),
    );
  }

  Dio get dio => _dio;
}