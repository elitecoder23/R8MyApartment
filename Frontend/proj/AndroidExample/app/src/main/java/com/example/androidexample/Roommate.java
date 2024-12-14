package com.example.androidexample;

public class Roommate {
    private String username;
    private double starRating;
    private MatchedUserProfile matchedUserProfile;

    public Roommate(String username, double starRating, MatchedUserProfile matchedUserProfile) {
        this.username = username;
        this.starRating = starRating;
        this.matchedUserProfile = matchedUserProfile;
    }

    public String getUsername() {
        return username;
    }

    public double getStarRating() {
        return starRating;
    }

    public MatchedUserProfile getMatchedUserProfile() {
        return matchedUserProfile;
    }

    public static class MatchedUserProfile {
        private String username;
        private int morningPerson;
        private int hosting;
        private int likingPets;
        private int smoking;
        private int organizationSkills;
        private int peopleOver;
        private int noiseLevel;
        private int cleanliness;

        public MatchedUserProfile(String username, int morningPerson, int hosting, int likingPets, int smoking, int organizationSkills, int peopleOver, int noiseLevel, int cleanliness) {
            this.username = username;
            this.morningPerson = morningPerson;
            this.hosting = hosting;
            this.likingPets = likingPets;
            this.smoking = smoking;
            this.organizationSkills = organizationSkills;
            this.peopleOver = peopleOver;
            this.noiseLevel = noiseLevel;
            this.cleanliness = cleanliness;
        }

        public String getUsername() {
            return username;
        }

        public int getMorningPerson() {
            return morningPerson;
        }

        public int getHosting() {
            return hosting;
        }

        public int getLikingPets() {
            return likingPets;
        }

        public int getSmoking() {
            return smoking;
        }

        public int getOrganizationSkills() {
            return organizationSkills;
        }

        public int getPeopleOver() {
            return peopleOver;
        }

        public int getNoiseLevel() {
            return noiseLevel;
        }

        public int getCleanliness() {
            return cleanliness;
        }
    }
}