/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.exportnySystem;

import java.util.Date;

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
    public String toCSV() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
