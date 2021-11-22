package ui;

import static java.util.Map.entry;

import core.ClientWarehouse;
import core.CoreConst;
import core.Item;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.itemfield.ItemField;
import ui.validators.BarcodeValidator;
import ui.validators.DoubleValidator;
import ui.validators.IntegerValidator;
import ui.validators.MaxLengthValidator;
import ui.validators.MaxValueValidator;
import ui.validators.NotEmptyValidatior;
import ui.validators.NotNegativeValidator;

import java.io.IOException;
import java.util.Map;

/**
 * This controller shows a separate window with all the properties of an item,
 * and the possibilty to change propeties of the selected item.
 */
public class DetailsViewController {
  private static final int SAFEBOUND_TOP = 30;
  private static final int SAFEBOUND_BOTTOM = 75;
  private static final int placementMaxLength = CoreConst.MAX_POSITION_LENGTH;
  private static BarcodeValidator barcodeValidator;

  @FXML private ScrollPane detailsViewScrollPane;

  @FXML private Label sharedErrorLabel;
  @FXML private TextField inpName;
  @FXML private Label nameErrorLabel;
  @FXML private TextField inpBrand;
  @FXML private TextField inpAmount;
  @FXML private Button btnIncrement;
  @FXML private Button btnDecrement;
  @FXML private TextField inpPlacementSection;
  @FXML private TextField inpPlacementRow;
  @FXML private TextField inpPlacementShelf;
  @FXML private TextField inpOrdinaryPrice;
  @FXML private TextField inpSalesPrice;
  @FXML private TextField inpRetailerPrice;
  @FXML private TextField inpDimensionsLength;
  @FXML private TextField inpDimensionsWidth;
  @FXML private TextField inpDimensionsHeigth;
  @FXML private TextField inpWeight;
  @FXML private TextField inpBarcode;
  @FXML private Button btnEdit;
  @FXML private Button btnSave;
  @FXML private Button btnDelete;
  @FXML private ImageView barcodeImageView;
  @FXML private HBox sectionSaveDelete;
  @FXML private Label barcodeErrorLabel;
  @FXML private Label notifyCannotEdit;

  private final Stage stage;
  private Parent detailsRoot;

  private final Item item;
  private final ClientWarehouse warehouse;
  private final WarehouseController warehouseController;

  private boolean editing = false;

  private enum Field {
    NAME, BRAND, AMOUNT, REGULAR_PRICE, SALE_PRICE, PURCHASE_PRICE, SECTION, ROW, SHELF, HEIGHT, WIDTH, LENGTH, WEIGHT,
    BARCODE
  }

  private Map<Field, ItemField> fields;

