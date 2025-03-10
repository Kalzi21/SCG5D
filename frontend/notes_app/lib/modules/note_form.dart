import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';



class NoteFormScreen extends StatefulWidget {
  @override
  _NoteFormScreenState createState() => _NoteFormScreenState();
}

class _NoteFormScreenState extends State<NoteFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _contentController = TextEditingController();
  DateTime? _reminder;
  List<String> _labels = [];

void _addLabel(String label) {
  setState(() {
    _labels.add(label);
  });
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

  if (date == null) return; // Exit early if no date is picked

  final time = await showTimePicker(
    context: context,
    initialTime: TimeOfDay.now(),
  );

  if (time == null) return; // Exit early if no time is picked

  setState(() {
    _reminder = DateTime(date.year, date.month, date.day, time.hour, time.minute);
  });
}

  

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Create Note'),
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
    final title = _titleController.text;
    final content = _contentController.text;
    final reminder = _reminder?.toIso8601String();
    final prefs = await SharedPreferences.getInstance();
  final token = prefs.getString('token');  // Retrieve the token
  if (token == null) {
    throw Exception('No token found');
  }

    final response = await http.post(
      Uri.parse('http://10.0.2.2:8000/main/notes/'),  // Replace with actual backend URL
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',  // Replace with actual auth token
      },
      body: jsonEncode({
        'title': title,
        'content': content,
        'labels': _labels,
        'reminder': reminder,
      }),
    );

    if (response.statusCode == 201) {
      Navigator.pop(context, jsonDecode(response.body));  // Return created note
    } else {
      print('Failed to create note: ${response.body}');
    }
  }
}


  @override
  void dispose() {
    _titleController.dispose();
    _contentController.dispose();
    super.dispose();
  }
}