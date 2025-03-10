import 'package:flutter/material.dart';


class EditNoteScreen extends StatefulWidget {
  final Map<String, dynamic> note;

  EditNoteScreen({required this.note});

  @override
  _EditNoteScreenState createState() => _EditNoteScreenState();
}

class _EditNoteScreenState extends State<EditNoteScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _contentController = TextEditingController();
  DateTime? _reminder;
  List<String> _labels = [];

void _addLabel(String label) {
  if (!_labels.contains(label)) {
    setState(() {
      _labels.add(label);
    });
  }
}


void _removeLabel(String label) {
  setState(() {
    _labels.remove(label);
  });
}

Future<void> _pickReminder() async {
  final date = await showDatePicker(
    context: context,
    initialDate: DateTime.now(),
    firstDate: DateTime.now(),
    lastDate: DateTime(2100),
  );

  if (date == null) return; // Handle user canceling date picker

  final time = await showTimePicker(
    context: context,
    initialTime: TimeOfDay.now(),
  );

  if (time == null) return; // Handle user canceling time picker

  setState(() {
    _reminder = DateTime(
      date.year,
      date.month,
      date.day,
      time.hour,
      time.minute,
    );
  });
}

  @override
  void initState() {
    super.initState();
    // Pre-fill the form with the existing note data
    _titleController.text = widget.note['title'];
    _contentController.text = widget.note['content'];
    if (widget.note['labels'] != null) {
    _labels = List<String>.from(widget.note['labels']);
  }
  // Pre-fill the reminder if it exists
  if (widget.note['reminder'] != null) {
    _reminder = DateTime.parse(widget.note['reminder']);
  }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Edit Note'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                controller: _titleController,
                decoration: InputDecoration(labelText: 'Title'),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter a title';
                  }
                  return null;
                },
              ),
              TextFormField(
                controller: _contentController,
                decoration: InputDecoration(labelText: 'Content'),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Please enter content';
                  }
                  return null;
                },
                maxLines: 5,
              ),
              SizedBox(height: 20),
              Wrap(
                spacing: 8.0,
                children: _labels.map((label) {
                  return Chip(
                    label: Text(label),
                    onDeleted: () => _removeLabel(label),
                  );
                }).toList(),
              ),
              TextField(
                decoration: InputDecoration(labelText: 'Add Label'),
                onSubmitted: (value) {
                  if (value.isNotEmpty) {
                    _addLabel(value);
                  }
                },
              ),
              SizedBox(height: 20),
              ElevatedButton(
                onPressed: _pickReminder,
                child: Text(_reminder == null ? 'Set Reminder' : 'Reminder: ${_reminder!.toString()}'),
              ),
              SizedBox(height: 20),
              ElevatedButton(
                onPressed: _submitForm,
                child: Text('Save'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _submitForm() async {
  if (_formKey.currentState!.validate()) {
    final title = _titleController.text.trim();
    final content = _contentController.text.trim();

    if (title.isEmpty || content.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Title and content cannot be empty.")),
      );
      return;
    }

    Navigator.pop(context, {
      'id': widget.note['id'],
      'title': title,
      'content': content,
      'labels': _labels,
      'reminder': _reminder?.toIso8601String(),
    });
  }
}


  @override
  void dispose() {
    _titleController.dispose();
    _contentController.dispose();
    super.dispose();
  }
}