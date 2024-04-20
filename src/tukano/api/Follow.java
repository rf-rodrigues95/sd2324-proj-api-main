package tukano.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Follow {

    @Id
    private String userId;

    @Id
    private String followed;

    // Constructors, getters, and setters
    public Follow() {
    }

    public Follow(String userId, String userId2) {
        this.userId = userId;
        this.followed = userId2;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId1) {
        this.userId = userId1;
    }

    public String getFollowed() {
        return followed;
    }

    public void setFollowed(String userId2) {
        this.followed = userId2;
    }

    @Override
    public String toString() {
        return "Follow{" +
                "userId='" + userId + '\'' +
                ", follows='" + followed + '\'' +
                '}';
    }
}