package com.management.tugaskita;

public class Riwayat {
    String id, academic, header, header_preview, paragraph, paragraph_preview, deadline, deadline_preview, prioritas;

    int image;
    String agama = "agama", ppkn = "ppkn", bahasa_indonesia = "bahasa_indonesia", matematika_peminatan = "matematika_peminatan", matematika_wajib = "matematika_wajib", fisika = "fisika", kimia = "kimia", biologi = "biologi", geografi = "geografi", sejarah = "sejarah", sosiologi = "sosiologi", seni_budaya = "seni_budaya", penjaskes = "penjaskes", bahasa_mandarin = "bahasa_mandarin", bahasa_inggris = "bahasa_inggris", lintas_minat = "lintas_minat", komputer = "komputer", lainnya = "lainnya";

    public Riwayat(String id, String academic, String header, String header_preview, String paragraph, String paragraph_preview, String deadline, String deadline_preview, String prioritas){
        if (academic.equals(agama))
            image = R.drawable.agama;
        else if (academic.equals(ppkn))
            image = R.drawable.ppkn;
        else if (academic.equals(bahasa_indonesia))
            image = R.drawable.bahasa_indonesia;
        else if (academic.equals(matematika_peminatan))
            image = R.drawable.matematika_peminatan;
        else if (academic.equals(matematika_wajib))
            image = R.drawable.matematika_wajib;
        else if (academic.equals(fisika))
            image = R.drawable.fisika;
        else if (academic.equals(kimia))
            image = R.drawable.kimia;
        else if (academic.equals(biologi))
            image = R.drawable.biologi;
        else if (academic.equals(geografi))
            image = R.drawable.geografi;
        else if (academic.equals(sejarah))
            image = R.drawable.sejarah;
        else if (academic.equals(sosiologi))
            image = R.drawable.sosiologi;
        else if (academic.equals(seni_budaya))
            image = R.drawable.seni_budaya;
        else if (academic.equals(penjaskes))
            image = R.drawable.penjaskes;
        else if (academic.equals(bahasa_mandarin))
            image = R.drawable.bahasa_mandarin;
        else if (academic.equals(bahasa_inggris))
            image = R.drawable.bahasa_inggris;
        else if (academic.equals(lintas_minat))
            image = R.drawable.lintas_minat;
        else if (academic.equals(komputer))
            image = R.drawable.komputer;
        else if (academic.equals(lainnya))
            image = R.drawable.lainnya;

        this.id = id;
        this.academic = academic;
        this.header = header;
        this.header_preview = header_preview;
        this.deadline = deadline;
        this.deadline_preview = deadline_preview;
        this.prioritas = prioritas;
        this.paragraph = paragraph;
        this.paragraph_preview = paragraph_preview;
    }

    public int getImage() {
        return this.image;
    }

    public String getId() {
        return this.id;
    }

    public String getAcademic() {
        return this.academic;
    }

    public String getHeader() {
        return this.header;
    }

    public String getHeaderPreview() {
        return this.header_preview;
    }

    public String getParagraph() {
        return this.paragraph;
    }

    public String getParagraphPreview() {
        return this.paragraph_preview;
    }

    public String getDeadline() {
        return this.deadline;
    }

    public String getDeadlinePreview() {
        return this.deadline_preview;
    }

    public String getPrioritas() {
        return this.prioritas;
    }


}
