package mk12.service;

import mk12.dto.MatchResultDTO;
import mk12.dto.QuizSubmissionDTO;
import mk12.model.QuizResponse;
import mk12.repository.IQuizRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
public class RoommateMatchingService {
    private static final Logger logger = LoggerFactory.getLogger(RoommateMatchingService.class);

    private final IQuizRepository quizRepository;

    // Constants for matching weights
    private static final int SMOKING_WEIGHT = 25;
    private static final int CLEANLINESS_WEIGHT = 20;
    private static final int NOISE_WEIGHT = 15;
    private static final int HOSTING_WEIGHT = 10;
    private static final int MORNING_WEIGHT = 10;
    private static final int ORGANIZATION_WEIGHT = 10;
    private static final int PETS_WEIGHT = 5;
    private static final int PEOPLE_OVER_WEIGHT = 5;

    public RoommateMatchingService(IQuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public void submitQuiz(QuizSubmissionDTO submission) {
        logger.info("Submitting quiz for user: {}", submission.getUsername());

        quizRepository.findByUsername(submission.getUsername()).ifPresent(existing -> {
            logger.error("Quiz already exists for username: {}", submission.getUsername());
            throw new RuntimeException("Quiz already exists for username: " + submission.getUsername());
        });

        try {
            QuizResponse response = new QuizResponse();
            response.setUsername(submission.getUsername());
            response.setMorningPerson(validateScore(submission.getMorningPerson()));
            response.setHosting(validateScore(submission.getHosting()));
            response.setLikingPets(validateScore(submission.getLikingPets()));
            response.setSmoking(validateScore(submission.getSmoking()));
            response.setOrganizationSkills(validateScore(submission.getOrganizationSkills()));
            response.setPeopleOver(validateScore(submission.getPeopleOver()));
            response.setNoiseLevel(validateScore(submission.getNoiseLevel()));
            response.setCleanliness(validateScore(submission.getCleanliness()));

            quizRepository.save(response);
            logger.info("Quiz submitted successfully for user: {}", submission.getUsername());
        } catch (Exception e) {
            logger.error("Error submitting quiz for user: {}", submission.getUsername(), e);
            throw new RuntimeException("Error submitting quiz: " + e.getMessage());
        }
    }

    public List<MatchResultDTO> findMatches(String username) {
        logger.info("Finding matches for user: {}", username);

        QuizResponse userResponse = quizRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Quiz response not found for username: {}", username);
                    return new RuntimeException("Quiz response not found for username: " + username);
                });

        List<QuizResponse> potentialMatches = quizRepository.findByUsernameNot(username);
        List<MatchResultDTO> matches = new ArrayList<>();

        QuizSubmissionDTO userDTO = convertToDTO(userResponse);

        for (QuizResponse potentialMatch : potentialMatches) {
            try {
                QuizSubmissionDTO matchDTO = convertToDTO(potentialMatch);
                double compatibilityScore = calculateMatchScore(userDTO, matchDTO);

                MatchResultDTO result = new MatchResultDTO();
                result.setUsername(potentialMatch.getUsername());
                result.setStarRating(convertToStarRating(compatibilityScore));
                result.setMatchedUserProfile(matchDTO);

                matches.add(result);
                logger.debug("Calculated match score {} between {} and {}",
                        compatibilityScore, username, potentialMatch.getUsername());
            } catch (Exception e) {
                logger.error("Error calculating match for users {} and {}",
                        username, potentialMatch.getUsername(), e);
            }
        }

