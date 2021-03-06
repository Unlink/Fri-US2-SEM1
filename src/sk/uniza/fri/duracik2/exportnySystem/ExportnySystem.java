/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.exportnySystem;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFileChooser;
import sk.uniza.fri.duracik2.gui.reflection.BasicFormFields;
import sk.uniza.fri.duracik2.gui.reflection.Funkcia;
import sk.uniza.fri.duracik2.gui.reflection.FunkciaParameter;
import sk.uniza.fri.duracik2.gui.reflection.FunkciaParametre;
import sk.uniza.fri.duracik2.io.Exporter;
import sk.uniza.fri.duracik2.io.Importer;
import sk.uniza.fri.duracik2.tree.RBTree;

/**
 *
 * @author Unlink
 */
public class ExportnySystem {
	private final RBTree<Tovar> aZoznamTovarov;
	private final RBTree<Velkosklad> aVelkosklady;

	public ExportnySystem() {
		this.aZoznamTovarov = new RBTree<>(Tovar.INDEXER);
		this.aVelkosklady = new RBTree<>(Velkosklad.INDEXER);
	}

	public Iterator<Velkosklad> dajZoznamSkladov() {
		return aVelkosklady.levelOrderIterator();
	}

	public Iterator<Tovar> dajZoznamTovatov() {
		return aZoznamTovarov.levelOrderIterator();
	}

	/**
	 * 1 vyhľadanie zadaného počtu tovarov s konkrétnym EAN kódom a s dátumom
	 * najneskoršej spotreby väčším ako zadaný dátum nachádzajúcich sa v zadanom
	 * veľkosklade (identifikovaný svojim identifikátorom), ktoré nie sú
	 * expedované
	 *
	 * @param idSkladu
	 * @param eanKod
	 * @param datSpotreby
	 * @param count
	 * @return
	 */
	@Funkcia(id = 1, parametre = {"Identifikátor skladu", "Ean kód tovaru", "Dátum spotreby", "Počet tovarov"})
	public List<Tovar> vyhladajTovaryPodlaEanADatumu(int idSkladu, String eanKod, Date datSpotreby, int count) {
		Velkosklad sklad = vyhladajSklad(idSkladu);

		return sklad.vyhladajPodlaEanADatumu(eanKod, datSpotreby, count);
	}

	/**
	 * 2 zistenie počtu tovarov s konkrétnym EAN kódom nachádzajúcich sa v
	 * zadanom veľkosklade (identifikovaný svojim identifikátorom), ktoré nie sú
	 * expedované
	 *
	 * @param idSkladu
	 * @param eanKod
	 * @return
	 */
	@Funkcia(id = 2, parametre = {"Identifikátor skladu", "Ean kód tovaru"})
	public int vyhladajTovaryPodlaEan(int idSkladu, String eanKod) {
		Velkosklad sklad = vyhladajSklad(idSkladu);

		return sklad.spocitajPodlaEan(eanKod);
	}

	/**
	 * 3 vyhľadanie tovaru (identifikovaný svojim výrobným kódom) a výpis
	 * všetkých o ňom dostupných informácii (v prípade prebiehajúcej expedície
	 * aj informácie o nej) - vrátane informácie o tom, v ktorom veľkosklade sa
	 * nachádza
	 *
	 * @param vyrobnyKod
	 * @return
	 */
	@Funkcia(id = 3, parametre = {"Výrobný kód tovaru"})
	public Tovar vyhladajTovar(long vyrobnyKod) {
		return aZoznamTovarov.findByParams(vyrobnyKod);
	}

	/**
	 * 4 naskladnenie (pridanie) tovaru do veľkoskladu (identifikovaný svojim
	 * identifikátorom)
	 *
	 * @param paTovar
	 * @param idSkladu
	 * @return
	 */
	@Funkcia(id = 4, parametre = {"Tovar", "Identifikátor skladu"})
	public boolean naskladniTovar(Tovar paTovar, int idSkladu) {
		Velkosklad sklad = vyhladajSklad(idSkladu);

		if (!aZoznamTovarov.insert(paTovar)) {
			throw new IllegalArgumentException("Duplikátny výrobný kód tovaru");
		}
		else {
			return sklad.naskladniTovar(paTovar);
		}

	}

