import 'package:flutter/material.dart';
// import 'services/api_service.dart'; // Import the API service
import 'modules/notes_list.dart';

void main() {
  runApp(NotesApp());
}

class NotesApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Notes App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: NotesListScreen(token: 'YOUR_AUTH_TOKEN'), // Replace with your token
    );
  }
}