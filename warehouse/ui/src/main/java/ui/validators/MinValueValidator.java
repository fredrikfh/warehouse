package ui.validators;

/**
 * Validates that the input value is larger or equal to the minValue.
 */
public class MinValueValidator implements InputValidator {
  private final double minValue;

  public MinValueValidator(double minValue) {
    this.minValue = minValue;
  }

  @Override
  public boolean validateInput(String input) {
    return Double.parseDouble(input) >= minValue;
  }

  @Override
  public String getErrorMessage() {
    return "Value must greater or equal to " + Double.toString(minValue);
  }
}
