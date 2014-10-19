/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.uniza.fri.duracik2.exportnySystem;

import sk.uniza.fri.duracik2.io.EObjectType;
import sk.uniza.fri.duracik2.io.IToCSV;
import sk.uniza.fri.duracik2.io.Importer;
import sk.uniza.fri.duracik2.tree.TreeIndexer;

/**
 *
 * @author Unlink
 */
public class Odberatel extends AMiesto implements IToCSV {
    public static final TreeIndexer<Odberatel> INDEXER = new TreeIndexer<Odberatel>() {
            @Override
            public int compare(Odberatel e1, Object... params) {
                String id = "0";
                if (params[0] instanceof Odberatel)
                    id = ((Odberatel)params[0]).getId();
                else if (params[0] instanceof String)
                    id = (String) params[0];
                else 
                    return 0;
                return id.compareTo(e1.getId());
            }
        };

    public static String getKey(String[] paAtrrs) {
        return paAtrrs[0];
    }
    
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

    @Override
    public EObjectType getTyp() {
        return EObjectType.ODBERATEL;
    }

    @Override
    public String getObjectKey() {
        return "o_"+getId();
    }

    @Override
    public void fromCSV(Importer paImporter, String[] paAtrrs) {
        aSklad = (paAtrrs[1].isEmpty()) ? null : paImporter.getVelkosklad(Integer.parseInt(paAtrrs[1].substring(2)));
        aNazov = paAtrrs[2];
        aAdresa = paAtrrs[3];
    }

    @Override
    public String toString() {
        return "Odberatel{" + "aId=" + aId + ", aSklad=" + aSklad + '}';
    } 
    
}
