/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.exportnySystem;

import sk.uniza.fri.duracik2.io.IToCSV;
import java.util.Date;
import sk.uniza.fri.duracik2.io.EObjectType;

/**
 *
 * @author Unlink
 */
public class Tovar implements Comparable<Tovar>, IToCSV {
    private long aVyrobneCislo;
    private String aEanKod;
    private Date aDatumVyroby;
    private Date aDatumSpotreby;
    private int aCena;

    private AMiesto aAktualnaLokacia;
    private Expedicia aPosExpZaznam;

    public Tovar(long paVyrobneCislo, String paEanKod, Date paDatumVyroby, Date paDatumSpotreby, int paCena) {
        this.aVyrobneCislo = paVyrobneCislo;
        this.aEanKod = paEanKod;
        this.aDatumVyroby = paDatumVyroby;
        this.aDatumSpotreby = paDatumSpotreby;
        this.aCena = paCena;
    }

    // <editor-fold defaultstate="collapsed" desc="Gettery a settery">
    public long getVyrobneCislo() {
        return aVyrobneCislo;
    }

    public String getEanKod() {
        return aEanKod;
    }

    public Date getDatumVyroby() {
        return aDatumVyroby;
    }

    public Date getDatumSpotreby() {
        return aDatumSpotreby;
    }

    public int getCena() {
        return aCena;
    }

    public AMiesto getAktualnaLokacia() {
        return aAktualnaLokacia;
    }

    public void setAktualnaLokacia(AMiesto aktualnaLokacia) {
        this.aAktualnaLokacia = aktualnaLokacia;
    }

    public Expedicia getPosExpZaznam() {
        return aPosExpZaznam;
    }

    public void setPosExpZaznam(Expedicia posExpZaznam) {
        this.aPosExpZaznam = posExpZaznam;
    }
    // </editor-fold>

    @Override
    public int compareTo(Tovar o) {
        return (aVyrobneCislo == o.aVyrobneCislo) ? 0 : (aVyrobneCislo > o.aVyrobneCislo) ? 1 : -1;
    }

    @Override
    public Object[] toCsvData() {
        return new Object[]{aVyrobneCislo, aEanKod, aDatumVyroby, aDatumSpotreby, aCena, aAktualnaLokacia, aPosExpZaznam};
    }

    @Override
    public String toString() {
        return ""+aVyrobneCislo;
    }

    @Override
    public EObjectType getTyp() {
        return EObjectType.TOVAR;
    }

    @Override
    public String getObjectKey() {
        return ""+getVyrobneCislo();
    }

}
