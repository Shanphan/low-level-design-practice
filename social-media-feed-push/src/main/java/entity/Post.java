package entity;

import service.IdGenerator;

import java.time.LocalDateTime;

public class Post {

    private String id;
    private String content;
    private String postedBy;
    private LocalDateTime postedTime;

    public Post(String content, String postedBy, LocalDateTime postedTime) {
        this.id = IdGenerator.createId("POST");
        this.content = content;
        this.postedBy = postedBy;
        this.postedTime = postedTime;
    }

    public String getId() {
        return id;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public LocalDateTime getPostedTime() {
        return postedTime;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "POST [id "+id+ " content "+content+ " postedBy "+postedBy+ " postedTime "+ postedTime.toString() + "]";
    }
}
