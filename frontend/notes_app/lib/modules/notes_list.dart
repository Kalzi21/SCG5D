import 'package:flutter/material.dart';
import 'package:notes_app/modules/note_edit.dart';
import 'package:notes_app/modules/note_form.dart';
import 'package:notes_app/services/api_service.dart';
// import 'notes_dialog.dart';


class NoteListScreen extends StatefulWidget {
  @override
  _NoteListScreenState createState() => _NoteListScreenState();
}

class _NoteListScreenState extends State<NoteListScreen> {
  List<dynamic> notes = [];

  @override
  void initState() {
    super.initState();
    _fetchNotes();
  }

  Future<void> _fetchNotes() async {
  try {
    final data = await ApiService.getNotes();
    print('Fetched Notes: $data');  // Log the fetched notes
    setState(() {
      notes = data;
    });
  } catch (e) {
    print('Error: $e');
  }
}

  Future<void> _createNote(String title, String content,List<String> labels, DateTime? reminder) async {
    try {
      await ApiService.createNote(title, content,labels, reminder);
      await _fetchNotes();  // Refresh the note list after creation
    } catch (e) {
      print('Error: $e');
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed to create note: $e')),
      );
    }
  }

  Future<void> _updateNote(int id, String title, String content, List<String> labels, DateTime? reminder) async {
  try {
    await ApiService.updateNote(id, title, content, labels, reminder);
    await _fetchNotes();  // Refresh the note list after updating
  } catch (e) {
    print('Error: $e');
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('Failed to update note: $e')),
    );
  }
}

Future<void> _deleteNote(int id) async {
  try {
    await ApiService.deleteNote(id);
    await _fetchNotes();  // Refresh the note list after deletion
  } catch (e) {
    print('Error: $e');
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('Failed to delete note: $e')),
    );
  }
}

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Notes')),
      body: notes.isEmpty
          ? Center(child: Text('No notes available'))
          : ListView.builder(
  itemCount: notes.length,
  itemBuilder: (context, index) {
    final note = notes[index];
    return ListTile(
      title: Text(note['title'] ?? 'No Title'),  // Handle null title
      subtitle: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(note['content'] ?? 'No Content'),  // Handle null content
          if (note['labels'] != null && note['labels'].isNotEmpty)
            Wrap(
              spacing: 8.0,
              children: List<String>.from(note['labels'] ?? []).map<Widget>((label) {
                      return Chip(
                  label: Text(label),
                );
              }).toList(),
            ),
          if (note['reminder'] != null)
            Text('Reminder: ${DateTime.parse(note['reminder']).toString()}'),
        ],
      ),
      onTap: () async {
        // Navigate to the EditNoteScreen and wait for the result
        final result = await Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => EditNoteScreen(note: note),
          ),
        );

        // If the user submitted the form, update the note
        if (result != null) {
          await _updateNote(
                result['id'],
                result['title'],
                result['content'],
                List<String>.from(result['labels'] ?? []),  // Ensure it's a list
                result['reminder'],
              );
             }
      },
    );
  },
),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          // Navigate to the NoteFormScreen and wait for the result
          final result = await Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => NoteFormScreen(),
            ),
          );

          // If the user submitted the form, create the note
          if (result != null) {
            await _createNote(result['title'], result['content'], result['labels'], result['reminder']);
;
          }
        },  // Create a new note
        child: Icon(Icons.add),
      ),
    );
  }
}


