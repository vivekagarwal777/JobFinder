package com.iborland.jobfinder;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

/**
 * Created by iborland on 20.03.16.
 */

/*

Класс объявлений. Здесь принимается из БД и содержится вся информация об объявлениях.

 */

public class Post implements Parcelable{

    int id;
    int ownerID;
    String ownerLogin;
    String ownerName;
    String postName;
    String postText;
    String cost;
    int status;
    int category;
    String createtime;
    boolean loaded = false;
    String[] Adresses;
    String[] Coords;
    int amount;
    String city;
    int executor;
    String executor_name;
    String reason;
    int vote_id;
    int deleted;
    String execute_start;
    int[] accept_post = new int[2];
    int buffer_id;

    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    PostLoad loadPost;

    Post(int db_id){
        buffer_id = db_id;
        loadPost = new PostLoad();
        try {
            loadPost.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    class PostLoad extends Thread{

        PostLoad(){
            start();
        }

        public void run() {
            try{
                APILoader apiLoader = new APILoader("http://api.jobfinder.ru.com/select_post.php");
                apiLoader.addParams(new String[]{"id"}, new String[]{"" + buffer_id});
                String str = apiLoader.execute();
                JSONObject jsonObject = new JSONObject(str);
                id = jsonObject.getInt("id");
                ownerID = jsonObject.getInt("ownerID");
                ownerLogin = jsonObject.getString("ownerLogin");
                ownerName = jsonObject.getString("ownerName");
                postName = jsonObject.getString("postName");
                postText = jsonObject.getString("postText");
                cost = jsonObject.getString("cost");
                status = jsonObject.getInt("status");
                category = jsonObject.getInt("Category");
                createtime = jsonObject.getString("createtime");
                String buffer_adresses = jsonObject.getString("Adresses");
                String buffer_coords = jsonObject.getString("Coordinates");
                amount = jsonObject.getInt("Amount");
                city = jsonObject.getString("City");
                executor = jsonObject.getInt("executor");
                executor_name = jsonObject.getString("executor_name");
                reason = jsonObject.getString("reason");
                vote_id = jsonObject.getInt("vote_id");
                deleted = jsonObject.getInt("deleted");
                execute_start = jsonObject.getString("execute_start");
                accept_post[0] = jsonObject.getInt("accept_executor");
                accept_post[1] = jsonObject.getInt("accept_owner");
                if(buffer_adresses != null) {
                    Adresses = buffer_adresses.split("split");
                    Coords = buffer_coords.split("split");
                }

                loaded = true;
                //Log.e("Loaded", "post " + postName + " был загружен");
                /*String query = "SELECT * FROM `posts` WHERE `id` = '" + buffer_id + "'";
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + "triniti.ru-hoster.com/iborlZer?characterEncoding=utf8", "iborlZer",
                        "22599226a");
                statement = connection.createStatement();
                rs = statement.executeQuery(query);
                while (rs.next()) {
                    id = rs.getInt("id");
                    ownerID = rs.getInt("ownerID");
                    ownerLogin = rs.getString("ownerLogin");
                    ownerName = rs.getString("ownerName");
                    postName = rs.getString("postName");
                    postText = rs.getString("postText");
                    cost = rs.getString("cost");
                    status = rs.getInt("status");
                    category = rs.getInt("Category");
                    createtime = rs.getString("createtime");
                    String buffer_adresses = rs.getString("Adresses");
                    String buffer_coords = rs.getString("Coordinates");
                    amount = rs.getInt("Amount");
                    city = rs.getString("City");
                    executor = rs.getInt("executor");
                    executor_name = rs.getString("executor_name");
                    reason = rs.getString("reason");
                    vote_id = rs.getInt("vote_id");
                    deleted = rs.getInt("deleted");
                    execute_start = rs.getString("execute_start");
                    accept_post[0] = rs.getInt("accept_executor");
                    accept_post[1] = rs.getInt("accept_owner");
                    if(buffer_adresses != null) {
                        Adresses = buffer_adresses.split("split");
                        Coords = buffer_coords.split("split");
                    }

                    loaded = true;
                    //Log.e("Loaded", "post " + postName + " был загружен");
                    break;
                }*/
            }
            catch (Exception e)
            {
                Log.e("ERROR", "Ошибка БД ", e);
            }
        }
    }

    public int describeContents() {
        return 0;
    }

    // упаковываем объект в Parcel
    public void writeToParcel(Parcel parcel, int flags) {
        if(loaded == false) {
            Log.e("Parcel", "Ошибка упаковка Parcel. Post not loaded");
            return;
        }
        Log.e("Parcel", "Parcel упакован");
        parcel.writeInt(id);
        parcel.writeInt(ownerID);
        parcel.writeString(ownerLogin);
        parcel.writeString(ownerName);
        parcel.writeString(postName);
        parcel.writeString(postText);
        parcel.writeString(cost);
        parcel.writeInt(status);
        parcel.writeInt(category);
        parcel.writeString(createtime);
        parcel.writeInt(amount);
        parcel.writeString(city);
        parcel.writeInt(executor);
        parcel.writeString(executor_name);
        parcel.writeString(reason);
        parcel.writeInt(vote_id);
        parcel.writeInt(deleted);
        parcel.writeString(execute_start);
        parcel.writeIntArray(accept_post);
        if(amount > 0)
        {
            parcel.writeStringArray(Adresses);
            parcel.writeStringArray(Coords);
        }

    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        // распаковываем объект из Parcel
        public Post createFromParcel(Parcel in) {
            Log.d("Parcel", "createFromParcel");
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    // конструктор, считывающий данные из Parcel
    private Post(Parcel parcel) {
        if(loaded == true) {
            Log.e("Parcel", "Ошибка загрузки из Parcel. Post has been loaded");
            return;
        }
        id = parcel.readInt();
        ownerID = parcel.readInt();
        ownerLogin = parcel.readString();
        ownerName = parcel.readString();
        postName = parcel.readString();
        postText = parcel.readString();
        cost = parcel.readString();
        status = parcel.readInt();
        category = parcel.readInt();
        createtime = parcel.readString();
        amount = parcel.readInt();
        city = parcel.readString();
        executor = parcel.readInt();
        executor_name = parcel.readString();
        reason = parcel.readString();
        vote_id = parcel.readInt();
        deleted = parcel.readInt();
        execute_start = parcel.readString();
        parcel.readIntArray(accept_post);
        if(amount > 0) {
            Adresses = new String[amount];
            Coords = new String[amount];
            parcel.readStringArray(Adresses);
            parcel.readStringArray(Coords);
        }

        Log.e("Parcel", "Post был распакован из Parcel");
        loaded = true;
    }

}
