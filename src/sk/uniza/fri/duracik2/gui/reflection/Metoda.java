/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author Unlink
 */
public class Metoda implements Comparable<Metoda> {

	protected final Method aMetoda;
	protected final String aName;
	protected final Funkcia aAnotacie;
	protected final Reflektor aReflektor;
	protected final FormGenerator aForm;

	public Metoda(Reflektor paReflektor, Method paMetoda) {
		this.aReflektor = paReflektor;
		this.aMetoda = paMetoda;
		this.aAnotacie = paMetoda.getAnnotation(Funkcia.class);
		this.aName = formatMethodName(paMetoda.getName());
		ArrayList<Field> fields = new ArrayList<>();
		int i = 0;
		for (Class<?> parameterType : aMetoda.getParameterTypes()) {
			String labelTitle = parameterType.toString();
			if (aAnotacie.parametre().length > 0) {
				labelTitle = aAnotacie.parametre()[i];
			}

			if (aMetoda.getAnnotation(FunkciaParametre.class) != null) {
				HashMap<String, FunkciaParameter> x = new HashMap<>();
				for (FunkciaParameter p : aMetoda.getAnnotation(FunkciaParametre.class).parametre()) {
					if (p.param() == i) {
						x.put(p.key(), p);
					}
				}
				fields.add(new Field(labelTitle, parameterType, x));
			}
			else {
				fields.add(new Field(labelTitle, parameterType));
			}
			i++;
		}
		this.aForm = new FormGenerator(fields);
	}

	@Override
	public int compareTo(Metoda paO) {
		return aName.compareTo(paO.aName);
	}

	@Override
	public String toString() {
		return aName;
	}

	private String formatMethodName(String methodName) {
		StringBuilder sb = new StringBuilder();
		if (aAnotacie.id() != -1) {
			sb.append(String.format("%2d. ", aAnotacie.id()));
		}
		int x = 0;
		for (char c : methodName.toCharArray()) {
			if (++x == 1) {
				sb.append(Character.toUpperCase(c));
			}
			else if (Character.isUpperCase(c)) {
				sb.append(' ').append(Character.toLowerCase(c));
			}
			else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public JPanel getInputPanel() {
		return aForm.getForm();
	}

	public void submitMethod() {
		Object[] params = null;
		try {
			params = aForm.getFormValues();
			Object output = aMetoda.invoke(aReflektor.getObj(), params);
			for (MethodExecuteListnerer l : (List<MethodExecuteListnerer>) aReflektor.getListnerers()) {
				l.methodExecuted(aName, params, output);
			}
		}
		/*catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
		 {
		 throw new ReflectorException("Nepodarilo sa vykonať metódu\n" + ex, ex);
		 }*/
		catch (Exception ex) {
			for (MethodExecuteListnerer l : (List<MethodExecuteListnerer>) aReflektor.getListnerers()) {
				l.methodExecuted(aName, params, ex);
			}
		}
	}

}
