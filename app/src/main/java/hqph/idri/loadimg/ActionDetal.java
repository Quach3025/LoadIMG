package hqph.idri.loadimg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hqph.idri.loadimg.adapter.AdapterDetal;
import hqph.idri.loadimg.loadIMG.LoadIMG;
import hqph.idri.loadimg.loadIMG.Photo;
import hqph.idri.loadimg.retrofit.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActionDetal extends AppCompatActivity {
    private FloatingActionMenu fab;
    private FloatingActionButton fabWallpaper;
    private FloatingActionButton fabDownload;
    private FloatingActionButton fabShare;
    private FloatingActionButton fabFavorite;
    private static final int PERMISSION_REQUEST_CODE = 1000;
    @SuppressLint("StaticFieldLeak")
    private static ViewPager mPager;
    Toolbar toolbar;
    TextView tvPosition;

    List<Photo> photos;
    AdapterDetal adapter;

    boolean showButton = false;
    public String id;
    public int currentPage, position;

    DownloadManager downloadManager;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout close, small, medium, large;
    //FavoriteDAO favDAO;

    ProgressBar progressBar;


    //Xin quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_detal);
        init();
        createBottomSheetDialog();

        fab.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showButton) {
                    showFloatingButton();
                    showButton = true;
                } else {
                    hideFloatingButton();
                    showButton = false;
                }
            }
        });
    }

    private void init() {
        fab = findViewById(R.id.fab);
        fabWallpaper = findViewById(R.id.fabWallpaper);
        fabDownload = findViewById(R.id.fabDownload);
        fabShare = findViewById(R.id.fabShare);
        fabFavorite = findViewById(R.id.fabFavorite);


        toolbar = findViewById(R.id.toolbar);
        tvPosition = findViewById(R.id.tvPosition);
        photos = new ArrayList<>();
        adapter = new AdapterDetal(ActionDetal.this, photos);
        mPager = findViewById(R.id.pager);

        progressBar = findViewById(R.id.spin_kit);
        Circle doubleBounce = new Circle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        position = getIntent().getIntExtra("position", 0);
//        images = photos.get(mPager.getCurrentItem()).getSourceUrl();

        onClickFloatingButton();
        getData();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE

                }, PERMISSION_REQUEST_CODE);
            }
    }

    private void getData() {
        Retrofit.getServices().getPhoto("flickr.favorites.getList", "02af7301a70b9e4c5c33cca0414b8f31",
                "184472677@N05"
                , "views,media,path_alias,url_sq,url_t,url_s,url_q,url_m,url_n,url_z,url_c,url_l,url_o",
                1, 50, "json", 1).enqueue(new Callback<LoadIMG>() {
            @Override
            public void onResponse(Call<LoadIMG> call, Response<LoadIMG> response) {
                progressBar.setVisibility(View.INVISIBLE);
                photos.addAll(response.body().getPhotos().getPhoto());
                mPager.setAdapter(adapter);
                mPager.setCurrentItem(position, true);
                mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int position) {
                        currentPage = position;
                        tvPosition.setText(position + 1 + "/" + (photos.size()));

                    }

                    @Override
                    public void onPageScrolled(int pos, float arg1, int arg2) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int pos) {

                    }
                });
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<LoadIMG> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void createBottomSheetDialog() {
        if (bottomSheetDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.size_img, null);
            close = view.findViewById(R.id.close);
            small = view.findViewById(R.id.small);
            medium = view.findViewById(R.id.medium);
            large = view.findViewById(R.id.large);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();
                }
            });

            small.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveImageSmall();
                    bottomSheetDialog.dismiss();
                }
            });

            medium.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveImageMedium();
                    bottomSheetDialog.dismiss();
                }
            });

            large.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveImageLarge();
                    bottomSheetDialog.dismiss();
                }
            });
            bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(view);
            FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);

        }
    }

    private void onClickFloatingButton() {
        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFloatingButton();
            }
        });
        fabDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.show();

            }
        });
        fabWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActionDetal.this);
                builder.setTitle("Đặt hình nền");
                builder.setMessage("Bạn có muốn đặt hình nền không?");
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setloadIMG();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Caned", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                hideFloatingButton();

                hideFloatingButton();

            }
        });
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareUrl(ActionDetal.this, photos.get(position).getUrlL());
                hideFloatingButton();
            }
        });
    }


    public static void shareUrl(Context context, String url) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Share link");
        share.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(share, "Share"));
    }

    private void saveImageLarge() {
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(photos.get(mPager.getCurrentItem()).getUrlL());
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        Picasso.get()
                .load(photos.get(mPager.getCurrentItem()).getUrlL())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        try {
                            File mydie = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/HD Wallpaper");
                            if (!mydie.exists()) {
                                mydie.mkdirs();
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream(new File(mydie, new Date().toString() + ".jpg"));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            Toast.makeText(ActionDetal.this, "Download Largest Size Success!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    private void saveImageMedium() {
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(photos.get(mPager.getCurrentItem()).getUrlZ());
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        Picasso.get()
                .load(photos.get(mPager.getCurrentItem()).getUrlZ())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        try {
                            File mydie = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/HD Wallpaper");
                            if (!mydie.exists()) {
                                mydie.mkdirs();
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream(new File(mydie, new Date().toString() + ".jpg"));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            Toast.makeText(ActionDetal.this, "Download Medium Size Success!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    private void saveImageSmall() {
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(photos.get(mPager.getCurrentItem()).getUrlN());
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        Picasso.get()
                .load(photos.get(mPager.getCurrentItem()).getUrlN())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        try {
                            File mydie = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/HD Wallpaper");
                            if (!mydie.exists()) {
                                mydie.mkdirs();
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream(new File(mydie, new Date().toString() + ".jpg"));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            Toast.makeText(ActionDetal.this, "Download Small Size Success!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    public void setloadIMG() {
        Picasso.get().load(photos.get(mPager.getCurrentItem()).getUrlL()).into(new Target() {


            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                WallpaperManager loadImg = WallpaperManager.getInstance(ActionDetal.this);
                try {
                    loadImg.setBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(ActionDetal.this, "Load Img", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                //Toasty.error(DetailPhotoActivity.this, "Loading image failed", Toast.LENGTH_SHORT, true).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                //Toasty.info(DetailPhotoActivity.this, "Downloading image", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    private void hideFloatingButton() {
        fabDownload.hide(true);
        fabWallpaper.hide(true);
        fabShare.hide(true);
        fabFavorite.hide(true);
    }

    private void showFloatingButton() {
        fabDownload.show(true);
        fabWallpaper.show(true);
        fabShare.show(true);
        fabFavorite.show(true);
    }

    public void back(View view) {
        finish();
    }
}
