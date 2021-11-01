package core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ItemTest {
  Item item;

  @BeforeEach
  void setup() {
    item = new Item("ItemName", 10);
  }

  @Test
  @DisplayName("Test Item Constructor")
  void testConstructor() {
    assertNotNull(item.getId());
    assertEquals("ItemName", item.getName());
    assertEquals(10, item.getAmount());
    assertNull(item.getBarcode());
    assertNull(item.getBrand());
    assertNull(item.getRegularPrice());
    assertNull(item.getSalePrice());
    assertNull(item.getPurchasePrice());
    assertNull(item.getSection());
    assertNull(item.getRow());
    assertNull(item.getShelf());
    assertNull(item.getHeight());
    assertNull(item.getWidth());
    assertNull(item.getLength());
    assertNull(item.getWeight());
    assertNotNull(item.getCreationDate());

    assertThrows(IllegalArgumentException.class, () -> new Item(null, 10));
    assertThrows(IllegalArgumentException.class, () -> new Item(null));
  }

  @Test
  @DisplayName("Test validation in setters")
  void testSetterValidation() {
    assertThrows(IllegalArgumentException.class, () -> item.setId(null));
    assertThrows(IllegalArgumentException.class, () -> item.setName(null));
    assertThrows(IllegalArgumentException.class, () -> item.setAmount(CoreConst.MIN_AMOUNT-1));
    assertThrows(IllegalArgumentException.class, () -> item.setAmount(CoreConst.MAX_AMOUNT+1));
    assertThrows(IllegalArgumentException.class, () -> item.setBarcode("abcabcabcabca"));
    assertThrows(IllegalArgumentException.class, () -> item.setBarcode("12345678"));
    assertThrows(IllegalArgumentException.class, () -> item.setRegularPrice(new BigDecimal(CoreConst.MIN_PRICE - 1)));
    assertThrows(IllegalArgumentException.class, () -> item.setSalePrice(new BigDecimal(CoreConst.MIN_PRICE - 1)));
    assertThrows(IllegalArgumentException.class, () -> item.setPurchasePrice(new BigDecimal(CoreConst.MIN_PRICE - 1)));
    assertThrows(IllegalArgumentException.class, () -> item.setSection("A".repeat(CoreConst.MAX_SECTION_LENGTH+1)));
    assertThrows(IllegalArgumentException.class, () -> item.setRow("A".repeat(CoreConst.MAX_ROW_LENGTH+1)));
    assertThrows(IllegalArgumentException.class, () -> item.setShelf("A".repeat(CoreConst.MAX_SHELF_LENGTH+1)));
    assertThrows(IllegalArgumentException.class, () -> item.setHeight(new BigDecimal(CoreConst.MIN_ITEM_DIMENSION - 1)));
    assertThrows(IllegalArgumentException.class, () -> item.setWidth(new BigDecimal(CoreConst.MIN_ITEM_DIMENSION - 1)));
    assertThrows(IllegalArgumentException.class, () -> item.setLength(new BigDecimal(CoreConst.MIN_ITEM_DIMENSION - 1)));
    assertThrows(IllegalArgumentException.class, () -> item.setWeight(new BigDecimal(CoreConst.MIN_WEIGHT - 1)));
  }

  @Test
  @DisplayName("Test increment/decrement")
  void testIncrementDecrement() {
    item.setAmount(10);
    item.decrementAmount();
    assertEquals(9, item.getAmount());
    item.incrementAmount();
    assertEquals(10, item.getAmount());
  }

  
  @Test
  @DisplayName("Test validation in amount increment/decrement")
  void testIncrementDecrementValidation() {
    item.setAmount(CoreConst.MIN_AMOUNT);
    assertThrows(IllegalStateException.class, () -> item.decrementAmount());
    item.setAmount(CoreConst.MAX_AMOUNT);
    assertThrows(IllegalStateException.class, () -> item.incrementAmount());
  }

  @Test
  @DisplayName("Test setters")
  void testSetters() {
    Random rnd = new Random();

    String itemId = getRandomString();
    item.setId(itemId);
    assertEquals(itemId, item.getId());

    String itemName = getRandomString();
    item.setName(itemName);
    assertEquals(itemName, item.getName());

    int itemAmount = rnd.nextInt(10000);
    item.setAmount(itemAmount);
    assertEquals(itemAmount, item.getAmount());

    String barcode = "0123456789012";
    item.setBarcode(barcode);
    assertEquals(barcode, item.getBarcode());

    String brandName = getRandomString();
    item.setBrand(brandName);
    assertEquals(brandName, item.getBrand());

    Double regularPrice = rnd.nextDouble();
    item.setRegularPrice(BigDecimal.valueOf(regularPrice));
    assertEquals(regularPrice, item.getRegularPrice().doubleValue());

    Double salePrice = rnd.nextDouble();
    item.setSalePrice(BigDecimal.valueOf(salePrice));
    assertEquals(salePrice, item.getSalePrice().doubleValue());

    Double purchasePrice = rnd.nextDouble();
    item.setPurchasePrice(BigDecimal.valueOf(purchasePrice));
    assertEquals(purchasePrice, item.getPurchasePrice().doubleValue());

    String section = "A";
    item.setSection(section);
    assertEquals(section, item.getSection());

    String row = String.valueOf(rnd.nextInt(50));
    item.setRow(row);
    assertEquals(row, item.getRow());

    String shelf = String.valueOf(rnd.nextInt(50));
    item.setShelf(shelf);
    assertEquals(shelf, item.getShelf());

    Double height = rnd.nextDouble();
    item.setHeight(BigDecimal.valueOf(height));
    assertEquals(height, item.getHeight().doubleValue());

    Double width = rnd.nextDouble();
    item.setWidth(BigDecimal.valueOf(width));
    assertEquals(width, item.getWidth().doubleValue());

    Double length = rnd.nextDouble();
    item.setLength(BigDecimal.valueOf(length));
    assertEquals(length, item.getLength().doubleValue());

    Double weight = rnd.nextDouble();
    item.setWeight(BigDecimal.valueOf(weight));
    assertEquals(weight, item.getWeight().doubleValue());
  }

  private String getRandomString() {
    return UUID.randomUUID().toString();
  }

  public static void assertItemsEqual(Item expected, Item actual) {
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getAmount(), actual.getAmount());
    assertEquals(expected.getBarcode(), actual.getBarcode());
    assertEquals(expected.getBrand(), actual.getBrand());
    assertEquals(expected.getCreationDate(), actual.getCreationDate());
    assertEquals(expected.getCurrentPrice(), actual.getCurrentPrice());
    assertEquals(expected.getSalePrice(), actual.getSalePrice());
    assertEquals(expected.getRegularPrice(), actual.getRegularPrice());
    assertEquals(expected.getPurchasePrice(), actual.getPurchasePrice());
    assertEquals(expected.getSection(), actual.getSection());
    assertEquals(expected.getRow(), actual.getRow());
    assertEquals(expected.getShelf(), actual.getShelf());
    assertEquals(expected.getHeight(), actual.getHeight());
    assertEquals(expected.getWidth(), actual.getWidth());
    assertEquals(expected.getLength(), actual.getLength());
    assertEquals(expected.getWeight(), actual.getWeight());
  }

  static int changeCounter;

  @Test
  @DisplayName("Test listener")
  void testListener() {
    ItemListener listener = () -> changeCounter++;
    item.addListener(listener);

    changeCounter = 0;
    item.setId("itemId");
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setName("name");
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setAmount(2);
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setBarcode("1234567890123");
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setBrand("brand");
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setRegularPrice(BigDecimal.valueOf(100.00));
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setSalePrice(BigDecimal.valueOf(99.99));
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setPurchasePrice(BigDecimal.valueOf(1.43));
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setSection("A");
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setRow("42");
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setShelf("02");
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setHeight(BigDecimal.valueOf(10.0));
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setWidth(BigDecimal.valueOf(2.32));
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setLength(BigDecimal.valueOf(53.0));
    assertEquals(changeCounter, 1);

    changeCounter = 0;
    item.setWeight(BigDecimal.valueOf(0.012));
    assertEquals(changeCounter, 1);


    item.removeListener(listener);

    changeCounter = 0;
    item.setLength(BigDecimal.valueOf(53.0));
    assertEquals(changeCounter, 0);
  }
}