	/**
	 * 5 vyhľadanie lokálneho odberateľa podľa jeho identifikátoru a priradeného
	 * veľkoskladu (identifikovaný svojim identifikátorom)
	 *
	 * @param idSkladu
	 * @param idOdberatela
	 * @return
	 */
	@Funkcia(id = 5, parametre = {"Identifikátor skladu", "Identifikátor odberaťela"})
	public Odberatel vyhladajOdberatela(int idSkladu, String idOdberatela) {
		Velkosklad sklad = vyhladajSklad(idSkladu);

		return sklad.vyhladajOdberatela(idOdberatela);
	}

	/**
	 * 6 vykonanie záznamu o začiatku expedovania daného tovaru (identifikovaný
	 * výrobným kódom) do iného veľkoskladu (identifikovaný svojim
	 * identifikátorom)
	 *
	 * @param idTovaru
	 * @param idCielovehoSkladu
	 * @param datumPrichodu
	 * @param evcVozidla
	 * @return
	 */
	@Funkcia(id = 6, parametre = {"Výrobné číslo tovaru", "Identifikátor cieľového skladu", "Ocakávaný dátum príchodu", "Evidenčné číslo prepravcu"})
	public boolean expedujTovarDoVelkoskladu(long idTovaru, int idCielovehoSkladu, Date datumPrichodu, String evcVozidla) {
		Velkosklad sklad = vyhladajSklad(idTovaru);
		Velkosklad cielovySklad = vyhladajSklad(idCielovehoSkladu);
		return sklad.exportujTovar(idTovaru, cielovySklad, datumPrichodu, evcVozidla);
	}

	/**
	 * 7 vykonanie záznamu o začiatku expedovania daného tovaru (identifikovaný
	 * výrobným kódom) k odberateľovi (identifikovaný svojim identifikátorom)
	 *
	 * @param idTovaru
	 * @param idOdberatela
	 * @param datumPrichodu
	 * @param evcVozidla
	 * @return
	 */
	@Funkcia(id = 7, parametre = {"Výrobné číslo tovaru", "Identifikátor odberaťela", "Ocakávaný dátum príchodu", "Evidenčné číslo prepravcu"})
	public boolean expedujTovarKOdberatelovi(long idTovaru, String idOdberatela, Date datumPrichodu, String evcVozidla) {
		Velkosklad sklad = vyhladajSklad(idTovaru);

		return sklad.exportujTovarKOdberatelovi(idTovaru, idOdberatela, datumPrichodu, evcVozidla);
	}

	/**
	 * 8 vykonanie záznamu o vyložení tovaru (identifikovaný výrobným kódom) –
	 * koniec expedovania do veľkoskladu (nepoznáme jeho identifikátor), alebo k
	 * odberateľovi (nepoznáme jeho identifikátor)
	 *
	 * @param idTovaru
	 * @return
	 */
	@Funkcia(id = 8, parametre = {"Výrobné číslo tovaru"})
	public boolean vylozTovar(long idTovaru) {
		Tovar tovar = aZoznamTovarov.findByParams(idTovaru);
		if (tovar == null || tovar.getPosExpZaznam() == null) {
			throw new IllegalArgumentException("Tovar s ID " + idTovaru + " práve nieje expedovaný");
		}
		Velkosklad sklad = (Velkosklad) tovar.getPosExpZaznam().getZdroj();
		AMiesto ciel = tovar.getPosExpZaznam().getCiel();
		while (ciel instanceof Velkosklad && !((Velkosklad) ciel).isValid()) {
			ciel = ((Velkosklad) ciel).getNahrada();
			tovar.getPosExpZaznam().setCiel(ciel);
		}
		//Už je vyložený
		if (tovar.getAktualnaLokacia() != sklad) {
			throw new IllegalArgumentException("Tovar je už vyložený");
		}
		sklad.ukonciExpediciu(tovar.getPosExpZaznam());
		ciel.naskladniTovar(tovar.getPosExpZaznam());
		return true;
	}

	/**
	 * 9 výpis odberateľov zadaného veľkoskladu (identifikovaný svojim
	 * identifikátorom) zotriedený podľa ich identifikátorov
	 *
	 * @param idSkladu
	 * @return
	 */
	@Funkcia(id = 9, parametre = {"Identifikátor skladu"})
	public List<Odberatel> vypisOdberatelovSkladu(int idSkladu) {
		Velkosklad sklad = vyhladajSklad(idSkladu);

		List<Odberatel> odberatelia = new LinkedList<>();
		Iterator<Odberatel> it = sklad.dajOdberatelov();
		while (it.hasNext()) {
			odberatelia.add(it.next());
		}
		return odberatelia;
	}

