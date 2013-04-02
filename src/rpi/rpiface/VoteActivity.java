package rpi.rpiface;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
 * Esta clase se encarga de realizar las votaciones al servidor
 * 
 * @author Francisco Javier García Gómez y Julio Alberto González Marín
 * @version 1.0
 * @since 2013-03-26
 * 
 */

public class VoteActivity extends Activity implements OnClickListener {

	/**
	 * Etiqueta para el log
	 */
	private static final String LOGTAG = SpeakFaceActivity.class
			.getCanonicalName();
	/**
	 * Parámetro usado en la petición post
	 */
	private static final String RPI_PARAM = "vote";
	/**
	 * Nombre de la variable para obtener el recuento de votos
	 */
	private static final String KEY_QUERY = "query";
	/**
	 * Valor de la variable para obtener el recuento de votos
	 */
	private static final String VALUE_VOTECOUNT = "votecount";
	/**
	 * Nombre de la variable que guarda el número de votos positivos en cambios
	 * de estado
	 */
	private static final String VOTE_STATE_PLUS = "rpi.rpiface.vote.plus";
	/**
	 * Nombre de la variable que guarda el número de votos negativos en cambios
	 * de estado
	 */
	private static final String VOTE_STATE_MINUS = "rpi.rpiface.vote.minus";
	/**
	 * Botón para votar positivo
	 */
	private Button botonYes;
	/**
	 * Botón para votar negativo
	 */
	private Button botonNo;
	/**
	 * Preferencias del usuario
	 */
	private SharedPreferences preferences;
	private int plus = 0;
	private int minus = 0;

	/**
	 * Crea la actividad
	 * 
	 * @param savedInstanceState
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vote);
		botonYes = (Button) findViewById(R.id.button_yes);
		botonNo = (Button) findViewById(R.id.button_no);
		botonYes.setOnClickListener(this);
		botonNo.setOnClickListener(this);
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		restoreState(savedInstanceState, preferences);
	}

	/**
	 * Recupera el recuento de votos si la actividad se está recuperando de un
	 * cambio de estado, o lo obtiene del servidor si la actividad se está
	 * creando.
	 * 
	 * @param savedInstanceState
	 *            Estado guardado de la actividad, o <b>null</b> si la actividad
	 *            se está creando.
	 * @param preferences
	 *            Preferencias de la aplicación.
	 */
	private void restoreState(Bundle savedInstanceState,
			SharedPreferences preferences) {
		if (savedInstanceState == null) {
			updateVoteCount(preferences);
			return;
		}
		if (savedInstanceState.containsKey(VOTE_STATE_PLUS)
				&& savedInstanceState.containsKey(VOTE_STATE_MINUS)) {
			botonYes.setText(savedInstanceState.getInt(VOTE_STATE_PLUS, 0));
			botonNo.setText(savedInstanceState.getInt(VOTE_STATE_MINUS, 0));
		} else {
			updateVoteCount(preferences);
		}
	}

	/**
	 * Guarda el recuento de votos en los cambios de estado.
	 * 
	 * @param outState
	 *            Estructura donde se guardarán los valores.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(VOTE_STATE_PLUS, plus);
		outState.putInt(VOTE_STATE_MINUS, minus);
	}

	/**
	 * Actualiza y muestra el recuento de votos
	 * 
	 * @param preferences
	 *            Preferencias de la aplicación
	 */
	private void updateVoteCount(SharedPreferences preferences) {
		String rpi = preferences.getString(PreferencesActivity.PREFS_URL,
				Url.RPI);
		String rpiPort = preferences.getString(PreferencesActivity.PREFS_PORT,
				Url.RPI_PORT);
		String rpiPath = preferences.getString(PreferencesActivity.PREFS_PATH,
				Url.RPI_PATH);
		new GetVoteCount(getApplicationContext()).execute(VALUE_VOTECOUNT, rpi,
				rpiPort, rpiPath, KEY_QUERY);
	}

	/**
	 * Código que se ejecuta cuando se hace click en un objeto con detector
	 * 
	 * @param v
	 *            Objeto sobre el cual se ha hecho click
	 */
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button_yes:
			Log.v(LOGTAG, "Se ha pulsado en el botón positivo");
			doPost(true);
			break;
		case R.id.button_no:
			Log.v(LOGTAG, "Se ha pulsado en el botón negativo");
			doPost(false);
			break;

