package com.management.tugaskita;

public class SecretaryAbsent {
    String id, image, image_alternative,  namalengkap, username, story;

    public SecretaryAbsent(String id, String image, String image_alternative, String namalengkap, String username, String story) {
        this.id = id;
        this.image = image;
        this.image_alternative = image_alternative;
        this.namalengkap = namalengkap;
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

    public String getNamaLengkap() {
        return this.namalengkap;
    }

    public String getUsername() {
        return this.username;
    }

    public String getStory() {
        return this.story;
    }
}
