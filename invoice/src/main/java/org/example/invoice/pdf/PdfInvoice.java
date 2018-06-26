package org.example.invoice.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.example.invoice.document.*;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

public class PdfInvoice {

    public static byte[] generatePdf(Invoice invoice, PdfInvoiceOptions options) {
        return new PdfInvoice(invoice, options).generatePdf();
    }

    private Invoice invoice;
    private PdfInvoiceOptions options;
    private PDDocument pdfDocument;
    private PDPage pdfPage;
    private PDPageContentStream contents;
    private PDImageXObject logoImage;

    public PdfInvoice(Invoice invoice, PdfInvoiceOptions options) {
        this.invoice = invoice;
        this.options = options;
    }

    public byte[] generatePdf() {
        System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider"); //higher rendering speed

        try {
            pdfDocument = new PDDocument();
            pdfPage = new PDPage();
            pdfDocument.addPage(pdfPage);
            contents = new PDPageContentStream(pdfDocument, pdfPage);
            logoImage = LosslessFactory.createFromImage(pdfDocument, options.getCompany().getLogoPng());

            printInvoice();

            contents.close();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            pdfDocument.save(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (pdfDocument != null) pdfDocument.close();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    private void printInvoice() throws IOException {
        printHeader();
        printFooter();
        printAddress(invoice.getShipTo(), false);
        printAddress(invoice.getBillTo(), true);
        printShipData();


        /* MAGIC CONSTANTS EXPLANATION
		First page with summation 16 rows
		First page without summation 24 rows
		Next page with summation 23 rows
		Next page without summation 31 rows

		16-24 = 12
		24-31 = 18
        */
        int maxRowSize = 23;
        int maxPageWithSummation = 16;
        int breakPoint = 12;

        int rowY = 520;
        int numPrintedRows = 0;

        int rowsLeft = invoice.getRows().size();

        printRowHeader(rowY);
        printRowBackGround(rowY-21, rowsLeft < maxPageWithSummation ? maxPageWithSummation : maxRowSize);

        for (InvoiceRow invoiceRow : invoice.getRows()) {
            numPrintedRows++;
            rowY -= 20;
            printRow(invoiceRow, rowY);
            if(newPageRequired(numPrintedRows, rowsLeft, maxRowSize, maxPageWithSummation, breakPoint)) {
                rowsLeft -= numPrintedRows;
                numPrintedRows = 0;
                maxRowSize = 30;
                maxPageWithSummation = 23;
                breakPoint = 18;
                rowY = 660;
                int numRows = rowsLeft < maxPageWithSummation ? maxPageWithSummation : maxRowSize;
                contents = newPage(rowY, numRows);

            }
        }

        printSummary();
        printNotes();
    }

    private PDPageContentStream newPage(int rowY, int numRows) throws IOException {
        contents.close();
        PDPage pdfPage = new PDPage();
        pdfDocument.addPage(pdfPage);
        contents = new PDPageContentStream(pdfDocument, pdfPage);
        printHeader();
        printRowHeader(rowY);
        printRowBackGround(rowY-21, numRows);
        printFooter();
        return contents;
    }


    private static boolean newPageRequired(int numPrintedRows, int rowsLeft, int maxRowSize, int maxPageWithSummation, int breakPoint) {
        if(numPrintedRows >= maxRowSize) return true;
        if(maxPageWithSummation < rowsLeft && rowsLeft < maxRowSize) {
            return numPrintedRows >= breakPoint;
        }
        return false;
    }

    private void printSummary() throws IOException {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (InvoiceRow invoiceRow : invoice.getRows()) {
            subTotal = subTotal.add(invoiceRow.getTotal());
        }
        Color strokeColor = new Color(100, 100, 100);
        contents.setStrokingColor(strokeColor);
        Color fillColor = new Color(240, 240, 240);
        contents.setNonStrokingColor(fillColor);

        PdfInvoice.PDFPrinter summeryLabelPrinter = new PdfInvoice.PDFPrinter(contents, PDType1Font.HELVETICA_BOLD, 8);
        PdfInvoice.PDFPrinter summeryValuePrinter = new PdfInvoice.PDFPrinter(contents, PDType1Font.HELVETICA, 12);

        BigDecimal vatValue = subTotal.multiply(invoice.getVat());
        BigDecimal totalCost = subTotal.add(vatValue);
        subTotal = subTotal.setScale(2, RoundingMode.HALF_EVEN);
        vatValue = vatValue.setScale(2, RoundingMode.HALF_EVEN);
        totalCost = totalCost.setScale(2, RoundingMode.HALF_EVEN);

        int summeryStartY = 171;

        summeryLabelPrinter.putText(451, summeryStartY, options.getLanguage().getSubTotal());
        contents.addRect(450, summeryStartY-17, 120, 16);
        contents.stroke();
        summeryValuePrinter.putTextToTheRight(566, summeryStartY-13, formatPriceWithCurrency(subTotal));

        summeryLabelPrinter.putText(451, summeryStartY - 30, options.getLanguage().getVat());
        contents.addRect(450, summeryStartY - 30 - 17, 120, 16);
        contents.stroke();
        summeryValuePrinter.putTextToTheRight(566, summeryStartY - 30 - 13, formatPriceWithCurrency(vatValue));

        summeryLabelPrinter.putText(451, summeryStartY - 60, options.getLanguage().getTotal());
        contents.addRect(450, summeryStartY - 60 - 17, 120, 16);
        contents.stroke();
        summeryValuePrinter.putTextToTheRight(566, summeryStartY - 60 - 13, formatPriceWithCurrency(totalCost));
    }


    private void printRowBackGround(int rowY, int numRows) throws IOException {
        Color strokeColor = new Color(100, 100, 100);
        contents.setStrokingColor(strokeColor);
        Color fillColor = new Color(240, 240, 240);
        contents.setNonStrokingColor(fillColor);

        boolean odd = true;
        for(int i=0; i<numRows; i++) {
            if(odd) {
                contents.addRect(51, rowY, 518, 20);
                contents.fill();
            }

            contents.moveTo(50, rowY);
            contents.lineTo(50, rowY+20);
            contents.moveTo(570, rowY);
            contents.lineTo(570, rowY+20);
            contents.stroke();
            rowY -= 20;
            odd = !odd;
        }

        contents.moveTo(50, rowY+20);
        contents.lineTo(570, rowY+20);
        contents.stroke();
    }

    private void printRowHeader(int headerY) throws IOException {
        Color fillColor = new Color(230, 230, 230);
        Color strokeColor = new Color(100, 100, 100);
        contents.setStrokingColor(strokeColor);
        contents.setNonStrokingColor(fillColor);
        contents.addRect(50, headerY, 520, 20);
        contents.fillAndStroke();

        PDFont font = PDType1Font.HELVETICA;
        PdfInvoice.PDFPrinter headerPrinter = new PdfInvoice.PDFPrinter(contents, font, 12);
        headerPrinter.putText(60, headerY+7, options.getLanguage().getItemProductNo());
        headerPrinter.putText(120, headerY+7, options.getLanguage().getItemDescription());
        headerPrinter.putText(380, headerY+7, options.getLanguage().getItemQuantity());
        headerPrinter.putText(440, headerY+7, options.getLanguage().getItemUnitPrice());
        headerPrinter.putText(510, headerY+7, options.getLanguage().getItemTotal());
    }

    private void printNotes() throws IOException {
        String notes = invoice.getNotes();
        if (notes == null) return;
        Color strokeColor = new Color(100, 100, 100);
        contents.setStrokingColor(strokeColor);
        contents.addRect(50, 90, 370, 80);
        contents.stroke();

        PdfInvoice.PDFPrinter footerLabelPrinter = new PdfInvoice.PDFPrinter(contents, PDType1Font.HELVETICA_BOLD, 8);
        PdfInvoice.PDFPrinter footerValuePrinter = new PdfInvoice.PDFPrinter(contents, PDType1Font.HELVETICA, 8);
        footerLabelPrinter.putText(50, 172, options.getLanguage().getNotes());
        int rowY = 160;
        StringBuilder sb = new StringBuilder();
        for(String s : notes.split(" ")) {
            if(footerValuePrinter.widthOfText(sb.toString() + " " + s) > 365) {
                if(rowY < 50) {
                    sb.append("...");
                    footerValuePrinter.putText(55, rowY, sb.toString());
                    sb = new StringBuilder();
                    break;
                }
                footerValuePrinter.putText(55, rowY, sb.toString());
                rowY -= 10;
                sb = new StringBuilder();
            }
            sb.append(s);
            sb.append(" ");
        }
        footerValuePrinter.putText(55, rowY, sb.toString());
    }

    private void printHeader() throws IOException {
        Header header = invoice.getHeader();
        final float width = 60f;
        final float scale = width / logoImage.getWidth();
        contents.drawImage(logoImage, 50, 690, width, logoImage.getHeight()*scale);


        PDFont headerFont = PDType1Font.HELVETICA_BOLD;
        PdfInvoice.PDFPrinter headerPrinter = new PdfInvoice.PDFPrinter(contents, headerFont, 16);
        headerPrinter.putText(120, 740, options.getCompany().getName());

        PDFont font = PDType1Font.HELVETICA;
        PdfInvoice.PDFPrinter textPrinter = new PdfInvoice.PDFPrinter(contents, font, 10);
        textPrinter.putText(120, 720, options.getCompany().getAddress1());
        textPrinter.putText(120, 708, options.getCompany().getAddress2());
        textPrinter.putText(120, 696, options.getCompany().getAddress3());

        Color color = new Color(200, 200, 200);
        PdfInvoice.PDFPrinter invoiceHeaderPrinter = new PdfInvoice.PDFPrinter(contents, font, 24, color);
        invoiceHeaderPrinter.putText(450, 740, options.getLanguage().getTopRightTitle());

        textPrinter.putText(400, 710, options.getLanguage().getInvoiceDate());
        textPrinter.putText(400, 698, options.getLanguage().getInvoiceNumber());
        textPrinter.putText(500, 710, formatDate(header.getInvoiceDate()));
        textPrinter.putText(500, 698, header.getInvoiceNumber());
    }

    private void printAddress(Address address, boolean rightSide) throws IOException {
        if (address == null) return;;
        PDFont font = PDType1Font.HELVETICA;
        Color color = new Color(80, 80, 80);

        int x = rightSide ? 400 : 120;

        int y = 660;

        PdfInvoice.PDFPrinter headerPrinter = new PdfInvoice.PDFPrinter(contents, font, 10);
        headerPrinter.putText(x, y, rightSide ? options.getLanguage().getBillTo() : options.getLanguage().getShipTo());

        y -= 12;
        PdfInvoice.PDFPrinter addressPrinter = new PdfInvoice.PDFPrinter(contents, font, 10, color);
        addressPrinter.putText(x, y, address.getFullName());
        y -= 12;
        addressPrinter.putText(x, y, address.getAddress1());
        y -= 12;
        if (address.getAddress2() != null && address.getAddress2().length() > 0) {
            addressPrinter.putText(x, y, address.getAddress2());
            y -= 12;
        }
        if (address.getAddress3() != null && address.getAddress3().length() > 0) {
            addressPrinter.putText(x, y, address.getAddress3());
            y -= 12;
        }
        addressPrinter.putText(x, y, address.getZipCode() + " " + address.getCity());
        y -= 12;
        if (address.getState() != null && address.getState().length() > 0) {
            addressPrinter.putText(x, y, address.getState() + ", " + address.getCountry());
        } else {
            addressPrinter.putText(x, y, address.getCountry());
        }
    }

    private void printShipData() throws IOException {
        ShippingData shippingData = invoice.getShipData();
        if (shippingData == null) return;
        Color fillColor = new Color(230, 230, 230);
        Color strokeColor = new Color(100, 100, 100);
        contents.setStrokingColor(strokeColor);
        contents.setNonStrokingColor(fillColor);
        contents.addRect(50, 570, 520, 20);
        contents.fillAndStroke();
        contents.addRect(50, 550, 520, 20);
        contents.stroke();

        final int headerY = 577;
        PDFont font = PDType1Font.HELVETICA;
        PdfInvoice.PDFPrinter headerPrinter = new PdfInvoice.PDFPrinter(contents, font, 12);
        headerPrinter.putText(60, headerY, options.getLanguage().getShipNumber());
        headerPrinter.putText(160, headerY, options.getLanguage().getShipSalesRep());
        headerPrinter.putText(280, headerY, options.getLanguage().getShipDate());
        headerPrinter.putText(340, headerY, options.getLanguage().getShipVia());
        headerPrinter.putText(450, headerY, options.getLanguage().getShipTerms());
        headerPrinter.putText(510, headerY, options.getLanguage().getShipDueDate());

        final int textY = 557;
        PdfInvoice.PDFPrinter textPrinter = new PdfInvoice.PDFPrinter(contents, font, 8);
        textPrinter.putText(60, textY, shippingData.getShipNumber());
        textPrinter.putText(160, textY, shippingData.getSalesRep());
        textPrinter.putText(280, textY, formatDate(shippingData.getShipDate()));
        textPrinter.putText(340, textY, shippingData.getShipVia());
        textPrinter.putText(450, textY, shippingData.getTerms());
        textPrinter.putText(510, textY, formatDate(shippingData.getDueDate()));
    }

    private void printRow(InvoiceRow row, int rowY) throws IOException {
        Color strokeColor = new Color(100, 100, 100);
        contents.setStrokingColor(strokeColor);

        PDFont font = PDType1Font.HELVETICA;
        PdfInvoice.PDFPrinter textPrinter = new PdfInvoice.PDFPrinter(contents, font, 8);
        textPrinter.putText(60, rowY+7, row.getProductNumber());
        textPrinter.putText(120, rowY+7, row.getProductDescription());
        textPrinter.putTextToTheRight(420, rowY+7, formatDouble(row.getQuantity()));
        textPrinter.putTextToTheRight(490, rowY+7, formatPrice(row.getPrice()));
        textPrinter.putTextToTheRight(560, rowY+7, formatPrice(row.getTotal()));
    }

    private void printFooter() throws IOException {
        Footer footer = invoice.getFooter();
        if (footer == null) return;
        PDFont footerFont = PDType1Font.HELVETICA;
        PdfInvoice.PDFPrinter footerPrinter = new PdfInvoice.PDFPrinter(contents, footerFont, 12);
        //TODO: compute center instead of hardcoding
        footerPrinter.putText(180, 30, footer.getLine1());
        footerPrinter.putText(200, 15, footer.getLine2());
    }

    private String formatDate(Date d) {
        return d == null ? "" : DateFormat.getDateInstance(DateFormat.SHORT, options.getLocale()).format(d);
    }

    private String formatPrice(BigDecimal p) {
        return p == null ? "" : NumberFormat.getCurrencyInstance(options.getLocale()).format(p)
                .replace(NumberFormat.getCurrencyInstance(options.getLocale()).getCurrency().getSymbol(), "")
                .replace("\u00A0", " "); //avoid https://stackoverflow.com/questions/46470158
    }

    private String formatPriceWithCurrency(BigDecimal p) {
        return p == null ? "" : NumberFormat.getCurrencyInstance(options.getLocale()).format(p)
                .replace("\u00A0", " "); //avoid https://stackoverflow.com/questions/46470158
    }

    private String formatDouble(Double d) {
        return d == null ? "" : NumberFormat.getInstance(options.getLocale()).format(d);
    }

    public static class PDFPrinter {
        private PDPageContentStream contents;
        private PDFont font;
        private int fontSize;
        private Color color;

        public PDFPrinter(PDPageContentStream contents, PDFont font, int fontSize) {
            this(contents, font, fontSize, Color.BLACK);
        }

        public PDFPrinter(PDPageContentStream contents, PDFont font, int fontSize, Color color) {
            this.contents = contents;
            this.font = font;
            this.fontSize = fontSize;
            this.color = color;
        }

        public void putText(int x, int y, String text) throws IOException {
            contents.setNonStrokingColor(color);
            contents.beginText();
            contents.setFont(font, fontSize);
            contents.newLineAtOffset(x, y);
            contents.showText(text);
            contents.endText();
        }

        public int widthOfText(String text) throws IOException {
            return Math.round((font.getStringWidth(text) / 1000f) * this.fontSize);
        }

        public void putTextToTheRight(int x, int y, String text) throws IOException {
            this.putText(x - widthOfText(text), y, text);
        }
    }


}
