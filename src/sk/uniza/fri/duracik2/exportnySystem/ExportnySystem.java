/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.uniza.fri.duracik2.exportnySystem;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import sk.uniza.fri.duracik2.tree.RBTree;
import sk.uniza.fri.duracik2.tree.TreeIndexer;

/**
 *
 * @author Unlink
 */
public class ExportnySystem {
    private RBTree<Tovar> aZoznamTovarov;
    private RBTree<Velkosklad> aVelkosklady;

    public ExportnySystem() {
        this.aZoznamTovarov = new RBTree<>(new TreeIndexer<Tovar>() {
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
        });
        this.aVelkosklady = new RBTree<>(new TreeIndexer<Velkosklad>() {
            @Override
            public int compare(Velkosklad e1, Object... params) {
                if (params[0] instanceof Velkosklad) {
                    Velkosklad t = (Velkosklad) params[0];
                    return (e1.getId()== t.getId()) ? 0 : (e1.getId() > t.getId()) ? -1 : 1;
                }
                else if (params[0] instanceof Integer) {
                    int l = (int) params[0];
                    return (e1.getId() == l) ? 0 : (e1.getId() > l) ? -1 : 1;
                }
                else {
                    return 0;
                }
            }
        });
    }

    public Iterator<Velkosklad> dajZoznamSkladov() {
        return aVelkosklady.levelOrderIterator();
    }
    
    public Iterator<Tovar> dajZoznamTovatov() {
        return aZoznamTovarov.levelOrderIterator();
    }
    
    /**
     * 1 vyhľadanie  zadaného  počtu  tovarov  s konkrétnym  EAN  kódom  a  s dátumom  najneskoršej  
     * spotreby väčším ako zadaný dátum nachádzajúcich sa v zadanom veľkosklade (identifikovaný 
     * svojim identifikátorom), ktoré nie sú expedované
     * @param idSkladu
     * @param eanKod
     * @param datSpotreby
     * @param count
     * @return 
     */
    public List<Tovar> vyhľadajTovaryPodľaEanADatumu(int idSkladu, String eanKod, Date datSpotreby, int count) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return null;
        
