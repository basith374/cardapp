package cardsapp;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bluroe
 */
public class DBAccess {
    
    private Connection con;
    
    private static DBAccess instance = new DBAccess();
    
    private DBAccess() {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:card.db");
            initTables();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static DBAccess getInstance() {
        return instance;
    }
    
    public void initTables() throws SQLException {
        Statement stmt = con.createStatement();
        try {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS cards(id integer primary key autoincrement, code integer, qty integer, retail_price double, actual_price double, company varchar(100));");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sales(id integer primary key autoincrement, code integer, qty integer, retail_price double, discount double, printing double, packing double, total double, customer_name varchar(50), customer_phone varchar(20), timestamp datetime default current_timestamp)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS admin(id integer primary key autoincrement, username varchar(50), password varchar(50));");
            stmt.executeUpdate("INSERT INTO admin (username, password) VALUES ('admin', 'admin')");
            createAdminAccount("admin", "admin");
        } catch (SQLException ex) {
            Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        stmt.close();
    }
    
    public void insertStock(int code, int qty, double actualPrice, double retailPrice, String company) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("INSERT INTO cards (code, qty, retail_price, actual_price, company) VALUES (" + code + ", " + qty + ", " + retailPrice + ", " + actualPrice + ", '" + company + "');");
        stmt.close();
    }
    
    public long insertSale(int code, int qty, double price, double discount, double printing, double packing, double total, String customerName, String customerPhone) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String sql = "INSERT INTO sales (code, qty, retail_price, discount,"
                + " printing, packing, total, customer_name, customer_phone,"
                + " timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, code);
        stmt.setInt(2, qty);
        stmt.setDouble(3, price);
        stmt.setDouble(4, discount);
        stmt.setDouble(5, printing);
        stmt.setDouble(6, packing);
        stmt.setDouble(7, total);
        stmt.setString(8, customerName);
        stmt.setString(9, customerPhone);
        stmt.setString(10, sdf.format(new Date()));
        stmt.execute();
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        stmt.close();
        generatedKeys.next();
        long id = generatedKeys.getLong(1);
        return id;
    }
    
    public void deleteAdminAccount(String username) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("DELETE FROM admin WHERE username='" + username + "';");
        stmt.close();
    }
    
    public boolean createAdminAccount(String username, String password) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet set = stmt.executeQuery("SELECT count(*) FROM admin WHERE username='" + username + "';");
        if(!set.next()) return false;
        stmt.executeUpdate("INSERT INTO admin (username, password) VALUES ('" + username + "','" + password + "');");
        stmt.close();
        return true;
    }
    
    public void changePassword(String username, String password) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("UPDATE admin SET password='" + password + "' WHERE username='" + username + "';");
        stmt.close();
    }
    
    public void deleteStock(int code) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("DELETE FROM cards WHERE code=" + code);
        stmt.close();
    }
    
    public void deleteSale(long id, int code, int qty) throws SQLException {
        Statement stmt = con.createStatement();
        int i = stmt.executeUpdate("DELETE FROM sales WHERE id=" + id + ";");
        stmt.close();
        stmt = con.createStatement();
        stmt.executeUpdate("UPDATE cards SET qty=qty+" + qty + " WHERE code=" + code);
        stmt.close();
    }
    
    public boolean login(String username, String password) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet set = stmt.executeQuery("SELECT * FROM admin WHERE username='" + username + "' AND password='" + password + "';");
        return set.next();
    }
    
    public ObservableList<Card> readStock() throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM cards;");
        ObservableList<Card> list = FXCollections.observableArrayList();
        while(rs.next()) {
            int code = rs.getInt("code");
            int qty = rs.getInt("qty");
            double retailPrice = rs.getDouble("retail_price");
            double actualPrice = rs.getDouble("actual_price");
            String company = rs.getString("company");
            list.add(new Card(code, qty, actualPrice, retailPrice, company));
        }
        stmt.close();
        return list;
    }
    
    public ObservableList<Sale> readSales(String date) throws SQLException, ParseException {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM sales;");
        ObservableList<Sale> list = FXCollections.observableArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        while(rs.next()) {
            long id = rs.getInt("id");
            int code = rs.getInt("code");
            int qty = rs.getInt("qty");
            double retailPrice = rs.getDouble("retail_price");
            double discount = rs.getDouble("discount");
            double printing = rs.getDouble("printing");
            double packing = rs.getDouble("packing");
            double total = rs.getDouble("total");
            String customerName = rs.getString("customer_name");
            String customerPhone = rs.getString("customer_phone");
            String someday = sdf.format(sdf.parse(rs.getString("timestamp")));
            boolean isToday = date.equals(someday);
            if(isToday) {
                Sale sale = new Sale(code, qty, retailPrice, discount, printing, packing, customerName, customerPhone, someday);
                sale.idProperty().set(id);
                list.add(sale);
            }
        }
        stmt.close();
        return list;
    }
    
    public void updateCardQty(int code, int qty) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("UPDATE cards SET qty=" + qty + " WHERE code=" + code + ";");
        stmt.close();
    }
    
    public void updateCard(int code, int qty, double actualPrice, double retailPrice, String company) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("UPDATE cards SET qty=" + qty + ", actual_price=" + actualPrice + ", retail_price=" + retailPrice + ", company='" + company + "' WHERE code=" + code + ";");
        stmt.close();
    }
    
    public void removeCard(int code) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("DELETE FROM cards WHERE code=" + code + ";");
        stmt.close();
    }
    
    public Card getCard(int code) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet set = stmt.executeQuery("SELECT * FROM cards WHERE code=" + code + ";");
        boolean found = set.next();
        if(!found) return null;
        int qty = set.getInt("qty");
        double actual = set.getDouble("actual_price");
        double retail = set.getDouble("retail_price");
        String company = set.getString("company");
        stmt.close();
        return new Card(code, qty, actual, retail, company);
    }
    
    public Connection getConnection() {
        return con;
    }

    public int stockExists(int code) throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet set = stmt.executeQuery("SELECT COUNT(*) FROM cards WHERE code=" + code);
        set.next();
        int count = set.getInt(1);
        return count;
    }
    
}
