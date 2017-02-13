package com.wsns.lor.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/7.
 */

public class Sellers implements Serializable{
    String account;
    String title;
    String avatar;
    Double star;
    int turnover;
    String repairsTypes;
    String city;
    int tradeTypes;
    int minimums;
    int distance;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getStar() {
        return star;
    }

    public void setStar(Double star) {
        this.star = star;
    }


    public String getRepairsTypes() {
        return repairsTypes;
    }

    public void setRepairsTypes(String repairsTypes) {
        this.repairsTypes = repairsTypes;
    }

    public int getTurnover() {
        return turnover;
    }

    public void setTurnover(int turnover) {
        this.turnover = turnover;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getTradeTypes() {
        return tradeTypes;
    }

    public void setTradeTypes(int tradeTypes) {
        this.tradeTypes = tradeTypes;
    }

    public int getMinimums() {
        return minimums;
    }

    public void setMinimums(int minimums) {
        this.minimums = minimums;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
