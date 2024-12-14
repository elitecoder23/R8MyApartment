package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoommateAdapter extends RecyclerView.Adapter<RoommateAdapter.RoommateViewHolder> {
    private List<Roommate> roommatesList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Roommate roommate);
    }

    public RoommateAdapter(List<Roommate> roommatesList, OnItemClickListener listener) {
        this.roommatesList = roommatesList;
        this.listener = listener;
    }

    @Override
    public RoommateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.roommate_item, parent, false);
        return new RoommateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoommateViewHolder holder, int position) {
        Roommate roommate = roommatesList.get(position);
        holder.bind(roommate, listener);
    }

    @Override
    public int getItemCount() {
        return roommatesList.size();
    }

    public static class RoommateViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public TextView starRatingTextView;
        public TextView morningPersonTextView;
        public TextView hostingTextView;
        public TextView likingPetsTextView;
        public TextView smokingTextView;
        public TextView organizationSkillsTextView;
        public TextView peopleOverTextView;
        public TextView noiseLevelTextView;
        public TextView cleanlinessTextView;

        public RoommateViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.roommate_username);
            starRatingTextView = itemView.findViewById(R.id.roommate_star_rating);
            morningPersonTextView = itemView.findViewById(R.id.roommate_morning_person);
            hostingTextView = itemView.findViewById(R.id.roommate_hosting);
            likingPetsTextView = itemView.findViewById(R.id.roommate_liking_pets);
            smokingTextView = itemView.findViewById(R.id.roommate_smoking);
            organizationSkillsTextView = itemView.findViewById(R.id.roommate_organization_skills);
            peopleOverTextView = itemView.findViewById(R.id.roommate_people_over);
            noiseLevelTextView = itemView.findViewById(R.id.roommate_noise_level);
            cleanlinessTextView = itemView.findViewById(R.id.roommate_cleanliness);
        }

        public void bind(final Roommate roommate, final OnItemClickListener listener) {
            usernameTextView.setText(roommate.getUsername());
            starRatingTextView.setText("Star Rating: " + roommate.getStarRating());
            Roommate.MatchedUserProfile profile = roommate.getMatchedUserProfile();
            morningPersonTextView.setText("Morning Person: " + profile.getMorningPerson());
            hostingTextView.setText("Hosting: " + profile.getHosting());
            likingPetsTextView.setText("Liking Pets: " + profile.getLikingPets());
            smokingTextView.setText("Smoking: " + profile.getSmoking());
            organizationSkillsTextView.setText("Organization Skills: " + profile.getOrganizationSkills());
            peopleOverTextView.setText("People Over: " + profile.getPeopleOver());
            noiseLevelTextView.setText("Noise Level: " + profile.getNoiseLevel());
            cleanlinessTextView.setText("Cleanliness: " + profile.getCleanliness());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(roommate);
                }
            });
        }
    }
}