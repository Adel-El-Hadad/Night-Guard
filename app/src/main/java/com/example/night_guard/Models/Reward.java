package com.example.night_guard.Models;

public class Reward {
   private String imageUrl;
    private int levelNumber;
   private RewardState rewardState;

    public Reward() {
    }

    public Reward(String imageUrl, int levelNumber) {
        this.imageUrl = imageUrl;
        this.levelNumber = levelNumber;
        this.rewardState = RewardState.NOT_ADDED;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public RewardState getRewardState() {
        return rewardState;
    }

    public void setRewardState(RewardState rewardState) {
        this.rewardState = rewardState;
    }
}
