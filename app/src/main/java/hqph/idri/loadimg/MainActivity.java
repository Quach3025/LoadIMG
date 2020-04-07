package hqph.idri.loadimg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import java.util.ArrayList;
import java.util.List;

import hqph.idri.loadimg.adapter.Adapter1;
import hqph.idri.loadimg.adapter.Adapter111;
import hqph.idri.loadimg.loadIMG.LoadIMG;
import hqph.idri.loadimg.loadIMG.Photo;
import hqph.idri.loadimg.retrofit.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private List<Photo> list;
    private RecyclerView recyclerView;
    private Adapter1 adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rcView);
        list = new ArrayList<>();
        adapter1 = new Adapter1(list, MainActivity.this, new Adapter1.AdapterItem() {
            @Override
            public void OnClick(int position) {
                Intent intent = new Intent(MainActivity.this, ActionDetal.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        recyclerView.addItemDecoration(new Adapter111(20));
        StaggeredGridLayoutManager sgm = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sgm);
        recyclerView.setAdapter(adapter1);
        getData();


    }

    private void getData() {
        Retrofit.getServices().getPhoto("flickr.favorites.getList", "02af7301a70b9e4c5c33cca0414b8f31",
                "184472677@N05"
                , "views,media,path_alias,url_sq,url_t,url_s,url_q,url_m,url_n,url_z,url_c,url_l,url_o",
                1, 50, "json", 1).enqueue(new Callback<LoadIMG>() {
            @Override
            public void onResponse(Call<LoadIMG> call, Response<LoadIMG> response) {

                list.addAll(response.body().getPhotos().getPhoto());
                Log.d("DATAAA", response.body().getPhotos().getPhoto().toString());
                adapter1.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<LoadIMG> call, Throwable t) {

            }
        });


    }

}
