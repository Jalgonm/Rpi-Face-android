package rpi.rpiface;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
 * Clase para ver y modificar las preferencias.
 * 
 * @author Francisco Javier García Gómez y Julio Alberto González Marín
 * @version 1.0
 * @since 2013-03-26
 * 
 */
public class PreferencesActivity extends Activity implements OnClickListener {
	/**
	 * Etiqueta del log
	 */
	private static final String LOGTAG = PreferencesActivity.class
			.getCanonicalName();
	/**
	 * Nombre del campo para la URL en las preferencias
	 */
	public static final String PREFS_URL = "rpi.rpiface.prefs.url";
	/**
	 * Nombre del campo para el puerto en las preferencias
	 */
	public static final String PREFS_PORT = "rpi.rpiface.prefs.port";
	/**
	 * Nombre del campo para el path en las preferencias
	 */
	public static final String PREFS_PATH = "rpi.rpiface.prefs.path";
	/**
	 * Nombre del campo para el modo en las preferencias
	 */
	public static final String PREFS_MODE = "rpi.rpiface.prefs.mode";
	/**
	 * Preferencias del usuario
	 */
	private SharedPreferences preferences;
	/**
	 * Cuadro de texto para editar la url
	 */
	private EditText editUrl;
	/**
	 * Cuadro de texto para editar el puerto
	 */
	private EditText editPort;
	/**
	 * Cuadro de texto para editar el path
	 */
	private EditText editPath;
	/**
	 * Radiogroup para seleccionar el modo por defecto de la actividad SpeakFace
	 */
	private RadioGroup radioMode;

	/**
	 * Crea la actividad para modificar las preferencias
	 * 
	 * @param savedInstanceState
	 *            Estado guardado de la actividad. No se usa.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		// asignar eventos a los botones
		((Button) findViewById(R.id.prefs_button_ok)).setOnClickListener(this);
		((Button) findViewById(R.id.prefs_button_cancel))
				.setOnClickListener(this);
		((Button) findViewById(R.id.prefs_button_restoreDefault))
				.setOnClickListener(this);
		// obtener referencias a los campos
		editUrl = (EditText) findViewById(R.id.prefs_field_url);
		editPort = (EditText) findViewById(R.id.prefs_field_port);
		editPath = (EditText) findViewById(R.id.prefs_field_path);
		radioMode = (RadioGroup) findViewById(R.id.prefs_radio_mode);
		// cargar preferencias
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		loadPreferences(preferences);
		Log.v(LOGTAG, "Actividad iniciada");

	}

	/**
	 * Carga los valores de las preferencias en los campos.
	 * 
	 * @param preferences
	 *            Objeto SharedPreferences con las preferencias.
	 */
	private void loadPreferences(SharedPreferences preferences) {
		editUrl.setText(preferences.getString(PREFS_URL, Url.RPI));
		editPort.setText(preferences.getString(PREFS_PORT, Url.RPI_PORT));
		editPath.setText(preferences.getString(PREFS_PATH, Url.RPI_PATH));
		radioMode
				.check(preferences.getBoolean(PREFS_MODE, true) ? R.id.prefs_radio_textMode
						: R.id.prefs_radio_voiceMode);
		Log.v(LOGTAG, "Preferencias cargadas en cada campo");
	}

	/**
	 * Crea el menú de opciones
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(LOGTAG, "Se pulsa el botón de menu");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_preferences, menu);
		return true;
	}

	/**
	 * Intenta guardar los valores de los campos en las preferencias. Si algún
	 * valor es incorrecto, muestra un mensaje de error y devuelve <b>false</b>.
	 * En caso contrario, guarda los valores y devuelve <b>true</b>
	 * 
	 * @param preferences
	 *            Dónde guardar las preferencias
	 * @return <p>
	 *         <b>true</b> si se guardó con éxito.
	 *         </p>
	 *         <p>
	 *         <b>false</b> si hubo algún error. Si este método devuelve
	 *         <b>false</b>, el objeto <i>preferences</i> no ha sido modificado.
	 *         </p>
	 */
	private boolean savePreferences(SharedPreferences preferences) {
		// obtener valores de los campos
		String url = editUrl.getText().toString();
		String port = editPort.getText().toString();
		String path = editPath.getText().toString();
		boolean mode = (radioMode.getCheckedRadioButtonId() == R.id.prefs_radio_textMode);
		// comprobar errores básicos
		if (!url.startsWith("http://")) {
			url = "http://" + url;
		}
		try {
			int portNumber = Integer.parseInt(port);
			if (portNumber < 0 || portNumber > 65535) {
				Toast.makeText(this, R.string.prefs_error_port,
						Toast.LENGTH_LONG).show();
				return false;
			}
		} catch (NumberFormatException e) {
			Toast.makeText(this, R.string.prefs_error_port, Toast.LENGTH_LONG)
					.show();
			return false;
		}
		// guardar valores en preferences
		preferences.edit().putString(PREFS_URL, url)
				.putString(PREFS_PORT, port).putString(PREFS_PATH, path)
				.putBoolean(PREFS_MODE, mode).commit();
		Log.v(LOGTAG, "Se han guardado las preferencias");
		return true;

	}

	/**
	 * Restablece los valores predeterminados en los campos de URL, puerto y
	 * path.
	 */
	private void restoreDefault() {
		editUrl.setText(Url.RPI);
		editPort.setText(Url.RPI_PORT);
		editPath.setText(Url.RPI_PATH);
		Log.v(LOGTAG, "Se han cargado las preferencias por defecto");
	}

	/**
	 * Se ejecuta cuando el usuario pulsa uno de los botones.
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.prefs_button_ok:
			Log.v(LOGTAG, "Se pulsa el botón de ok");
			if (savePreferences(preferences)) {
				finish();
			}
			break;
		case R.id.prefs_button_cancel:
			Log.v(LOGTAG, "Se pulsa el botón de cancel");
			finish();
			break;
		case R.id.prefs_button_restoreDefault:
			Log.v(LOGTAG, "Se pulsa el botón de restaurar por defecto");
			restoreDefault();
			break;
		default:
			Log.e(LOGTAG, "Error, opción no implementada aún");
			break;
		}

	}

}
