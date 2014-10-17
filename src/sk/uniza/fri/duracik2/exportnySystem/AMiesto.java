/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.uniza.fri.duracik2.exportnySystem;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import sk.uniza.fri.duracik2.tree.RBTree;

/**
 *
 * @author Unlink
 */
public abstract class AMiesto implements Comparable<AMiesto> {
    protected String aNazov;
    protected String aAdresa;
    protected List<Expedicia> aZoznamPrijatychTovatov;

    public AMiesto(String aNazov, String aAdresa) {
        this.aNazov = aNazov;
        this.aAdresa = aAdresa;
        aZoznamPrijatychTovatov = new LinkedList<>();
    }

    
    // <editor-fold defaultstate="collapsed" desc="Gettery a settery">
    public String getNazov() {
        return aNazov;
    }

    public String getAdresa() {
        return aAdresa;
    }
    // </editor-fold>

    public boolean naskladniTovar(Expedicia paExpedicia) {
        aZoznamPrijatychTovatov.add(paExpedicia);
        paExpedicia.getTovar().setAktualnaLokacia(this);
        return true;
    }
    
    public Iterator<Expedicia> dajDodavky() {
        return aZoznamPrijatychTovatov.iterator();
    }
    
}
