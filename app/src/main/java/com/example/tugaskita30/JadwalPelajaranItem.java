package com.example.tugaskita30;

public class JadwalPelajaranItem {
    String academic;

    int image;
    String agama = "agama", ppkn = "ppkn", bahasa_indonesia = "bahasa_indonesia", matematika_peminatan = "matematika_peminatan", matematika_wajib = "matematika_wajib", fisika = "fisika", kimia = "kimia", biologi = "biologi", geografi = "geografi", sejarah = "sejarah", sosiologi = "sosiologi", seni_budaya = "seni_budaya", penjaskes = "penjaskes", bahasa_mandarin = "bahasa_mandarin", bahasa_inggris = "bahasa_inggris", lintas_minat = "lintas_minat", komputer = "komputer", lainnya = "lainnya", wirausaha = "wirausaha", mulok = "mulok";

    public JadwalPelajaranItem(String academic) {
        if (academic.equals(agama))
            image = R.drawable.agama_icon;
        else if (academic.equals(ppkn))
            image = R.drawable.ppkn_icon;
        else if (academic.equals(bahasa_indonesia))
            image = R.drawable.bahasa_indonesia_icon;
        else if (academic.equals(matematika_peminatan))
            image = R.drawable.matematika_peminatan_icon;
        else if (academic.equals(matematika_wajib))
            image = R.drawable.matematika_wajib_icon;
        else if (academic.equals(fisika))
            image = R.drawable.fisika_icon;
        else if (academic.equals(kimia))
            image = R.drawable.kimia_icon;
        else if (academic.equals(biologi))
            image = R.drawable.biologi_icon;
        else if (academic.equals(geografi))
            image = R.drawable.geografi_icon;
        else if (academic.equals(sejarah))
            image = R.drawable.sejarah_icon;
        else if (academic.equals(sosiologi))
            image = R.drawable.sosiologi_icon;
        else if (academic.equals(seni_budaya))
            image = R.drawable.seni_budaya_icon;
        else if (academic.equals(penjaskes))
            image = R.drawable.penjaskes_icon;
        else if (academic.equals(bahasa_mandarin))
            image = R.drawable.bahasa_mandarin_icon;
        else if (academic.equals(bahasa_inggris))
            image = R.drawable.bahasa_inggris_icon;
        else if (academic.equals(lintas_minat))
            image = R.drawable.lintas_minat_icon;
        else if (academic.equals(komputer))
            image = R.drawable.komputer_icon;
        else if (academic.equals(wirausaha))
            image = R.drawable.wirausaha_icon;
        else if (academic.equals(mulok))
            image = R.drawable.mulok_icon;
        else if (academic.equals(lainnya))
            image = R.drawable.lainnya_icon;

        if (academic.equals("lintas_minat"))
            academic = "lintas minat";
        else if (academic.equals("seni_budaya"))
            academic = "seni budaya";
        else if (academic.equals("matematika_peminatan"))
            academic = "matematika peminatan";
        else if (academic.equals("bahasa_indonesia"))
            academic = "bahasa indonesia";
        else if (academic.equals("matematika_wajib"))
            academic = "matematika wajib";
        else if (academic.equals("bahasa_inggris"))
            academic = "bahasa inggris";

        this.academic = academic;
    }

    public String getAcademic() { return this.academic; }

    public int getLogo() { return this.image; }
}
