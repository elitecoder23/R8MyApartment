package mk12.repository;

import mk12.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IReportRepository extends JpaRepository<Report, Long> {
    // Find reports by apartment owner email
    List<Report> findByApartmentOwnerEmail(String ownerEmail);

    // Find reports by apartment name
    List<Report> findByApartmentName(String apartmentName);

    // Find reports by status
    List<Report> findByStatus(Report.ReportStatus status);

    // Find reports by apartment owner email and status
    List<Report> findByApartmentOwnerEmailAndStatus(String ownerEmail, Report.ReportStatus status);

    // Find reports by apartment name and owner email
    List<Report> findByApartmentNameAndApartmentOwnerEmail(String apartmentName, String ownerEmail);

    // Delete reports by apartment name
    void deleteByApartmentName(String apartmentName);
}