import 'package:flutter/material.dart';

class NoteEditorDialog extends StatefulWidget {
  final int? id;
  final String? initialTitle;
  final String? initialContent;

  NoteEditorDialog({this.id, this.initialTitle, this.initialContent});

  @override
  _NoteEditorDialogState createState() => _NoteEditorDialogState();
}

class _NoteEditorDialogState extends State<NoteEditorDialog> {
  final _titleController = TextEditingController();
  final _contentController = TextEditingController();

  @override
  void initState() {
    super.initState();
    if (widget.initialTitle != null) {
      _titleController.text = widget.initialTitle!;
    }
    if (widget.initialContent != null) {
      _contentController.text = widget.initialContent!;
    }
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(widget.id == null ? 'New Note' : 'Edit Note'),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          TextField(
            controller: _titleController,
            decoration: InputDecoration(labelText: 'Title'),
          ),
          TextField(
            controller: _contentController,
            decoration: InputDecoration(labelText: 'Content'),
            maxLines: null,
          ),
        ],
      ),
      actions: [
        TextButton(
          onPressed: () {
            Navigator.pop(context);
          },
          child: Text('Cancel'),
        ),
        TextButton(
          onPressed: () {
            final title = _titleController.text;
            final content = _contentController.text;
            if (title.isNotEmpty && content.isNotEmpty) {
              Navigator.pop(context, {
                'id': widget.id,
                'title': title,
                'content': content,
              });
            }
          },
          child: Text('Save'),
        ),
      ],
    );
  }
}