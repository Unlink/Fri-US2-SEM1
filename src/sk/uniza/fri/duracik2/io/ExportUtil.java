/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.io;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Unlink
 */
public class ExportUtil {

	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private static ExportUtil aInstance;

	public static ExportUtil getInstance() {
		synchronized (ExportUtil.class) {
			if (aInstance == null) {
				aInstance = new ExportUtil();
			}
			return aInstance;
		}
	}

	private ExportUtil() {
	}
	
	public String convertDate(Date date) {
		return DATE_FORMAT.format(date);
	}
	
	public Date convertDate(String date) {
        try {
            return DATE_FORMAT.parse(date);
        } catch (ParseException ex) {
            return null;
        }
    }
	
}
