package org.jeonfeel.withlol2.DTO;

import androidx.annotation.Keep;

public class User {

    public String email;
    public String summonerName;
    public String tier;
    public String rank;
    public String token;
    public int wins;
    public int losses;
    public int notification;

    @Keep
    User(){}

    public User(String email, String summonerName, String tier, String rank,String token, int wins, int losses,int notification) {

        this.email = email;
        this.summonerName = summonerName;
        this.tier = tier;
        this.rank = rank;
        this.token = token;
        this.wins = wins;
        this.losses = losses;
        this.notification = notification;
    }
}
