package com.iborland.jobfinder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by iborland on 19.03.16.
 */
public class User implements Parcelable{

    static final String DB_ID = "id";
    static final String DB_LOGIN = "Login";
    static final String DB_PASSWORD = "Password";
    static final String DB_SURNAME= "Surname";
    static final String DB_NAME = "Name";
    static final String DB_EMAIL = "Email";
    static final String DB_SCORE = "Score";
    static final String DB_STATUS = "Status";
    static final String DB_AMOUNT_POSTS = "AmountPosts";
    static final String DB_DATE_REGISTRATION = "DateRegistration";
    static final String DB_TOKEN = "Token";
    static final String DB_PHONE = "phone";
    static final String DB_CITY = "City";
    static final String DB_AGE = "Age";
    static final String DB_LOST_MESSAGE = "LostMessage";
    static final String DB_ADMIN = "admin";
    static final String USER_CREATED_ACTION = "com.iborland.jobfinder.u_created";

    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    String query;
    String buffer_Token;

    int id;
    String login;
    String password;
    String surname;
    String name;
    String email;
    int score;
    int status;
    int ad_amount;
    String regdata;
    String token;
    String phone;
    String city;
    int age;
    int lost_msg;
    int admin;
    String msg_id;

    boolean loaded = false;
    boolean security = true;

    String update_sql = "";
    UpdateUserInfo upd;

    int d_id;
    Context context;
    boolean first_loaded = false;

    User(int db_id, String db_buffer, boolean off_security, boolean waiting, Context con){
        d_id = db_id;
        context = con;
        if(db_id < 1){
            Log.e("Error", "Неверный ID");
            return;
        }
        if(loaded == true) {
            Log.e("Error", "User уже загружен");
            return;
        }
        buffer_Token = db_buffer;
        if(off_security == true) security = false;
        query = "SELECT * FROM `users` WHERE `id` = '" + db_id + "'";
        LoadUser loadUser = new LoadUser();
        if(waiting == true) {
            try {
                loadUser.join(30000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class LoadUser extends Thread
    {

        public LoadUser(){ start(); }

        public void run() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = null;
                connection = DriverManager.getConnection("jdbc:mysql://" + "triniti.ru-hoster.com/iborlZer?characterEncoding=utf8", "iborlZer",
                        "22599226a");
                statement = null;
                rs = null;
                statement = connection.createStatement();
                query = "SELECT * FROM `users` WHERE `id` = '" + d_id + "'";
                rs = statement.executeQuery(query);
                while (rs.next()) {
                    id = rs.getInt(DB_ID);
                    login = rs.getString(DB_LOGIN);
                    password = rs.getString(DB_PASSWORD);
                    surname = rs.getString(DB_SURNAME);
                    name = rs.getString(DB_NAME);
                    email = rs.getString(DB_EMAIL);
                    score = rs.getInt(DB_SCORE);
                    status = rs.getInt(DB_STATUS);
                    ad_amount = rs.getInt(DB_AMOUNT_POSTS);
                    regdata = rs.getString(DB_DATE_REGISTRATION);
                    token = rs.getString(DB_TOKEN);
                    phone = rs.getString(DB_PHONE);
                    city = rs.getString(DB_CITY);
                    age = rs.getInt(DB_AGE);
                    lost_msg = rs.getInt(DB_LOST_MESSAGE);
                    admin = rs.getInt(DB_ADMIN);
                    msg_id = rs.getString("msg_id");
                    if(security == true) {
                        if (!token.equals(buffer_Token)) {
                            Log.e("Error:", "Ошибка. Несоответствие токена");
                            return;
                        }
                    }
                    loaded = true;
                    Log.e("Loaded", "User " + login + " был загружен");
                    Log.e("Loaded", "admin = " + admin);
                    if(first_loaded == false) {
                        Intent broadcast = new Intent(USER_CREATED_ACTION);
                        broadcast.putExtra("id", id);
                        context.sendBroadcast(broadcast);
                        first_loaded = true;
                    }
                    break;
                }
            }
            catch (Exception e){
                Log.e("Error", "Ошибка загрузки БД ", e);
            }
            return;
        }
    }

    public int describeContents() {
        return 0;
    }

    // упаковываем объект в Parcel
    public void writeToParcel(Parcel parcel, int flags) {
        if(loaded == false) {
            Log.e("Parcel", "Ошибка упаковка Parcel. User not loaded");
            return;
        }
        Log.e("Parcel", "Parcel упакован");
        parcel.writeInt(id);
        parcel.writeString(login);
        parcel.writeString(password);
        parcel.writeString(surname);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeInt(score);
        parcel.writeInt(status);
        parcel.writeInt(ad_amount);
        parcel.writeString(regdata);
        parcel.writeString(token);
        parcel.writeString(phone);
        parcel.writeString(city);
        parcel.writeInt(age);
        parcel.writeInt(lost_msg);
        parcel.writeInt(admin);
        parcel.writeString(msg_id);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        // распаковываем объект из Parcel
        public User createFromParcel(Parcel in) {
            Log.d("Parcel", "createFromParcel");
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // конструктор, считывающий данные из Parcel
    private User(Parcel parcel) {
        if(loaded == true) {
            Log.e("Parcel", "Ошибка загрузки из Parcel. User has been loaded");
            return;
        }
        id = parcel.readInt();
        login = parcel.readString();
        password = parcel.readString();
        surname = parcel.readString();
        name = parcel.readString();
        email = parcel.readString();
        score = parcel.readInt();
        status = parcel.readInt();
        ad_amount = parcel.readInt();
        regdata = parcel.readString();
        token = parcel.readString();
        phone = parcel.readString();
        city = parcel.readString();
        age = parcel.readInt();
        lost_msg = parcel.readInt();
        admin = parcel.readInt();
        msg_id = parcel.readString();
        Log.e("Parcel", "User " + login + "был распакован из Parcel");
        loaded = true;
    }

    boolean update(String[] row, Object[] value){
        if(row.length != value.length) return false;
        int l = row.length;
        update_sql = "UPDATE `users` SET ";
        for(int i = 0; i != l; i++){
            if(i == (l - 1)){
                String buffer_sql = "`" + row[i] + "` = '" + value[i] + "' ";
                update_sql += buffer_sql;
                break;
            }
            String buffer_sql = "`" + row[i] + "` = '" + value[i] + "', ";
            update_sql += buffer_sql;
        }
        update_sql += "WHERE `id` = '" + id + "'";

        if(upd != null && upd.isAlive() == true) return false;

        upd = new UpdateUserInfo();
        upd.start();
        return true;
    }

    class UpdateUserInfo extends Thread{

        public void start() { run(); }

        public void run(){
            Log.w("QUERY - UPDATE", update_sql);
            try{
                connection = null;
                statement = null;
                rs = null;
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + "triniti.ru-hoster.com/iborlZer?characterEncoding=utf8", "iborlZer",
                        "22599226a");
                statement = connection.createStatement();
                statement.executeUpdate(update_sql);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
