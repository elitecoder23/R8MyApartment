package mk12.service;

import mk12.dto.AdminLoginDTO;
import mk12.dto.LoginResponse;
import mk12.dto.ReportDTO;
import mk12.model.AdminUser;
import mk12.model.Apartment;
import mk12.model.Report;
import mk12.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final IAdminRepository adminRepository;
    private final IReportRepository reportRepository;
    private final IApartmentRepository apartmentRepository;
    private final IApartmentApplicationRepository applicationRepository;
    private final IReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminService(IAdminRepository adminRepository,
                        IReportRepository reportRepository,
                        IApartmentRepository apartmentRepository,
                        IApartmentApplicationRepository applicationRepository,
                        IReviewRepository reviewRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.reportRepository = reportRepository;
        this.apartmentRepository = apartmentRepository;
        this.applicationRepository = applicationRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse createAdmin(AdminLoginDTO signupDTO) {
        logger.info("Attempting to create new admin with email: {}", signupDTO.getEmail());

        if (adminRepository.findByEmail(signupDTO.getEmail()).isPresent()) {
            logger.error("Admin already exists with email: {}", signupDTO.getEmail());
            throw new RuntimeException("Admin already exists with this email");
        }

        try {
            AdminUser admin = new AdminUser();
            admin.setEmail(signupDTO.getEmail());
            admin.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
            admin.setName(signupDTO.getName());

            admin = adminRepository.save(admin);
            String token = jwtService.generateToken(admin.getEmail());

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setEmail(admin.getEmail());
            response.setName(admin.getName());

            logger.info("Successfully created admin account for: {}", signupDTO.getEmail());
            return response;
        } catch (Exception e) {
            logger.error("Error creating admin account: {}", e.getMessage());
            throw new RuntimeException("Failed to create admin account: " + e.getMessage());
        }
    }

    public LoginResponse loginAdmin(AdminLoginDTO loginDTO) {
        logger.info("Attempting admin login for email: {}", loginDTO.getEmail());

        AdminUser admin = adminRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> {
                    logger.error("Admin not found with email: {}", loginDTO.getEmail());
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            logger.error("Invalid password for admin: {}", loginDTO.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(admin.getEmail());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setEmail(admin.getEmail());
        response.setName(admin.getName());

        logger.info("Admin login successful for: {}", loginDTO.getEmail());
        return response;
    }

    public Page<ReportDTO> getAllReports(Pageable pageable) {
        logger.info("Retrieving all reports with pagination");
        Page<Report> reportPage = reportRepository.findAll(pageable);
        List<ReportDTO> reportDTOs = reportPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(reportDTOs, pageable, reportPage.getTotalElements());
    }

    public Page<ReportDTO> getReportsByStatus(Report.ReportStatus status, Pageable pageable) {
        logger.info("Retrieving reports with status: {}", status);
        List<Report> reports = reportRepository.findByStatus(status);
        List<ReportDTO> reportDTOs = reports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), reportDTOs.size());

        return new PageImpl<>(
                reportDTOs.subList(start, end),
                pageable,
                reportDTOs.size()
        );
    }

    public ReportDTO updateReportStatus(Long id, Report.ReportStatus status) {
        logger.info("Updating report status. ID: {}, New Status: {}", id, status);

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Report not found with ID: {}", id);
                    return new RuntimeException("Report not found");
                });

        report.setStatus(status);
        Report updatedReport = reportRepository.save(report);
        logger.info("Successfully updated report status. ID: {}", id);
        return convertToDTO(updatedReport);
    }

    @Transactional
    public void deleteApartment(String apartmentName) {
        logger.info("Attempting to delete apartment: {}", apartmentName);

        Optional<Apartment> apartment = apartmentRepository.findByName(apartmentName);
        if (apartment.isEmpty()) {
            logger.error("Apartment listing not found: {}", apartmentName);
            throw new RuntimeException("Apartment listing not found: " + apartmentName);
        }

        try {
            // Delete all applications
            applicationRepository.findByApartmentName(apartmentName)
                    .forEach(application -> {
                        logger.debug("Deleting application ID: {}", application.getId());
                        applicationRepository.delete(application);
                    });
            logger.info("Deleted all applications for apartment: {}", apartmentName);

            // Delete all reports
            reportRepository.findByApartmentName(apartmentName)
                    .forEach(report -> {
                        logger.debug("Deleting report ID: {}", report.getId());
                        reportRepository.delete(report);
                    });
            logger.info("Deleted all reports for apartment: {}", apartmentName);

            // Delete all reviews
            reviewRepository.findByApartment_Name(apartmentName)
                    .forEach(review -> {
                        logger.debug("Deleting review ID: {}", review.getId());
                        reviewRepository.delete(review);
                    });
            logger.info("Deleted all reviews for apartment: {}", apartmentName);

            // Finally delete the apartment
            apartmentRepository.delete(apartment.get());
            logger.info("Successfully deleted apartment: {}", apartmentName);

        } catch (Exception e) {
            logger.error("Error occurred while deleting apartment: {}", apartmentName, e);
            throw new RuntimeException("Failed to delete apartment: " + e.getMessage());
        }
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