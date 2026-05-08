package com.onghub.api.util;

import com.onghub.api.entity.Donation;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class TransparencyCsvWriter {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private TransparencyCsvWriter() {}

    public static byte[] toBytes(List<Donation> donations) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,status,campaignTitle,donorEmail,amount,paymentMethod,materialDescription,quantity,createdAt\n");
        for (Donation d : donations) {
            sb.append(d.getId()).append(',')
                .append(d.getDonationType()).append(',')
                .append(d.getStatus()).append(',')
                .append(csvEscape(d.getCampaign().getTitle())).append(',')
                .append(csvEscape(d.getDonor().getEmail())).append(',')
                .append(d.getAmount() != null ? d.getAmount().toPlainString() : "").append(',')
                .append(csvEscape(d.getPaymentMethod())).append(',')
                .append(csvEscape(d.getMaterialDescription())).append(',')
                .append(d.getQuantity() != null ? d.getQuantity().toString() : "").append(',')
                .append(d.getCreatedAt() != null ? FMT.format(d.getCreatedAt()) : "").append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String csvEscape(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        boolean needsQuotes = raw.contains(",") || raw.contains("\"") || raw.contains("\n") || raw.contains("\r");
        String escaped = raw.replace("\"", "\"\"");
        return needsQuotes ? "\"" + escaped + "\"" : escaped;
    }
}
