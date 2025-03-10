import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';

class ApiService {
  static const String _baseUrl = 'http://10.0.2.2:8000/main/';  

  // Login and save token
  static Future<void> login(String username, String password) async {
    final response = await http.post(
      Uri.parse('http://10.0.2.2:8000/api-token-auth/'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'username': username,
        'password': password,
      }),
    );

    if (response.statusCode == 200) {
      final token = jsonDecode(response.body)['token'];
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('token', token); 
      print('Saved Token: $token');

    } else {
      throw Exception('Login failed');
    }
  }

  // Fetch notes
  static Future<List<dynamic>> getNotes() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('token');
    if (token == null) {
      throw Exception('No token found');
    }
    print('Retrieved Token: $token');


    final response = await http.get(
      Uri.parse('${_baseUrl}notes/'),
      headers: {'Authorization': 'Token $token'},
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data.map((note) => {
        'id': note['id'],
        'title': note['title'] ?? 'No Title',
        'content': note['content'] ?? 'No Content',
        'labels': List<String>.from(note['labels'] ?? []),
        'reminder': note['reminder'],
      }).toList();
    } else {
      throw Exception('Failed to load notes');
    }
  }

  // Create note
  static Future<void> createNote(String title, String content, List<String> labels, DateTime? reminder) async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('token');
    
    if (token == null) {
    throw Exception('No token found - User not logged in');
  }

    print('Retrieved Token: $token');


    final body = {
      'title': title,
      'content': content,
      'labels': labels,
    };

    if (reminder != null) {
      body['reminder'] = reminder.toIso8601String();
    }

    final response = await http.post(
      Uri.parse('${_baseUrl}notes/'),
      headers: {
        'Authorization': 'Token $token',
        'Content-Type': 'application/json',
      },
      body: jsonEncode(body),
    );

    if (response.statusCode != 201) {
      throw Exception('Failed to create note: ${response.body}');
    }
  }

  // Update note
  static Future<void> updateNote(int id, String title, String content, List<String> labels, DateTime? reminder) async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('token');
    if (token == null) {
      throw Exception('No token found');
    }
    print('Retrieved Token: $token');


    final body = {
      'title': title,
      'content': content,
      'labels': labels,
    };

    if (reminder != null) {
      body['reminder'] = reminder.toIso8601String();
    }

    final response = await http.put(
      Uri.parse('${_baseUrl}notes/$id/'),
      headers: {
        'Authorization': 'Token $token',
        'Content-Type': 'application/json',
      },
      body: jsonEncode(body),
    );

    if (response.statusCode != 200) {
      throw Exception('Failed to update note');
    }
  }

  // Delete note
  static Future<void> deleteNote(int id) async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('token');
    if (token == null) {
      throw Exception('No token found');
    }
    print('Retrieved Token: $token');


    final response = await http.delete(
      Uri.parse('${_baseUrl}notes/$id/'),
      headers: {'Authorization': 'Token $token'},
    );

    if (response.statusCode != 204) {
      throw Exception('Failed to delete note');
    }
  }
}
