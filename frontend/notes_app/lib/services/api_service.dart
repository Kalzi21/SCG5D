import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  static const String baseUrl = 'http://10.0.2.2:8000'; // Django backend URL

  // Fetch all notes for the authenticated user
  static Future<List<dynamic>> getNotes(String token) async {
    final response = await http.get(
      Uri.parse('$baseUrl/main/notes/'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Failed to load notes');
    }
  }

  // Create a new note
  static Future<void> createNote(String token, String title, String content) async {
    final response = await http.post(
      Uri.parse('$baseUrl/notes/'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
      body: jsonEncode({
        'title': title,
        'content': content,
        'labels': [], // Assuming labels are optional and default to empty list
        'reminder': null, // Assuming reminder is optional and default to null
      }),
    );

    if (response.statusCode != 201) {
      throw Exception('Failed to create note');
    }
  }

  // Update a note
  static Future<void> updateNote(String token, int id, String title, String content) async {
    try {
    final response = await http.post(
      Uri.parse('$baseUrl/notes/'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
      body: jsonEncode({
        'title': title,
        'content': content,
        'labels': [], // Optional
        'reminder': null, // Optional
      }),
    );

    if (response.statusCode != 201) {
      print('Failed to create note. Status code: ${response.statusCode}');
      print('Response body: ${response.body}');
      throw Exception('Failed to create note');
    }
  } catch (e) {
    print('Error creating note: $e');
    throw Exception('Failed to create note');
  }
  }

  // Delete a note
  static Future<void> deleteNote(String token, int id) async {
    final response = await http.delete(
      Uri.parse('$baseUrl/notes/$id/'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode != 204) {
      throw Exception('Failed to delete note');
    }
  }
}