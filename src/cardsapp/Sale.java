/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cardsapp;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author bluroe
 */
public class Sale {

    private SimpleLongProperty id = new SimpleLongProperty();
    private SimpleIntegerProperty code = new SimpleIntegerProperty();
    private SimpleIntegerProperty qty = new SimpleIntegerProperty();
    private SimpleDoubleProperty price = new SimpleDoubleProperty();
    private SimpleDoubleProperty printing = new SimpleDoubleProperty();
    private SimpleDoubleProperty packing = new SimpleDoubleProperty();
    private SimpleDoubleProperty discount = new SimpleDoubleProperty();
    private SimpleDoubleProperty total = new SimpleDoubleProperty();
    private SimpleStringProperty customerName = new SimpleStringProperty();
    private SimpleStringProperty customerPhone = new SimpleStringProperty();
    private SimpleStringProperty date = new SimpleStringProperty();
    
    public Sale(int code, int qty, double price, double discount, double printing, double packing, String customerName, String customerPhone, String date) {
        this.code.set(code);
        this.price.set(price);
        this.qty.set(qty);
        this.discount.set(discount * qty);
        this.printing.set(printing * qty);
        this.packing.set(packing * qty);
        this.total.set((price * qty) + (printing * qty) + (packing * qty) - (discount * qty));
        this.customerName.set(customerName);
        this.customerPhone.set(customerPhone);
        this.date.set(date);
    }
    
    public SimpleLongProperty idProperty() {
        return id;
    }
    
    public SimpleIntegerProperty codeProperty() {
        return code;
    }
    
    public SimpleIntegerProperty qtyProperty() {
        return qty;
    }
    
    public SimpleDoubleProperty priceProperty() {
        return price;
    }
    
    public SimpleDoubleProperty discountProperty() {
        return discount;
    }
    
    public SimpleDoubleProperty printingProperty() {
        return printing;
    }
    
    public SimpleDoubleProperty packingProperty() {
        return packing;
    }
    
    public SimpleDoubleProperty totalProperty() {
        return total;
    }
    
    public SimpleStringProperty customerNameProperty() {
        return customerName;
    }
    
    public SimpleStringProperty customerPhoneProperty() {
        return customerPhone;
    }
    
    public SimpleStringProperty dateProperty() {
        return date;
    }
    
}
