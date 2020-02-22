package com.gutotech.narutogame.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;
import com.gutotech.narutogame.R;
import com.gutotech.narutogame.data.firebase.FirebaseConfig;
import com.gutotech.narutogame.data.model.Character;
import com.gutotech.narutogame.utils.StorageUtil;

import java.util.List;

public class RankingNinjasRecyclerAdapter extends RecyclerView.Adapter<RankingNinjasRecyclerAdapter.MyViewHolder> {
    private Context mContext;
    private List<Character> mNinjas;

    public RankingNinjasRecyclerAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_ninja_item,
                viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int i) {
        if (mNinjas != null) {
            final Character character = mNinjas.get(i);

            StorageReference imageReference = FirebaseConfig.getStorage()
                    .child("images").child("dojo").child(character.getNinja().getId() + ".png");

            StorageUtil.downloadImage(mContext, imageReference, holder.personagemImageView);

            holder.posicaoTextView.setText(1 + "°");
            holder.nickTextView.setText(character.getNick());
            holder.nickTextView.setOnClickListener(v -> exibirProfile(character));

//        holder.tituloTextView.setText(character.getTitle());
            holder.pontosTextView.setText(String.valueOf(character.getScore()));
            holder.levelTextView.setText(String.valueOf(character.getLevel()));

            if (character.isOnline()) {
                holder.onlineImageView.setImageResource(R.drawable.layout_on);
            } else {
                holder.onlineImageView.setImageResource(R.drawable.layout_off);
            }

            switch (character.getVillage()) {
                case FOLHA:
                    holder.vilaImageView.setImageResource(R.drawable.layout_bandanas_1);
                    break;
                case AREIA:
                    holder.vilaImageView.setImageResource(R.drawable.layout_bandanas_2);
                    break;
                case NEVOA:
                    holder.vilaImageView.setImageResource(R.drawable.layout_bandanas_3);
                    break;
                case PEDRA:
                    holder.vilaImageView.setImageResource(R.drawable.layout_bandanas_4);
                    break;
                case NUVEM:
                    holder.vilaImageView.setImageResource(R.drawable.layout_bandanas_5);
                    break;
                case AKATSUKI:
                    holder.vilaImageView.setImageResource(R.drawable.layout_bandanas_6);
                    break;
                case SOM:
                    holder.vilaImageView.setImageResource(R.drawable.layout_bandanas_7);
                    break;
                case CHUVA:
                    holder.vilaImageView.setImageResource(R.drawable.layout_bandanas_8);
                    break;
            }

            if (i % 2 == 0) {
                holder.bgConstraint.setBackgroundColor(mContext.getResources().getColor(R.color.colorItem1));
            } else {
                holder.bgConstraint.setBackgroundColor(mContext.getResources().getColor(R.color.colorItem2));
            }
        }
    }

    private void exibirProfile(Character character) {
        Dialog profileDialog = new Dialog(mContext);
        profileDialog.setContentView(R.layout.dialog_acao_profile);

        ImageView profileLogadoimageView = profileDialog.findViewById(R.id.profilePersonagemOnImageView);
        StorageUtil.downloadProfile(mContext, profileLogadoimageView, character.getProfilePath());

        TextView nickPersonagemOnTextView = profileDialog.findViewById(R.id.nickPersonagemOnTextView);
        nickPersonagemOnTextView.setText(character.getNick());

        TextView classe = profileDialog.findViewById(R.id.classeTextView);
        classe.setText(String.valueOf(character.getClasse()));

        TextView vila = profileDialog.findViewById(R.id.vilaTextView);
        vila.setText(String.valueOf(character.getVillage()));

        TextView level = profileDialog.findViewById(R.id.levelTextView);
        level.setText(String.valueOf(character.getLevel()));

        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileDialog.show();
    }

    @Override
    public int getItemCount() {
        return mNinjas != null ? mNinjas.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView posicaoTextView;
        private ImageView onlineImageView;
        private TextView nickTextView;
        private TextView tituloTextView;
        private TextView levelTextView;
        private TextView pontosTextView;

        private ImageView vilaImageView;
        private ImageView personagemImageView;
        private ConstraintLayout bgConstraint;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            posicaoTextView = itemView.findViewById(R.id.posicaoTextView);
            nickTextView = itemView.findViewById(R.id.nickTextView);
            tituloTextView = itemView.findViewById(R.id.tituloTextView);
            onlineImageView = itemView.findViewById(R.id.onlineImageView);
            levelTextView = itemView.findViewById(R.id.levelTextView);
            pontosTextView = itemView.findViewById(R.id.pontosTextView);
            vilaImageView = itemView.findViewById(R.id.vilaImageView);
            personagemImageView = itemView.findViewById(R.id.personagemImageView);
            bgConstraint = itemView.findViewById(R.id.bgConstraint);
        }
    }

    public void setNinjas(List<Character> ninjas) {
        mNinjas = ninjas;
        notifyDataSetChanged();
    }
}