		default:
			Toast.makeText(getApplicationContext(),
					"Error, esa opción no está implementada",
					Toast.LENGTH_SHORT).show();
			break;
		}

	}

	/**
	 * Código que se encarga de establecer los parámetros de la petición post
	 * 
	 * @param vote
	 *            Si es 1 es un voto positivo, si es 0 es un voto negativo
	 */
	private void doPost(boolean vote) {

		String rpi = preferences.getString(PreferencesActivity.PREFS_URL,
				Url.RPI);
		String rpiPort = preferences.getString(PreferencesActivity.PREFS_PORT,
				Url.RPI_PORT);
		String rpiPath = preferences.getString(PreferencesActivity.PREFS_PATH,
				Url.RPI_PATH);
		Log.v(LOGTAG + " Voto a enviar:", vote ? "Positivo" : "Negativo");
		// Se crea un postAsynctask que hará la petición post al servidor
		PostVote postVote = new PostVote(getApplicationContext());
		// Se ejecuta el asynctask pasándole los parámetros correspondientes
		postVote.execute(vote ? "1" : "0", rpi, rpiPort, rpiPath, RPI_PARAM);
	}

	/**
	 * Se ejecuta cada vez que se llama al menú estando en la actividad
	 * 
	 * @param menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}

	/**
	 * Se ejecuta cuando se pulsa un botón del menu
	 * 
	 * @param item
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent intentPrefs = new Intent(this, PreferencesActivity.class);
			startActivity(intentPrefs);
			return true;
		case R.id.menu_exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Obtiene asíncronamente el recuento de votos
	 * 
	 * @author Francisco Javier García Gómez y Julio Alberto González Marín
	 * @version 1.0
	 * @since 2013-04-01
	 */
	private class GetVoteCount extends AsyncTask<String, Void, Boolean> {
		/**
		 * Contexto de la aplicación
		 */
		private Context context;

		/**
		 * Crea un nuevo GetAsyncTask.
		 * 
		 * @param context
		 *            Contexto de la aplicación.
		 */
		public GetVoteCount(Context context) {
			this.context = context;
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
		 *            movimiento a realizar, el segundo con la dirección, el
		 *            tercero con el puerto y el cuarto con el path
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
			HttpResponse response = null;
			try {
				// Se ejecuta el cliente pasándole como parámetro la petición
				// get.
				response = httpClient.execute(httpGet);
				Log.i(LOGTAG + " Asynctask" + " Http Response:",
						response.toString());
			} catch (ClientProtocolException e) {
				Log.i(LOGTAG + " Asynctask" + " Http Response:",
						"Error en el protocolo http");
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				Log.i(LOGTAG + " Asynctask" + " Http Response:",
						"Conexión abortada. No se pudo contactar con el servidor");
				e.printStackTrace();
				return false;
			}
			Scanner count;
			try {
				count = new Scanner(response.getEntity().getContent());
				plus = count.nextInt();
				minus = count.nextInt();
				count.close();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;

		}

		/**
		 * Se ejecuta al final
		 * 
		 * @param result
		 *            Resultado de la tarea en segundo plano
		 */
		protected void onPostExecute(Boolean result) {
			Log.i(LOGTAG + " Asynctask", "Async terminada");
			if (!result) {
				Toast.makeText(
						context,
						"Hubo problemas con el servidor. Reinténtelo más tarde.",
						Toast.LENGTH_SHORT).show();
			} else {
				botonYes.setText("Sí (" + plus + ")");
				botonNo.setText("No (" + minus + ")");
			}
		}

	}

	/**
	 * Envía un voto al servidor.
	 * 
	 * @author Francisco Javier García Gómez y Julio Alberto González Marín
	 * @version 1.0
	 * @since 2013-04-01
	 */
	public class PostVote extends AsyncTask<String, Void, Boolean> {
		/**
		 * Contexto de la aplicación
		 */
		private Context context;

		/**
		 * Crea un nuevo PostAsyncTask.
		 * 
		 * @param context
		 *            Contexto de la aplicación.
		 */
		public PostVote(Context context) {
			this.context = context;
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
		 *            texto a envir, el segundo con la dirección, el tercero con
		 *            el puerto y el cuarto con el path
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

			// String paramString = URLEncodedUtils.format(params,
			// "iso-8859-15");
			Log.i(LOGTAG + " Asynctask" + " Http get:", url);

			try {
				// Se le pasan los parámetros debidamente formateados al
				// httpPost
				httpPost.setEntity(new UrlEncodedFormEntity(params,
						"iso-8859-15"));
				// Se ejecuta el cliente pasándole como parámetro la petición
				// post.
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
		 * @param result
		 *            Resultado de la tarea en segundo plano.
		 */
		protected void onPostExecute(Boolean result) {
			Log.i(LOGTAG + " Asynctask", "Async terminada");
			if (!result) {
				Toast.makeText(
						context,
						"Hubo problemas con el servidor. Reinténtelo más tarde.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "Enviado correctamente.",
						Toast.LENGTH_SHORT).show();
				updateVoteCount(preferences);
			}
		}

	}
}
