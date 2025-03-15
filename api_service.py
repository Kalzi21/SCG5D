import requests
import json

BASE_URL = "http://10.0.2.2:8000/main/"
TOKEN_FILE = "token.txt"

def save_token(token):
    with open(TOKEN_FILE, "w") as file:
        file.write(token)

def load_token():
    try:
        with open(TOKEN_FILE, "r") as file:
            return file.read()
    except FileNotFoundError:
        return None

def login(username, password):
    response = requests.post(
        "http://10.0.2.2:8000/api-token-auth/",
        headers={"Content-Type": "application/json"},
        data=json.dumps({"username": username, "password": password}),
    )
    if response.status_code == 200:
        token = response.json().get("token")
        save_token(token)
        return True
    return False

def get_notes():
    token = load_token()
    if not token:
        return []

    response = requests.get(
        f"{BASE_URL}notes/",
        headers={"Authorization": f"Token {token}"}
    )
    return response.json() if response.status_code == 200 else []
