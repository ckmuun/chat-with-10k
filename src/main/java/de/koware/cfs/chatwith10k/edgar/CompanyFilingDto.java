package de.koware.cfs.chatwith10k.edgar;

import java.io.InputStream;

public record CompanyFilingDto(
        CompanyFilingMetadataDto metadata,
        InputStream file
) {
}
