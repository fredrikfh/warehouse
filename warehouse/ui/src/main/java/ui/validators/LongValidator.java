package ui.validators;

/**
 * Validates that input string can be parsed as an Integer.
 */
public class LongValidator implements InputValidator {
  @Override
  public boolean validateInput(String input) {
    try {
      Long.valueOf(input);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
