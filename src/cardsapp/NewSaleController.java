/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardsapp;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author bluroe
 */
public class NewSaleController implements Initializable {
        
    @FXML private TextField codeFld;
    @FXML private TextField qtyFld;
    @FXML private TextField discountFld;
    @FXML private TextField printingFld;
    @FXML private TextField packingFld;
    @FXML private TextField customerNameFld;
    @FXML private TextField customerPhoneFld;
    
    private DBAccess dbaccess;
    
    private StockReportController stockController;
    private DailySaleReportController salesController;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbaccess = DBAccess.getInstance();
    }
    
    public void setSRC(StockReportController src) {
        stockController = src;
    }
    
    public void setDSRC(DailySaleReportController dsrc) {
        salesController = dsrc;
    }
    
    @FXML
    private void onCancel(ActionEvent event) {
        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void onSale(ActionEvent event) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.ERROR, "One or more input boxes are empty", ButtonType.OK);
        alert.initOwner(stage);
        alert.initModality(Modality.WINDOW_MODAL);
        if(codeFld.getText().equals("") || qtyFld.getText().equals("") || discountFld.getText().equals("") || printingFld.getText().equals("") || packingFld.getText().equals("")) {
            alert.showAndWait();
            return;
        }
        int cardCode = Integer.parseInt(codeFld.getText());
        int cardQty = Integer.parseInt(qtyFld.getText());
        double cardDiscount = Double.parseDouble(discountFld.getText());
        double cardPacking = Double.parseDouble(packingFld.getText());
        double cardPrinting = Double.parseDouble(printingFld.getText());
        String customerName = customerNameFld.getText();
        String customerPhone = customerPhoneFld.getText();
        try {
            Card card = findCard(cardCode);
            if(card == null) {
                alert.setContentText("Card not found");
                alert.showAndWait();
                return;
            }
            double price = card.retailPriceProperty().get();
            Sale item = new Sale(cardCode, cardQty, price, cardDiscount, cardPrinting, cardPacking, customerName, customerPhone, sdf.format(new Date()));
//            System.out.println("total " + item.priceProperty().get() + " x " + cardQty + " = " +  card.retailPriceProperty().get() * cardQty);
//            System.out.println("discount " + cardDiscount + " x " + cardQty + " = " +  cardDiscount * cardQty);

            int stock = card.qtyProperty().get();
            int sold = item.qtyProperty().get();
            int newStock = stock - sold;
            if(newStock < 0) {
                alert.setContentText("Not enough cards");
                alert.showAndWait();
                return;
            }
            dbaccess.updateCardQty(cardCode, newStock);
            long id = dbaccess.insertSale(cardCode, cardQty, price, cardPrinting, cardPacking, cardDiscount, item.totalProperty().get(), customerName, customerPhone);
            item.idProperty().set(id);
            Platform.runLater(new Runnable() {
                public void run() {
                    card.qtyProperty().set(newStock);
                    salesController.getSalesTable().getItems().add(item);
                }
            });
            stage.close();
        } catch (SQLException ex) {
            Logger.getLogger(NewSaleController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setCardCode(int code) {
        codeFld.setText(String.valueOf(code));
    }
    
    public void processSale(ObservableList<Sale> items) throws SQLException {
        double subtotal = 0;
        double discount = 0;
        for(Sale item:items) {
            subtotal += item.qtyProperty().get() * item.priceProperty().get();
            discount += item.qtyProperty().get() * item.discountProperty().get();
            Card card = dbaccess.getCard(item.codeProperty().get());
            int stock = card.qtyProperty().get();
            int sold = item.qtyProperty().get();
            int newStock = stock - sold;
            card.qtyProperty().set(newStock);
            System.out.println("card " + card.codeProperty().get() + " ,stock: " + stock + ", sold:" + sold + ",updated:" + newStock);
        }
    }
    
    public void tempAction(ActionEvent event) {
        
    }
    
    public Card findCard(int cardCode) {
        for(Card card : stockController.getStockTable().getItems()) {
            if(card.codeProperty().get() == cardCode) {
                return card;
            }
        }
        return null;
    }
    
}
