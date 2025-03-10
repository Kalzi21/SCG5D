import 'package:flutter/material.dart';
import 'package:notes_app/modules/login.dart';
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
      initialRoute: '/login',
      routes: {
        '/login': (context) => LoginScreen(),
        '/home': (context) => NoteListScreen(),
      },
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: NoteListScreen(), // Replace with your token
    );
  }
}