        matches.sort((a, b) -> Double.compare(b.getStarRating(), a.getStarRating()));
        logger.info("Found {} potential matches for user: {}", matches.size(), username);
        return matches;
    }

    public MatchResultDTO calculateSingleMatch(String username1, String username2) {
        logger.info("Calculating match between users: {} and {}", username1, username2);

        QuizResponse user1Response = quizRepository.findByUsername(username1)
                .orElseThrow(() -> new RuntimeException("Quiz response not found for username: " + username1));

        QuizResponse user2Response = quizRepository.findByUsername(username2)
                .orElseThrow(() -> new RuntimeException("Quiz response not found for username: " + username2));

        QuizSubmissionDTO user1DTO = convertToDTO(user1Response);
        QuizSubmissionDTO user2DTO = convertToDTO(user2Response);

        try {
            double compatibilityScore = calculateMatchScore(user1DTO, user2DTO);

            MatchResultDTO result = new MatchResultDTO();
            result.setUsername(username2);
            result.setStarRating(convertToStarRating(compatibilityScore));
            result.setMatchedUserProfile(user2DTO);

            logger.info("Successfully calculated match between users with score: {}", compatibilityScore);
            return result;
        } catch (Exception e) {
            logger.error("Error calculating match between users", e);
            throw new RuntimeException("Error calculating match: " + e.getMessage());
        }
    }

    public QuizSubmissionDTO getQuizSubmissionByUsername(String username) {
        logger.info("Retrieving quiz submission for user: {}", username);

        QuizResponse response = quizRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Quiz response not found for username: " + username));
        return convertToDTO(response);
    }

    public boolean deleteQuizSubmissionByUsername(String username) {
        logger.info("Deleting quiz submission for user: {}", username);

        try {
            QuizResponse response = quizRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Quiz response not found for username: " + username));
            quizRepository.delete(response);
            logger.info("Successfully deleted quiz submission for user: {}", username);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting quiz submission for user: {}", username, e);
            return false;
        }
    }

    public QuizSubmissionDTO updateQuizSubmissionByUsername(String username, QuizSubmissionDTO updatedSubmission) {
        logger.info("Updating quiz submission for user: {}", username);

        QuizResponse response = quizRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Quiz response not found for username: " + username));

        try {
            response.setMorningPerson(validateScore(updatedSubmission.getMorningPerson()));
            response.setHosting(validateScore(updatedSubmission.getHosting()));
            response.setLikingPets(validateScore(updatedSubmission.getLikingPets()));
            response.setSmoking(validateScore(updatedSubmission.getSmoking()));
            response.setOrganizationSkills(validateScore(updatedSubmission.getOrganizationSkills()));
            response.setPeopleOver(validateScore(updatedSubmission.getPeopleOver()));
            response.setNoiseLevel(validateScore(updatedSubmission.getNoiseLevel()));
            response.setCleanliness(validateScore(updatedSubmission.getCleanliness()));

            QuizResponse updatedResponse = quizRepository.save(response);
            logger.info("Successfully updated quiz submission for user: {}", username);
            return convertToDTO(updatedResponse);
        } catch (Exception e) {
            logger.error("Error updating quiz submission for user: {}", username, e);
            throw new RuntimeException("Error updating quiz submission: " + e.getMessage());
        }
    }

    private double convertToStarRating(double matchScore) {
        if (matchScore >= 90) return 5.0;
        if (matchScore >= 75) return 4.0;
        if (matchScore >= 60) return 3.0;
        if (matchScore >= 45) return 2.0;
        if (matchScore >= 30) return 1.0;
        return 0.0;
    }

    private QuizSubmissionDTO convertToDTO(QuizResponse response) {
        QuizSubmissionDTO dto = new QuizSubmissionDTO();
        dto.setUsername(response.getUsername());
        dto.setMorningPerson(response.getMorningPerson());
        dto.setHosting(response.getHosting());
        dto.setLikingPets(response.getLikingPets());
        dto.setSmoking(response.getSmoking());
        dto.setOrganizationSkills(response.getOrganizationSkills());
        dto.setPeopleOver(response.getPeopleOver());
        dto.setNoiseLevel(response.getNoiseLevel());
        dto.setCleanliness(response.getCleanliness());
        return dto;
    }

    private int validateScore(int score) {
        if (score < 0 || score > 10) {
            throw new IllegalArgumentException("Score must be between 0 and 10");
        }
        return score;
    }

    private double calculateMatchScore(QuizSubmissionDTO user1, QuizSubmissionDTO user2) {
        double totalScore = 0;
        double maxPossibleScore = 0;

        // Smoking compatibility (highest weight due to health implications)
        int smokingDiff = Math.abs(user1.getSmoking() - user2.getSmoking());
        totalScore += (10 - smokingDiff) * SMOKING_WEIGHT;
        maxPossibleScore += 10 * SMOKING_WEIGHT;

        // Cleanliness compatibility
        int cleanlinessDiff = Math.abs(user1.getCleanliness() - user2.getCleanliness());
        totalScore += (10 - cleanlinessDiff) * CLEANLINESS_WEIGHT;
        maxPossibleScore += 10 * CLEANLINESS_WEIGHT;

        // Noise level compatibility
        int noiseDiff = Math.abs(user1.getNoiseLevel() - user2.getNoiseLevel());
        totalScore += (10 - noiseDiff) * NOISE_WEIGHT;
        maxPossibleScore += 10 * NOISE_WEIGHT;

        // Hosting compatibility
        int hostingDiff = Math.abs(user1.getHosting() - user2.getHosting());
        totalScore += (10 - hostingDiff) * HOSTING_WEIGHT;
        maxPossibleScore += 10 * HOSTING_WEIGHT;

        // Morning person compatibility
        int morningDiff = Math.abs(user1.getMorningPerson() - user2.getMorningPerson());
        totalScore += (10 - morningDiff) * MORNING_WEIGHT;
        maxPossibleScore += 10 * MORNING_WEIGHT;

        // Organization compatibility
        int organizationDiff = Math.abs(user1.getOrganizationSkills() - user2.getOrganizationSkills());
        totalScore += (10 - organizationDiff) * ORGANIZATION_WEIGHT;
        maxPossibleScore += 10 * ORGANIZATION_WEIGHT;

        // Pets compatibility
        int petsDiff = Math.abs(user1.getLikingPets() - user2.getLikingPets());
        totalScore += (10 - petsDiff) * PETS_WEIGHT;
        maxPossibleScore += 10 * PETS_WEIGHT;

        // People over compatibility
        int peopleDiff = Math.abs(user1.getPeopleOver() - user2.getPeopleOver());
        totalScore += (10 - peopleDiff) * PEOPLE_OVER_WEIGHT;
        maxPossibleScore += 10 * PEOPLE_OVER_WEIGHT;

        // Convert to percentage
        return (totalScore / maxPossibleScore) * 100;
    }
}