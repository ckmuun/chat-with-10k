package de.koware.cfs.chatwith10k.edgar;

public record CompanyFilingDto(
        String cik,
        String name,
        String accessionNumber,
        String filingDate,
        String reportDate,
        String acceptanceDateTime,
        String act,
        String form,
        String fileNumber,
        String filmNumber,
        String items,
        String coreType,
        String size,
        boolean isXbrl,
        boolean isInlineXbrl,
        String primaryDocument,
        String primaryDocDescription
) {
}
