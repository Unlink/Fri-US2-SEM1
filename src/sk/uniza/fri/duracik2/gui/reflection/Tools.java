/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

/**
 *
 * @author Unlink
 */
public abstract class Tools {
	public static String getErrorMessage(Exception ex) {
		ex.printStackTrace();
		Throwable e = ex;
		while (e.getCause() != null) {
			e = e.getCause();
		}
		return e.getMessage();
	}
}
