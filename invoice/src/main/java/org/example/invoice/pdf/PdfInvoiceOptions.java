package org.example.invoice.pdf;

import java.awt.image.BufferedImage;
import java.util.Locale;

public class PdfInvoiceOptions {
    private Locale locale;
    private PdfInvoiceOptionsCompany company;
    private PdfInvoiceOptionsLanguage language;

    public PdfInvoiceOptions(Locale locale, PdfInvoiceOptionsCompany company, PdfInvoiceOptionsLanguage language) {
        this.locale = locale;
        this.company = company;
        this.language = language;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public PdfInvoiceOptionsCompany getCompany() {
        return company;
    }

    public PdfInvoiceOptionsLanguage getLanguage() {
        return language;
    }

    public static class PdfInvoiceOptionsCompany {
        private BufferedImage logoPng;
        private String name;
        private String address1;
        private String address2;
        private String address3;

        public PdfInvoiceOptionsCompany(BufferedImage logoPng, String name, String address1, String address2, String address3) {
            this.logoPng = logoPng;
            this.name = name;
            this.address1 = address1;
            this.address2 = address2;
            this.address3 = address3;
        }

        public BufferedImage getLogoPng() {
            return this.logoPng;
        }

        public String getName() {
            return this.name;
        }

        public String getAddress1() {
            return address1;
        }

        public String getAddress2() {
            return address2;
        }

        public String getAddress3() {
            return address3;
        }
    }

    public static class PdfInvoiceOptionsLanguage {
        private String topRightTitle;
        private String subTotal;
        private String vat;
        private String total;
        private String itemProductNo;
        private String itemDescription;
        private String itemQuantity;
        private String itemUnitPrice;
        private String itemTotal;
        private String invoiceDate;
        private String invoiceNumber;
        private String shipTo;
        private String billTo;
        private String shipNumber;
        private String shipSalesRep;
        private String shipDate;
        private String shipVia;
        private String shipTerms;
        private String shipDueDate;
        private String notes;

        public PdfInvoiceOptionsLanguage(String topRightTitle, String subTotal, String vat, String total, String itemProductNo, String itemDescription, String itemQuantity, String itemUnitPrice, String itemTotal, String invoiceDate, String invoiceNumber, String shipTo, String billTo, String shipNumber, String shipSalesRep, String shipDate, String shipVia, String shipTerms, String shipDueDate, String notes) {
            this.topRightTitle = topRightTitle;
            this.subTotal = subTotal;
            this.vat = vat;
            this.total = total;
            this.itemProductNo = itemProductNo;
            this.itemDescription = itemDescription;
            this.itemQuantity = itemQuantity;
            this.itemUnitPrice = itemUnitPrice;
            this.itemTotal = itemTotal;
            this.invoiceDate = invoiceDate;
            this.invoiceNumber = invoiceNumber;
            this.shipTo = shipTo;
            this.billTo = billTo;
            this.shipNumber = shipNumber;
            this.shipSalesRep = shipSalesRep;
            this.shipDate = shipDate;
            this.shipVia = shipVia;
            this.shipTerms = shipTerms;
            this.shipDueDate = shipDueDate;
            this.notes = notes;
        }

        public String getTopRightTitle() {
            return topRightTitle;
        }

        public String getSubTotal() {
            return subTotal;
        }

        public String getVat() {
            return vat;
        }

        public String getTotal() {
            return total;
        }

        public String getItemProductNo() {
            return itemProductNo;
        }

        public String getItemDescription() {
            return itemDescription;
        }

        public String getItemQuantity() {
            return itemQuantity;
        }

        public String getItemUnitPrice() {
            return itemUnitPrice;
        }

        public String getItemTotal() {
            return itemTotal;
        }

        public String getInvoiceDate() {
            return invoiceDate;
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public String getShipTo() {
            return shipTo;
        }

        public String getBillTo() {
            return billTo;
        }

        public String getShipNumber() {
            return shipNumber;
        }

        public String getShipSalesRep() {
            return shipSalesRep;
        }

        public String getShipDate() {
            return shipDate;
        }

        public String getShipVia() {
            return shipVia;
        }

        public String getShipTerms() {
            return shipTerms;
        }

        public String getShipDueDate() {
            return shipDueDate;
        }

        public String getNotes() {
            return notes;
        }
    }
}
