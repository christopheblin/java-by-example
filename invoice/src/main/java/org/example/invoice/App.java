package org.example.invoice;

import org.example.invoice.json.JsonInvoice;
import org.example.invoice.pdf.PdfInvoice;
import org.example.invoice.pdf.PdfInvoiceOptions;
import org.json.simple.JSONValue;
import org.json.simple.JSONObject;

import java.awt.image.BufferedImage;
import java.io.*;

import org.example.invoice.document.*;

import javax.imageio.ImageIO;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class App 
{
    public static void main( String[] args )
    {
        List<String> files = Arrays.asList("single.json", "afew.json", "lots.json");
        for (String file : files) {
            Invoice invoice = JsonInvoice.parse(readJsonFile(file));
            byte[] bytes = PdfInvoice.generatePdf(invoice, options);

            try {
                Files.write(Paths.get(file + ".pdf"), bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.write(Paths.get("manual.pdf"), PdfInvoice.generatePdf(manualInvoice(), options));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Invoice manualInvoice() {
        return new Invoice(
                new Header(new Date(), "201806-000001"),
                new Address("Groupe W", "12 Avenue du Président Roosevelt", "", "", "Paris", "", "75000", "FRANCE"),
                null,
                null,
                Arrays.asList(
                    new InvoiceRow("PART_ELI", "Abonnement EXCELENCE jusqu'au 23/06/2019", 1.0d, new BigDecimal(6000))
                ),
                new BigDecimal(0.2),
                "Payé le 23/06/18",
                new Footer("ACME CORP S.A.S au capital de 5 000 000 Euros", "SIRET 01234567891234 - APE 7777A"));
    }

    private static JSONObject readJsonFile(String pathname) {
        JSONObject jsonDocument;
        try {
            FileReader f = new FileReader(new File(pathname));
            jsonDocument = (JSONObject)JSONValue.parse(f);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return jsonDocument;
    }

    private static BufferedImage getLogoPng() {
        try {
            return ImageIO.read(PdfInvoice.class.getResource("/logo.png").openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static PdfInvoiceOptions options = new PdfInvoiceOptions(
            Locale.FRANCE,
            new PdfInvoiceOptions.PdfInvoiceOptionsCompany(
                    getLogoPng(),
                    "ACME CORP.",
                    "3 av du Général de Gaulle",
                    "26000 Valence",
                    "FRANCE"
            ),
            new PdfInvoiceOptions.PdfInvoiceOptionsLanguage(
                    "FACTURE",
                    "Sous-total HT",
                    "TVA (20%)",
                    "Total TTC",
                    "N° article",
                    "Article",
                    "Quantité",
                    "Prix pce HT",
                    "Prix HT",
                    "Date",
                    "N° facture",
                    "Adresse de livraison",
                    "Adresse de facturation",
                    "N° expédition",
                    "Vendeur",
                    "Expédition",
                    "Livreur",
                    "Note",
                    "Livraison",
                    "Notes")
    );
}
