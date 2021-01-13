package dto;

import annotation.*;

@Entity
@Table(name = "post")
public class PostEntity {
    @Id
    @Column(name = "id")
    private int postId;

    @Column(name = "title")
    private String postTitle;

    @ManyToOne
    @JoinColumn(name = "user_id", referenceColumnName = "user_id")
    private UserEntity user;

    public PostEntity() {
    }

    public PostEntity(String postTitle) {
        this.postTitle = postTitle;
    }

    public PostEntity(String postTitle, UserEntity user) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.user = user;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "PostEntity{" +
                "postId=" + postId +
                ", postTitle='" + postTitle + '\'' +
                ", user=" + user +
                '}';
    }
}
