package com.example.enes.myapplication;

public class Haber {
    private int id;

    private String baslik;
    private String icerik;
    private String resim;
    private String tur;
    private String tarih;
    private int begenme;
    private int begenmeme;
    private int goruntulenme;

    Haber() {
    }

//    public Haber(int id, String baslik, String icerik, String resim, String tur) {
//        this.id = id;
//        this.baslik = baslik;
//        this.icerik = icerik;
//        this.resim = resim;
//        this.tur = tur;
//    }

    public int getId() {
        return id;
    }

    public String getBaslik() {
        return baslik;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }

    public void setTur(String tur) {
        this.tur = tur;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public void setBegenme(int begenme) {
        this.begenme = begenme;
    }

    public void setBegenmeme(int begenmeme) {
        this.begenmeme = begenmeme;
    }

    public void setGoruntulenme(int goruntulenme) {
        this.goruntulenme = goruntulenme;
    }

    public String getIcerik() {
        return icerik;
    }

    public String getResim() {
        return resim;
    }

    public String getTur() {
        return tur;
    }

    public String getTarih() {
        return tarih;
    }

    public int getBegenme() {
        return begenme;
    }

    public int getBegenmeme() {
        return begenmeme;
    }

    public int getGoruntulenme() {
        return goruntulenme;
    }
}
