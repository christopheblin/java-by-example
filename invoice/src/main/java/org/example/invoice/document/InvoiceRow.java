package org.example.invoice.document;

import java.math.BigDecimal;

public class InvoiceRow {
	private String productNumber;
	private String productDescription;
	private Double quantity;
	private BigDecimal price;

	public InvoiceRow(String productNumber, String productDescription, Double quantity, BigDecimal price) {
		this.productNumber = productNumber;
		this.productDescription = productDescription;
		this.quantity = quantity;
		this.price = price;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public Double getQuantity() {
		return quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getTotal() {
    	return this.price.multiply(new BigDecimal(quantity)); 
    }
}