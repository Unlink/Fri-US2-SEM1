/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.io;

/**
 *
 * @author Unlink
 */
public interface IToCSV {

	/**
	 * Vráti pole objektov, ktoré sa zapíšu do CSV súboru
	 *
	 * @return
	 */
	public Object[] toCsvData();

	/**
	 * Načíta sa z CSV dát
	 *
	 * @param paImporter
	 * @param paAtrrs
	 */
	public void fromCSV(Importer paImporter, String[] paAtrrs);

	public EObjectType getTyp();

	/**
	 * Vráti klúč pre zápis do súboru
	 *
	 * @return
	 */
	public String getObjectKey();

}
