package com.onghub.api.util;

import com.onghub.api.entity.Donation;
import com.onghub.api.entity.DonationReceipt;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;

public final class DonationPdfWriter {

    private DonationPdfWriter() {}

    public static byte[] build(Donation donation, DonationReceipt receipt) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();
        document.add(new Paragraph("OngHub — Recibo de doacao"));
        document.add(new Paragraph("Numero: " + receipt.getReceiptNumber()));
        document.add(new Paragraph("Campanha: " + donation.getCampaign().getTitle()));
        document.add(new Paragraph("Tipo: " + donation.getDonationType()));
        if (donation.getAmount() != null) {
            document.add(new Paragraph("Valor: " + donation.getAmount()));
        }
        if (donation.getMaterialDescription() != null) {
            document.add(new Paragraph("Descricao: " + donation.getMaterialDescription()));
        }
        if (donation.getQuantity() != null) {
            document.add(new Paragraph("Quantidade: " + donation.getQuantity()));
        }
        document.add(new Paragraph("Emitido em: " + receipt.getIssuedAt()));
        document.close();
        return baos.toByteArray();
    }
}
