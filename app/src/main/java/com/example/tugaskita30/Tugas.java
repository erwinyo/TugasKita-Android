package com.example.tugaskita30;

public class Tugas {
    String id, academic, header, header_preview, paragraph, paragraph_preview, deadline, deadline_preview, prioritas;

    int image, logo;
    String agama = "agama", ppkn = "ppkn", bahasa_indonesia = "bahasa_indonesia", matematika_peminatan = "matematika_peminatan", matematika_wajib = "matematika_wajib", fisika = "fisika", kimia = "kimia", biologi = "biologi", geografi = "geografi", sejarah = "sejarah", sosiologi = "sosiologi", seni_budaya = "seni_budaya", penjaskes = "penjaskes", bahasa_mandarin = "bahasa_mandarin", bahasa_inggris = "bahasa_inggris", lintas_minat = "lintas_minat", komputer = "komputer", lainnya = "lainnya", wirausaha = "wirausaha", mulok = "mulok";

    public Tugas(String id, String academic, String header, String header_preview, String paragraph, String paragraph_preview, String deadline, String deadline_preview, String prioritas){
        if (academic.equals(agama)) {
            image = R.drawable.agama;
            logo = R.drawable.agama_icon;
        } else if (academic.equals(ppkn)) {
            image = R.drawable.ppkn;
            logo = R.drawable.ppkn_icon;
        } else if (academic.equals(bahasa_indonesia)) {
            image = R.drawable.bahasa_indonesia;
            logo = R.drawable.bahasa_indonesia_icon;
        } else if (academic.equals(matematika_peminatan)) {
            image = R.drawable.matematika_peminatan;
            logo = R.drawable.matematika_peminatan_icon;
        } else if (academic.equals(matematika_wajib)) {
            image = R.drawable.matematika_wajib;
            logo = R.drawable.matematika_wajib_icon;
        } else if (academic.equals(fisika)) {
            image = R.drawable.fisika;
            logo = R.drawable.fisika_icon;
        } else if (academic.equals(kimia)) {
            image = R.drawable.kimia;
            logo = R.drawable.kimia_icon;
        } else if (academic.equals(biologi)) {
            image = R.drawable.biologi;
            logo = R.drawable.biologi_icon;
        } else if (academic.equals(geografi)) {
            image = R.drawable.geografi;
            logo = R.drawable.geografi_icon;
        } else if (academic.equals(sejarah)) {
            image = R.drawable.sejarah;
            logo = R.drawable.sejarah_icon;
        } else if (academic.equals(sosiologi)) {
            image = R.drawable.sosiologi;
            logo = R.drawable.sosiologi_icon;
        } else if (academic.equals(seni_budaya)) {
            image = R.drawable.seni_budaya;
            logo = R.drawable.seni_budaya_icon;
        } else if (academic.equals(penjaskes)) {
            image = R.drawable.penjaskes;
            logo = R.drawable.penjaskes_icon;
        } else if (academic.equals(bahasa_mandarin)) {
            image = R.drawable.bahasa_mandarin;
            logo = R.drawable.bahasa_mandarin_icon;
        } else if (academic.equals(bahasa_inggris)) {
            image = R.drawable.bahasa_inggris;
            logo = R.drawable.bahasa_inggris_icon;
        } else if (academic.equals(lintas_minat)) {
            image = R.drawable.lintas_minat;
            logo = R.drawable.lintas_minat_icon;
        } else if (academic.equals(komputer)) {
            image = R.drawable.komputer;
            logo = R.drawable.komputer_icon;
        } else if (academic.equals(lainnya)) {
            image = R.drawable.lainnya;
            logo = R.drawable.lainnya_icon;
        } else if (academic.equals(wirausaha)) {
            image = R.drawable.wirausaha;
            logo = R.drawable.wirausaha_icon;
        } else if (academic.equals(mulok)) {
            image = R.drawable.mulok;
            logo = R.drawable.mulok_icon;
        }

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

    public int getLogo() {
        return this.logo;
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
