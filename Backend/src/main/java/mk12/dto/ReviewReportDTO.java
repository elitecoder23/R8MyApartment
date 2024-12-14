package mk12.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import mk12.model.ReviewReport.ReportStatus;

@Data
public class ReviewReportDTO {
    private Long id;
    private int reviewId;
    private String reviewContent;
    private String reporterUsername;
    private String reason;
    private ReportStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime reportDate;

    private String apartmentName;
    private String ownerEmail;
}