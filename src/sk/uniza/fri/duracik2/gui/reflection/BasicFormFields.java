/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Unlink
 */
public class BasicFormFields
{

	private static HashMap<Class<?>, Method> aRenders;
	private static HashMap<Class<?>, Method> aValidators;
	private static Class<? extends BasicFormFields> aRenderingClass = BasicFormFields.class;
	private static BasicFormFields aInstance;

	public static Method getRender(Class<?> paClass)
	{
		if (aRenders == null)
		{
			reflectThis();
		}
		return aRenders.get(paClass);
	}

	public static JComponent getRenderedComponent(Class<?> paClass)
	{
		Method m = getRender(paClass);
		if (m == null)
		{
			throw new ReflectorException("Nepodarilo sa vyvolať metódu, nebola nájdená render metoda pre " + paClass.getName());
		}
		else
		{
			try
			{
				return (JComponent) m.invoke(aInstance, new Object[0]);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
			{
				throw new ReflectorException("Nepodarilo sa vyvolať metódu", ex);
			}
		}
	}

	public static Method getValidator(Class<?> paClass)
	{
		if (aValidators == null)
		{
			reflectThis();
		}
		return aValidators.get(paClass);
	}
	
	public static Object getData(JComponent paComponent, Class<?> paClass) {
		Method m = getValidator(paClass);
		if (m == null)
		{
			throw new ReflectorException("Nepodarilo sa vyvolať metódu, nebola nájdená validate metoda pre " + paClass.getName());
		}
		else
		{
			try
			{
				return m.invoke(aInstance, paComponent);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
			{
				throw new ReflectorException("Nepodarilo sa vyvolať validačnú metódu", ex);
			}
		}
	}

	public static void setaRenderingClass(Class<? extends BasicFormFields> paRenderingClass)
	{
		aRenderingClass = paRenderingClass;
	}

	private static void reflectThis()
	{
		aRenders = new HashMap<>();
		aValidators = new HashMap<>();
		try
		{
			aInstance = aRenderingClass.newInstance();
		}
		catch (InstantiationException | IllegalAccessException ex)
		{
			throw new ReflectorException("Nepodarilo sa instanciovať classu", ex);
		}
		for (Method method : aRenderingClass.getMethods())
		{
			if (method.getName().startsWith("render"))
			{
				for (Class<?> t : method.getAnnotation(FormField.class).typ())
				{
					aRenders.put(t, method);
				}
			}
			else if (method.getName().startsWith("validate"))
			{
				aValidators.put(method.getReturnType(), method);
			}
		}
	}

	@FormField(typ = {String.class, Integer.class, Double.class, Date.class})
	public JTextComponent renderBasic()
	{
		return new JTextField();
	}

	public String validateString(JComponent paComponent)
	{
		return ((JTextComponent) paComponent).getText();
	}

	public Integer validateInteger(JComponent paComponent)
	{
		return Integer.parseInt(((JTextComponent) paComponent).getText());
	}

	public Date validateDate(JComponent paComponent) throws ParseException
	{
		return new SimpleDateFormat("dd.mm.yyyy").parse(((JTextComponent) paComponent).getText());
	}

}
