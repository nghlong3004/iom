# IOM - Input Output Money

## 1. Product Overview

IOM is a personal finance assistant that helps users record income and expenses through natural language messages.

In the first version, IOM focuses on Telegram. A user can send messages such as:

```text
ăn sáng 30k
đổ xăng 50k
nhận lương 5 triệu
mua sách 120k hôm qua
```

IOM will parse the message, extract structured financial data, save it to the database, and reply with a confirmation.

In later versions, IOM can support image input, OCR, AI-based parsing, Excel export, a web dashboard, and other platforms such as Zalo or Facebook.

---

## 2. Problem

Many people want to track their personal spending, but traditional finance apps require too many manual steps.

A typical flow is:

```text
Open app
→ Choose income or expense
→ Enter amount
→ Select category
→ Select date
→ Add note
→ Save
```

This creates friction. Users often stop tracking after a few days because recording expenses feels slower than the actual spending.

---

## 3. Solution

IOM reduces friction by turning chat messages into structured financial records.

Instead of filling out a form, the user only sends a natural message.

Example:

```text
ăn sáng 30k
```

IOM extracts:

```text
type: EXPENSE
amount: 30000
currency: VND
category: FOOD
note: ăn sáng
date: today
source: TELEGRAM
```

Then the bot replies:

```text
Đã ghi nhận: Chi 30.000đ cho ăn sáng.
```

---

## 4. Product Principle

Telegram is only one channel. The finance core must be platform-independent.

This means the core business logic should not depend directly on Telegram.

Telegram, Web, Zalo, Facebook, or other platforms should only act as input/output channels. They send user input into the same finance core and receive responses from it.

The system should be designed so that adding a new channel does not require rewriting the core finance logic.

---

## 5. Target Users

Primary users:

- Students
- Young workers
- People who want simple personal finance tracking
- People who prefer chatting over manually filling forms

Initial user:

- The developer uses the product personally first to validate the idea.

---

## 6. MVP Scope

MVP means Minimum Viable Product, the smallest useful version that can be tested.

### Must Have

- Telegram bot receives text messages
- Bot supports `/start`
- Bot supports `/help`
- Bot parses simple Vietnamese money inputs
- Bot detects income or expense
- Bot extracts amount
- Bot saves transaction to PostgreSQL
- Bot replies with a confirmation message
- User can view a simple daily or monthly summary through Telegram

### Should Have

- Edit recent transaction
- Delete recent transaction
- Basic category suggestion
- REST API for future web client
- Export transactions to Excel

### Later

- Image receipt parsing
- OCR for extracting text from images
- AI-based natural language parsing
- Web dashboard
- Web account login
- Telegram account linking
- Zalo/Facebook integration
- Budget warning
- Recurring transactions

---

## 7. Non-goals in Phase 1

Phase 1 should stay small and focused.

The following features are not required in the first version:

- No Zalo integration
- No Facebook integration
- No image/OCR parsing
- No complex AI agent
- No web dashboard
- No payment or subscription system
- No advanced analytics
- No multi-user organization/team feature

---

## 8. Current Milestone

The current milestone is Telegram communication.

Completed:

- Telegram bot can connect through long polling
- Telegram bot can receive text messages
- Telegram bot can reply to user messages
- Basic `/start`, `/help`, and echo behavior can be implemented

Next milestone:

- Refactor message handling using Command Pattern
- Add simple money parser
- Parse messages like `ăn sáng 30k`
- Save parsed transactions to PostgreSQL

---

## 9. Architecture Style

The project follows a Clean Architecture-lite / Hexagonal Architecture-lite style.

Main idea:

```text
External channels
    ↓
Adapters
    ↓
Application use cases
    ↓
Domain model
    ↓
Repositories / external infrastructure
```

The core business logic should not depend directly on external frameworks or platforms.

Telegram is an adapter, not the center of the system.

---

## 10. Core User Flow

```text
User sends a message on Telegram
        ↓
Telegram bot receives Update
        ↓
Telegram channel adapter converts Telegram message to internal input
        ↓
Application use case handles the message
        ↓
Command router selects a suitable handler
        ↓
Money parser extracts structured data
        ↓
Transaction service validates the parsed result
        ↓
Transaction is saved to PostgreSQL
        ↓
Reply service creates confirmation message
        ↓
Telegram adapter sends reply back to user
```

---

## 11. Initial Architecture

```text
Telegram
   ↓
Telegram Channel Adapter
   ↓
IncomingMessage
   ↓
HandleIncomingMessageUseCase
   ↓
BotCommandRouter
   ↓
Command Handler
   ↓
Domain / Service
   ↓
Repository
   ↓
PostgreSQL
```

