package org.example.invoice.json;

import org.example.invoice.document.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonInvoice {
    public static Invoice parse(JSONObject doc) {
        //TODO: replace all this parsing logic by GSON
        Header header = parseHeader(getJsonObjectFromDocument(doc, "invoiceHeader"));
        Address billTo = parseAddress(getJsonObjectFromDocument(doc, "billTo"));
        Address shipTo = null;
        if(doc.containsKey("shipTo")) {
            JSONObject shipToObj = (JSONObject)doc.get("shipTo");
            if(shipToObj.containsKey("sameAsBilling") && ((Boolean)shipToObj.get("sameAsBilling")) == true) {
                shipTo = billTo;
            } else {
                shipTo = parseAddress(shipToObj);
            }
        }

        ShippingData shipData = parseShippingData(getJsonObjectFromDocument(doc, "shippingData"));

        List<InvoiceRow> rows = new ArrayList<InvoiceRow>();
        if(doc.containsKey("invoiceRows")) {
            Object simpleInvoiceRowsObject = doc.get("invoiceRows");
            if(simpleInvoiceRowsObject instanceof JSONArray) {
                for(Object simpleInvRowObj : ((JSONArray)simpleInvoiceRowsObject)) {
                    if(simpleInvRowObj instanceof JSONObject) {
                        rows.add(parseInvoiceRow((JSONObject)simpleInvRowObj));
                    }
                }
            }
        }

        BigDecimal vat = new BigDecimal(0.2f);
        if(doc.containsKey("vat")) {
            vat = BigDecimal.valueOf(Double.valueOf((String)doc.get("vat")));
        }

        String notes = null;
        if(doc.containsKey("notes")) {
            notes = (String)doc.get("notes");
        }

        Footer footer = null;
        if(doc.containsKey("footer")) {
            JSONObject jsonFooter = (JSONObject) doc.get("footer");
            footer = new Footer((String)jsonFooter.get("line1"), (String)jsonFooter.get("line2"));
        }

        return new Invoice(header, billTo, shipTo, shipData, rows, vat, notes, footer);
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static Date parseDate(String d) {
        try {
            return sdf.parse(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Header parseHeader(JSONObject jsonHeader) {
        Date invoiceDate = null;
        String invoiceNumber = null;
        if(jsonHeader.containsKey("invoiceDate")) {
            invoiceDate = parseDate((String) jsonHeader.get("invoiceDate"));
        }
        if(jsonHeader.containsKey("invoiceNumber")) {
            invoiceNumber = (String)jsonHeader.get("invoiceNumber");
        }
        return new Header(invoiceDate, invoiceNumber);
    }

    private static JSONObject getJsonObjectFromDocument(JSONObject doc, String key) {
        if(doc.containsKey(key)) {
            Object simpleObject = doc.get(key);
            if(simpleObject instanceof JSONObject) {
                return (JSONObject)simpleObject;
            }
        }
        return null;
    }

    private static Address parseAddress(JSONObject jsonAddress) {
        String title = null;
        String first = null;
        String last = null;
        if(jsonAddress.containsKey("name")) {
            if(jsonAddress.get("name") instanceof JSONObject) {
                JSONObject jsonName = (JSONObject) jsonAddress.get("name");
                if(jsonName.containsKey("title")) {
                    title = (String)jsonName.get("title");
                }
                if(jsonName.containsKey("first")) {
                    first = (String)jsonName.get("first");
                }
                if(jsonName.containsKey("last")) {
                    last = (String)jsonName.get("last");
                }
            }
        }
        String address1 = null;
        if(jsonAddress.containsKey("address1")) {
            address1 = (String)jsonAddress.get("address1");
        }
        String address2 = null;
        if(jsonAddress.containsKey("address2")) {
            address2 = (String)jsonAddress.get("address2");
        }
        String address3 = null;
        if(jsonAddress.containsKey("address3")) {
            address3 = (String)jsonAddress.get("address3");
        }
        String city = null;
        if(jsonAddress.containsKey("city")) {
            city = (String)jsonAddress.get("city");
        }
        String state = null;
        if(jsonAddress.containsKey("state")) {
            state = (String)jsonAddress.get("state");
        }
        String zipCode = null;
        if(jsonAddress.containsKey("zipCode")) {
            zipCode = (String)jsonAddress.get("zipCode");
        }
        String country = null;
        if(jsonAddress.containsKey("country")) {
            country = (String)jsonAddress.get("country");
        }
        return new Address(title + " " + first + " " + last, address1, address2, address3, city, state, zipCode, country);
    }

    private static ShippingData parseShippingData(JSONObject jsonShippingData) {
        String shipNumber = null;
        if(jsonShippingData.containsKey("shipNumber")) {
            shipNumber = (String)jsonShippingData.get("shipNumber");
        }
        String salesRep = null;
        if(jsonShippingData.containsKey("salesRep")) {
            salesRep = (String)jsonShippingData.get("salesRep");
        }
        Date shipDate = null;
        if(jsonShippingData.containsKey("shipDate")) {
            shipDate = parseDate((String)jsonShippingData.get("shipDate"));
        }
        String shipVia = null;
        if(jsonShippingData.containsKey("shipVia")) {
            shipVia = (String)jsonShippingData.get("shipVia");
        }
        String terms = null;
        if(jsonShippingData.containsKey("terms")) {
            terms = (String)jsonShippingData.get("terms");
        }
        Date dueDate = null;
        if(jsonShippingData.containsKey("dueDate")) {
            dueDate = parseDate((String)jsonShippingData.get("dueDate"));
        }
        return new ShippingData(shipNumber, salesRep, shipDate, shipVia, terms, dueDate);
    }

    private static InvoiceRow parseInvoiceRow(JSONObject jsonInvoiceRow) {
        String productNumber = null;
        if(jsonInvoiceRow.containsKey("productId")) {
            productNumber = (String)jsonInvoiceRow.get("productId");
        }
        String description = null;
        if(jsonInvoiceRow.containsKey("description")) {
            description = (String)jsonInvoiceRow.get("description");
        }
        Double quantity = null;
        if(jsonInvoiceRow.containsKey("quantity")) {
            quantity = Double.valueOf((String)jsonInvoiceRow.get("quantity"));
        }
        BigDecimal unitPrice = null;
        if(jsonInvoiceRow.containsKey("unitPrice")) {
            unitPrice = BigDecimal.valueOf(Double.valueOf((String) jsonInvoiceRow.get("unitPrice")));
        }
        return new InvoiceRow(productNumber, description, quantity, unitPrice);
    }
}
