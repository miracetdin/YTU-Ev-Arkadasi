package com.example.ytu_ev_arkadasi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProfileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<ProfileModel> profileList;
    public Context profileContext;

    public static class ProfileListViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView nameSurname, status;

        public ProfileListViewHolder(View view) {
            super(view);

            // Her duyuru; görsel, başlık ve tarih içermektedir
            this.image = (ImageView) view.findViewById(R.id.profilecard_iv_profilephoto);
            this.nameSurname = (TextView) view.findViewById(R.id.profilecard_tv_name_surname);
            this.status = (TextView) view.findViewById(R.id.profilecard_tv_status);
        }
    }

    public ProfileListAdapter(ArrayList<ProfileModel> dataSet, Context context) {
        this.profileList = dataSet;
        this.profileContext = context;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profilecard, parent, false);
        return new ProfileListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProfileModel announcement = profileList.get(position);

        if(announcement != null) {
            ((ProfileListViewHolder) holder).image.setImageResource(announcement.profilePhoto);
            ((ProfileListViewHolder) holder).nameSurname.setText(announcement.name);
            ((ProfileListViewHolder) holder).status.setText(announcement.status);
        }
    }

    public int getItemCount() { return profileList.size(); }

}
