# Run with: hurl --test --variable host=localhost:8080 --variable username=hurl-user-$(date +%s) e2e/api.hurl

# Protected endpoints reject unauthenticated requests.
GET http://{{host}}/api/data

HTTP/1.1 401

# Create a user for this E2E run.
POST http://{{host}}/auth/register
Content-Type: application/json

{
  "username": "{{username}}",
  "password": "hurl-password-123"
}

HTTP/1.1 200
Content-Type: application/json
[Asserts]
jsonpath "$.username" == "{{username}}"

# Log in and capture the JWT for subsequent requests.
POST http://{{host}}/auth/login
Content-Type: application/json

{
  "username": "{{username}}",
  "password": "hurl-password-123"
}

HTTP/1.1 200
Content-Type: application/json
[Captures]
token: jsonpath "$.token"
[Asserts]
jsonpath "$.token" matches ".+"

# Authenticated users can create data.
POST http://{{host}}/api/data
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "text": "Hurl E2E data"
}

HTTP/1.1 201
Content-Type: application/json
[Captures]
data_id: jsonpath "$.id"
[Asserts]
jsonpath "$.text" == "Hurl E2E data"

# Authenticated users can read data.
GET http://{{host}}/api/data
Authorization: Bearer {{token}}

HTTP/1.1 200
Content-Type: application/json
[Asserts]
body contains "Hurl E2E data"
