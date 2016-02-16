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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author bluroe
 */
public class ChangePasswordController implements Initializable {

    @FXML TextField userFld;
    @FXML PasswordField oldPassFld;
    @FXML PasswordField newPassFld;
    @FXML PasswordField rePassFld;
    
    private DBAccess dbaccess;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        dbaccess = DBAccess.getInstance();
    }
    
    @FXML
    private void changePass(ActionEvent event) throws SQLException {
        Button button = (Button) event.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        String username = userFld.getText();
        String oldPass = oldPassFld.getText();
        String newPass = newPassFld.getText();
        String rePass = rePassFld.getText();
        
        Alert alert = new Alert(Alert.AlertType.ERROR, "Something is wrong...", ButtonType.OK);
        alert.initOwner(stage);
        alert.initModality(Modality.WINDOW_MODAL);
        
        if(username.equals("")) {
            alert.setContentText("Username cannot be blank");
            alert.showAndWait();
            return;
        } else if(oldPass.equals("")) {
            alert.setContentText("Old password cannot be blank");
            alert.showAndWait();
            return;
        } else if(newPass.equals("")) {
            alert.setContentText("New password cannot be blank");
            alert.showAndWait();
            return;
        } else if(!newPass.equals(rePass)) {
            alert.setContentText("Passwords do not match");
            alert.showAndWait();
            return;
        } else if(!dbaccess.login(username, oldPass)) {
            alert.setContentText("Wrong password");
            alert.showAndWait();
            return;
        }
        try {
            dbaccess.changePassword(username, newPass);
            stage.close();
        } catch (SQLException ex) {
            Logger.getLogger(ChangePasswordController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
