package ui;

import core.CoreConst.SortOption;
import core.Item;
import core.Warehouse;
import core.WarehouseListener;
import data.DataPersistence;
import data.WarehouseFileSaver;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main Controller class. Controls the main Warehouse view.
 */
public class WarehouseController implements WarehouseListener {
  private static final String FILENAME = "warehouse";

  private Warehouse warehouse;
  private final DataPersistence dataPersistence = new WarehouseFileSaver(FILENAME);

  @FXML private Label usernameLabel;
  @FXML private Button loginButton;
  @FXML private Button addItemButton;
  @FXML private AnchorPane root;
  @FXML private GridPane dividerGridPane;
  @FXML private ScrollPane itemContainer;
  @FXML private VBox itemList;
  @FXML private TextField searchInput;
  @FXML private ComboBox<String> sortBySelector;
  @FXML private Button orderByButton;
  @FXML private VBox sortAndOrderSelectors;
  @FXML private VBox titleAddandSearch;

  private SortOption sortBy = SortOption.DATE;
  private boolean ascending = true;

  private Map<Item, DetailsViewController> detailsViewControllers = new HashMap<>();
  private LoginController loginController;

  @FXML
  void initialize() {
    try {
      warehouse = dataPersistence.getWarehouse();
    } catch (Exception e) {
      System.out.println("Could not load saved warehouse");
      System.out.println(e.toString());
    }
    if (warehouse == null) {
      warehouse = new Warehouse();
    }

    loginController = new LoginController(this, warehouse);
    usernameLabel.setVisible(false);
    updateInventory();
        
    List<String> displaySortStrings = List.of("Antall", "Dato", "Navn", "Pris", "Vekt");
    sortBySelector.getItems().addAll(displaySortStrings);

    searchInput.textProperty().addListener((observable, oldValue, newValue) -> updateInventory());
    
    warehouse.addListener(this);
  }

  @FXML
  private void login() {
    if (warehouse.getCurrentUser() == null) {
      loginController.showLoginView();
    } else {
      Alert promptLogoutConfirmationAlert = new Alert(AlertType.WARNING);
      promptLogoutConfirmationAlert.setHeaderText("Er du sikker på at du vil logge ut?");
      promptLogoutConfirmationAlert.initStyle(StageStyle.UTILITY);
      ButtonType confirmOkButtonType = new ButtonType("OK", ButtonData.OK_DONE);
     
      promptLogoutConfirmationAlert.getButtonTypes().setAll(confirmOkButtonType);

      promptLogoutConfirmationAlert.showAndWait()
      .filter(response -> response == confirmOkButtonType)
              .ifPresent(response -> confirmLogout());
    }
    updateInventory();
  }

  private void confirmLogout() {
    warehouse.removeCurrentUser();
    usernameLabel.setVisible(false);
    usernameLabel.setText("");
    loginButton.setText("Logg inn");
  }

  protected void updateUser() {
    usernameLabel.setText(warehouse.getCurrentUser().getUserName());
    usernameLabel.setVisible(true);
    loginButton.setText("Logg ut");
    updateInventory();
  }

  @FXML
  private void updateInventory() {
    itemList.getChildren().clear();
    List<Item> items = getItems();
    for (int i = 0; i < items.size(); i++) {
      ItemElementAnchorPane itemElement = new ItemElementAnchorPane(items.get(i));

      String id = items.get(i).getId();
      if (warehouse.isAdmin()) {
        itemElement.getDecrementButton().setOnAction(e -> decrementAmount(id));
        itemElement.getIncrementButton().setOnAction(e -> incrementAmount(id));
      } else {
        itemElement.getDecrementButton().setDisable(true);
        itemElement.getIncrementButton().setDisable(true);
        addItemButton.setVisible(false);
      }
      if (warehouse.findItem(id).getAmount() == 0) {
        itemElement.getDecrementButton().setDisable(true);
      }

      itemList.getChildren().add(itemElement);

      int index = i;

      itemElement.setOnMouseClicked(e -> openDetailsView(items.get(index)));
      itemElement.setOnMouseEntered(e -> hover(itemElement, index));
      itemElement.setOnMouseExited(e -> notHover(itemElement, index));
      notHover(itemElement, index);
    }

    if (warehouse.getCurrentUser() != null) {
      addItemButton.setVisible(true);
    } else {
      addItemButton.setVisible(false);
    }
  }

