import flet as ft
import json
import os

dialog = None

# Note storage file
NOTES_FILE = "notes.json"

class Note:
    def __init__(self, title, content):
        self.title = title
        self.content = content

def main(page: ft.Page):
    page.title = "Notes App"
    page.window_width = 400
    page.window_height = 600
    page.theme_mode = ft.ThemeMode.LIGHT

    # Load existing notes
    def load_notes():
        if os.path.exists(NOTES_FILE):
            with open(NOTES_FILE, "r") as f:
                return [Note(**note) for note in json.load(f)]
        return []

    notes = load_notes()
    notes_list = ft.Column()
    editing_note = None  # Define editing_note in the correct scope

    def save_notes():
        with open(NOTES_FILE, "w") as f:
            json.dump([note.__dict__ for note in notes], f)

    def build_note_tile(note):
        return ft.ListTile(
            title=ft.Text(note.title),
            subtitle=ft.Text(note.content),
        )

    def refresh_notes():
        notes_list.controls = [build_note_tile(note) for note in notes]
        page.update()

    title_field = ft.TextField(label="Title")
    content_field = ft.TextField(label="Content", multiline=True)

    def open_editor(e):
        global dialog  # Ensure the dialog is accessible globally
        nonlocal editing_note
        print("Opening editor...")  # Debugging: Check if this prints when the button is clicked
        title_field.value = ""
        content_field.value = ""
        editing_note = None

        # Create the dialog
        dialog = ft.AlertDialog(
            title=ft.Text("New Note"),
            content=ft.Column([title_field, content_field]),
            actions=[
                ft.TextButton("Save", on_click=save_note),
                ft.TextButton("Cancel", on_click=close_dialog),
            ],
        )

        # Attach the dialog to the page
        page.dialog = dialog

        # Now open the dialog
        dialog.open = True
        page.update()  # Refresh the page to show the dialog


    def save_note(e):
        global dialog  # Ensure the dialog is accessible globally
        nonlocal editing_note
        notes.append(Note(title=title_field.value, content=content_field.value))
        save_notes()
        refresh_notes()
        close_dialog(e)

    def close_dialog(e):
        global dialog  # Ensure the dialog is accessible globally
        page.dialog.open = False
        page.update()

    # Add UI elements to the page
    page.add(
        ft.AppBar(title=ft.Text("Notes")),
        ft.Column(
            controls=[
                notes_list,  # Add the notes list here
                ft.FloatingActionButton(icon=ft.icons.ADD, on_click=open_editor),
            ],
            expand=True,  # Ensure the column takes up the full space
        )
    )
    refresh_notes()  # Populate the notes list initially

ft.app(target=main)