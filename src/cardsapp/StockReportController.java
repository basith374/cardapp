/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardsapp;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 *
 * @author bluroe
 */
public class StockReportController implements Initializable {
    
    @FXML private TableView<Card> stockTable;
    private DBAccess dbaccess;
    
    private boolean detailsShown = false;
    private LoginController loginController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        dbaccess = DBAccess.getInstance();
        try {
            stockTable.setItems(null);
            ObservableList<Card> cards = dbaccess.readStock();
            stockTable.setItems(cards);
        } catch (SQLException ex) {
            Logger.getLogger(StockReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void toggleDetails(ActionEvent event) {
        Button btn = (Button) event.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                detailsShown = !detailsShown;
                if(detailsShown) {
                    btn.setText("Hide Details");
                    stockTable.getColumns().get(0).prefWidthProperty().bind(stockTable.widthProperty().divide(5));
                    stockTable.getColumns().get(1).prefWidthProperty().bind(stockTable.widthProperty().divide(5));
                    stockTable.getColumns().get(2).prefWidthProperty().bind(stockTable.widthProperty().divide(5));
                    stockTable.getColumns().get(3).prefWidthProperty().bind(stockTable.widthProperty().divide(5));
                    stockTable.getColumns().get(4).prefWidthProperty().bind(stockTable.widthProperty().divide(5));
                } else {
                    btn.setText("Show Details");
                    stockTable.getColumns().get(0).prefWidthProperty().bind(stockTable.widthProperty().divide(3));
                    stockTable.getColumns().get(1).prefWidthProperty().bind(stockTable.widthProperty().divide(3));
                    stockTable.getColumns().get(2).prefWidthProperty().bind(stockTable.widthProperty().divide(3));
                }
                stockTable.getColumns().get(3).setVisible(detailsShown);
                stockTable.getColumns().get(4).setVisible(detailsShown);
            }   
        };
        if(detailsShown)
            Platform.runLater(runnable);
        else {
            loginController.setAction(runnable);
            loginController.showLoginPrompt();
        }
    }
    
    public TableView<Card> getStockTable() {
        return stockTable;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
    
    @FXML
    private void onPrint(ActionEvent event) {
        PrinterJob job = PrinterJob.createPrinterJob();
        job.printPage(stockTable);
    }
    
    @FXML
    private void deleteStock(ActionEvent event) {
        Card card = stockTable.getSelectionModel().getSelectedItem();
        int index = stockTable.getSelectionModel().getSelectedIndex();
        if(card != null) {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    try {
                        dbaccess.deleteStock(card.codeProperty().get());
                        stockTable.getItems().remove(index);
                    } catch (SQLException ex) {
                        Logger.getLogger(StockReportController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            };
            loginController.setAction(runnable);
            loginController.showLoginPrompt();
        }
    }
        
}
