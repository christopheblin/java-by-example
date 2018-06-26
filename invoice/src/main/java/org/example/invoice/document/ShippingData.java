package org.example.invoice.document;

import java.util.Date;

public class ShippingData {
	private String shipNumber;
	private String salesRep;
	private Date shipDate;
	private String shipVia;
	private String terms;
	private Date dueDate;

    public ShippingData(String shipNumber, String salesRep, Date shipDate, String shipVia, String terms, Date dueDate) {
        this.shipNumber = shipNumber;
        this.salesRep = salesRep;
        this.shipDate = shipDate;
        this.shipVia = shipVia;
        this.terms = terms;
        this.dueDate = dueDate;
    }

    public String getShipNumber() {
        return shipNumber;
    }

    public String getSalesRep() {
        return salesRep;
    }

    public Date getShipDate() {
        return shipDate;
    }

    public String getShipVia() {
        return shipVia;
    }

    public String getTerms() {
        return terms;
    }

    public Date getDueDate() {
        return dueDate;
    }
}