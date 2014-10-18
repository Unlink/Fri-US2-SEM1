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
public class Odberatel extends AMiesto implements IToCSV {
    private String aId;
    private Velkosklad aSklad;

    public Odberatel(String aId, Velkosklad aSklad, String aNazov, String aAdresa) {
        super(aNazov, aAdresa);
        this.aId = aId;
        this.aSklad = aSklad;
    }

    // <editor-fold defaultstate="collapsed" desc="Gettery a settery">
    public String getId() {
        return aId;
    }

    public Velkosklad getSklad() {
        return aSklad;
    }

    public void setSklad(Velkosklad sklad) {
        this.aSklad = sklad;
    }
    // </editor-fold>

    @Override
    public int compareTo(AMiesto o) {
        return ((Odberatel) o).aId.compareTo(aId);
    }

    @Override
    public Object[] toCsvData() {
        return new Object[]{aId, aSklad, aNazov, aAdresa};
    }
    
}
