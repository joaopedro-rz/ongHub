package com.onghub.api.util;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.onghub.api.entity.Donation;

import java.io.ByteArrayOutputStream;
import java.util.List;

public final class TransparencyPdfWriter {

    private TransparencyPdfWriter() {}

    public static byte[] build(String ngoName, List<Donation> donations) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document();
        PdfWriter.getInstance(doc, baos);
        doc.open();
        doc.add(new Paragraph("Relatorio de transparencia"));
        doc.add(new Paragraph("ONG: " + ngoName));
        doc.add(new Paragraph("Total registros: " + donations.size()));
        doc.add(new Paragraph(" "));
        for (Donation d : donations) {
            String line = "#" + d.getId()
                + " | " + d.getDonationType()
                + " | " + d.getStatus()
                + " | campanha: " + d.getCampaign().getTitle()
                + " | valor: " + (d.getAmount() != null ? d.getAmount().toPlainString() : "-")
                + " | material: " + (d.getMaterialDescription() != null ? d.getMaterialDescription() : "-");
            doc.add(new Paragraph(line));
        }
        doc.close();
        return baos.toByteArray();
    }
}
