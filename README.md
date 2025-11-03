#  SkillSwap Backend
---
## Current Features (changes as develope)

- User registration and profile management  
- Profile photos with upload timestamps  
- Skill management (offer / seek)  
- Matchmaking system (swipe left / right)  
- Messaging system for matched users  
- University, organization, and interest linking  
- Fully Dockerized setup for local development  

---

## Tech Stack (Currently)

| Layer | Technology |
|-------|-------------|
| **Language** | Java 21 (LTS) |
| **Framework** | Spring Boot 3. something (check the pom.xml) |
| **Database** | PostgreSQL 16 |
| **ORM** | Spring Data JPA |
| **API Format** | REST (JSON) |
| **Build Tool** | Maven |
| **Containerization** | Docker Compose |

---

## Set up instructions (using git bash)


### 1. Clone into local machine 
git clone https://github.com/<your-username>/skillswap-backend.git
cd skillswap-backend

###  2️ Start with Docker
docker-compose up --build
Your backend will be available at
http://localhost:8080

### 3. Database Schema Overview
Core Tables

users – basic user accounts

profile – user bio, major, year, etc.

profile_photo – linked images

skills – catalog of available skills

user_skill – links users to skills

user_interest – user interests

user_organization – clubs / org roles

swipe – user likes / dislikes

match – successful connections

message – chat between matched users

Schema automatically loads from schema.sql at startup.

### 4. API Examples (for testing API end points)
Add a new user

curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","passwordHash":"12345","firstName":"John","lastName":"Doe","university":"MGA"}'
Send a message


curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{"messageContent":"Hey there!","match":{"matchId":1},"sender":{"userId":1}}'
### 5. Team Setup Guide Summary
Clone the repo

Run docker-compose up --build

Verify database initialized from schema.sql

Insert two test users if needed

Access all APIs under /api/…

### 6. Developer Notes
Java Version: 21 (LTS)

Spring Boot: 3.x

PostgreSQL: 16

Port: 8080

Database: skillswap (via Docker)