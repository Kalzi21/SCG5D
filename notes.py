import flet as ft
from services.api_service import get_notes

def main(page: ft.Page):
    notes = get_notes()
    notes_list = ft.ListView(
        controls=[ft.Text(note["title"]) for note in notes]
    )
    page.add(notes_list)

ft.app(target=main)
