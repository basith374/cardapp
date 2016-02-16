/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardsapp;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author bluroe
 */
public class NewStockController implements Initializable {

    @FXML private TextField codeFld;
    @FXML private TextField qtyFld;
    @FXML private TextField retailPriceFld;
    @FXML private TextField actualPriceFld;
    @FXML private TextField mfgCoFld;
    
    private DBAccess dbaccess;
    
    private StockReportController stockController;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        dbaccess = DBAccess.getInstance();
    }
    
    public void setSRC(StockReportController src) {
        stockController = src;
    }
    
    @FXML
    private void onCancel(ActionEvent event) {
        Stage dialog = (Stage) ((Button)event.getSource()).getScene().getWindow();
        dialog.close();
    }
    
    
    public void addStock(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "One or more input boxes are empty", ButtonType.OK);
        alert.initOwner(stage);
        alert.initModality(Modality.WINDOW_MODAL);
        if(codeFld.getText().equals("") || qtyFld.getText().equals("") || retailPriceFld.getText().equals("") || actualPriceFld.getText().equals("") || mfgCoFld.getText().equals("")) {
            alert.showAndWait();
            return;
        }
        int code = Integer.parseInt(codeFld.getText());
        int qty = Integer.parseInt(qtyFld.getText());
        double retailprice = Double.parseDouble(retailPriceFld.getText());
        double actualPrice = Double.parseDouble(actualPriceFld.getText());
        String mfgCo = mfgCoFld.getText();
        try {
            int count = dbaccess.stockExists(code);
            if(count > 0) {
                alert.setContentText("Card exists");
                alert.showAndWait();
                return;
            }
            dbaccess.insertStock(code, qty, actualPrice, retailprice, mfgCo);
            Card card = new Card(code, qty, actualPrice, retailprice, mfgCo);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stockController.getStockTable().getItems().add(card);
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(NewStockController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void onStockAndClose(ActionEvent event) {
        Stage dialog = (Stage) ((Button)event.getSource()).getScene().getWindow();
        addStock(dialog);
        dialog.close();
    }
    
    @FXML
    private void onStock(ActionEvent event) {
        Stage dialog = (Stage) ((Button)event.getSource()).getScene().getWindow();
        addStock(dialog);
        codeFld.setText("");
        qtyFld.setText("");
        retailPriceFld.setText("");
        actualPriceFld.setText("");
        mfgCoFld.setText("");
    }
    
    public void verifyInputs(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "One or more input boxes are empty", ButtonType.OK);
        alert.initOwner(stage);
        alert.initModality(Modality.WINDOW_MODAL);
        if(codeFld.getText().equals("") || qtyFld.getText().equals("") || retailPriceFld.getText().equals("") || actualPriceFld.getText().equals("") || mfgCoFld.getText().equals("")) {
            alert.showAndWait();
            return;
        }
    }
    
}
