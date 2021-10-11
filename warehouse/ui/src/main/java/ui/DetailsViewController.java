package ui;

import core.Item;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

/**
 * This controller shows a separate window with all the properties of an item, and the possibilty to change propeties of the selected item.
 */
public class DetailsViewController {
  @FXML private ScrollPane detailsViewScrollPane;
  
  @FXML private TextField inpName;
  @FXML private TextField inpAmount;
  @FXML private Button btnIncrement;
  @FXML private Button btnDecrement;
  @FXML private Label placementSection;
  @FXML private Label placementRow;
  @FXML private Label placementShelf;
  @FXML private TextField inpOrdinaryPrice;
  @FXML private TextField inpSalesPrice;
  @FXML private TextField inpRetailerPrice;
  @FXML private ComboBox<String> comboBoxSalesTax;
  @FXML private TextField inpPlacementSection;
  @FXML private TextField inpPlacementRow;
  @FXML private TextField inpPlacementShelf;
  @FXML private TextField inpDimensionsLength;
  @FXML private TextField inpDimensionsWidth;
  @FXML private TextField inpDimensionsHeight;
  @FXML private TextField inpWeight;
  @FXML private TextField inpBarcode;
  @FXML private ImageView barcodeImageView;

  private FXMLLoader loader;
  private Stage stage;
  private Parent detailsRoot;
  private Item item;

  private static final int safeBoundTop = 30;
  private static final int safeBoundBottom = 75;

  public DetailsViewController(Item item) {
    if (item == null) {
      throw new IllegalArgumentException();
    }
    this.item = item;
    try {
      loader = new FXMLLoader(getClass().getResource("DetailsView.fxml"));
      loader.setController(this);
      detailsRoot = loader.load();
      stage = new Stage();
      stage.setScene(new Scene(detailsRoot));
    } catch (IOException e) {
      e.printStackTrace();
    }
    stage.setMinWidth(450);
    stage.setHeight(Screen.getPrimary().getBounds().getHeight() - safeBoundBottom);
    stage.setY(safeBoundTop);
    stage.setTitle("Edit: " + item.getName());
  }

  protected void requestFocus() {
    stage.requestFocus();
  }

  protected void showDetailsView() {
    stage.show();
    stage.setIconified(false);
    requestFocus();
    stage.setMaxHeight(detailsRoot.prefHeight(0));
    this.update();
  }
 
  private void update() {
    this.inpName.setText(item.getName());
    this.inpAmount.setText(String.valueOf(item.getAmount()));
    this.btnIncrement.setOnAction(e -> item.incrementAmount());
    this.btnDecrement.setOnAction(e -> item.decrementAmount());
    // placements
    this.inpOrdinaryPrice.setText(String.valueOf(item.getRegularPrice()));
    // priser
    // comboBox
    // dimensions
    this.inpWeight.setText(String.valueOf(item.getWeight()));
    this.inpBarcode.setText(item.getBarcode());

    if (item.getBarcode() != null) {
      generateBarcodeImage();
    }
  }

  private void generateBarcodeImage() {
    try (InputStream imageInputStream =
             BarcodeCreator.generateBarcodeImageInputStream(item.getBarcode().substring(0, 12))) {
      Image barcodeImage = new Image(imageInputStream);
      barcodeImageView.setImage(barcodeImage);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void saveItem() {
    item.setName(inpName.getText());
    item.setAmount(Integer.parseInt(inpAmount.getText()));
    item.setRegularPrice(Double.parseDouble(inpOrdinaryPrice.getText()));
    item.setWeight(Double.parseDouble(inpWeight.getText()));
    item.setBarcode(inpBarcode.getText());
  }
  
  
}
