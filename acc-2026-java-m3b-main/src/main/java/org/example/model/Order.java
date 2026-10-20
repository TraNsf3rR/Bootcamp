package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String customerName;
    private final List<OrderItem> items;
    private OrderStatus status;
    private Discount discount = new NoDiscount();

    public Order(Builder builder) {
        this.customerName = builder.customerName;
        this.items = builder.items;
        this.status = OrderStatus.NEW;
    }

    public void addItem(OrderItem item){
//          prevented adding items if order is already paid
        if (isPaid()) {
            throw new IllegalStateException("Cannot add items to a paid order.");
        }
        if (item == null) {
            throw new IllegalArgumentException("Order item cannot be null.");
        }
        items.add(item);
    }

    public double calculateTotal(){
//          Initialize total
        double subtotal = 0;

//          Add up price of order
        for (OrderItem item : items) {
            subtotal += item.calculateTotal();
        }
//          Return total deducing discount
        return discount.apply(subtotal);
    }

    public void markAsPaid(){
//          Validation of empty order
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot mark an empty order as paid.");
        }
        this.status = OrderStatus.PAID;
    }

    public void applyDiscount(Discount discount){
        this.discount = discount;
    }

    public boolean isPaid(){
        return this.status == OrderStatus.PAID;
    }

    public List<OrderItem> getItems() {
        return items;
    }
    public String getCustomerName() {
        return customerName;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public static Builder builder(){
        return new Builder();
    }
    public static class Builder{
        private String customerName;
        private List<OrderItem> items = new ArrayList<>();
        public Builder customerName(String customerName){
            this.customerName = customerName;
            return this;
        }
        public Builder addItem(OrderItem item){
            this.items.add(item);
            return this;
        }
        public Order build(){
//              Check for valid customer name and initialize order.
            if (customerName == null || customerName.isBlank()) {
                throw new IllegalArgumentException("Customer name must not be blank.");
            }
            return new Order(this);
        }
    }
}
