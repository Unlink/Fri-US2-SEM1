/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.exportnySystem;

import java.awt.Color;
import sk.uniza.fri.duracik2.io.IToCSV;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import sk.uniza.fri.duracik2.gui.JColorTextPane;
import sk.uniza.fri.duracik2.io.EObjectType;
import sk.uniza.fri.duracik2.io.Importer;
import sk.uniza.fri.duracik2.tree.RBTree;
import sk.uniza.fri.duracik2.tree.TreeIndexer;

/**
 *
 * @author Unlink
 */
public class Velkosklad extends AMiesto implements IToCSV {
	public static final TreeIndexer<Velkosklad> INDEXER = new TreeIndexer<Velkosklad>() {
		@Override
		public int compare(Velkosklad e1, Object... params) {
			if (params[0] instanceof Velkosklad) {
				Velkosklad t = (Velkosklad) params[0];
				return (e1.getId() == t.getId()) ? 0 : (e1.getId() > t.getId()) ? -1 : 1;
			}
			else if (params[0] instanceof Integer) {
				int l = (int) params[0];
				return (e1.getId() == l) ? 0 : (e1.getId() > l) ? -1 : 1;
			}
			else {
				return 0;
			}
		}
	};

	private static final TreeIndexer<Tovar> aTovarIndexerID;
	private static final TreeIndexer<Tovar> aTovarIndexerEan;
	private static final TreeIndexer<Tovar> aTovarIndexerDatum;
	private static final TreeIndexer<Odberatel> aOdberatelIndexer;
	private static final TreeIndexer<Expedicia> aExpedicieIndexer;

	public static Integer getKey(String[] paAtrrs) {
		return Integer.parseInt(paAtrrs[0]);
	}

	private int aId;
	private RBTree<Tovar> aTovaryById;
	private RBTree<Tovar> aTovaryByEan;
	private RBTree<Tovar> aTovaryByDatum;

	private RBTree<Odberatel> aOdberatelia;
	private RBTree<Expedicia> aExpedovane;

	private Velkosklad aNahrada;

	public Velkosklad(int aId, String aNazov, String aAdresa) {
		super(aNazov, aAdresa);
		this.aId = aId;
		aNahrada = null;
		aTovaryById = new RBTree<>(aTovarIndexerID);
		aTovaryByEan = new RBTree<>(aTovarIndexerEan);
		aTovaryByDatum = new RBTree<>(aTovarIndexerDatum);
		aOdberatelia = new RBTree<>(aOdberatelIndexer);
		aExpedovane = new RBTree<>(aExpedicieIndexer);
	}

	// <editor-fold defaultstate="collapsed" desc="Gettery a settery">
	public int getId() {
		return aId;
	}
	// </editor-fold>

	@Override
	public int compareTo(AMiesto o) {
		return Integer.compare(aId, ((Velkosklad) o).aId);
	}

	public List<Tovar> vyhladajPodlaEanADatumu(String paEankod, Date paDatSpotreby, int count) {
		List<Tovar> tovary = new LinkedList<>();
		Iterator<Tovar> it = aTovaryByEan.findRange(paEankod, paEankod, paDatSpotreby, null);
		int i = 0;
		while (it.hasNext() && i < count) {
			tovary.add(it.next());
			i++;
		}
		return tovary;
	}

	public int spocitajPodlaEan(String paEanKod) {
		Iterator<Tovar> it = aTovaryByEan.findAll(paEanKod);
		int i = 0;
		while (it.hasNext()) {
			it.next();
			i++;
		}
		return i;
	}

	public boolean naskladniTovar(Tovar paPaTovar) {
		aTovaryByDatum.insert(paPaTovar);
		aTovaryByEan.insert(paPaTovar);
		aTovaryById.insert(paPaTovar);
		paPaTovar.setAktualnaLokacia(this);
		return true;
	}

	@Override
	public boolean naskladniTovar(Expedicia paExpedicia) {
		super.naskladniTovar(paExpedicia);
		return naskladniTovar(paExpedicia.getTovar());
	}

	public Odberatel vyhladajOdberatela(String paIdOdberatela) {
		return aOdberatelia.findByParams(paIdOdberatela);
	}

