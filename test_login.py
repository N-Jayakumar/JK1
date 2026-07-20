import requests
import re

session = requests.Session()
r = session.get('http://localhost:8080/login')

# Extract CSRF
csrf = ''
if '_csrf' in r.text:
    m = re.search(r'name=\"_csrf\"\s+value=\"([^\"]+)\"', r.text)
    if m: csrf = m.group(1)

# Login
login_data = {'email': 'admin@jk0.com', 'password': 'password', '_csrf': csrf}
r2 = session.post('http://localhost:8080/login', data=login_data, allow_redirects=False)

# Fetch home
r3 = session.get('http://localhost:8080/')
print("STATUS:", r3.status_code)
if r3.status_code == 500:
    print('Body excerpt:')
    print(r3.text[:1000])
    print('---------')
    print(r3.text[-1000:])
