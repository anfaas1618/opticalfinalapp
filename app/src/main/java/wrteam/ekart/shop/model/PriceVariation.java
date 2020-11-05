package wrteam.ekart.shop.model;

import java.io.Serializable;

public class PriceVariation implements Serializable {
    private final String id;
    private final String product_id;
    private final String type;
    private final String measurement;
    private final String measurement_unit_id;
    private final String price;
    private final String discounted_price;
    private final String serve_for;
    private final String stock;
    private final String stock_unit_id;
    private final String measurement_unit_name;
    private final String stock_unit_name;
    private final String discountpercent;
    private String cart_count;
    private String productPrice;
    private int qty;
    private double totalprice;

    public PriceVariation(String cart_count, String id, String product_id, String type, String measurement, String measurement_unit_id, String productPrice, String price, String discounted_price, String serve_for, String stock, String stock_unit_id, String measurement_unit_name, String stock_unit_name, String discountpercent) {
        this.cart_count = cart_count;
        this.id = id;
        this.product_id = product_id;
        this.type = type;
        this.measurement = measurement;
        this.measurement_unit_id = measurement_unit_id;
        this.price = price;
        this.productPrice = productPrice;
        this.discounted_price = discounted_price;
        this.serve_for = serve_for;
        this.stock = stock;
        this.stock_unit_id = stock_unit_id;
        this.measurement_unit_name = measurement_unit_name;
        this.stock_unit_name = stock_unit_name;
        this.discountpercent = discountpercent.replace("-", "").replace(".00", "");
    }

    public String getCart_count() {
        return cart_count;
    }

    public void setCart_count(String cart_count) {
        this.cart_count = cart_count;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getDiscountpercent() {
        return discountpercent;
    }

    public String getId() {
        return id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getType() {
        return type;
    }

    public String getMeasurement() {
        return measurement;
    }

    public String getMeasurement_unit_id() {
        return measurement_unit_id;
    }

    public String getPrice() {
        return price;
    }

    public String getDiscounted_price() {
        return discounted_price;
    }

    public String getServe_for() {
        return serve_for;
    }

    public String getStock() {
        return stock;
    }

    public String getStock_unit_id() {
        return stock_unit_id;
    }

    public String getMeasurement_unit_name() {
        return measurement_unit_name;
    }

    public String getStock_unit_name() {
        return stock_unit_name;
    }
}
