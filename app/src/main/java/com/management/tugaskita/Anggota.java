package com.management.tugaskita;

public class Anggota {
    String id, image, image_alternative, header, paragraph, username, story;

    public Anggota(String id, String image, String image_alternative, String header, String paragraph, String username, String story) {
        this.id = id;
        this.image = image;
        this.image_alternative = image_alternative;
        this.header = header;
        this.paragraph = paragraph;
        this.username = username;
        this.story = story;
    }
    public String getId() {
        return this.id;
    }

    public String getImage() {
        return this.image;
    }

    public String getImageAlternative() {
        return this.image_alternative;
    }

    public String getHeader() {
        return this.header;
    }

    public String getParagraph() {
        return this.paragraph;
    }

    public String getUsername() {
        return this.username;
    }

    public String getStory() {
        return this.story;
    }
}
