package rpi.rpiface;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Copyright (C) 2013 Javier García, Julio Alberto González
 * <p>
 * This file is part of Rpi-Face. Rpi-Face is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * Rpi-Face is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * Rpi-Face. If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * <p>
 * Actividad que se encarga de hacer un get al servidor
 * 
 * @author Francisco Javier García Gómez y Julio Alberto González Marín
 * @version 1.0
 * @since 2013-03-26
 * 
 */

public class GetAsyncTask extends AsyncTask<String, Float, Boolean> {
	// Etiqueta del log
	private static final String LOGTAG = GetAsyncTask.class.getCanonicalName();

	private Context context;
	/**
	 * Crea un nuevo GetAsyncTask.
	 * @param context Contexto de la aplicación.
	 */
	public GetAsyncTask(Context context) {
		this.context=context;
	}
	/**
	 * Se ejecuta al principio
	 */
	protected void onPreExecute() {
		Log.i(LOGTAG + " Asynctask", "Async iniciada");
	}

	/**
	 * Se ejecuta en paralelo sin bloquear el sistema
	 * 
	 * @param param
	 *            Array de parámetros: el primero se corresponde con el
	 *            movimiento a realizar, el segundo con la dirección, el tercero
	 *            con el puerto y el cuarto con el path
	 */
	protected Boolean doInBackground(String... param) {
		// Se asignan variables a los parámetros del asynctask
		String value = param[0];
		String rpi = param[1];
		String port = param[2];
		String rpiPath = param[3];
		String rpiParam = param[4];
		// Se crea un cliente http
		HttpClient httpClient = new DefaultHttpClient();
		// Se crea una lista con los parámetros
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(rpiParam, value));
		// Se pasa la lista a un string debidamente formateado
		String paramString = URLEncodedUtils.format(params, "iso-8859-15");
		// Se crea la url
		String url = rpi + ":" + port + rpiPath + "?" + paramString;
		Log.i(LOGTAG + " Asynctask" + " Http get:", url);
		// Se crea un objeto httpget
		HttpGet httpGet = new HttpGet(url);
		try {
			// Se ejecuta el cliente pasándole como parámetro la petición get.
			HttpResponse response = httpClient.execute(httpGet);
			Log.i(LOGTAG + " Asynctask" + " Http Response:",
					response.toString());
		} catch (ClientProtocolException e) {
			Log.i(LOGTAG + " Asynctask" + " Http Response:",
					"Error en el protocolo httml");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			Log.i(LOGTAG + " Asynctask" + " Http Response:",
					"Conexión abortada. No se pudo contactar con el servidor");
			e.printStackTrace();
			return false;
		}
		return true;

	}

	/**
	 * Se ejecuta al final
	 * 
	 * @param result Resultado de la tarea en segundo plano
	 */
	protected void onPostExecute(Boolean result) {
		Log.i(LOGTAG + " Asynctask", "Async terminada");
		if (!result) {
			Toast.makeText(
					context,
					"Hubo problemas con el servidor. Reinténtelo más tarde.",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context,
					"Enviado correctamente.", Toast.LENGTH_SHORT).show();
		}
	}

}