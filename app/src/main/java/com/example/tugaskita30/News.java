package com.example.tugaskita30;

public class News {
    String imageUrl, header, paragraph, source;

    public News(String imageUrl, String header, String paragraph, String source) {
        this.imageUrl = imageUrl;
        this.header = header;
        this.paragraph = paragraph;
        this.source = source;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getHeader() { return this.header; }

    public String getParagraph() { return this.paragraph; }

    public String getSource() { return this.source; }
}
