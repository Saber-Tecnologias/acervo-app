package br.ufpe.sabertecnologias.acervoapp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Timestamp;

public abstract class DateUtil {
	
	@Deprecated
	public static String convertDate(long dataLong){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSS");
		return df.format(new Date(dataLong));
	}

	@Deprecated
	public static long convertDate(String dataString){
		Date d = null;
		long longData = 0;
			try {
				//d = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(dataString);
				d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSS").parse(dataString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(d != null){
				longData = d.getTime();
			}
			return longData;
	}

	@Deprecated
	public static String convertDateView(String dataString){
		Date d = null;
		String longData = "";
			try {
				d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSS").parse(dataString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(d != null){
				longData = new SimpleDateFormat("dd/MM/yyyy").format(d);
			}
			return longData;
	}
	
	/**
	 * Método para retornar o offset do timezone para envio via json
	 * @param timestampMilis System.currentTimeMilis
	 * @return string com o offset
	 */
	private static String getTimestampOffset(long timestampMilis)
	{
		TimeZone tz = TimeZone.getDefault();		
		String offset =""+(tz.getOffset(timestampMilis)/(1000*60));
		
		return offset;
	}
	
	private static String convertToTimestampISO8601(long timestampMilis)
	{
		TimeZone tz = TimeZone.getDefault();		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSSZ", Locale.getDefault()); //2015-06-18 22:23:53.586-0300
		//TODO: Verificar se o Locale.getDefault acima pode rolar algo esquisito nos devices. Acredito que não, mas....
		
		df.setTimeZone(tz);
		
		return df.format(new Date());				
	}
	
	/**
	 * Método para retornar o objeto com timestamp + timezone
	 * @param timestampMilis System.currentTimeMilis
	 * @return Timestamp object
	 */
	public static Timestamp getTimestampObject(long timestampMilis){
		Timestamp timestamp = new Timestamp(convertToTimestampISO8601(timestampMilis),
				getTimestampOffset(timestampMilis));
		return timestamp;
	}



	public static long getMS(Timestamp tms){
		long ll;
		try {
			ll = convertToMS(tms.getDatetime());
		} catch (ParseException e){
			ll = -1;
		}
		return ll;
	}

	private static long convertToMS(String datetime) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSS").parse(datetime).getTime();
	}


	public static String getDefaultFormat(String date) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSS").parse(date).toString();
	}

}
