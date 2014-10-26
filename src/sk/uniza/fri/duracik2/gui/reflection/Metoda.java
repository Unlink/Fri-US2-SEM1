/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.text.JTextComponent;
import layout.SpringUtilities;

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
	protected final List<JTextComponent> aParams;
	protected static final String CREATE_LABEL_METHOD = "renderLabel";
	protected static final String VALIDATE_LABEL_METHOD = "validateLabel";

	public Metoda(Reflektor paReflektor, Method paMetoda)
	{
		this.aParams = new ArrayList<>();
		this.aReflektor = paReflektor;
		this.aMetoda = paMetoda;
		this.aAnotacie = paMetoda.getAnnotation(Funkcia.class);
		this.aName = formatMethodName(paMetoda.getName());
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

	public JPanel getInputPanel(JPanel p)
	{
		aParams.clear();
		int i = 0;
		for (Class<?> parameterType : aMetoda.getParameterTypes())
		{
			String labelTitle = parameterType.toString();
			if (aAnotacie.parametre().length > 1)
			{
				labelTitle = aAnotacie.parametre()[i];
			}
			JLabel l = new JLabel(labelTitle, JLabel.TRAILING);
			p.add(l);
			try
			{
				Method m = this.getClass().getMethod(CREATE_LABEL_METHOD, parameterType);
				JTextComponent componenet = (JTextComponent) m.invoke(this, (Object) null);
				aParams.add(componenet);
				l.setLabelFor(componenet);
				p.add(componenet);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex)
			{
				throw new ReflectorException("Nepodarilo sa vytvoriť panel pre metódu, chýba render metóda pre typ " + parameterType.getName() + "\n" + ex, ex);
			}
			i++;
		}
		SpringUtilities.makeCompactGrid(p, aParams.size(), 2, 3, 3, 3, 3);
		return p;
	}

	public void submitMethod()
	{
		Object[] params = new Object[0];
		try
		{
			params = new Object[aParams.size()];
			int i = 0;
			for (Class<?> parameterType : aMetoda.getParameterTypes())
			{
				try
				{
					Method m = Metoda.this.getClass().getMethod(VALIDATE_LABEL_METHOD, JTextComponent.class, parameterType);
					params[i] = m.invoke(Metoda.this, aParams.get(i), (Object) null);

				}
				catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
				{
					throw new ReflectorException("Nepodarilo sa validovať dáta pre metódu, chýba validate metóda pre typ " + parameterType.getName() + "\n" + ex, ex);
				}
				i++;
			}
			Object output = aMetoda.invoke(aReflektor.getObj(), params);
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

	public JTextComponent renderLabel()
	{
		return new JTextField();
	}

	public JTextComponent renderLabel(String dummy)
	{
		return renderLabel();
	}

	public JTextComponent renderLabel(Integer dummy)
	{
		return renderLabel();
	}

	public JTextComponent renderLabel(Date dummy)
	{
		return renderLabel();
	}

	public String validateLabel(JTextComponent paComponent, String dummy)
	{
		return paComponent.getText();
	}

	public Integer validateLabel(JTextComponent paComponent, Integer dummy)
	{
		return Integer.parseInt(paComponent.getText());
	}

	public Date validateLabel(JTextComponent paComponent, Date dummy) throws ParseException
	{
		return new SimpleDateFormat("dd.mm.yyyy").parse(paComponent.getText());
	}

}