  public DetailsViewController(Item item, ClientWarehouse warehouse, WarehouseController warehouseController) {
    if (item == null || warehouseController == null || warehouse == null) {
      throw new IllegalArgumentException();
    }

    this.item = item;
    this.warehouse = warehouse;
    this.warehouseController = warehouseController;

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("DetailsView.fxml"));
      loader.setController(this);
      detailsRoot = loader.load();
    } catch (IOException e) {
      close();
    }
    double height = Screen.getPrimary().getBounds().getHeight() - SAFEBOUND_BOTTOM;
    if (height > 740) {
      height = 740;
    }
    stage = new Stage();
    stage.setScene(new Scene(detailsRoot, 450, height));
    stage.setMinWidth(450);
    stage.setY(SAFEBOUND_TOP);
    stage.setTitle("Rediger: " + item.getName());
    stage.setOnCloseRequest(e -> close());

    try {
      stage.getIcons().add(new Image(WarehouseApp.class.getResourceAsStream("appIcon/1-rounded.png")));
    } catch (Exception e) {
      e.printStackTrace();
    }

    // toggles between info-text and edit-button
    btnEdit.setVisible(!(warehouse.getCurrentUser() == null));
    notifyCannotEdit.setVisible(warehouse.getCurrentUser() == null);

    maxCharsLimiter(inpPlacementSection, placementMaxLength);
    maxCharsLimiter(inpPlacementRow, placementMaxLength);
    maxCharsLimiter(inpPlacementShelf, placementMaxLength);
    onlyIntLimiter(inpAmount);
    onlyFloatLimiter(inpOrdinaryPrice);
    onlyFloatLimiter(inpSalesPrice);
    onlyFloatLimiter(inpRetailerPrice);
    onlyFloatLimiter(inpDimensionsHeigth);
    onlyFloatLimiter(inpDimensionsLength);
    onlyFloatLimiter(inpDimensionsWidth);
    onlyFloatLimiter(inpWeight);
    maxCharsLimiter(inpBarcode, CoreConst.MAX_BARCODE_LENGTH);
    onlyIntLimiter(inpBarcode);
  }
  
  private static void onlyIntLimiter(TextField textField) {
    textField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, 
          String newValue) {
        if (!newValue.matches("\\d*")) {
            textField.setText(newValue.replaceAll("[^\\d]", ""));
        }
      }
    });
  }

  private static void onlyFloatLimiter(TextField textField) {
    textField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue, 
          String newValue) {
        if (!newValue.matches("[0-9]*\\.?[0-9]*")) {
            textField.setText(newValue.replaceAll("[^[0-9]|\\.]", ""));
        }
      }
    });
  }

  private static void maxCharsLimiter(TextField textField, final int maxLength) {
    textField.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
        if (textField.getText().length() > maxLength) {
          String s = textField.getText().substring(0, maxLength);
          textField.setText(s);
        }
      }
    });
  }

  @FXML
  public void initialize() {
    fields = Map.ofEntries(
        entry(Field.NAME,
            new ItemField(inpName, false, itemField -> item.setName(itemField.getStringValue()),
                item::getName, nameErrorLabel)),

        entry(Field.BRAND,
            new ItemField(inpBrand, true, itemField -> item.setBrand(itemField.getStringValue()),
                item::getBrand, sharedErrorLabel)),

        entry(Field.AMOUNT,
            new ItemField(inpAmount, false, itemField -> item.setAmount(itemField.getIntegerValue()),
                item::getAmount, sharedErrorLabel)),

        entry(Field.REGULAR_PRICE,
            new ItemField(inpOrdinaryPrice, true, itemField -> item.setRegularPrice(itemField.getDoubleValue()),
                item::getRegularPrice, sharedErrorLabel)),

        entry(Field.SALE_PRICE,
            new ItemField(inpSalesPrice, true, itemField -> item.setSalePrice(itemField.getDoubleValue()),
                item::getSalePrice, sharedErrorLabel)),

        entry(Field.PURCHASE_PRICE,
            new ItemField(inpRetailerPrice, true, itemField -> item.setPurchasePrice(itemField.getDoubleValue()),
                item::getPurchasePrice, sharedErrorLabel)),

        entry(Field.SECTION,
            new ItemField(inpPlacementSection, true, itemField -> item.setSection(itemField.getStringValue()),
                item::getSection, sharedErrorLabel)),

        entry(Field.ROW,
            new ItemField(inpPlacementRow, true, itemField -> item.setRow(itemField.getStringValue()),
                item::getRow, sharedErrorLabel)),

        entry(Field.SHELF,
            new ItemField(inpPlacementShelf, true, itemField -> item.setShelf(itemField.getStringValue()),
                item::getShelf, sharedErrorLabel)),

        entry(Field.HEIGHT,
            new ItemField(inpDimensionsHeigth, true, itemField -> item.setHeight(itemField.getDoubleValue()),
                item::getHeight, sharedErrorLabel)),

        entry(Field.WIDTH,
            new ItemField(inpDimensionsWidth, true, itemField -> item.setWidth(itemField.getDoubleValue()),
                item::getWidth, sharedErrorLabel)),

        entry(Field.LENGTH,
            new ItemField(inpDimensionsLength, true, itemField -> item.setLength(itemField.getDoubleValue()),
                item::getLength, sharedErrorLabel)),

        entry(Field.WEIGHT,
            new ItemField(inpWeight, true, itemField -> item.setWeight(itemField.getDoubleValue()),
                item::getWeight, sharedErrorLabel)),

        entry(Field.BARCODE, 
            new ItemField(inpBarcode, true, itemField -> item.setBarcode(itemField.getStringValue()),
                item::getBarcode, sharedErrorLabel))
    );
  }

  protected boolean isEditing() {
    return editing;
  }

  protected void close() {
    warehouseController.removeDetailsViewController(item);
    stage.close();
  }

  protected void showDetailsView() {
    stage.show();
    stage.setIconified(false);
    stage.requestFocus();
    this.update();
    addInputValidationListeners();
    inpName.setOnMouseClicked(e -> inpName.selectAll());
  }

  private void addInputValidationListeners() {
    fields.get(Field.NAME).addValidators(new NotEmptyValidatior());
    fields.get(Field.BRAND).addValidators();
    fields.get(Field.AMOUNT).addValidators(new IntegerValidator(), new NotNegativeValidator(), new MaxValueValidator(CoreConst.MAX_AMOUNT));

    fields.get(Field.REGULAR_PRICE).addValidators(new DoubleValidator(), new NotNegativeValidator());
    fields.get(Field.SALE_PRICE).addValidators(new DoubleValidator(), new NotNegativeValidator());
    fields.get(Field.PURCHASE_PRICE).addValidators(new DoubleValidator(), new NotNegativeValidator());

    fields.get(Field.SECTION).addValidators(new MaxLengthValidator(placementMaxLength));
    fields.get(Field.ROW).addValidators(new MaxLengthValidator(placementMaxLength));
    fields.get(Field.SHELF).addValidators(new MaxLengthValidator(placementMaxLength));

    fields.get(Field.HEIGHT).addValidators(new DoubleValidator(), new NotNegativeValidator());
    fields.get(Field.LENGTH).addValidators(new DoubleValidator(), new NotNegativeValidator());
    fields.get(Field.WIDTH).addValidators(new DoubleValidator(), new NotNegativeValidator());

    fields.get(Field.WEIGHT).addValidators(new DoubleValidator(), new NotNegativeValidator());
    fields.get(Field.BARCODE).addValidators(new BarcodeValidator());
  }

  private void update() {
    for (ItemField field : fields.values()) {
      field.updateField();
    }
    
    if (item.getBarcode() != null) {
      generateBarcodeImage();
    }
  }

  private void generateBarcodeImage() {
    try {
      Image barcodeImage = BarcodeCreator.generateBarcodeImage(item.getBarcode().substring(0, 12));
      barcodeImageView.setImage(barcodeImage);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void saveItem() {
    nameErrorLabel.setText("");
    barcodeErrorLabel.setText("");
    if (fields.get(Field.NAME).getStringValue() == null) {
      nameErrorLabel.setText("Legg til produktnavn for å lagre.");
      return;
    }
    String barcode = inpBarcode.getText();
    barcodeValidator = new BarcodeValidator();
    if (barcode.length() > 0 && barcode.length() < 13) {
      barcodeErrorLabel.setText("Barcode må inneholde 13 tall.");
      return;
    } else if (barcode.length() != 0 && !barcodeValidator.validateInput(barcode)) {
      barcodeErrorLabel.setText("Kontrolltallet stemmet ikke med de 12 første tallene.");
      return;
    }
    for (ItemField field : fields.values()) {
      if (!field.isValid()) {
        return;
      }
    }
    item.doBatchChanges(() -> {
      for (ItemField field : fields.values()) {
        field.saveField();
      }
    });
    if (!warehouse.containsItem(item.getId())) {
      barcodeErrorLabel.setText("");
      warehouse.addItem(item);
    }
    toggleEditing();
    update();
  }
  
  @FXML private void promptRemoveItem() {
    if (item.getName().equals("")) {
      stage.close();
      return;
    }

    Alert promptDeleteConfirmationAlert = new Alert(AlertType.WARNING);
    promptDeleteConfirmationAlert.setHeaderText("Er du sikker på at du vil slette " + item.getName() + "?");
    promptDeleteConfirmationAlert.setContentText("Denne handlingen kan ikke angres");
    promptDeleteConfirmationAlert.initStyle(StageStyle.UTILITY);
    ButtonType dontDeleteButtonType = new ButtonType("Ikke slett", ButtonData.CANCEL_CLOSE);
    ButtonType confirmDeleteButtonType = new ButtonType("Slett", ButtonData.OK_DONE);

    promptDeleteConfirmationAlert.getButtonTypes().setAll(dontDeleteButtonType, confirmDeleteButtonType);

    promptDeleteConfirmationAlert.showAndWait()
    .filter(response -> response == confirmDeleteButtonType)
        .ifPresent(response -> removeItem());
  }

  private void removeItem() {
    warehouse.removeItem(item.getId());
    warehouseController.removeDetailsViewController(item);
    stage.close();
  }

  @FXML
  private void decrement() {
    inpAmount.setText(String.valueOf(Integer.parseInt(inpAmount.getText()) - 1));
  }

  @FXML
  private void increment() {
    inpAmount.setText(String.valueOf(Integer.parseInt(inpAmount.getText()) + 1));
  }

  @FXML
  protected void toggleEditing() {
    editing = !editing;
    for (ItemField field : fields.values()) {
      field.setDisabled(!editing);
    }
    
    btnIncrement.setDisable(!editing);
    btnDecrement.setDisable(!editing);

    WarehouseController.setRegionVisibility((Region) btnSave.getParent(), editing);
    WarehouseController.setRegionVisibility((Region) btnEdit.getParent(), !editing);

    btnEdit.setVisible(!editing);
    btnSave.setVisible(editing);
    btnSave.setDisable(!editing);
    btnDelete.setVisible(editing);
    btnDelete.setDisable(!editing);

    btnDelete.prefWidthProperty().bind(sectionSaveDelete.widthProperty().divide(2));
    btnSave.prefWidthProperty().bind(sectionSaveDelete.widthProperty().divide(2));
  }

  public String toString() {
    return "" + inpName.getText() + "\n" + inpBrand.getText() + "\n" + inpAmount.getText() + "\n"
        + inpOrdinaryPrice.getText() + "\n" + inpSalesPrice.getText() + "\n" + inpRetailerPrice.getText() + "\n"
        + inpPlacementSection.getText() + "\n" + inpPlacementRow.getText() + "\n" + inpPlacementShelf.getText() + "\n"
        + inpDimensionsLength.getText() + "\n" + inpDimensionsWidth.getText() + "\n" + inpDimensionsHeigth.getText()
        + "\n" + inpWeight.getText() + "\n" + inpBarcode.getText();
  }

  
  
  protected ScrollPane getScrollPane() {
    return detailsViewScrollPane;
  }
  
}
