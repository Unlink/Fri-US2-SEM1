/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

import java.lang.reflect.Constructor;
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

	private HashMap<Class<?>, Method> aRenders;
	private HashMap<Class<?>, Method> aValidators;
	private static BasicFormFields aInstance;

	public static BasicFormFields getInstance()
	{
		if (aInstance == null)
		{
			aInstance = new BasicFormFields();
		}
		return aInstance;
	}

	private BasicFormFields()
	{
		aRenders = new HashMap<>();
		aValidators = new HashMap<>();
		for (Method method : this.getClass().getMethods())
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

	public Method getRender(Class<?> paClass)
	{
		return aRenders.get(getObjType(paClass));
	}

	public JComponent getRenderedComponent(Class<?> paClass)
	{
		Method m = getRender(paClass);
		if (m != null)
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
		else if (hasAnotatedConstructor(paClass))
		{
			return new ReflectionalJTextField(paClass);
		}
		else
		{
			throw new ReflectorException("Nepodarilo sa vyvolať metódu, nebola nájdená render metoda pre " + paClass.getName());
		}
	}

	public Method getValidator(Class<?> paClass)
	{
		return aValidators.get(getObjType(paClass));
	}

	public Object getData(JComponent paComponent, Class<?> paClass)
	{
		Method m = getValidator(paClass);
		if (m != null)
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
		else if (paComponent instanceof ReflectionalJTextField)
		{
			return ((ReflectionalJTextField) paComponent).getObject();
		}
		else
		{
			throw new ReflectorException("Nepodarilo sa vyvolať metódu, nebola nájdená render metoda pre " + paClass.getName());
		}
	}

	@FormField(typ = {String.class, Integer.class, Double.class, Date.class, Long.class})
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
	
	public Long validateLong(JComponent paComponent)
	{
		return Long.parseLong(((JTextComponent) paComponent).getText());
	}

	public Date validateDate(JComponent paComponent) throws ParseException
	{
		return new SimpleDateFormat("dd.mm.yyyy").parse(((JTextComponent) paComponent).getText());
	}

	private boolean hasAnotatedConstructor(Class<?> paClass)
	{
		for (Constructor<?> constructor : paClass.getConstructors())
		{
			if (constructor.getAnnotation(FunkcnyKonstruktor.class) != null)
			{
				return true;
			}
		}
		return false;
	}

	private Class<?> getObjType(Class<?> paClass)
	{
		switch (paClass.getName())
		{
			case "int":
				return Integer.class;
			case "double":
				return Double.class;
			case "long":
				return Long.class;
			default:
				return paClass;
		}
	}

}
