/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Unlink
 */
public class Field {
	private String aName;
	private Class<?> aType;
	private Map<String, FunkciaParameter> aParams;

	public Field(String aName, Class<?> aType) {
		this(aName, aType, new HashMap<>());
	}

	public Field(String aName, Class<?> aType, Map aParams) {
		this.aName = aName;
		this.aType = aType;
		this.aParams = aParams;
	}

	public String getName() {
		return aName;
	}

	public Class<?> getType() {
		return aType;
	}

	public FunkciaParameter getParam(String paKey) {
		return aParams.get(paKey);
	}

	public boolean hasParam(String paKey) {
		return aParams.get(paKey) != null;
	}
}