	/**
	 * 10 výpis práve expedovaných tovarov z daného veľkoskladu (identifikovaný
	 * svojim identifikátorom)
	 *
	 * @param idSkladu
	 * @return
	 */
	@Funkcia(id = 10, parametre = {"Identifikátor skladu"})
	public List<Tovar> vypisPraveExpedovanychTovarov(int idSkladu) {
		Velkosklad sklad = vyhladajSklad(idSkladu);

		List<Tovar> tovary = new LinkedList<>();
		Iterator<Expedicia> it = sklad.dajExpedicie();
		while (it.hasNext()) {
			tovary.add(it.next().getTovar());
		}
		return tovary;
	}

	/**
	 * 11 výpis uskutočnených dodávok k zadanému odberateľovi (identifikovaný
	 * svojim identifikátorom) priradeného k veľkoskladu (identifikovaný svojim
	 * identifikátorom) požadujú sa informácie: začiatok expedovania, koniec
	 * expedovania, EČV
	 *
	 * @param idSkladu
	 * @param idOdberatela
	 * @return
	 */
	@Funkcia(id = 11, parametre = {"Identifikátor skladu", "Identofikátor odberateľa"})
	public List<Expedicia> vypisUskutocnenychDodavok(int idSkladu, String idOdberatela) {
		Velkosklad sklad = vyhladajSklad(idSkladu);

		Odberatel odberatel = sklad.vyhladajOdberatela(idOdberatela);
		if (odberatel == null) {
			throw new IllegalArgumentException("Nepodarilo sa nájsť odberatela s ID " + idOdberatela);
		}
		List<Expedicia> expedicie = new LinkedList<>();
		Iterator<Expedicia> it = odberatel.dajDodavky();
		while (it.hasNext()) {
			expedicie.add(it.next());
		}
		return expedicie;
	}

	/**
	 * 12 výpis uskutočnených dodávok k zadanému veľkoskladu (identifikovaný
	 * svojim identifikátorom) - požadujú sa informácie: začiatok expedovania,
	 * koniec expedovania, EČV
	 *
	 * @param idSkladu
	 * @return
	 */
	@Funkcia(id = 12, parametre = {"Identifikátor skladu"})
	public List<Expedicia> vypisUskotocnenychDodavok(int idSkladu) {
		Velkosklad sklad = vyhladajSklad(idSkladu);

		List<Expedicia> expedicie = new LinkedList<>();
		Iterator<Expedicia> it = sklad.dajDodavky();
		while (it.hasNext()) {
			expedicie.add(it.next());
		}
		return expedicie;
	}

	/**
	 * 13 výpis tovarov na danom veľkosklade (identifikovaný svojim
	 * identifikátorom), ktorým končí spotreba do zadaného počtu dní (napr. do 5
	 * dní) od zadaného dátumu
	 *
	 * @param idSkladu
	 * @param datSpotreby
	 * @param pocDni
	 * @return
	 */
	@Funkcia(id = 13, parametre = {"Identifikátor skladu", "Dátum spotreby", "Počet dní"})
	@FunkciaParametre(parametre = {
		@FunkciaParameter(param = 2, key = BasicFormFields.DEFAULT, value = "5")})
	public List<Tovar> vypisTovarovSDatumomSpotreby(int idSkladu, Date datSpotreby, int pocDni) {
		Velkosklad sklad = vyhladajSklad(idSkladu);
		datSpotreby = new Date(datSpotreby.getTime() + pocDni * 60 * 60 * 24 * 1000L);
		return sklad.vyhladajPodlaDatumuSpotreby(datSpotreby);
	}

	/**
	 * 14 pridanie veľkoskladu
	 *
	 * @param idSkladu
	 * @param nazov
	 * @param adresa
	 * @return
	 */
	@Funkcia(id = 14, parametre = {"Identifikátor skladu", "Názov skladu", "Adresa skladu"})
	public boolean pridajVelkosklad(int idSkladu, String nazov, String adresa) {
		Velkosklad sklad = new Velkosklad(idSkladu, nazov, adresa);
		return aVelkosklady.insert(sklad);
	}

