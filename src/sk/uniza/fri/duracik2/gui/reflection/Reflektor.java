/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Unlink
 */
public class Reflektor<T> {

	private List<Metoda> aMedoty;
	private T aObj;
	private List<MethodExecuteListnerer> aListnerers;

	public Reflektor(T paObj) {
		this.aObj = paObj;
		this.aMedoty = new ArrayList<>();
		this.aListnerers = new LinkedList<>();
		for (Method m : paObj.getClass().getDeclaredMethods()) {
			if (m.getAnnotation(Funkcia.class) != null) {
				aMedoty.add(new Metoda(this, m));
			}
		}
		Collections.sort(aMedoty);
	}

	public List<Metoda> getMedoty() {
		return aMedoty;
	}

	public T getObj() {
		return aObj;
	}

	public void setObj(T obj) {
		this.aObj = obj;
	}

	public List<MethodExecuteListnerer> getListnerers() {
		return aListnerers;
	}

	public void addExecutionListnerer(MethodExecuteListnerer paMel) {
		this.aListnerers.add(paMel);
	}

	public void removeExecutionListnerer(MethodExecuteListnerer paMel) {
		this.aListnerers.remove(paMel);
	}

}
