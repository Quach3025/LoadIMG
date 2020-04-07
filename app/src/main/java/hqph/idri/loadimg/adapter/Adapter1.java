package hqph.idri.loadimg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import hqph.idri.loadimg.R;
import hqph.idri.loadimg.loadIMG.Photo;

public class Adapter1 extends RecyclerView.Adapter<Adapter1.ViewHodel> {
    private List<Photo> list;
    private Context context;
    //clickItem
    private AdapterItem adapterItem;

    public Adapter1(List<Photo> list, Context context ,AdapterItem adapterItem) {
        this.list = list;
        this.context = context;
        this.adapterItem = adapterItem;
    }
    public interface AdapterItem{
        void OnClick(int position);
    }

    @NonNull
    @Override
    public ViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Khoi tao view hodel lien ket layout
        View view = LayoutInflater.from(context).inflate(R.layout.itemimg,parent,false);
        return new ViewHodel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodel holder, final int position) {
        //tuong tac cac thanh phan trong viewhodel
        try {
            Glide.with(context).load(list.get(position).getUrlL()).centerCrop().into(holder.img);
            holder.tvView.setText(list.get(position).getViews());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterItem.OnClick(position);
                }
            });
       }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        //so luong hien thi
        return list== null? 0:list.size();
    }

    public class ViewHodel extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvView;
        CardView cardView;


        public ViewHodel(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_photo);
            tvView = itemView.findViewById(R.id.tvView);
            cardView= itemView.findViewById(R.id.cardView);
        }
    }
}
