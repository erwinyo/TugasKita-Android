package com.management.tugaskita;

public class UndangTeman {
    String id, image, image_alternative, header, paragraph;

    public UndangTeman(String id, String image, String image_alternative, String header, String paragraph){
        this.id = id;
        this.image = image;
        this.image_alternative = image_alternative;
        this.header = header;
        this.paragraph = paragraph;
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

}
