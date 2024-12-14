package mk12.repository;

import mk12.model.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    List<ReviewReport> findByReviewId(int reviewId);
    List<ReviewReport> findByReporterUsername(String username);
    List<ReviewReport> findByStatus(ReviewReport.ReportStatus status);
    List<ReviewReport> findByReviewApartmentOwnerEmail(String ownerEmail);
}