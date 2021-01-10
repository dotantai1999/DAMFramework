package dto;

import annotation.Column;
import annotation.Entity;
import annotation.Id;
import annotation.Table;

@Entity
@Table(name = "post")
public class PostEntity {
    @Id
    @Column(name = "id")
    private int postId;

    @Column(name = "title")
    private String postTitle;

    public PostEntity() {
    }

    public PostEntity(String postTitle) {
        this.postTitle = postTitle;
    }

    public int getPostId() {
        return postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }
}
