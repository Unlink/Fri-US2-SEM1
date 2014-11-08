/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.uniza.fri.duracik2.exportnySystem;

import java.awt.Color;
import sk.uniza.fri.duracik2.io.IToCSV;
import java.util.Date;
import sk.uniza.fri.duracik2.gui.IGuiPrint;
import sk.uniza.fri.duracik2.gui.IGuiPrintListHead;
import sk.uniza.fri.duracik2.gui.JColorTextPane;
import sk.uniza.fri.duracik2.io.EObjectType;
import sk.uniza.fri.duracik2.io.ExportUtil;
import sk.uniza.fri.duracik2.io.Importer;
import sk.uniza.fri.duracik2.tree.TreeIndexer;

/**
 *
 * @author Unlink
 */
public class Expedicia implements Comparable<Expedicia>, IToCSV, IGuiPrint, IGuiPrintListHead {
    public static final TreeIndexer<Expedicia> INDEXER = new TreeIndexer<Expedicia>() {
        @Override
        public int compare(Expedicia e1, Object... params) {
            if (params[0] instanceof Expedicia) {
                Expedicia t = (Expedicia) params[0];
                return (e1.getId()== t.getId()) ? 0 : (e1.getId()> t.getId()) ? -1 : 1;
            }
            else if (params[0] instanceof Long) {
                long l = (long) params[0];
                return (e1.getId()== l) ? 0 : (e1.getId()> l) ? -1 : 1;
            }
            else {
                return 0;
            }
        }
    };
    private static long aLastId = 1;

    public static Long getKey(String[] paAtrrs) {
        return Long.parseLong(paAtrrs[0]);
    }
    
    //Auto generované skrité ID
    private final long aId;
    private Tovar aTovar;
    private String aEvcPrepravcu;
    private Date aDatZaciatku;
    private Date aDatKonca;
    private Expedicia aPredchadzajuca;
    private AMiesto aZdroj;
    private AMiesto aCiel;

    public Expedicia(long paId) {
        this.aId = paId;
        aLastId = Math.max(aLastId, aId+1);
    }
    
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

    @Override
    public EObjectType getTyp() {
        return EObjectType.EXPEDICIA;
    }

    @Override
    public String getObjectKey() {
        return ""+getId();
    }

    @Override
    public void fromCSV(Importer paImporter, String[] paAtrrs) {
        aTovar = paImporter.getTovar(Long.parseLong(paAtrrs[1]));
        aEvcPrepravcu = paAtrrs[2];
        aDatZaciatku = ExportUtil.getInstance().convertDate(paAtrrs[3]);
        aDatKonca = ExportUtil.getInstance().convertDate(paAtrrs[4]);
        aPredchadzajuca = (paAtrrs[5].isEmpty()) ? null : paImporter.getExpedicia(Long.parseLong(paAtrrs[5]));
        aZdroj = paImporter.getVelkosklad(Integer.parseInt(paAtrrs[6].substring(2)));
        aCiel = (paAtrrs[7].isEmpty())
            ? null : (paAtrrs[7].startsWith("o_"))
                ? paImporter.getOrberatel(paAtrrs[7].substring(2)) : 
                    paImporter.getVelkosklad(Integer.parseInt(paAtrrs[7].substring(2)));
    }

    @Override
    public String toString() {
        return "Expedicia{" + "aId=" + aId + ", aTovar=" + aTovar.getVyrobneCislo() + ", aEvcPrepravcu=" + aEvcPrepravcu + ", aPredchadzajuca=" + (aPredchadzajuca  == null ? "null" : aPredchadzajuca.getId()) + ", aZdroj=" + aZdroj.getNazov() + ", aCiel=" + aCiel.getNazov() + '}';
    }

	@Override
	public void print(JColorTextPane pane) {
		pane.append(aTovar.getEanKod()+"\t");
		pane.append(aTovar.getVyrobneCislo()+"\t");
		aZdroj.print(pane);
		pane.append(" \t");
		pane.append(aEvcPrepravcu);
	}

	@Override
	public void printListHead(JColorTextPane pane) {
		pane.append("Ean\t");
		pane.append("ID\t");
		pane.append("Zdroj\t\t");
		pane.append("Prepravca");
		pane.append("\n==================================================");
	}
    
}
