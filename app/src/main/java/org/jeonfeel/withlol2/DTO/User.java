package org.jeonfeel.withlol2.DTO;

import androidx.annotation.Keep;

public class User {

    public String email;
    public String summonerName;
    public String tier;
    public String rank;
    public String token;
    public int leaguePoint;
    public int notification;
    public int summonerLevel;

    @Keep
    User(){}

    public User(String email, String summonerName, String tier, String rank,String token, int leaguePoint, int notification,int summonerLevel) {

        this.email = email;
        this.summonerName = summonerName;
        this.tier = tier;
        this.rank = rank;
        this.token = token;
        this.leaguePoint = leaguePoint;
        this.notification = notification;
        this.summonerLevel = summonerLevel;
    }
}
