package com.management.tugaskita;

public class UndanganMasuk {
    String invite_from, userid, invite_to_grup, grupid, invite_from_imageURL, invite_from_imageURL_alternative, invite_to_grup_imageURL, invite_asstatus;

    public UndanganMasuk(String invite_from, String userid, String invite_to_grup, String grupid, String invite_from_imageURL, String invite_from_imageURL_alternative, String invite_to_grup_imageURL, String invite_asstatus) {
        this.invite_from = invite_from;
        this.userid = userid;
        this.invite_to_grup = invite_to_grup;
        this.grupid = grupid;
        this.invite_from_imageURL = invite_from_imageURL;
        this.invite_from_imageURL_alternative = invite_from_imageURL_alternative;
        this.invite_to_grup_imageURL = invite_to_grup_imageURL;
        this.invite_asstatus = invite_asstatus;
    }

    public String getInviteFrom() { return this.invite_from; }

    public String getUserId() { return this.userid; }

    public String getInviteToGrup() { return this.invite_to_grup; }

    public String getGrupId() { return this.grupid; }

    public String getInviteFromImageURL() { return this.invite_from_imageURL; }

    public String getInviteFromImageURLAlternative() { return this.invite_from_imageURL_alternative; }

    public String getInviteToGrupImageURL() { return this.invite_to_grup_imageURL; }

    public String getInviteAsstatus() { return this.invite_asstatus; }
}
