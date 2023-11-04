package br.com.universalorigin.androidqualityrequisitionapis.interfaces;

import java.util.ArrayList;

import br.com.universalorigin.androidqualityrequisitionapis.entitys.EntityPost;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RemoteService{

    @GET("posts")
    Call<ArrayList<EntityPost>> getPost();

    @GET("posts/{id}")
    Call<ArrayList<EntityPost>> getPost(@Path("id") int postId);
}
