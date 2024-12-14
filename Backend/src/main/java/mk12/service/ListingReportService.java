package mk12.service;

import mk12.dto.ReportDTO;
import mk12.model.Report;
import mk12.model.Apartment;
import mk12.repository.IReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ListingReportService {
    private static final Logger logger = LoggerFactory.getLogger(ListingReportService.class);

    private final IReportRepository reportRepository;

    public ListingReportService(IReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public ReportDTO submitReport(Apartment apartment, ReportDTO reportDTO) {
        logger.info("Creating new report for apartment: {} by user: {}",
                apartment.getName(), reportDTO.getReporterUsername());

        Report report = new Report();
        report.setApartment(apartment);
        report.setReporterUsername(reportDTO.getReporterUsername());
        report.setReporterEmail(reportDTO.getReporterEmail());
        report.setReason(reportDTO.getReason());
        report.setStatus(Report.ReportStatus.PENDING);
        report.setReportDate(LocalDateTime.now());

        Report savedReport = reportRepository.save(report);
        logger.info("Successfully created report with ID: {}", savedReport.getId());

        return convertToDTO(savedReport);
    }

    public ReportDTO getReportById(Long id) {
        return reportRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
    }

    public List<ReportDTO> getReportsByApartmentName(String apartmentName) {
        return reportRepository.findByApartmentName(apartmentName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReportDTO> getReportsByStatus(Report.ReportStatus status) {
        return reportRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReportDTO updateReportStatus(Long reportId, Report.ReportStatus newStatus) {
        logger.info("Updating status for report ID: {} to {}", reportId, newStatus);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + reportId));

        report.setStatus(newStatus);
        Report updatedReport = reportRepository.save(report);

        logger.info("Successfully updated report status");
        return convertToDTO(updatedReport);
    }

    private ReportDTO convertToDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setReporterUsername(report.getReporterUsername());
        dto.setReporterEmail(report.getReporterEmail());
        dto.setReason(report.getReason());
        dto.setApartmentName(report.getApartment().getName());
        dto.setStatus(report.getStatus());
        dto.setReportDate(report.getReportDate().toString());
        return dto;
    }
}