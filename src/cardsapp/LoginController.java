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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author bluroe
 */
public class LoginController implements Initializable {
    
    @FXML private TextField userFld;
    @FXML private PasswordField passFld;
    
    private DBAccess dbaccess;
    
    private Runnable runnable;
    private Stage stage;
    
    public void setAction(Runnable runnable) {
        this.runnable = runnable;
    }

    @FXML
    private void onLogin(ActionEvent event) throws IOException, SQLException {
        login();
    }
    
    public void login() throws SQLException {
        String username = userFld.getText();
        String password = passFld.getText();
        if(dbaccess.login(username, password)) {
            stage.close();
            Platform.runLater(runnable);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong username or password", ButtonType.OK);
            alert.initOwner(stage);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.showAndWait();
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbaccess = DBAccess.getInstance();
        EventHandler handler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    try {
                        login();
                    } catch (SQLException ex) {
                        Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        userFld.setOnKeyPressed(handler);
        passFld.setOnKeyPressed(handler);
    }

    public void showLoginPrompt() {
        userFld.setText("");
        passFld.setText("");
        userFld.requestFocus();
        this.stage.centerOnScreen();
        this.stage.show();
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public Stage getStage() {
        return stage;
    }

    public void setOwnerStage(Stage stage) {
        this.stage.initOwner(stage);
    }
    
}
