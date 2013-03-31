package rpi.rpiface;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
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
	private final String RPI_PARAM = "vote";
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
		AsyncTask<String, Void, Boolean> postAsyncTask = new PostAsyncTask(
				getApplicationContext());
		// Se ejecuta el asynctask pasándole los parámetros correspondientes
		postAsyncTask.execute(vote ? "1" : "0", rpi, rpiPort, rpiPath,
				RPI_PARAM);
	}

	/**
	 * Se ejecuta cada vez que se llama al menú estando en la actividad
	 * 
	 * @param menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_vote, menu);
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
}
