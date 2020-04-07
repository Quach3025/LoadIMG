package hqph.idri.loadimg.retrofit;



import hqph.idri.loadimg.loadIMG.LoadIMG;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Services {
    @GET("services/rest")

    Call<LoadIMG> getPhoto(@Query("method") String method,
                           @Query("api_key") String api_key,
                           @Query("user_id") String user_id,
                           @Query("extras") String extras,
                           @Query("page") int page,
                           @Query("per_page") int per_page,
                           @Query("format") String format,
                           @Query("nojsoncallback") int nojsoncallback);

}
