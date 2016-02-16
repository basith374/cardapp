/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardsapp;

import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author bluroe
 */
public class DailySaleReportController implements Initializable {

    @FXML
    private TableView<Sale> salesTable;
    private DBAccess dbaccess;
    
    @FXML private DatePicker datePicker;
    private Stage stage;
    
    private LoginController loginController;
    private StockReportController src;
    
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        dbaccess = DBAccess.getInstance();
        String pattern = "dd-MM-yyyy";
        datePicker.setPromptText(pattern);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        datePicker.setConverter(new StringConverter<LocalDate>() {
            
            @Override
            public String toString(LocalDate date) {
                if(date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            
            @Override
            public LocalDate fromString(String string) {
                if(string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        try {
            salesTable.setItems(null);
            ObservableList<Sale> sales = dbaccess.readSales(LocalDate.now().format(dateFormatter));
            salesTable.setItems(sales);
        } catch (SQLException ex) {
            Logger.getLogger(DailySaleReportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(DailySaleReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void printSingle(ActionEvent event) {
        Sale sale = salesTable.getSelectionModel().getSelectedItem();
        if(sale != null) {
            int qty = sale.qtyProperty().get();
            double price = sale.priceProperty().get();
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setBackground(Background.EMPTY);
            Label nameLabel = new Label(sale.customerNameProperty().get());
            Label phoneLabel = new Label(sale.customerPhoneProperty().get());
            Label dateLabel = new Label(sale.dateProperty().get());
            Label codeLabel = new Label(String.valueOf(sale.codeProperty().get()));
            Label qtyLabel = new Label(String.valueOf(qty));
            Label priceLabel = new Label(String.valueOf(price));
            Label subTotalLabel = new Label(String.valueOf(qty * price));
            Label printingLabel = new Label(String.valueOf(sale.printingProperty().get()));
            Label packingLabel = new Label(String.valueOf(sale.packingProperty().get()));
            Label totalLabel = new Label(String.valueOf(sale.totalProperty().get()));
            anchorPane.getChildren().add(nameLabel);
            anchorPane.getChildren().add(phoneLabel);
            anchorPane.getChildren().add(dateLabel);
            anchorPane.getChildren().add(codeLabel);
            anchorPane.getChildren().add(qtyLabel);
            anchorPane.getChildren().add(priceLabel);
            anchorPane.getChildren().add(subTotalLabel);
            anchorPane.getChildren().add(printingLabel);
            anchorPane.getChildren().add(packingLabel);
            anchorPane.getChildren().add(totalLabel);
            nameLabel.setLayoutX(279);
            nameLabel.setLayoutY(154);
            phoneLabel.setLayoutX(566);
            phoneLabel.setLayoutY(154);
            dateLabel.setLayoutX(720);
            dateLabel.setLayoutY(154);
            codeLabel.setLayoutX(230);
            codeLabel.setLayoutY(238);
            qtyLabel.setLayoutX(419);
            qtyLabel.setLayoutY(238);
            priceLabel.setLayoutX(506);
            priceLabel.setLayoutY(238);
            subTotalLabel.setLayoutX(661);
            subTotalLabel.setLayoutY(238);
            printingLabel.setLayoutX(661);
            printingLabel.setLayoutY(338);
            packingLabel.setLayoutX(661);
            packingLabel.setLayoutY(370);
            totalLabel.setLayoutX(661);
            totalLabel.setLayoutY(432);
            PrinterJob job = PrinterJob.createPrinterJob();
            job.printPage(anchorPane);
        }
    }
    
    @FXML
    private void onPrint(ActionEvent event) {
        PrinterJob job = PrinterJob.createPrinterJob();
        boolean print = job.printPage(salesTable);
    }
    
    @FXML
    private void onDateChange(ActionEvent event) throws SQLException, ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DatePicker dp = (DatePicker) event.getSource();
        LocalDate date = dp.getValue();
        ObservableList<Sale> sales = dbaccess.readSales(date.format(dtf));
        salesTable.setItems(sales);
    }
    
    public TableView<Sale> getSalesTable() {
        return salesTable;
    }
    
    @FXML
    private void onDelete(ActionEvent event) {
        Sale sale = salesTable.getSelectionModel().getSelectedItem();
        int index = salesTable.getSelectionModel().getSelectedIndex();
        if(sale != null) {
            int qty = sale.qtyProperty().get();
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    try {
                        dbaccess.deleteSale(sale.idProperty().get(), sale.codeProperty().get(), sale.qtyProperty().get());
                        for(Card card:src.getStockTable().getItems()) {
                            if(card.codeProperty().get() == sale.codeProperty().get()) {
                                card.qtyProperty().set(card.qtyProperty().get() + qty);
                            }
                        }
                        salesTable.getItems().remove(index);
                    } catch (SQLException ex) {
                        Logger.getLogger(DailySaleReportController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            };
            loginController.setAction(runnable);
            loginController.showLoginPrompt();
        }
    }

    public void setSRC(StockReportController src) {
        this.src = src;
    }
    
}
