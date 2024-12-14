package mk12.service;

import mk12.dto.ReviewReportDTO;
import mk12.model.Review;
import mk12.model.ReviewReport;
import mk12.repository.IReviewRepository;
import mk12.repository.IReviewReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewReportService {
    private final IReviewReportRepository reportRepository;
    private final IReviewRepository reviewRepository;

    public ReviewReportService(IReviewReportRepository reportRepository,
                               IReviewRepository reviewRepository) {
        this.reportRepository = reportRepository;
        this.reviewRepository = reviewRepository;
    }

    public ReviewReportDTO submitReport(int reviewId, String reporterUsername, String reason) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        ReviewReport report = new ReviewReport();
        report.setReview(review);
        report.setReporterUsername(reporterUsername);
        report.setReason(reason);

        return convertToDTO(reportRepository.save(report));
    }

    public List<ReviewReportDTO> getReportsByOwner(String ownerEmail) {
        return reportRepository.findByReviewApartmentOwnerEmail(ownerEmail).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewReportDTO> getReportsByStatus(ReviewReport.ReportStatus status) {
        return reportRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReviewReportDTO updateReportStatus(Long reportId, ReviewReport.ReportStatus status) {
        ReviewReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus(status);
        return convertToDTO(reportRepository.save(report));
    }

    private ReviewReportDTO convertToDTO(ReviewReport report) {
        ReviewReportDTO dto = new ReviewReportDTO();
        dto.setId(report.getId());
        dto.setReviewId(report.getReview().getId());
        dto.setReviewContent(report.getReview().getReviewText());
        dto.setReporterUsername(report.getReporterUsername());
        dto.setReason(report.getReason());
        dto.setStatus(report.getStatus());
        dto.setReportDate(report.getReportDate()); // This will be formatted as ISO string
        dto.setApartmentName(report.getReview().getApartment().getName());
        dto.setOwnerEmail(report.getReview().getApartment().getOwner().getEmail());
        return dto;
    }
}