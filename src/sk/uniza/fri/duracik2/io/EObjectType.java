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
public enum EObjectType {
    TOVAR("tovary.csv"), SKLAD("sklady.csv"), ODBERATEL("odberatelia.csv"), EXPEDICIA("expedicie.csv");
    
    private final String aFilename;
    EObjectType(String paFilename) {
        this.aFilename = paFilename;
    }
    
    
}