For now, Telegram is the only channel.

Later, Web, Zalo, Facebook, or other platforms can be added as additional adapters.

---

## 12. Repository Structure

```text
iom/
├── api/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── client/
│   ├── src/
│   ├── package.json
│   └── Dockerfile
│
├── docker-compose.yml
├── .env.example
├── .env.prod.example
├── README.md
└── BRIEF.md
```

### api

The `api` folder contains the Spring Boot backend.

It is responsible for:

- Telegram bot integration
- REST API
- Business logic
- Money input parsing
- Transaction management
- Database access
- Account linking later
- Export logic later

Although the folder is named `api`, it also contains the Telegram bot and core business logic.

### client

The `client` folder contains the React web application.

It is responsible for:

- Login screen
- Transaction dashboard
- Reports
- Charts
- Account linking UI
- Export UI

In Phase 1, the project can start with only `api`. The `client` can be added later.

---

## 13. Backend Package Structure

```text
me.nghlong3004.iom
├── IomApplication.java
│
├── config
│   ├── TelegramBotProperties.java
│   └── TelegramClientConfig.java
│
├── channel
│   └── telegram
│       ├── TelegramBot.java
│       ├── TelegramUpdateDispatcher.java
│       ├── TelegramMessageMapper.java
│       └── TelegramReplySender.java
│
├── application
│   ├── command
│   │   ├── BotCommandHandler.java
│   │   ├── BotCommandRouter.java
│   │   ├── StartCommandHandler.java
│   │   ├── HelpCommandHandler.java
│   │   ├── UnknownCommandHandler.java
│   │   └── EchoMessageHandler.java
│   │
│   ├── port
│   │   └── out
│   │       └── MessageSender.java
│   │
│   └── usecase
│       └── HandleIncomingMessageUseCase.java
│
├── domain
│   ├── message
│   │   ├── MessageChannel.java
│   │   ├── IncomingMessage.java
│   │   └── OutgoingMessage.java
│   │
│   └── transaction
│       ├── Transaction.java
│       ├── TransactionType.java
│       └── Category.java
│
├── service
│   ├── MoneyInputParser.java
│   └── TransactionService.java
│
├── repository
│   └── TransactionRepository.java
│
└── common
    ├── BaseEntity.java
    └── ErrorCode.java
```

---

## 14. Package Responsibilities

### config

Contains application configuration.

Examples:

- Telegram bot properties
- Telegram client configuration
- Web/security configuration later

### channel

Contains external input/output channel adapters.

Current channel:

- Telegram

Future channels:

- Web
- Zalo
- Facebook Messenger

The channel layer should convert external platform objects into internal application objects.

Example:

```text
Telegram Update → IncomingMessage
OutgoingMessage → Telegram SendMessage
```

### application

Contains use cases and command handling logic.

This layer coordinates the flow but should not contain low-level Telegram or database details.

Examples:

- Handle incoming message
- Route bot commands
- Record transaction use case later
- Generate summary use case later

### domain

Contains core business objects.

Examples:

- IncomingMessage
- OutgoingMessage
- Transaction
- Category
- TransactionType

This layer should stay clean and not depend on Telegram, Spring MVC, or database-specific details when possible.

### service

Contains business services.

Examples:

- Money input parser
- Transaction service
- Summary service later
- Export service later

### repository

Contains database access interfaces.

Examples:

- TransactionRepository
- UserRepository
- ExternalAccountRepository

---

## 15. Design Patterns

### Adapter Pattern

Used in the `channel` layer.

Telegram-specific objects are converted to internal objects.

Example:

```text
Telegram Update → IncomingMessage
OutgoingMessage → SendMessage
```

This keeps the core logic independent from Telegram.

### Command Pattern

Used for bot commands.

Each command has its own handler.

Examples:

```text
/start  → StartCommandHandler
/help   → HelpCommandHandler
/abc    → UnknownCommandHandler
normal text → EchoMessageHandler or RecordTransactionMessageHandler later
```

Adding a new command should only require adding a new handler class.

### Strategy Pattern

Used by `BotCommandRouter`.

The router receives a list of command handlers and selects the first handler that supports the incoming message.

Example:

```text
handlers.stream()
        .filter(handler -> handler.supports(message))
        .findFirst()
        .handle(message)
```

---

## 16. Main Domain Concepts

### AppUser

Represents a user inside IOM.

A user may later login through the web app and link one or more external accounts.

### ExternalAccount

Represents an account from an external platform.

Examples:

- Telegram account
- Zalo account
- Facebook account

### Transaction

Represents one income or expense record.

Main fields:

```text
id
userId
type
amount
currency
category
note
occurredAt
sourcePlatform
rawInput
createdAt
updatedAt
```

