package org.example.invoice.document;

import java.math.BigDecimal;
import java.util.*;

public class Invoice {
	private Header header;
	private Address shipTo;
	private Address billTo;
	private List<InvoiceRow> rows;
	private ShippingData shipData;
	private BigDecimal vat;
	private String notes;
	private Footer footer;

	public Invoice(Header header, Address billTo, Address shipTo, ShippingData shipData, List<InvoiceRow> rows, BigDecimal vat, String notes, Footer footer) {
		this.header = header;
		this.shipTo = shipTo;
		this.billTo = billTo;
		this.rows = rows;
		this.shipData = shipData;
        this.vat = vat;
        this.notes = notes;
        this.footer = footer;
    }

    public Header getHeader() {
        return header;
    }

    public Address getShipTo() {
        return shipTo;
    }

    public Address getBillTo() {
        return billTo;
    }

    public List<InvoiceRow> getRows() {
        return rows;
    }

    public ShippingData getShipData() {
        return shipData;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public String getNotes() {
        return notes;
    }


    public Footer getFooter() {
        return footer;
    }
}