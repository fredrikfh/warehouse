package ui.validators;

import java.util.regex.Pattern;

/**
 * Validates that input matches the given regex string.
 */
public class RegexValidator implements InputValidator {
  private final Pattern pattern;

  public RegexValidator(String regex) {
    this.pattern = Pattern.compile(regex);
  }

  @Override
  public boolean validateInput(String input) {
    return pattern.matcher(input).find();
  }

  @Override
  public String getErrorMessage() {
    return "Validation error";
  }
}
