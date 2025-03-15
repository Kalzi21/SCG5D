import flet as ft
from services.api_service import login

def main(page: ft.Page):
    username = ft.TextField(label="Username")
    password = ft.TextField(label="Password", password=True)
    
    def handle_login(e):
        if login(username.value, password.value):
            page.clean()
            page.add(ft.Text("Login Successful!"))
        else:
            page.add(ft.Text("Login Failed!", color="red"))

    login_button = ft.ElevatedButton(text="Login", on_click=handle_login)
    
    page.add(username, password, login_button)

ft.app(target=main)
