/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.uniza.fri.duracik2.exportnySystem;

/**
 *
 * @author Unlink
 */
public class Statistika {
    private String aEan;
    private int aCount;
    private int aSum;

    public Statistika(String aEan) {
        this.aEan = aEan;
        this.aCount = 0;
        this.aSum = 0;
    }

    public String getEan() {
        return aEan;
    }

    public int getCount() {
        return aCount;
    }

    public int getSum() {
        return aSum;
    }
 
    public void addTovar(Tovar paTovar) {
        aCount++;
        aSum+=paTovar.getCena();
    } 
    
}