	/**
	 * 15 pridanie odberateľa veľkoskladu (identifikovaný svojim
	 * identifikátorom)
	 *
	 * @param idSkladu
	 * @param idOdberatela
	 * @param nazov
	 * @param adresa
	 * @return
	 */
	@Funkcia(id = 15, parametre = {"Identifikátor skladu", "Id odberatela", "Názov", "Adresa"})
	public boolean pridajOdberatela(Integer idSkladu, String idOdberatela, String nazov, String adresa) {
		Velkosklad sklad = vyhladajSklad(idSkladu);
		Odberatel odberatel = new Odberatel(idOdberatela, sklad, nazov, adresa);
		return sklad.priradOdberatela(odberatel);
	}

	/**
	 * 16 vyradenie konkrétneho tovaru z evidencie (identifikovaný výrobným
	 * kódom)
	 *
	 * @param idTovaru
	 * @return
	 */
	@Funkcia(id = 16, parametre = {"Výrobné číslo tovaru"})
	public boolean vyradTovar(long idTovaru) {
		Tovar tovar = vyhladajTovar(idTovaru);
		if (tovar == null) {
			throw new IllegalArgumentException("Nepodarilo sa nájsť tovar s ID " + idTovaru);
		}
		AMiesto lokacia = tovar.getAktualnaLokacia();
		if (lokacia == null) {
			throw new IllegalArgumentException("Tovar je už vyradený");
		}
		else if (lokacia instanceof Velkosklad) {
			return ((Velkosklad) lokacia).vyradTovar(tovar);
		}
		else {
			throw new IllegalArgumentException("Tovar je už u zákazníka");
		}
	}

	/**
	 * 17 výpis celkového množstva a sumárnej hodnoty tovarov (podľa EAN kódov)
	 * pre daný veľkosklad (identifikovaný svojim identifikátorom), to znamená
	 * uviesť koľko kusov tovaru sa nachádza v sklade s daným EAN kódom a aká je
	 * sumárna cena za tieto tovary
	 *
	 * @param idSkladu
	 * @return
	 */
	@Funkcia(id = 17, parametre = {"Identifikátor skladu"})
	public List<Statistika> dajStatistiku(int idSkladu) {
		Velkosklad sklad = vyhladajSklad(idSkladu);
		LinkedList<Statistika> stats = new LinkedList<>();
		stats.add(new Statistika("dummy"));
		Iterator<Tovar> it = sklad.dajEanIteratorTovarov();
		while (it.hasNext()) {
			Tovar tovar = it.next();
			if (!stats.getLast().getEan().equals(tovar.getEanKod())) {
				stats.addLast(new Statistika(tovar.getEanKod()));
			}
			stats.getLast().addTovar(tovar);
		}
		stats.removeFirst();
		return stats;
	}

	/**
	 * 18 zrušenie veľkoskladu (identifikovaný svojim identifikátorom) celá jeho
	 * agenda sa presunie do iného veľkoskladu (identifikovaný svojim
	 * identifikátorom)
	 *
	 * @param idSkladu
	 * @param idSkladu2
	 * @return
	 */
	@Funkcia(id = 18, parametre = {"Identifikátor skladu", "Identifikátor cieľového skladu"})
	public boolean zrusVelkosklad(int idSkladu, int idSkladu2) {
		Velkosklad sklad = vyhladajSklad(idSkladu);
		Velkosklad sklad2 = vyhladajSklad(idSkladu2);

		sklad.zrusSklad(sklad2);
		return aVelkosklady.delete(sklad);
	}

	/**
	 * 19 zrušenie odoberateľa (identifikovaný svojim identifikátorom)
	 * veľkoskladu (identifikovaný svojim identifikátorom)
	 *
	 * @param idSkladu
	 * @param idOdberatela
	 * @return
	 */
	@Funkcia(id = 19, parametre = {"Identifikátor skladu", "Identifikátor odberateľa"})
	public boolean zrusOdberatela(int idSkladu, String idOdberatela) {
		Velkosklad sklad = vyhladajSklad(idSkladu);
		return sklad.zrusOdberatela(idOdberatela);
	}

	private Velkosklad vyhladajSklad(int idSkladu) {
		Velkosklad sklad = aVelkosklady.findByParams(idSkladu);
		if (sklad == null) {
			throw new IllegalArgumentException("Nepodarilo sa nájsť sklad s ID " + idSkladu);
		}
		return sklad;
	}

