/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardsapp;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author bluroe
 */
public class Card {

    private final SimpleIntegerProperty code = new SimpleIntegerProperty();
    private final SimpleIntegerProperty qty = new SimpleIntegerProperty();
    private final SimpleDoubleProperty retailPrice = new SimpleDoubleProperty();
    private final SimpleDoubleProperty actualPrice = new SimpleDoubleProperty();
    private final SimpleStringProperty company = new SimpleStringProperty();

    Card(int code, int qty, double actualPrice, double retailPrice, String company) {
        this.code.set(code);
        this.qty.set(qty);
        this.actualPrice.set(actualPrice);
        this.retailPrice.set(retailPrice);
        this.company.set(company);
    }
    
    public SimpleIntegerProperty codeProperty() {
        return code;
    }
    
    public SimpleIntegerProperty qtyProperty() {
        return qty;
    }
    
    public SimpleDoubleProperty actualPriceProperty() {
        return actualPrice;
    }
    
    public SimpleDoubleProperty retailPriceProperty() {
        return retailPrice;
    }
    
    public SimpleStringProperty companyProperty() {
        return company;
    }
    
}
