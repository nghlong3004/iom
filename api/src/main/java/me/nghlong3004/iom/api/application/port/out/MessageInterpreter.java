package me.nghlong3004.iom.api.application.port.out;

import java.util.Optional;
import me.nghlong3004.iom.api.domain.transaction.ParsedTransaction;

/**
 * Interprets a raw user message into structured financial data. Implementations may use LLM,
 * rule-based parsing, or hybrid approaches.
 *
 * @author nghlong3004 (Nguyen Hoang Long)
 * @since 5/24/2026
 */
public interface MessageInterpreter {

  Optional<ParsedTransaction> interpret(String text);
}