### Category

Represents a spending or income category.

Examples:

```text
FOOD
TRANSPORT
SALARY
EDUCATION
SHOPPING
ENTERTAINMENT
OTHER
```

### ParsedMoneyInput

Represents the result extracted from user input before saving to the database.

Example:

```text
rawInput: ăn sáng 30k
type: EXPENSE
amount: 30000
category: FOOD
note: ăn sáng
occurredAt: today
confidence: 0.85
```

---

## 17. Example Inputs

### Expense

Input:

```text
ăn sáng 30k
```

Parsed result:

```text
type: EXPENSE
amount: 30000
currency: VND
category: FOOD
note: ăn sáng
date: today
```

### Income

Input:

```text
lương tháng này 5tr
```

Parsed result:

```text
type: INCOME
amount: 5000000
currency: VND
category: SALARY
note: lương tháng này
date: today
```

### Expense with Date

Input:

```text
mua sách 120k hôm qua
```

Parsed result:

```text
type: EXPENSE
amount: 120000
currency: VND
category: EDUCATION
note: mua sách
date: yesterday
```

---

## 18. Technical Stack

Initial stack:

- Backend: Spring Boot
- Java version: Java 21
- Threading: Java 21 virtual threads
- Database: PostgreSQL
- Migration: Flyway
- ORM: Spring Data JPA
- Bot platform: Telegram
- Bot mode: Long polling
- Monitoring: Spring Boot Actuator + Prometheus
- Frontend later: React
- Export later: Excel

For local development, Telegram long polling is preferred because it does not require a public HTTPS domain.

Webhook can be considered later when the system is deployed to production.

---

## 19. Runtime Profiles

The backend should support at least two profiles:

```text
dev
prod
```

### dev

Used for local development.

Expected behavior:

- Runs on local machine
- Uses local PostgreSQL
- Enables detailed logs
- Shows SQL logs if needed
- Exposes actuator health and Prometheus endpoints
- Uses Telegram long polling

### prod

Used for production deployment.

Expected behavior:

- Reads database credentials from environment variables
- Reads Telegram bot token from environment variables
- Does not show SQL logs
- Uses safer actuator exposure
- Supports reverse proxy headers
- Uses Flyway migration
- Uses Java 21 virtual threads

---

## 20. Docker Direction

The project can run locally without Docker during early development.

Docker will be used later for consistent deployment.

Initial Docker setup:

```text
api container
postgres container
```

PostgreSQL should not expose its port publicly in production. The API container should connect to PostgreSQL through the internal Docker network.

Example internal database URL:

```text
jdbc:postgresql://postgres:5432/iom
```

---

## 21. Phase 1 Development Plan

### Step 1: Telegram Echo Bot

Goal:

- Bot receives a Telegram message
- Bot replies with the same message

Example:

```text
User: hello
Bot: IOM đã nhận: hello
```

### Step 2: Command Handler Refactor

Goal:

- Move `/start`, `/help`, unknown command, and echo logic into separate command handlers

Expected handlers:

```text
StartCommandHandler
HelpCommandHandler
UnknownCommandHandler
EchoMessageHandler
```

### Step 3: Simple Money Parser

Goal:

- Parse simple Vietnamese money input

Example:

```text
ăn sáng 30k
```

Expected result:

```text
amount = 30000
type = EXPENSE
note = ăn sáng
```

### Step 4: Save Transaction

Goal:

- Save parsed transaction to PostgreSQL

### Step 5: Confirmation Reply

Goal:

- Bot replies with a clean confirmation message

Example:

```text
Đã ghi nhận: Chi 30.000đ cho ăn sáng.
```

### Step 6: Basic Summary

Goal:

- User can ask for today or month summary

Examples:

```text
/today
/month
```

Expected bot replies:

```text
Hôm nay bạn đã chi 120.000đ.
```

```text
Tháng này bạn đã chi 2.300.000đ và thu 5.000.000đ.
```

---

## 22. Success Criteria

The first version is successful when:

- User can send a natural Vietnamese message to Telegram
- Bot can detect amount and transaction type
- Transaction is saved correctly
- Bot replies with a clear confirmation
- User can view a simple spending summary
- Core logic is not tightly coupled to Telegram
- The backend can be extended later for Web, Zalo, Facebook, OCR, and AI parsing

---

## 23. Future Ideas

Possible future features:

- Upload receipt image and auto-extract expense
- Monthly spending chart
- Budget warning
- Category auto-suggestion
- Excel export
- Web dashboard
- Account linking between Telegram and Web
- AI parser for more flexible Vietnamese input
- Multi-platform support: Zalo, Facebook, Messenger
- Recurring transactions
- Spending anomaly detection