	public boolean exportujTovar(long paIdTovaru, AMiesto ciel, Date datumPrichodu, String evcVozidla) {
		Tovar tovar = aTovaryById.findByParams(paIdTovaru);
		if (tovar == null) {
			throw new IllegalArgumentException("Tovar s ID " + paIdTovaru + " sa v sklade nenachádza");
		}
		Expedicia expZaznam = new Expedicia(tovar, evcVozidla, new Date(), datumPrichodu, tovar.getPosExpZaznam(), this, ciel);
		tovar.setPosExpZaznam(expZaznam);
		aTovaryByDatum.delete(tovar);
		aTovaryByEan.delete(tovar);
		aTovaryById.delete(tovar);

		aExpedovane.insert(expZaznam);
		return true;
	}

	public boolean exportujTovarKOdberatelovi(long paIdTovaru, String paIdOdberatela, Date paDatumPrichodu, String paEvcVozidla) {
		Odberatel odberatel = aOdberatelia.findByParams(paIdOdberatela);
		if (odberatel == null) {
			throw new IllegalArgumentException("Odberateľ s ID " + paIdOdberatela + " nieje evidovaný");
		}
		return exportujTovar(paIdTovaru, odberatel, paDatumPrichodu, paEvcVozidla);
	}

	public boolean ukonciExpediciu(Expedicia paPosExpZaznam) {
		return aExpedovane.delete(paPosExpZaznam);
	}

	public Iterator<Odberatel> dajOdberatelov() {
		return aOdberatelia.inOrderIterator();
	}

	public Iterator<Expedicia> dajExpedicie() {
		return aExpedovane.inOrderIterator();
	}

	/**
	 * @TODO +5 dní možno dorobiť
	 * @param paDatSpotreby
	 * @return
	 */
	public List<Tovar> vyhladajPodlaDatumuSpotreby(Date paDatSpotreby) {
		List<Tovar> tovary = new LinkedList<>();
		Iterator<Tovar> it = aTovaryByDatum.findRange(null, paDatSpotreby);
		while (it.hasNext()) {
			tovary.add(it.next());
		}
		return tovary;
	}

	public boolean priradOdberatela(Odberatel paOdberatel) {
		paOdberatel.setSklad(this);
		return aOdberatelia.insert(paOdberatel);
	}

	public boolean vyradTovar(Tovar paTovar) {
		if (paTovar.getPosExpZaznam() != null && aExpedovane.contains(paTovar.getPosExpZaznam())) {
			aExpedovane.delete(paTovar.getPosExpZaznam());
			paTovar.setPosExpZaznam(paTovar.getPosExpZaznam().getPredchadzajuca());
		}
		else {
			aTovaryByDatum.delete(paTovar);
			aTovaryByEan.delete(paTovar);
			aTovaryById.delete(paTovar);
		}
		paTovar.setAktualnaLokacia(null);
		return true;
	}

	public void zrusSklad(Velkosklad paSklad2) {
		if (aExpedovane.size() > 0) {
			throw new IllegalArgumentException("Zo skladu sú stále expedované tovary!");
		}
		aNahrada = paSklad2;
		for (Tovar tovar : aTovaryById) {
			paSklad2.naskladniTovar(tovar);
		}
		for (Odberatel odberatel : aOdberatelia) {
			paSklad2.priradOdberatela(odberatel);
		}
		aTovaryByDatum.clear();
		aTovaryByEan.clear();
		aTovaryById.clear();
		aExpedovane.clear();
	}

	public boolean zrusOdberatela(String paIdOdberatela) {
		Odberatel odberatel = vyhladajOdberatela(paIdOdberatela);
		if (odberatel == null) {
			throw new IllegalArgumentException("Odberateľ s ID " + paIdOdberatela + " nieje evidovaný");
		}
		for (Expedicia expedicia : aExpedovane) {
			if (expedicia.getCiel() == odberatel) {
				throw new IllegalArgumentException("K oberateľovi sú stále expedované tovary!");
			}
		}
		odberatel.setSklad(null);
		return aOdberatelia.delete(odberatel);
	}

	public Iterator<Tovar> dajEanIteratorTovarov() {
		return aTovaryByEan.inOrderIterator();
	}

