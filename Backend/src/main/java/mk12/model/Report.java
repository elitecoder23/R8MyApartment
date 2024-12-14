package mk12.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "listing_reports")
@Data
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reported_listing", nullable = false)
    private Apartment apartment;

    @Column(nullable = false)
    private String reporterUsername;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Column(nullable = false)
    private String reporterEmail;

    private LocalDateTime reportDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING;

    public void setOwnerResponse(String response) {

    }

    public void setReporterEmail(String reporterEmail) {
    }

    public enum ReportStatus {
        PENDING,
        REVIEWED,
        RESOLVED,
        DISMISSED
    }
}