        return sklad.vyhladajPodlaEanADatumu(eanKod, datSpotreby, count);
    }
    
    /**
     * 2 zistenie počtu tovarov s konkrétnym EAN kódom  nachádzajúcich sa  v zadanom veľkosklade 
     * (identifikovaný svojim identifikátorom), ktoré nie sú expedované
     * @param idSkladu
     * @param eanKod
     * @return 
     */
    public int vyhľadajTovaryPodľaEan(int idSkladu, String eanKod) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return -1;
        return sklad.spocitajPodlaEan(eanKod);
    }
    
    /**
     * 3 vyhľadanie  tovaru  (identifikovaný  svojim  výrobným  kódom)  a výpis  všetkých  o ňom 
     * dostupných  informácii  (v  prípade  prebiehajúcej  expedície  aj  informácie  o  nej)  -  vrátane 
     * informácie o tom, v ktorom veľkosklade sa nachádza
     * @param vyrobnyKod
     * @return 
     */
    public Tovar vyhľadajTovar(long vyrobnyKod) {
        return aZoznamTovarov.findByParams(vyrobnyKod);
    }
    
    /**
     * 4 naskladnenie (pridanie) tovaru do veľkoskladu (identifikovaný svojim identifikátorom)
     * @param paTovar
     * @param idSkladu
     * @return 
     */
    public boolean naskladniTovar(Tovar paTovar, int idSkladu) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return false;
        
        aZoznamTovarov.insert(paTovar);
        return sklad.naskladniTovar(paTovar);
        
    }
    
    /**
     * 5 vyhľadanie  lokálneho  odberateľa  podľa  jeho  identifikátoru  a priradeného  veľkoskladu
     * (identifikovaný svojim identifikátorom)
     * @param idSkladu
     * @param idOdberatela
     * @return 
     */
    public Odberatel vyhľadajOdberateľa(int idSkladu, String idOdberatela) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return null;
        return sklad.vyhladajOdberatela(idOdberatela);
    }
    
    /**
     * 6 vykonanie záznamu o začiatku expedovania daného tovaru (identifikovaný výrobným kódom) 
     * do iného veľkoskladu (identifikovaný svojim identifikátorom)
     * @param idSkladu
     * @param idTovaru
     * @param idCielovehoSkladu
     * @param datumPrichodu
     * @param evcVozidla
     * @return 
     */
    public boolean expedujTovarDoVeľkoskladu(int idSkladu, long idTovaru, int idCielovehoSkladu, Date datumPrichodu, String evcVozidla) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return false;
        Velkosklad cielovySklad = vyhladajSklad(idCielovehoSkladu);
        if (cielovySklad == null) return false;
        return sklad.exportujTovar(idTovaru, cielovySklad, datumPrichodu, evcVozidla);
    }
    
    /**
     * 7 vykonanie záznamu o začiatku expedovania daného tovaru (identifikovaný výrobným kódom) 
     * k odberateľovi (identifikovaný svojim identifikátorom) 
     * @param idSkladu
     * @param idTovaru
     * @param idOdberatela
     * @param datumPrichodu
     * @param evcVozidla
     * @return 
     */
    public boolean expedujTovarKOdberateľovi(int idSkladu, long idTovaru, String idOdberatela, Date datumPrichodu, String evcVozidla) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return false;
        return sklad.exportujTovarKOdberatelovi(idTovaru, idOdberatela, datumPrichodu, evcVozidla);
    }
    
    /**
     * 8 vykonanie  záznamu  o vyložení  tovaru  (identifikovaný  výrobným  kódom)  –  koniec 
     * expedovania  do  veľkoskladu  (nepoznáme  jeho  identifikátor),  alebo  k odberateľovi
     * (nepoznáme jeho identifikátor)
     * @param idTovaru
     * @return 
     */
    public boolean vyložTovar(long idTovaru) {
        Tovar tovar = aZoznamTovarov.findByParams(idTovaru);
        if (tovar == null || tovar.getPosExpZaznam() == null) return false;
        Velkosklad sklad = (Velkosklad) tovar.getPosExpZaznam().getZdroj();
        AMiesto ciel = tovar.getPosExpZaznam().getCiel();
        //Už je vyložený
        if (tovar.getAktualnaLokacia() != sklad) return false;
        sklad.ukonciExpediciu(tovar.getPosExpZaznam());
        ciel.naskladniTovar(tovar.getPosExpZaznam());
        return true;
    }
    
    /**
     * 9 výpis odberateľov zadaného veľkoskladu  (identifikovaný svojim  identifikátorom)  zotriedený 
     * podľa ich identifikátorov
     * @param idSkladu
     * @return 
     */
    public List<Odberatel> výpisOdberateľovSkladu(int idSkladu) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return null;
        List<Odberatel> odberatelia = new LinkedList<>();
        Iterator<Odberatel> it = sklad.dajOdberatelov();
        while (it.hasNext()) {
            odberatelia.add(it.next());
        }
        return odberatelia;
    }
    
    /**
     * 10 výpis  práve  expedovaných  tovarov  z  daného  veľkoskladu  (identifikovaný  svojim 
     * identifikátorom)
     * @param idSkladu
     * @return 
     */
    public List<Tovar> výpisPráveExpedovanýchTovarov(int idSkladu) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return null;
        List<Tovar> tovary = new LinkedList<>();
        Iterator<Expedicia> it = sklad.dajExpedicie();
        while (it.hasNext()) {
            tovary.add(it.next().getTovar());
        }
        return tovary;
    }
    
    /**
     * 11 výpis  uskutočnených  dodávok  k zadanému  odberateľovi  (identifikovaný  svojim 
     * identifikátorom)  priradeného  k veľkoskladu  (identifikovaný  svojim  identifikátorom)     
     * požadujú sa informácie: začiatok expedovania, koniec expedovania, EČV
     * @param idSkladu
     * @param idOdberatela
     * @return 
     */
    public List<Expedicia> výpisUskutocnenýchDodavok(int idSkladu, String idOdberatela) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return null;
        Odberatel odberatel = sklad.vyhladajOdberatela(idOdberatela);
        if (odberatel == null) return null;
        List<Expedicia> expedicie = new LinkedList<>();
        Iterator<Expedicia> it = odberatel.dajDodavky();
        while (it.hasNext())
            expedicie.add(it.next());
        return expedicie;
    }
    
    /**
     * 12 výpis  uskutočnených  dodávok  k zadanému  veľkoskladu  (identifikovaný  svojim 
     * identifikátorom) - požadujú sa informácie: začiatok expedovania, koniec expedovania, EČV
     * @param idSkladu
     * @return 
     */
    public List<Expedicia> výpisUskotočnenýchDodávok(int idSkladu) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return null;
        List<Expedicia> expedicie = new LinkedList<>();
        Iterator<Expedicia> it = sklad.dajDodavky();
        while (it.hasNext())
            expedicie.add(it.next());
        return expedicie;
    }
    
    /**
     * 13 výpis tovarov na danom  veľkosklade (identifikovaný svojim  identifikátorom), ktorým končí 
     * spotreba do zadaného počtu dní (napr. do 5 dní) od zadaného dátumu
     * @param idSkladu
     * @param datSpotreby
     * @param datum
     * @return 
     */
    public List<Tovar> výpisTovarovSDátumomSpotreby(int idSkladu, Date datSpotreby) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return null;
        return sklad.vyhladajPodlaDatumuSpotreby(datSpotreby);
    }
    
    /**
     * 14 pridanie veľkoskladu
     * @param idSkladu
     * @param nazov
     * @param adresa
     * @return 
     */
    public boolean pridajVelkosklad(int idSkladu, String nazov, String adresa) {
        Velkosklad sklad = new Velkosklad(idSkladu, nazov, adresa);
        return aVelkosklady.insert(sklad);
    }
    
    /**
     * 15 pridanie odberateľa veľkoskladu (identifikovaný svojim identifikátorom)
     * @param idSkladu
     * @param idOdberatela
     * @param nazov
     * @param adresa
     * @return 
     */
    public boolean pridajOdberatela(int idSkladu, String idOdberatela, String nazov, String adresa) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return false;
        Odberatel odberatel = new Odberatel(idOdberatela, sklad, nazov, adresa);
        return sklad.priradOdberatela(odberatel);
    }
    
    /**
     * 16 vyradenie konkrétneho tovaru z evidencie (identifikovaný výrobným kódom)
     * @param idTovaru
     * @return 
     */
    public boolean vyradTovar(long idTovaru) {
        Tovar tovar = vyhľadajTovar(idTovaru);
        if (tovar == null) return false;
        AMiesto lokacia = tovar.getAktualnaLokacia();
        if (lokacia instanceof Velkosklad) {
            return ((Velkosklad) lokacia).vyradTovar(tovar);
        }
        return false;
    }
    
    /**
     * 17 výpis  celkového  množstva  a sumárnej  hodnoty  tovarov  (podľa  EAN  kódov)  pre  daný 
     * veľkosklad (identifikovaný svojim identifikátorom), to znamená uviesť koľko kusov tovaru sa 
     * nachádza v sklade s daným EAN kódom a aká je sumárna cena za tieto tovary
     * @param idSkladu
     * @return 
     */
    public List<Statistika> dajStatistiku(int idSkladu) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return null;
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
     * 18 zrušenie veľkoskladu  (identifikovaný svojim  identifikátorom) 
     * celá jeho agenda sa presunie do iného veľkoskladu (identifikovaný svojim identifikátorom)
     * @param idSkladu
     * @param idSkladu2
     * @return 
     */
    public boolean zrusVelkosklad(int idSkladu, int idSkladu2) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return false;
        Velkosklad sklad2 = vyhladajSklad(idSkladu2);
        if (sklad2 == null) return false;
        
        sklad.presunTovary(sklad2);
        return aVelkosklady.delete(sklad);
    }
    
    /**
     * 19 zrušenie  odoberateľa  (identifikovaný  svojim  identifikátorom)  veľkoskladu  (identifikovaný 
     * svojim identifikátorom)
     * @param idSkladu
     * @param idOdberatela
     * @return 
     */
    public boolean zrusOdberatela(int idSkladu, String idOdberatela) {
        Velkosklad sklad = vyhladajSklad(idSkladu);
        if (sklad == null) return false;
        return sklad.zrusOdberatela(idOdberatela);
    }
    
    private Velkosklad vyhladajSklad(int idSkladu) {
        return aVelkosklady.findByParams(idSkladu);
    }
}
