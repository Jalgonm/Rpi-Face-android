package rpi.rpiface;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
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
 * Actividad que se encarga de hacer un post al servidor
 * 
 * @author Francisco Javier García Gómez y Julio Alberto González Marín
 * @version 1.0
 * @since 2013-03-26
 * 
 */

public class PostAsyncTask extends AsyncTask<String, Void, Boolean> {
	// Etiqueta del log
	private static final String LOGTAG = PostAsyncTask.class.getCanonicalName();
	
	private Context context;
	/**
	 * Crea un nuevo PostAsyncTask.
	 * @param context Contexto de la aplicación.
	 */
	public PostAsyncTask(Context context) {
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
	 *            Array de parámetros: el primero se corresponde con el texto a
	 *            envir, el segundo con la dirección, el tercero con el puerto y
	 *            el cuarto con el path
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
		// Se crea la url
		String url = rpi + ":" + port + rpiPath;
		// Se crea un objeto httpPost
		HttpPost httpPost = new HttpPost(url);
		// Se crea una lista con los parámetros
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair(rpiParam, value));

		// String paramString = URLEncodedUtils.format(params, "iso-8859-15");
		Log.i(LOGTAG + " Asynctask" + " Http get:", url);

		try {
			// Se le pasan los parámetros debidamente formateados al httpPost
			httpPost.setEntity(new UrlEncodedFormEntity(params, "iso-8859-15"));
			// Se ejecuta el cliente pasándole como parámetro la petición post.
			HttpResponse response = httpClient.execute(httpPost);
			Log.i(LOGTAG + " Asynctask" + " Http Response:",
					response.toString());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Se ejecuta al final
	 * 
	 * @param result Resultado de la tarea en segundo plano.
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