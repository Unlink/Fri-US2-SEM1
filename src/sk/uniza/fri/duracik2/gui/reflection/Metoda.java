/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Unlink
 */
public class Metoda implements Comparable<Metoda>
{

	protected final Method aMetoda;
	protected final String aName;
	protected final Funkcia aAnotacie;
	protected final Reflektor aReflektor;
	protected final FormGenerator aForm;

	public Metoda(Reflektor paReflektor, Method paMetoda)
	{
		this.aReflektor = paReflektor;
		this.aMetoda = paMetoda;
		this.aAnotacie = paMetoda.getAnnotation(Funkcia.class);
		this.aName = formatMethodName(paMetoda.getName());
		ArrayList<Field> fields = new ArrayList<>();
		int i = 0;
		for (Class<?> parameterType : aMetoda.getParameterTypes())
		{
			String labelTitle = parameterType.toString();
			if (aAnotacie.parametre().length > 1)
			{
				labelTitle = aAnotacie.parametre()[i];
			}
			i++;
			fields.add(new Field(labelTitle, parameterType));
		}
		this.aForm = new FormGenerator(fields);
	}

	@Override
	public int compareTo(Metoda paO)
	{
		return aName.compareTo(paO.aName);
	}

	@Override
	public String toString()
	{
		return aName;
	}

	private String formatMethodName(String methodName)
	{
		StringBuilder sb = new StringBuilder();
		if (aAnotacie.id() != -1)
		{
			sb.append(String.format("%2d ", aAnotacie.id()));
		}
		int x = 0;
		for (char c : methodName.toCharArray())
		{
			if (++x == 1)
			{
				sb.append(Character.toUpperCase(c));
			}
			else if (Character.isUpperCase(c))
			{
				sb.append(' ').append(Character.toLowerCase(c));
			}
			else
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public JPanel getInputPanel()
	{
		return aForm.getForm();
	}

	public void submitMethod()
	{
		Object[] params = new Object[0];
		try
		{
			Object output = aMetoda.invoke(aReflektor.getObj(), aForm.getFormValues());
			for (MethodExecuteListnerer l : (List<MethodExecuteListnerer>) aReflektor.getListnerers())
			{
				l.methodExecuted(aName, params, output);
			}
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
		{
			throw new ReflectorException("Nepodarilo sa vykonať metódu\n" + ex, ex);
		}
		catch (RuntimeException ex)
		{
			for (MethodExecuteListnerer l : (List<MethodExecuteListnerer>) aReflektor.getListnerers())
			{
				l.methodExecuted(aName, params, ex.getMessage());
			}
		}
	}
}
