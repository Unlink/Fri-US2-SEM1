/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.jdatepicker.JDateComponentFactory;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

/**
 *
 * @author Unlink
 */
public class BasicFormFields
{

	private final HashMap<Class<?>, Method> aRenders;
	private final HashMap<Class<?>, Method> aValidators;
	private static BasicFormFields aInstance;
	public static final String DEFAULT = "DEFAULT";

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

	public JComponent getRenderedComponent(Field f)
	{
		Method m = getRender(f.getType());
		if (m != null)
		{
			try
			{
				JComponent jc = (JComponent) m.invoke(aInstance, f);
				if (jc instanceof JTextComponent && f.hasParam(DEFAULT)) {
					((JTextComponent) jc).setText(f.getParam(DEFAULT).value());
				}
				return jc;
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
			{
				throw new ReflectorException("Nepodarilo sa vyvolať metódu", ex);
			}
		}
		else if (hasAnotatedConstructor(f.getType()))
		{
			return new ReflectionalJTextField(f);
		}
		else
		{
			throw new ReflectorException("Nepodarilo sa vyvolať metódu, nebola nájdená render metoda pre " + f.getType().getName());
		}
	}

	public Method getValidator(Class<?> paClass)
	{
		return aValidators.get(getObjType(paClass));
	}

	public Object getData(JComponent paComponent, Field f)
	{
		Method m = getValidator(f.getType());
		if (m != null)
		{
			try
			{
				return m.invoke(aInstance, paComponent, f);
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
			throw new ReflectorException("Nepodarilo sa vyvolať metódu, nebola nájdená validate metoda pre " + f.getType().getName());
		}
	}

	@FormField(typ = {String.class, Integer.class, Double.class, Long.class})
	public JTextComponent renderBasic(Field paField)
	{
		return new JTextField();
	}
	
	@FormField(typ = {File.class})
	public JComponent renderFile(Field paField)
	{
		return new FileComponentField(paField);
	}
	
	@FormField(typ = {Date.class})
	public JComponent renderDate(Field paField) {
		JDateComponentFactory componentFactory = new JDateComponentFactory();
		JDatePicker datePicker = componentFactory.createJDatePicker();
		datePicker.setTextEditable(true);
		datePicker.setShowYearButtons(true);
		return (JComponent) datePicker;
	}

	public String validateString(JComponent paComponent, Field paField)
	{
		return ((JTextComponent) paComponent).getText();
	}

	public Integer validateInteger(JComponent paComponent, Field paField)
	{
		try {
			return Integer.parseInt(((JTextComponent) paComponent).getText());
		}
		catch (NumberFormatException ex) {
			throw new NumberFormatException("Nesprávny číselný vstup pre pole "+paField.getName()+" - zadané: \""+((JTextComponent) paComponent).getText()+"\"");
		}
	}
	
	public Long validateLong(JComponent paComponent, Field paField)
	{
		try {
			return Long.parseLong(((JTextComponent) paComponent).getText());
		}
		catch (NumberFormatException ex) {
			throw new NumberFormatException("Nesprávny číselný vstup pre pole "+paField.getName());
		}
	}
	
	public Double validateDouble(JComponent paComponent, Field paField)
	{
		try {
			return Double.parseDouble(((JTextComponent) paComponent).getText());
		}
		catch (NumberFormatException ex) {
			throw new NumberFormatException("Nesprávny číselný vstup pre pole "+paField.getName());
		}
	}

	public Date validateDate(JComponent paComponent, Field paField) throws ParseException
	{
		try {
			//return new SimpleDateFormat("dd.MM.yyyy").parse(((JTextComponent) paComponent).getText());
			Calendar c = (Calendar) ((JDatePicker) paComponent).getModel().getValue();
			return c.getTime();
		}
		catch (Exception ex) {
			throw new RuntimeException("Nepodarilo sa načítať dátum z pola "+paField.getName());
		}
	}

	public File validateFile(JComponent paComponent, Field paField) throws FileNotFoundException 
	{
		if (((FileComponentField) paComponent).getFile() == null) {
			throw new FileNotFoundException("Nepodarilo sa nájsť súbor z poľa "+paField.getName());
		}
		return ((FileComponentField) paComponent).getFile();
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
