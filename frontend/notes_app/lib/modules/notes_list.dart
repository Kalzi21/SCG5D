import 'package:flutter/material.dart';
import 'package:notes_app/services/api_service.dart';
import 'notes_dialog.dart';

class NotesListScreen extends StatefulWidget {
  final String token;

  NotesListScreen({required this.token});

  @override
  _NotesListScreenState createState() => _NotesListScreenState();
}

class _NotesListScreenState extends State<NotesListScreen> {
  List<dynamic> _notes = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _fetchNotes();
  }

  Future<void> _fetchNotes() async {
    try {
      final notes = await ApiService.getNotes(widget.token);
      setState(() {
        _notes = notes;
        _isLoading = false;
      });
    } catch (e) {
      print('Error fetching notes: $e');
    }
  }

  void _openNoteEditor(BuildContext context, [int? id, String? title, String? content]) async {
    final result = await showDialog(
      context: context,
      builder: (context) => NoteEditorDialog(
        id: id,
        initialTitle: title,
        initialContent: content,
      ),
    );

    if (result != null) {
      final token = widget.token;
      if (id == null) {
        await ApiService.createNote(token, result['title'], result['content']);
      } else {
        await ApiService.updateNote(token, id, result['title'], result['content']);
      }
      _fetchNotes(); // Refresh the notes list
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Notes'),
      ),
      body: _isLoading
          ? Center(child: CircularProgressIndicator())
          : ListView.builder(
              itemCount: _notes.length,
              itemBuilder: (context, index) {
                final note = _notes[index];
                return ListTile(
                  title: Text(note['title']),
                  subtitle: Text(note['content']),
                  onTap: () => _openNoteEditor(context, note['id'], note['title'], note['content']),
                  trailing: IconButton(
                    icon: Icon(Icons.delete),
                    onPressed: () async {
                      await ApiService.deleteNote(widget.token, note['id']);
                      _fetchNotes(); // Refresh the notes list
                    },
                  ),
                );
              },
            ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _openNoteEditor(context),
        child: Icon(Icons.add),
      ),
    );
  }
}