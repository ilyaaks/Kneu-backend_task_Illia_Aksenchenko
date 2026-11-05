<!-- LOGO -->
<br />
<h1>
<p align="center">
  <img src="https://assets.kneu.com/images/kneu/logo-secondary-forest-kneu.png" width="200px" height="200px" alt="Logo">
  <br/>
  <br>Kneu Health
</p>
</h1>
<p align="center">
    Backend Engineering Technical Test
    <br/><br/>
</p>

This exercise is intended to resemble a small, real-world backend feature at Kneu Health. We’re primarily interested in how you approach the problem rather than how much you complete.

## Overview

Kneu Health is building a medication management platform that allows patients to manage their prescriptions and track when medications should be taken.

This repository contains a partially implemented Spring Boot application backed by PostgreSQL. A read-only Medications API is already provided. Your task is to extend the system by building support for prescriptions.

## Time Expectation

⏱ **Approximately 2 hours**

Please stop once you reach the 2-hour mark, even if your solution feels incomplete.

We are not expecting a production-ready system. Instead, we are interested in:

- How you structure and organise code
- How you reason about the problem
- How you make trade-offs and assumptions
- How you communicate what you would do next

If you do not finish, please leave comments or notes explaining your thinking.

## AI Tools are Encouraged

Use them as you would on the job (Claude, ChatGPT, Copilot, Cursor or similar). We are interested in how you use them well:

- Using them to understand unfamiliar code and concepts, not only to generate code
- Checking what they produce by running it, testing it and questioning it
- Owning the result: be ready to explain and change any line you submit

## Technical Stack

🌱 **You don't need prior experience with this stack, but we do expect you to be comfortable programming.** You should be able to write, debug and test code confidently in at least one language, such as Python, C#, or JavaScript. If you can, you'll be able to pick up what you need here as you go, and you won't be marked down for being new to these tools.

- **Java 21**: the programming language. If you've used another language, such as Python, C# or JavaScript, the concepts carry over
- **Spring Boot**: a framework that handles the web-server plumbing, so you write classes and annotate them
- **Spring Data JPA**: lets you read and write database rows as Java objects, without writing SQL by hand
- **PostgreSQL (via Docker)**: the database. It runs in Docker, so you don't need to install it yourself
- **Maven**: the build tool. `./mvnw` downloads everything the project needs, so there's nothing extra to install

## Getting Started

Click **"Use this template"** to create your own copy of this repository.

<img width="400" height="169" alt="image" src="https://github.com/user-attachments/assets/c757dad7-3a6d-4284-a42d-6a1644ecb847" />

### Prerequisites

- [Java 21](https://adoptium.net/temurin/releases/?version=21)
- [Docker](https://docs.docker.com/get-docker/)

### Setup

```bash
# Start the PostgreSQL database
docker compose up -d

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test
```

The application runs on **port 8080**.

Verify the existing API:

```bash
curl http://localhost:8080/medications
```

### Stopping the Database

```bash
docker compose down        # keeps data
docker compose down -v     # removes data (full reset)
```

---

## What’s Provided

The starter project includes:

- A PostgreSQL database with a pre-populated `medications` table
- A `Medication` JPA entity
- A Spring Data `MedicationRepository`
- A working `GET /medications` endpoint

You are free to refactor or adjust any existing code if you believe it improves clarity or quality.

## Task

Implement support for **prescriptions**.

A prescription should:

- Reference an existing medication
- Include a time of day the medication should be taken (e.g. `08:00`)
- Optionally include a short, human-readable label or nickname

Users should be able to create and view prescriptions.

You may make reasonable assumptions where details are unspecified. Please document them.

## Business Rules

- A prescription must reference a valid medication
- Invalid input should be handled appropriately

## What We’re Looking For

We will be reviewing your solution with attention to:

- **Code structure and organisation**
- **API design and correctness**
- **Clarity and readability**
- **Error handling**
- **Testing approach**
- **Engineering judgement and trade-offs**

We value thoughtful, well-explained decisions over feature completeness.

## Submission

Please share a link to your repository when finished.

If you’d like, include a short note describing:

- Your overall approach
- Any trade-offs or assumptions
- What you would improve or add with more time
