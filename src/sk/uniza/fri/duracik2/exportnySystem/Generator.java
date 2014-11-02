/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.exportnySystem;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Unlink
 */
public class Generator {

	static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz";
	private Random rg;

	public Generator() {
		rg = new Random();
	}

	public void generuj(ExportnySystem sys, int pocetSkladov, int pocetOdberatelov, int pocetTovarov, double ppnost) {
		for (int i = 0; i < pocetSkladov; i++) {
			sys.pridajVelkosklad(i, "s" + randomString(5), randomString(8));
			for (int j = 0; j < pocetOdberatelov; j++) {
				sys.pridajOdberatela(i, "" + ((i+1) * j), "o" + randomString(5), randomString(8));
			}
		}
		//Generujeme tovary
		for (int i = 0; i < pocetSkladov; i++) {
			for (int j = 0; j < pocetTovarov; j++) {
				int presuny = 1;
				Date datV = randomDate(new Date(System.currentTimeMillis()-1000*60*60*24*200));
				Tovar t = new Tovar(i*j+1, randomString(2).toUpperCase(), datV, new Date(datV.getTime()+randomBetween(1000*60*60*24*30, 1000*60*60*24*600)), randomBetween(1, 100));
				sys.naskladniTovar(t, i);
				while (rg.nextDouble() < (ppnost/presuny)) {
					presuny++;
					//Ideme k odberatelovi s pp50%
					if (rg.nextDouble() < 0.5) {
						List<Odberatel> os = sys.vypisOdberatelovSkladu(i);
						Odberatel o = os.get(randomBetween(0, Math.max(0, os.size()-1)));
						sys.expedujTovarKOdberatelovi(i, t.getVyrobneCislo(), o.getId(), randomDate(t.getDatumVyroby(), t.getDatumSpotreby()), randomString(2)+" "+randomString(3)+" "+randomString(2));
						if (rg.nextDouble() < 0.5) {
							sys.vylozTovar(t.getVyrobneCislo());
						}
						break;
					}
					else {	
						int sklad = randomBetweenAndNot(0, pocetSkladov, ((Velkosklad)t.getAktualnaLokacia()).getId());
						sys.expedujTovarDoVelkoskladu(i, t.getVyrobneCislo(), sklad, randomDate(t.getDatumVyroby(), t.getDatumSpotreby()), randomString(2)+" "+randomString(3)+" "+randomString(2));
						if (rg.nextDouble() < 0.5) {
							sys.vylozTovar(t.getVyrobneCislo());
						}
						else {
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Zdroj:
	 * http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
	 *
	 * @param len
	 * @return
	 */
	public String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(AB.charAt(rg.nextInt(AB.length())));
		}
		return sb.toString();
	}
	
	public long randomBetween(long start, long end) {
        return start + (long)Math.round(rg.nextDouble() * (end - start));
    }
	
	public int randomBetween(int start, int end) {
        return start + (int)Math.round(rg.nextDouble() * (end - start));
    }
	
	public int randomBetweenAndNot(int start, int end, int not) {
        int res = not;
		while (res == not) {
			res = (int) randomBetween(start, end);
		}
		return res;
    }

	public Date randomDate(Date date) {
		if (date == null) {
			date = new Date(System.currentTimeMillis());
		}
		return new Date(randomBetween(date.getTime(), date.getTime()+1000*60*60*24*150));
	}
	
	public Date randomDate(Date date, Date date2) {
		return new Date(randomBetween(date.getTime(), date2.getTime()));
	}

}