	static {
		aTovarIndexerID = Tovar.INDEXER;
		aTovarIndexerEan = new TreeIndexer<Tovar>() {
			@Override
			public int compare(Tovar e1, Object... params) {
				int cmp1 = 0;
				int cmp2 = 0;
				int cmp3 = 0;
				if (params[0] instanceof Tovar) {
					Tovar e2 = (Tovar) params[0];
					cmp1 = e2.getEanKod().compareTo(e1.getEanKod());
					cmp2 = e2.getDatumSpotreby().compareTo(e1.getDatumSpotreby());
					cmp3 = Long.compare(e2.getVyrobneCislo(), e1.getVyrobneCislo());
				}
				else {
					if (params[0] instanceof String) {
						cmp1 = (-1) * e1.getEanKod().compareTo((String) params[0]);
					}
					if (params.length > 1 && params[1] instanceof Date) {
						cmp2 = (-1) * e1.getDatumSpotreby().compareTo((Date) params[1]);
					}
					if (params.length > 2 && params[2] instanceof Number) {
						cmp3 = Long.compare((long) params[2], e1.getVyrobneCislo());
					}
				}

				if (cmp1 == 0) {
					if (cmp2 == 0) {
						return cmp3;
					}
					else {
						return cmp2;
					}
				}
				else {
					return cmp1;
				}
			}
		};
		aTovarIndexerDatum = new TreeIndexer<Tovar>() {
			@Override
			public int compare(Tovar e1, Object... params) {
				int cmp1 = 0;
				int cmp2 = 0;
				if (params[0] instanceof Tovar) {
					Tovar e2 = (Tovar) params[0];
					cmp1 = e2.getDatumSpotreby().compareTo(e1.getDatumSpotreby());
					cmp2 = Long.compare(e2.getVyrobneCislo(), e1.getVyrobneCislo());
				}
				else {
					if (params[0] instanceof Date) {
						cmp1 = (-1) * e1.getDatumSpotreby().compareTo((Date) params[0]);
					}
					if (params.length > 1 && params[1] instanceof Long) {
						cmp2 = Long.compare((long) params[1], e1.getVyrobneCislo());
					}
				}

				if (cmp1 == 0) {
					return cmp2;
				}
				else {
					return cmp1;
				}
			}

		};
		aOdberatelIndexer = Odberatel.INDEXER;
		aExpedicieIndexer = new TreeIndexer<Expedicia>() {
			@Override
			public int compare(Expedicia e1, Object... params) {
				int cmp1 = 0;
				if (params[0] instanceof Expedicia) {
					Expedicia e2 = (Expedicia) params[0];
					cmp1 = Long.compare(e2.getId(), e1.getId());
				}
				else {
					if (params[0] instanceof Number) {
						cmp1 = Long.compare((long) params[0], e1.getTovar().getVyrobneCislo());
					}
				}

				return cmp1;
			}
		};
	}

	@Override
	public Object[] toCsvData() {
		return new Object[]{aId, aNazov, aAdresa, aNahrada};
	}

	@Override
	public EObjectType getTyp() {
		return EObjectType.SKLAD;
	}

	@Override
	public String getObjectKey() {
		return "s_" + getId();
	}

	@Override
	public void fromCSV(Importer paImporter, String[] paAtrrs) {
		aNazov = paAtrrs[1];
		aAdresa = paAtrrs[2];
		aNahrada = (paAtrrs.length > 3 && !paAtrrs[3].isEmpty()) ? paImporter.getVelkosklad(Integer.parseInt(paAtrrs[3].substring(2))) : null;
	}

	@Override
	public String toString() {
		return "Velkosklad{" + "aId=" + aId + ", aValid=" + isValid() + '}';
	}

	public boolean isValid() {
		return aNahrada == null;
	}

	public void vlozExpedicnyZaznam(Expedicia paPosExpZaznam) {
		aExpedovane.insert(paPosExpZaznam);
	}

	public Velkosklad getNahrada() {
		return aNahrada;
	}

	@Override
	public void print(JColorTextPane pane) {
		pane.append(Color.BLUE, getNazov());
		pane.append(Color.GRAY, " (" + getId() + ")");
	}

}
