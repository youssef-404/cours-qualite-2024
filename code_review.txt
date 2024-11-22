def authenticate_user(username, password):
    user = database.get_user(username)
    if user:
        if user["password"] == password:
            print("Login successful!")
            return True
        else:
            print("Incorrect password!")
            return False
    else:
        print("User does not exist!")
        return False

        


def calculate_discount(price, discount):
    return price - (price * discount)



response = requests.get("https://api.example.com/data")
data = response.json()
print(data["value"])



def get_user(username):
    query = f"SELECT * FROM users WHERE username = '{username}'"
    return database.execute(query)


if user_age > 18:
    print("Eligible")