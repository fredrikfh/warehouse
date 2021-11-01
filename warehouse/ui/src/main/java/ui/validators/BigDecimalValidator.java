package ui.validators;

import java.math.BigDecimal;

/**
 * Validates that input string can be parsed as a BigDecimal.
 */
public class BigDecimalValidator implements InputValidator {
  @Override
  public boolean validateInput(String input) {
    try {
      BigDecimal.valueOf(Double.valueOf(input));
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
