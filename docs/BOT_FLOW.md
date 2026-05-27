# Bot Flow And Intent Handling

This document is the quick handover for the current Telegram bot behavior.
Read this before opening the command handler classes.

## Current Scope

The bot currently supports:

- Recording income/expense transactions from natural-language messages.
- Slash commands: `/start`, `/help`, `/today`, `/month`.
- Bot-name suffix normalization, for example `/today@my_bot`.
- Natural-language summary requests, for example:
  - `xem tong hom nay`
  - `thang nay chi bao nhieu`
  - `hom qua tieu bao nhieu`
  - `tu 1/5 den 20/5 chi bao nhieu`
- Fallback guidance for unrelated text.
- Vietnamese user-facing replies via `messages.properties`.

## High-Level Runtime Flow

```text
Telegram update
  -> TelegramUpdateDispatcher
  -> TelegramMessageMapper
  -> HandleIncomingMessageUseCase
  -> BotCommandRouter
  -> ordered BotCommandHandler chain
  -> MessageSender reply
```

The router calls every handler whose `supports(message)` returns `true`.
Each handler returns a boolean from `handle(message)`:

- `true`: message was handled; routing stops.
- `false`: handler did not handle it; router tries the next handler.

This matters because non-command text first goes through transaction parsing. If the
transaction parser says "not a transaction", routing continues to summary intent and then
fallback.

## Handler Order

| Order | Handler | Purpose |
| --- | --- | --- |
| `1` | `StartCommandHandler` | `/start` welcome message |
| `2` | `HelpCommandHandler` | `/help` usage message |
| `3` | `TodaySummaryHandler` | `/today` command summary |
| `4` | `MonthSummaryHandler` | `/month` command summary |
| `10` | `UnknownCommandHandler` | unknown slash commands |
| `50` | `RecordTransactionHandler` | non-command transaction recording |
| `80` | `SummaryIntentHandler` | natural-language summary intent |
| `99` | `EchoMessageHandler` | fallback guidance |

## Transaction Recording

Key files:

- `application/port/out/MessageInterpreter.java`
- `service/LlmMessageInterpreter.java`
- `application/command/RecordTransactionHandler.java`
- `service/TransactionService.java`
- `common/ConfirmationFormatter.java`

Flow:

```text
RecordTransactionHandler
  -> MessageInterpreter.interpret(text)
  -> Optional.empty(): return false
  -> ParsedTransaction: resolve user, save transaction, send confirmation, return true
```

`LlmMessageInterpreter` uses Spring AI `ChatModel` with DeepSeek configured by Spring AI.
It returns empty for blank input, non-transaction JSON, malformed JSON, invalid values, or
model call failures.

## Summary Intent Handling

Key files:

- `application/port/out/SummaryIntentInterpreter.java`
- `domain/summary/ParsedSummaryIntent.java`
- `domain/summary/FlowFilter.java`
- `service/LlmSummaryIntentInterpreter.java`
- `application/command/SummaryIntentHandler.java`
- `common/SummaryFormatter.java`
- `config/BotIntentProperties.java`

`SummaryIntentHandler` is deterministic first, LLM second:

```text
SummaryIntentHandler
  -> if action keyword + today keyword: send today's summary
  -> else if action keyword + month keyword: send this month's summary
  -> else SummaryIntentInterpreter.interpret(text)
       -> Optional.empty(): return false
       -> needsClarification: send clarification reply, return true
       -> valid date range: summarize(from inclusive, to exclusive), return true
```

`FlowFilter` controls reply focus:

- `ALL`: show both expense and income totals.
- `EXPENSE`: show expense total only.
- `INCOME`: show income total only.

Command summaries keep `ALL` by default. Natural-language summaries can use any filter.

## Summary Date Range Rules

`ParsedSummaryIntent` uses:

- `from`: inclusive `Instant`.
- `to`: exclusive `Instant`.
- `label`: short display label from the parser.
- `flowFilter`: `ALL`, `EXPENSE`, or `INCOME`.

Examples expected from the LLM adapter:

| User text | Expected intent |
| --- | --- |
| `hom qua tieu bao nhieu` | yesterday, `EXPENSE` |
| `hom kia thu bao nhieu` | two days ago, `INCOME` |
| `tu 1/5 den 20/5 chi bao nhieu` | current-year May 1 inclusive to May 21 exclusive, `EXPENSE` |
| `7 ngay qua` | seven-day range ending tomorrow at start of day |
| `may hom truoc thi sao` | clarification instead of guessing |
| `hello` | no summary intent; fallback handles it |

There is no repair loop. If the LLM output is malformed or invalid, the adapter returns
`Optional.empty()`.

## Configuration

Keyword config lives in profile YAML files under:

```yaml
iom:
  bot:
    intents:
      summary:
        action-keywords: [...]
        today-keywords: [...]
        month-keywords: [...]
        expense-keywords: [...]
        income-keywords: [...]
```

DeepSeek/Spring AI config uses:

```properties
spring.ai.deepseek.api-key=${DEEPSEEK_API_KEY:}
spring.ai.deepseek.base-url=${DEEPSEEK_BASE_URL:https://api.deepseek.com}
spring.ai.deepseek.chat.options.model=${DEEPSEEK_MODEL:deepseek-v4-flash}
spring.ai.deepseek.chat.options.temperature=0
spring.ai.deepseek.chat.options.max-tokens=${DEEPSEEK_MAX_TOKENS:512}
```

## User-Facing Messages

All Vietnamese bot replies should go through:

- `common/BotMessages.java`
- `src/main/resources/messages.properties`

Do not hardcode Vietnamese user-facing text in Java classes. Prompt/schema text inside LLM
adapters may stay in Java because it is not sent directly as a bot reply.

## Test Map

Useful tests:

- `BotCommandRouterTest`: routing continues on `false` and stops on `true`.
- `BotCommandParserTest`: slash command normalization, including bot-name suffix.
- `RecordTransactionHandlerTest`: transaction parse empty vs valid result.
- `SummaryIntentHandlerTest`: deterministic rule, LLM fallback, clarification, unrelated text.
- `LlmMessageInterpreterTest`: DeepSeek transaction JSON parsing.
- `LlmSummaryIntentInterpreterTest`: DeepSeek summary intent JSON parsing.
- `SummaryFormatterTest`: `ALL`, `EXPENSE`, `INCOME` output.

Verification command:

```powershell
cd api
.\mvnw.cmd test
```

Integration tests using Testcontainers may skip when Docker Desktop is unavailable.
