package tukano.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ShortLikes {

    @Id
    private String shortId;

    @Id
    private String userId;

    // Constructors, getters, and setters
    public ShortLikes() {
    }

    public ShortLikes(String shortId, String userId) {
        this.shortId = shortId;
        this.userId = userId;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getUserId() {
        return userId;
    }

    public void setFollowed(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Like{" +
                "shortId='" + shortId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}