	private Velkosklad vyhladajSklad(long idTovaru) {
		Tovar tovar = aZoznamTovarov.findByParams(idTovaru);
		if (tovar == null) {
			throw new IllegalArgumentException("Nepodarilo sa nájsť tovar s ID " + idTovaru);
		}
		if (tovar.getAktualnaLokacia() instanceof Velkosklad) {
			return (Velkosklad) tovar.getAktualnaLokacia();
		}
		else {
			throw new IllegalArgumentException("Tovar nieje na veľkosklade");
		}
	}

	@Funkcia(parametre = {"Zložka s dátami"})
	@FunkciaParametre(parametre = {
		@FunkciaParameter(param = 0, key = "JFileChooser.type", intValue = JFileChooser.OPEN_DIALOG),
		@FunkciaParameter(param = 0, key = "JFileChooser.fileSelectionMode", intValue = JFileChooser.DIRECTORIES_ONLY)
	})
	public boolean importujData(File paZlozka) {
		Importer importer = new Importer(paZlozka);
		if (!importer.importuj()) {
			return false;
		}

		//Vlož tovary do datazy tovarov
		for (Tovar t : importer.getImportovaneTovary()) {
			aZoznamTovarov.insert(t);
		}

		//Vlož veľkosklady
		for (Velkosklad s : importer.getImportovaneSklady()) {
			if (s.isValid()) {
				aVelkosklady.insert(s);
			}
		}

		//Prirad Odberatelov
		for (Odberatel odberatel : importer.getImportovanyOdberatelia()) {
			if (odberatel.getSklad() != null) {
				odberatel.getSklad().priradOdberatela(odberatel);
			}
		}

		//Vloženie dát o tovaroch
		for (Tovar tovar : importer.getImportovaneTovary()) {
			//Ak nebol nikam expedovaný
			if (tovar.getPosExpZaznam() == null) {
				((Velkosklad) tovar.getAktualnaLokacia()).naskladniTovar(tovar);
			}
			else {
				//Ak je tovar tam, kde by mal byť podla poslednej expedicie
				AMiesto ciel = tovar.getPosExpZaznam().getCiel();
				while (ciel instanceof Velkosklad && !((Velkosklad) ciel).isValid()) {
					ciel = ((Velkosklad) ciel).getNahrada();
				}
				if (tovar.getPosExpZaznam().getCiel() == tovar.getAktualnaLokacia()) {
					tovar.getAktualnaLokacia().naskladniTovar(tovar.getPosExpZaznam());
				}
				//Tovar je ešte v zdroji expedovania
				else if (tovar.getPosExpZaznam().getZdroj() == tovar.getAktualnaLokacia()) {
					((Velkosklad) tovar.getAktualnaLokacia()).vlozExpedicnyZaznam(tovar.getPosExpZaznam());
				}
				//Postupne prejdeme predchadzajuce expedicie a vložíme históriu
				Expedicia prev = tovar.getPosExpZaznam().getPredchadzajuca();
				while (prev != null) {
					prev.getCiel().vlozInfoODovavke(prev);
					prev = prev.getPredchadzajuca();
				}
			}
		}

		return true;
	}

	@Funkcia(parametre = {"Zložka s dátami"})
	@FunkciaParametre(parametre = {
		@FunkciaParameter(param = 0, key = "JFileChooser.type", intValue = JFileChooser.SAVE_DIALOG),
		@FunkciaParameter(param = 0, key = "JFileChooser.fileSelectionMode", intValue = JFileChooser.DIRECTORIES_ONLY)
	})
	public boolean exportujData(File paZlozka) {
		Exporter exporter = new Exporter(paZlozka);
		try {
			exporter.exportuj(this);
			return true;
		}
		catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Funkcia(parametre = {"Počet skladov", "Počet odberatelov na sklad", "Pocet tovarov na sklad", "Pravdepodobnosť expedicie"})
	@FunkciaParametre(parametre = {
		@FunkciaParameter(param = 0, key = BasicFormFields.DEFAULT, value = "200"),
		@FunkciaParameter(param = 1, key = BasicFormFields.DEFAULT, value = "500"),
		@FunkciaParameter(param = 2, key = BasicFormFields.DEFAULT, value = "1000"),
		@FunkciaParameter(param = 3, key = BasicFormFields.DEFAULT, value = "0.2"),})
	public void vygenerujData(int pocetSkladov, int pocetOdberatelov, int pocetTovarov, double ppnost) {
		aVelkosklady.clear();
		aZoznamTovarov.clear();
		new Generator().generuj(this, pocetSkladov, pocetOdberatelov, pocetTovarov, ppnost);
	}
}
