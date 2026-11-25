import '../../core/config/app_config.dart';
import '../../domain/entities/user_entity.dart';
import 'client_model.dart';

class UserModel extends UserEntity {
  const UserModel({
    required super.id,
    required super.email,
    required super.name,
    required super.client,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    final clientData = json['client'];
    final ClientModel clientModel;

    if (clientData is Map<String, dynamic>) {
      clientModel = ClientModel.fromJson(clientData);
    } else {
      clientModel = ClientModel(
        id: (json['clientId'] as num?)?.toInt() ?? 0,
        name: AppConfig.clientDomain,
      );
    }

    return UserModel(
      id: (json['id'] as num).toInt(),
      email: json['email'] as String,
      name: json['name'] as String,
      client: clientModel,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'name': name,
      'clientId': client.id,
      'client': (client as ClientModel).toJson(),
    };
  }
}