package me.nghlong3004.iom.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.nghlong3004.iom.api.application.port.out.MessageInterpreter;
import me.nghlong3004.iom.api.domain.transaction.ParsedTransaction;
import org.springframework.stereotype.Service;

/**
 * {@link MessageInterpreter} implementation backed by an LLM API. Sends structured prompts
 * containing raw user text and parses the JSON response into {@link ParsedTransaction}.
 *
 * <p>Returns {@link Optional#empty()} when:
 * <ul>
 *   <li>The LLM determines the message is not a financial transaction</li>
 *   <li>The LLM call fails (graceful degradation with warning log)</li>
 *   <li>The JSON response cannot be parsed</li>
 * </ul>
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/24/2026
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LlmMessageInterpreter implements MessageInterpreter {

  private final ObjectMapper objectMapper;

  private static final String SYSTEM_PROMPT =
      """
      You are a financial transaction parser. Given a user message, extract structured data.

      Rules:
      - If the message is NOT about a financial transaction, respond with: {"is_transaction": false}
      - If it IS a transaction, respond with JSON:
        {
          "is_transaction": true,
          "type": "INCOME" or "EXPENSE",
          "amount": <number in smallest currency unit>,
          "currency": "VND" or "USD" or "EUR" or "JPY" or "KRW" or "GBP",
          "category": "FOOD" or "TRANSPORT" or "SALARY" or "EDUCATION" or "SHOPPING" or "ENTERTAINMENT" or "HEALTH" or "HOUSING" or "OTHER",
          "note": "<descriptive note extracted from the message>",
          "occurred_at": "<ISO date yyyy-MM-dd or null for today>"
        }

      Currency detection:
      - "k", "nghin", "nghìn", "trieu", "triệu", "dong", "đồng", "d", no indicator -> VND
      - "$", "dollar", "usd" -> USD
      - "euro", "eur" -> EUR
      - "yen", "jpy" -> JPY
      - "won", "krw" -> KRW
      - "pound", "gbp" -> GBP

      Amount conversion to smallest unit:
      - VND: "30k" = 30000, "5tr" = 5000000, "50 nghin" = 50000
      - USD/EUR/GBP: "$10.50" = 1050 (cents), "$5" = 500
      - JPY/KRW: store as-is (no fractional units)

      Type detection:
      - Income keywords: luong, thuong, nhan, thu, duoc cho, tien thuong, freelance, salary, bonus, income, received
      - Default: EXPENSE

      Date detection:
      - "hom qua" / "yesterday" -> yesterday's date
      - "hom kia" -> day before yesterday
      - Default: today

      Respond ONLY with valid JSON. No explanation.
      """;

  @Override
  public Optional<ParsedTransaction> interpret(String text) {
    if (text == null || text.isBlank()) {
      return Optional.empty();
    }

    // TODO: Replace with actual LLM API call.
    // This is the integration point for the LLM provider (OpenAI/Gemini/Anthropic).
    // The implementation should:
    // 1. Send SYSTEM_PROMPT + user text to the LLM
    // 2. Parse the JSON response
    // 3. Map to ParsedTransaction using objectMapper
    // 4. Return Optional.empty() if is_transaction=false or on any failure

    log.warn("LLM integration not yet configured. Returning empty for text: {}", text);
    return Optional.empty();
  }
}
