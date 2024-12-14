package mk12.service;

import mk12.dto.ReportDTO;
import mk12.model.Report;
import mk12.repository.IReportRepository;
import mk12.repository.IApartmentOwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OwnerReportService {
    private static final Logger logger = LoggerFactory.getLogger(OwnerReportService.class);

    private final IReportRepository reportRepository;
    private final IApartmentOwnerRepository ownerRepository;

    public OwnerReportService(IReportRepository reportRepository,
                              IApartmentOwnerRepository ownerRepository) {
        this.reportRepository = reportRepository;
        this.ownerRepository = ownerRepository;
    }

    public List<ReportDTO> getReportsByOwner(String ownerEmail) {
        logger.info("Retrieving all reports for owner: {}", ownerEmail);
        validateOwner(ownerEmail);
        return reportRepository.findByApartmentOwnerEmail(ownerEmail).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReportDTO> getPendingReportsByOwner(String ownerEmail) {
        logger.info("Retrieving pending reports for owner: {}", ownerEmail);
        validateOwner(ownerEmail);
        return reportRepository.findByApartmentOwnerEmailAndStatus(
                        ownerEmail, Report.ReportStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReportDTO addOwnerResponse(Long reportId, String ownerEmail, String response) {
        logger.info("Adding owner response to report ID: {}", reportId);
        validateOwner(ownerEmail);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (!report.getApartment().getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Unauthorized to respond to this report");
        }

        report.setOwnerResponse(response);
        report.setStatus(Report.ReportStatus.REVIEWED);

        return convertToDTO(reportRepository.save(report));
    }

    public List<ReportDTO> getReportsByOwnerAndStatus(String ownerEmail, Report.ReportStatus status) {
        logger.info("Retrieving reports for owner: {} with status: {}", ownerEmail, status);
        validateOwner(ownerEmail);
        return reportRepository.findByApartmentOwnerEmailAndStatus(ownerEmail, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReportDTO> getReportsByApartmentAndOwner(String apartmentName, String ownerEmail) {
        logger.info("Retrieving reports for apartment: {} owned by: {}", apartmentName, ownerEmail);
        validateOwner(ownerEmail);
        return reportRepository.findByApartmentNameAndApartmentOwnerEmail(apartmentName, ownerEmail)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void validateOwner(String ownerEmail) {
        ownerRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
    }

    private ReportDTO convertToDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setReporterUsername(report.getReporterUsername());
      //  dto.setReporterEmail(report.getReporterEmail());
        dto.setReason(report.getReason());
        dto.setApartmentName(report.getApartment().getName());
        dto.setStatus(report.getStatus());
        dto.setReportDate(String.valueOf(report.getReportDate()));
        return dto;
    }
}