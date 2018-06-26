package org.example.invoice.document;

import java.util.Date;

public class Header {
    private Date invoiceDate;
	private String invoiceNumber;

    public Header(Date invoiceDate, String invoiceNumber) {
        this.invoiceDate = invoiceDate;
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }
}