  private void openDetailsView(Item item) {
    if (! detailsViewControllers.containsKey(item)) {
      detailsViewControllers.put(item, new DetailsViewController(item, this.warehouse, this));
    }
    detailsViewControllers.get(item).showDetailsView();
  }

  private void notHover(ItemElementAnchorPane itemElement, int i) {
    itemElement.getStyleClass().removeAll(Arrays.asList("hoverOverDark", "hoverOverLight"));
    if (i % 2 == 0) {
      itemElement.getStyleClass().add("notHoverOverLight");
    } else { 
      itemElement.getStyleClass().add("notHoverOverDark");
    }
    itemElement.setButtonsVisible(false);
  }

  private void hover(ItemElementAnchorPane itemElement, int i) {
    itemElement.setCursor(Cursor.HAND);
    itemElement.getStyleClass().removeAll(Arrays.asList("notHoverOverDark", "notHoverOverLight"));
    if (i % 2 == 0) {
      itemElement.getStyleClass().add("hoverOverLight");
    } else { 
      itemElement.getStyleClass().add("hoverOverDark");
    }
    itemElement.setButtonsVisible(true);
  }

  protected void removeDetailsViewController(Item item) {
    if (detailsViewControllers.containsKey(item)) {
      detailsViewControllers.remove(item);
    }
    updateInventory();
  }

  protected List<Item> getItems() {
    return warehouse.getItemsSortedAndFiltered(sortBy, ascending, searchInput.getText());
  }

  @FXML
  private void addItem() {
    if (warehouse.isAdmin()) {
      Item item = new Item("");
      openDetailsView(item);
      detailsViewControllers.get(item).toggleEditing();
    }
  }

  @FXML
  protected void removeItem(String id) {
    warehouse.removeItem(warehouse.findItem(id));
    saveWarehouse();
  }

  protected void incrementAmount(String id) {
    if (warehouse.isAdmin()) {
      warehouse.findItem(id).incrementAmount();
      saveWarehouse();
    }
  }

  protected void decrementAmount(String id) {
    if (warehouse.isAdmin()) {
      warehouse.findItem(id).decrementAmount();
      saveWarehouse();
    }
  }

  @FXML
  private void changeSortBy() {

    String value = "";
    if (sortBySelector.getValue() != null) {
      value = sortBySelector.getValue().toString();
    }

    switch (value) {
      case "Dato":
        sortBy = SortOption.DATE;
        ascending = true;
        break;
      case "Antall":
        sortBy = SortOption.AMOUNT;
        ascending = false;
        break;
      case "Navn":
        sortBy = SortOption.NAME;
        ascending = true;
        break;
      case "Pris":
        sortBy = SortOption.PRICE;
        ascending = true;
        break;
      case "Vekt":
        sortBy = SortOption.WEIGHT;
        ascending = true;
        break;
      default:
        sortBy = SortOption.NAME;
        break;
    }
    updateInventory();
  }

  @FXML
  private void changeOrderBy() {
    ascending = !ascending;

    if (ascending && orderByButton.getStyleClass().contains("descending")) {
      orderByButton.getStyleClass().remove("descending");
    }
    if (!ascending && !orderByButton.getStyleClass().contains("descending")) {
      orderByButton.getStyleClass().add("descending");
    }

    updateInventory();
  }

  protected void saveWarehouse() {
    try {
      dataPersistence.saveItems(warehouse);
    } catch (Exception e) {
      System.out.println(e.toString());
    }
  }

  protected boolean canExit() {
    boolean currentlyEditing = false;
    for (DetailsViewController controller : detailsViewControllers.values()) {
      if (controller.isEditing()) {
        currentlyEditing = true;
      }
    }
    return !currentlyEditing;
  }

  @Override
  public void itemAddedToWarehouse(Item item) {
    updateInventory();
  }

  @Override
  public void warehouseItemsUpdated() {
    updateInventory();
  }

  @Override
  public void itemRemovedFromWarehouse(Item item) {
    updateInventory();
  }

  // for testing purposes only
  protected HashMap<Item, DetailsViewController> getDetailViewControllers() {
    return new HashMap<>(detailsViewControllers);
  }
} 
