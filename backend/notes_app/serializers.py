# notes/serializers.py
from rest_framework import serializers
from .models import Note

class NoteSerializer(serializers.ModelSerializer):
    class Meta:
        model = Note
        fields = ['id', 'title', 'content', 'labels', 'reminder', 'created_at', 'updated_at']
        read_only_fields = ['user', 'created_at', 'updated_at']


    def validate_labels(self, value):
        if not isinstance(value, list):
            raise serializers.ValidationError("Labels must be a list.")
        return value
        