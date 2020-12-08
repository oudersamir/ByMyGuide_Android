package com.example.myapplication;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder>   {
    private List<place> mPeopleList;
    private Context mContext;
    private RecyclerView mRecyclerV;
    @NonNull
    @Override
    public PlaceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.single_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaceAdapter(List<place> myDataset, Context context, RecyclerView recyclerView) {
        mPeopleList = myDataset;
        mContext = context;
        mRecyclerV = recyclerView;

    }
    @Override
    public void onBindViewHolder(@NonNull PlaceAdapter.ViewHolder holder, final int position) {
        final place place = mPeopleList.get(position);


        holder.personImageImgV.setBackgroundResource(0);
        if (place.getStatut() == 1)
            holder.personImageImgV.setImageResource(R.drawable.marker_green3);
        else
            holder.personImageImgV.setImageResource(R.drawable.marker_blue);

        String personne="";
        if(place.getNumber()==1) personne="personne";
        else personne="personnes";
        holder.personNameTxtV.setText("     " + place.getPlace()+"\n");
        holder.Reviews.setText("*visité par "+place.getNumber()+"  "+personne+"\n*Note : "+place.getNotes());


    holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Hello")
                        .setMessage("voulez vous ouvrir sur la MAP")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {


                                dialoginterface.cancel();
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                Intent ii = new Intent(mContext, MapsActivity.class);
                                Bundle b=new Bundle();
                                b.putDouble("lng",new Double(mPeopleList.get(position).getLng()));
                                b.putDouble("lat",new Double(mPeopleList.get(position).getLat()));
                                b.putInt("status",(mPeopleList.get(position).getStatut()));
                                ii.putExtras(b);
                               mContext.startActivity(ii);
                                Toast.makeText(mContext,mPeopleList.get(position).getPlace(),Toast.LENGTH_LONG).show();
                            }
                        }).show();

                return true;
            }
        });



    }
    @Override
    public int getItemCount() {
        return mPeopleList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView personNameTxtV;

        public ImageView personImageImgV;
        public TextView  Reviews;



        public View layout;

        public ViewHolder(View v) {
            super(v);
            Reviews = (TextView) v.findViewById(R.id.place2);

            personNameTxtV = (TextView) v.findViewById(R.id.place);
             personImageImgV = (ImageView) v.findViewById(R.id.imageViewStatus);


            layout = v;


        }
    }

}