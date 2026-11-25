import '../../domain/entities/client_entity.dart';

class ClientModel extends ClientEntity {
  const ClientModel({
    required super.id,
    required super.name,
    super.logoUrl,
    super.primaryColor,
  });

  factory ClientModel.fromJson(Map<String, dynamic> json) {
    return ClientModel(
      id: (json['id'] as num).toInt(),
      name: json['name'] as String,
      logoUrl: (json['logoUrl'] ?? json['logo_url']) as String?,
      primaryColor: (json['primaryColor'] ?? json['primary_color']) as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'logoUrl': logoUrl,
      'primaryColor': primaryColor,
    };
  }
}