import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'core/config/app_config.dart';
import 'core/di/injection.dart';
import 'presentation/auth/blocs/auth_bloc.dart';
import 'presentation/auth/pages/login_page.dart';
import 'presentation/auth/pages/register_page.dart';
import 'presentation/products/blocs/products_bloc.dart';
import 'presentation/products/pages/products_list_page.dart';
import 'presentation/theme/blocs/theme_bloc.dart';
import 'presentation/theme/blocs/theme_event.dart';
import 'presentation/theme/blocs/theme_state.dart';
import 'presentation/shared/pages/splash_page.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Setup dependency injection
  setupDependencies();

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
      providers: [
        BlocProvider<ThemeBloc>(
          create: (context) => getIt<ThemeBloc>()
            ..add(LoadClientTheme(domain: AppConfig.clientDomain)),
        ),
        BlocProvider<AuthBloc>(
          create: (context) => getIt<AuthBloc>(),
        ),
        BlocProvider<ProductsBloc>(
          create: (context) => getIt<ProductsBloc>(),
        ),
      ],
      child: BlocBuilder<ThemeBloc, ThemeState>(
        builder: (context, state) {
          ThemeData theme;

          if (state is ThemeLoaded) {
            theme = state.themeData;
          } else {
            theme = ThemeData(
              colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
              useMaterial3: true,
            );
          }

          return MaterialApp(
            title: 'E-commerce Whitelabel',
            debugShowCheckedModeBanner: false,
            theme: theme,
            initialRoute: '/',
            routes: {
              '/': (context) => const SplashPage(),
              '/login': (context) => const LoginPage(),
              '/register': (context) => const RegisterPage(),
              '/products': (context) => const ProductsListPage(),
            },
          );
        },
      ),
    );
  }
}