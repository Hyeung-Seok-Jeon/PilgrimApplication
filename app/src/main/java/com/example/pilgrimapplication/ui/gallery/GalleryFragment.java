package com.example.pilgrimapplication.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pilgrimapplication.ImageRD;
import com.example.pilgrimapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {
    private List<ImageRD> galleryListUri=new ArrayList<>();
    private FirebaseDatabase database;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        database=FirebaseDatabase.getInstance();
        RecyclerView recyclerView=root.findViewById(R.id.recycierview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        final GalleryRecyclerViewAdapter galleryRecyclerViewAdapter=new GalleryRecyclerViewAdapter();
        recyclerView.setAdapter(galleryRecyclerViewAdapter);

        database.getReference("GalleryUris").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                galleryListUri.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ImageRD imageRD=snapshot.getValue(ImageRD.class);
                    galleryListUri.add(imageRD);
                }
                galleryRecyclerViewAdapter.notifyDataSetChanged();//새로고침하여 매번 갤러리를 갱신해준다.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return root;
    }
    class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery,parent,false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            //uri를 가져와서 이미지를 그려주는 라이브러리 Picasso 라이브러리 보다 속도가 빠르다.
            Glide.with(holder.itemView.getContext()).load(galleryListUri.get(position).imageUrI).into(((CustomViewHolder)holder).imageView);
        }

        @Override
        public int getItemCount() {
            return galleryListUri.size();
        }
        private class CustomViewHolder extends RecyclerView.ViewHolder{
            private ImageView imageView;
            public CustomViewHolder(View view){
                super(view);
                imageView=view.findViewById(R.id.item_ImageView);

            }
        }
    }

}
