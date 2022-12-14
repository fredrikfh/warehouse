package ui.validators;

/**
 * Validates that the input is not null or empty.
 */
public class NotEmptyValidator implements InputValidator {

  @Override
  public boolean validateInput(String input) {
    return input != null && !input.equals("");
  }

  @Override
  public String getErrorMessage() {
    return "Field cannot be empty";
  }
}
