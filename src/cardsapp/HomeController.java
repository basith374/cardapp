/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardsapp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.event.HyperlinkEvent;

/**
 * FXML Controller class
 *
 * @author bluroe
 */
public class HomeController implements Initializable {

    @FXML private MenuBar menuBar;
    
    @FXML private TabPane tabPane;
    
    private Stage saleDialog;
    
    private DBAccess dbaccess;
    
    private StockReportController src;
    private DailySaleReportController dsrc;
    private NewSaleController nsc;
    private LoginController loginController;
    private Stage stage;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        dbaccess = DBAccess.getInstance();
        FXMLLoader stockLoader = new FXMLLoader(getClass().getResource("StockReport.fxml"));
        FXMLLoader saleLoader = new FXMLLoader(getClass().getResource("DailySaleReport.fxml"));
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        try {
            Parent stockRoot = stockLoader.load();
            Tab stockTab = tabPane.getTabs().get(0);
            stockTab.setContent(stockRoot);
            src = stockLoader.getController();
            
            Parent loginRoot = loginLoader.load();
            loginController = loginLoader.getController();
            Stage dialog = new Stage();
            Scene scene = new Scene(loginRoot);
            dialog.setScene(scene);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setOpacity(.9);
            dialog.setTitle("Login");
            loginController.setStage(dialog);
            src.setLoginController(loginController);
            
            Parent saleRoot = saleLoader.load();
            Tab saleTab = tabPane.getTabs().get(1);
            saleTab.setContent(saleRoot);
            dsrc = saleLoader.getController();
            dsrc.setLoginController(loginController);
            dsrc.setSRC(src);
            
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void newStock(ActionEvent event) throws IOException {
        Stage loginDialog = loginController.getStage();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("NewStock.fxml"));
                    Parent root = loader.load();
                    NewStockController nsc = loader.getController();
                    nsc.setSRC(src);
                    Scene newStockScene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(newStockScene);
                    stage.setTitle("New Stock");
                    stage.initOwner(loginDialog);
                    stage.centerOnScreen();
                    stage.showAndWait();
                } catch (IOException ex) {
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        };
        loginController.setAction(runnable);
        loginController.showLoginPrompt();
    }
    
    @FXML
    private void newSale(ActionEvent event) throws IOException {
        Button btn = (Button) event.getSource();
        Stage stage = (Stage) btn.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("NewSale.fxml"));
        Parent root = loader.load();
        nsc = loader.getController();
        nsc.setDSRC(dsrc);
        nsc.setSRC(src);
        Scene scene = new Scene(root);
        if(saleDialog == null) {
            saleDialog = createDialog(stage, true, "New Sale");
        }
//        Card card = src.getStockTable().getSelectionModel().getSelectedItem();
//        if(card != null) {
//            nsc.setCardCode(card.codeProperty().get());
//        }
        saleDialog.setScene(scene);
        saleDialog.centerOnScreen();
        saleDialog.showAndWait();
    }
    
    public Stage createDialog(Stage owner, boolean modal, String title) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        Modality m = modal ? Modality.WINDOW_MODAL : Modality.NONE;
        stage.initModality(m);
        stage.setOpacity(.9);
        stage.setTitle(title);
        return stage;
    }
    
    public void onExit(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    private void changePass(ActionEvent event) throws IOException {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("ChangePassword.fxml"));
        Scene scene = new Scene(root);
        Stage dialog = createDialog(stage, true, "Change Password");
        dialog.setScene(scene);
        dialog.centerOnScreen();
        dialog.show();
    }

    @FXML
    private void showAbout(ActionEvent event) throws IOException {
        Stage owner = (Stage) menuBar.getScene().getWindow();
        GridPane root = FXMLLoader.load(getClass().getResource("About.fxml"));
        ImageView imageview = (ImageView) root.getChildren().get(1);
//        File file = new File("logo.ico");
//        System.out.println(file.exists());
//        new URL("logo.ico").openStream();
        Image image = new Image("file:logo.png");
        imageview.setImage(image);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("About");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
        loginController.setOwnerStage(stage);
    }
    
}
