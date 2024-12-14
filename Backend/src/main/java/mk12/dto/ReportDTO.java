package mk12.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import mk12.model.Report.ReportStatus;

@Data
public class ReportDTO {
    private Long id;
    private String reporterUsername;
    private String reason;
    private String reporterEmail;
    private String apartmentName;
    private ReportStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private String reportDate;
}