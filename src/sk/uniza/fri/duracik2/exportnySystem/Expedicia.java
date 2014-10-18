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
public class Expedicia implements Comparable<Expedicia>, IToCSV {

    private static long aLastId = 1;
    
    //Auto generované skrité ID
    private final long aId;
    private Tovar aTovar;
    private final String aEvcPrepravcu;
    private final Date aDatZaciatku;
    private final Date aDatKonca;
    private final Expedicia aPredchadzajuca;
    private AMiesto aZdroj;
    private AMiesto aCiel;

    public Expedicia(Tovar aTovar, String aEvcPrepravcu, Date aDatZaciatku, Date aDatKonca, Expedicia aPredchadzajuca, AMiesto aZdroj, AMiesto aCiel) {
        this.aTovar = aTovar;
        this.aEvcPrepravcu = aEvcPrepravcu;
        this.aDatZaciatku = aDatZaciatku;
        this.aDatKonca = aDatKonca;
        this.aPredchadzajuca = aPredchadzajuca;
        this.aZdroj = aZdroj;
        this.aCiel = aCiel;
        aId = aLastId++;
    }

    // <editor-fold defaultstate="collapsed" desc="Gettery a settery">
    public long getId() {
        return aId;
    }

    public Tovar getTovar() {
        return aTovar;
    }

    public String getEvcPrepravcu() {
        return aEvcPrepravcu;
    }

    public Date getDatZaciatku() {
        return aDatZaciatku;
    }

    public Date getDatKonca() {
        return aDatKonca;
    }

    public Expedicia getPredchadzajuca() {
        return aPredchadzajuca;
    }

    public AMiesto getZdroj() {
        return aZdroj;
    }

    public AMiesto getCiel() {
        return aCiel;
    }

    public void setZdroj(AMiesto paZdroj) {
        this.aZdroj = paZdroj;
    }

    public void setCiel(AMiesto paCiel) {
        this.aCiel = paCiel;
    }
    
    
    // </editor-fold>
    
    @Override
    public int compareTo(Expedicia o) {
        return Long.compare(aId, o.aId);
    }

    @Override
    public Object[] toCsvData() {
        return new Object[]{aId, aTovar, aEvcPrepravcu, aDatZaciatku, aDatKonca, aPredchadzajuca, aZdroj, aCiel};
    }
    
}
