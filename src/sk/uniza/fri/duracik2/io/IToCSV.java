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
    
    public Object[] toCsvData();
    public EObjectType getTyp();
    /**
     * Vráti klúč pre zápis do súboru
     * @return 
     */
    public String getObjectKey();
}
