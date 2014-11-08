/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.exportnySystem;

import java.awt.Color;
import java.text.SimpleDateFormat;
import sk.uniza.fri.duracik2.io.IToCSV;
import java.util.Date;
import sk.uniza.fri.duracik2.gui.IGuiPrint;
import sk.uniza.fri.duracik2.gui.JColorTextPane;
import sk.uniza.fri.duracik2.gui.reflection.FunkcnyKonstruktor;
import sk.uniza.fri.duracik2.io.EObjectType;
import sk.uniza.fri.duracik2.io.Importer;
import sk.uniza.fri.duracik2.tree.TreeIndexer;

/**
 *
 * @author Unlink
 */
public class Tovar implements Comparable<Tovar>, IToCSV, IGuiPrint {

	public static final TreeIndexer<Tovar> INDEXER = new TreeIndexer<Tovar>() {
		@Override
		public int compare(Tovar e1, Object... params) {
			if (params[0] instanceof Tovar) {
				Tovar t = (Tovar) params[0];
				return (e1.getVyrobneCislo() == t.getVyrobneCislo()) ? 0 : (e1.getVyrobneCislo() > t.getVyrobneCislo()) ? -1 : 1;
			}
			else if (params[0] instanceof Long) {
				long l = (long) params[0];
				return (e1.getVyrobneCislo() == l) ? 0 : (e1.getVyrobneCislo() > l) ? -1 : 1;
			}
			else {
				return 0;
			}
		}
	};

	public static Long getKey(String[] paAtrrs) {
		return Long.parseLong(paAtrrs[0]);
	}

	private long aVyrobneCislo;
	private String aEanKod;
	private Date aDatumVyroby;
	private Date aDatumSpotreby;
	private int aCena;

	private AMiesto aAktualnaLokacia;
	private Expedicia aPosExpZaznam;

	@FunkcnyKonstruktor(parametre = {"Výrobné číslo", "Ean kód", "Dátum výroby", "Dátum spotreby", "Cena"})
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
		return "Tovar{" + "aVyrobneCislo=" + aVyrobneCislo + ", aEanKod=" + aEanKod + ", aAktualnaLokacia=" + (aAktualnaLokacia != null ? aAktualnaLokacia.getNazov() : "null") + ", aPosExpZaznam=" + ((aPosExpZaznam == null) ? "null" : aPosExpZaznam.getId()) + '}';
	}

	@Override
	public EObjectType getTyp() {
		return EObjectType.TOVAR;
	}

	@Override
	public String getObjectKey() {
		return "" + getVyrobneCislo();
	}

	@Override
	public void fromCSV(Importer paImporter, String[] paAtrrs) {

		aEanKod = paAtrrs[1];
		aDatumVyroby = new Date(Long.parseLong(paAtrrs[2]));
		aDatumSpotreby = new Date(Long.parseLong(paAtrrs[3]));
		aCena = Integer.parseInt(paAtrrs[4]);
		aAktualnaLokacia = (paAtrrs[5].isEmpty())
			? null : (paAtrrs[5].startsWith("o_"))
				? paImporter.getOrberatel(paAtrrs[5].substring(2))
				: paImporter.getVelkosklad(Integer.parseInt(paAtrrs[5].substring(2)));
		aPosExpZaznam = (paAtrrs.length == 7 && !paAtrrs[6].isEmpty()) ? paImporter.getExpedicia(Long.parseLong(paAtrrs[6])) : null;

	}

	@Override
	public void print(JColorTextPane pane) {
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		pane.append("Tovar");
		pane.append("\n Ean: ");
		pane.append(Color.BLUE, aEanKod);
		pane.append("\n Cena: ");
		pane.append(Color.BLUE, aCena + "");
		pane.append("\n Vyrobny kod: ");
		pane.append(Color.BLUE, aVyrobneCislo + "");
		pane.append("\n Lokácia: ");
		if (aAktualnaLokacia == null) {
			pane.append(Color.RED, "N/A");
		}
		else {
			aAktualnaLokacia.print(pane);
		}
		pane.append("\n Dátum výroby/spotreby: ");
		pane.append(Color.BLUE, format.format(aDatumVyroby) + "/" + format.format(aDatumSpotreby));
		if (aPosExpZaznam != null && aAktualnaLokacia == aPosExpZaznam.getZdroj()) {
			pane.append(Color.GREEN.darker(), "\n Tovar je expedovaný: ");
			pane.append(Color.BLACK, "\n  EVC prepravcu: ");
			pane.append(Color.BLUE, aPosExpZaznam.getEvcPrepravcu());
			pane.append(Color.BLACK, "\n  Ciel: ");
			if (aPosExpZaznam.getCiel() instanceof Odberatel) {
				pane.append(Color.GREEN.darker().darker(), aPosExpZaznam.getCiel().getNazov());
				pane.append(Color.GRAY, " (" + ((Odberatel) aPosExpZaznam.getCiel()).getId() + ")");
			}
			else {
				pane.append(Color.BLUE, aPosExpZaznam.getCiel().getNazov());
				pane.append(Color.GRAY, " (" + ((Velkosklad) aPosExpZaznam.getCiel()).getId() + ")");
			}

			//64
		}
	}

}
