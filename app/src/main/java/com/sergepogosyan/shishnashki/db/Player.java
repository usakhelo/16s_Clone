package com.sergepogosyan.shishnashki.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Player {

    @Nullable
    Long _id;

    @NonNull
    String name;

    int score;
    int time;


    Player() {
    }

    private Player(@Nullable Long id, @NonNull String name, int score, int time) {
        this._id = id;
        this.name = name;
        this.score = score;
        this.time = time;
    }

    @NonNull
    public static Player newPlayer(@NonNull String author, int score, int time) {
        return new Player(null, author, score, time);
    }

    @Nullable
    public Long id() {
        return _id;
    }

    @NonNull
    public String player() {
        return name;
    }

    public int score() {
        return score;
    }
    public int time() { return time; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (_id != null ? !_id.equals(player._id) : player._id != null) return false;
        if (!name.equals(player.name)) return false;
        return score == score;
    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "id=" + _id +
                ", name='" + name + '\'' +
                ", score='" + score + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}