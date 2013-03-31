package rpi.rpiface;

import java.util.ArrayList;
import java.util.List;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
 * * Esta clase se encarga de mostrar la interfaz para hablar con la cara tanto
 * por texto como por voz y de recoger los métodos necesarios.
 * 
 * @author Francisco Javier García Gómez y Julio Alberto González Marín
 * @version 1.0
 * @since 2013-03-26
 * 
 */

public class SpeakFaceActivity extends Activity implements OnClickListener {
	/**
	 * Etiqueta para el log
	 */
	private static final String LOGTAG = SpeakFaceActivity.class
			.getCanonicalName();
	/**
	 * Etiqueta del modo actual para guardar estado
	 */
	private static final String SPEAK_STATE_MODE = "rpi.rpiface.speak.state.mode";
	/**
	 * Etiqueta del texto actualmente introducido para guardar estado
	 */
	private static final String SAVED_TEXT = "rpi.rpiface.text.saved";
	/**
	 * Botón para cambiar al modo de voz
	 */
	private Button botonVoice;
	/**
	 * Botón para cambiar al modo texto
	 */
	private Button botonText;
	/**
	 * Botón para grabar voz
	 */
	private Button botonRecord;
	/**
	 * Botón para enviar el texto
	 */
	private Button botonSend;
	/**
	 * Cuadro de texto para insertar el mensaje
	 */
	private EditText editInsert;
	/**
	 * Parámetro usado en la petición post
	 */
	private final String RPI_PARAM = "message";
	/**
	 * Objeto para comprobar el estado de le conexión
	 */
	private ConnectionStatus cd;
	/**
	 * Booleano que guardará el estado de la conexión
	 */
	private Boolean internetConnection = false;

	/**
	 * Variable para reconocer actividad de reconocimiento de voz
	 */
	private static final int VR_REQUEST = 31415;
	/**
	 * Preferencias del usuario
	 */
	private SharedPreferences preferences;
	/**
	 * Nombre de la aplicación de reconocimiento de voz
	 */
	final String appName = "com.google.android.voicesearch";
	/**
	 * Guarda el modo actual para su posterior recuperación
	 */
	private boolean currentMode;

	/**
	 * Crea la actividad
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Pone la vista activity_speak
		setContentView(R.layout.activity_speakmod);
		currentMode = true;
		Log.v(LOGTAG, "Se ha cargado la interfaz de SpeakFaceActivity");
		// Asigna a cada botón su correspondiente botón gráfico
		botonVoice = (Button) findViewById(R.id.button_voice);
		botonText = (Button) findViewById(R.id.button_text);
		botonRecord = (Button) findViewById(R.id.button_record);
		botonSend = (Button) findViewById(R.id.button_send);
		editInsert = (EditText) findViewById(R.id.editText_text);

		// Pone detectores de clicks a cada botón y al reconocedor
		botonVoice.setOnClickListener(this);
		botonText.setOnClickListener(this);
		botonSend.setOnClickListener(this);

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		restoreState(savedInstanceState, preferences);
		Log.v(LOGTAG, "Actividad creada");

	}

	/**
	 * Recupera el estado de la actividad. Si hay un estado guardado (por
	 * ejemplo, después de un giro de pantalla), recupera el modo en el que
	 * estaba la actividad antes del evento. Si no, pone el modo por defecto
	 * establecido en las preferencias
	 * 
	 * @param savedInstanceState
	 *            Estado guardado
	 * @param preferences
	 *            Preferencias
	 */
	private void restoreState(Bundle savedInstanceState,
			SharedPreferences preferences) {
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(SPEAK_STATE_MODE)) {
			// Estado guardado
			if (savedInstanceState.getBoolean(SPEAK_STATE_MODE, true)) {
				textMode();
				editInsert.setText(savedInstanceState.getString(SAVED_TEXT));
			} else {
				voiceMode();
			}
		} else if (preferences.getBoolean(PreferencesActivity.PREFS_MODE, true)) {
			textMode();
		} else {
			voiceMode();
		}
	}

	/**
	 * Se ejecuta al guardar estado debido a un evento destructivo (como un giro
	 * de pantalla o un paso a segundo plano). Guarda el estado de la actividad
	 * (en este caso, el modo actual).
	 * 
	 * @param outState
	 *            Dónde guardar el estado.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(SPEAK_STATE_MODE, currentMode);
		outState.putString(SAVED_TEXT, editInsert.getText().toString());
	}

	/**
	 * Cambia la interfaz al modo voz
	 */
	private void voiceMode() {
		botonSend.setVisibility(View.GONE);
		editInsert.setVisibility(View.GONE);
		botonRecord.setVisibility(View.VISIBLE);
		currentMode = false;
		Log.i(LOGTAG, "Se ha pasado al modo voz");
		botonRecord.requestFocus();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Comprueba si se puede hacer reconocimiento de voz
		PackageManager packManager = getPackageManager();
		List<ResolveInfo> intActivities = packManager.queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (intActivities.size() != 0) {
			// Puede hacerse reconocimiento de voz
			Log.v(LOGTAG, "El dispositivo permite hacer reconocimiento");
			botonRecord.setOnClickListener(this);

		} else {
			// El reconocimiento de voz no está soportado
			Log.v(LOGTAG, "El dispositivo no permite hacer reconocimiento");
			botonRecord.setEnabled(false);

			AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
			dialogo.setTitle("No dispone del software de reconocimiento de voz");
			dialogo.setMessage("¿Le gustaría instalarlo?");
			dialogo.setCancelable(false);
			dialogo.setPositiveButton("Si",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogo1, int id) {
							aceptar();
						}
					});
			dialogo.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogo1, int id) {
							cancelar();
						}
					});
			dialogo.show();

		}

	}

	/**
	 * Se ejecuta si se pulsa el botón aceptar en el cuadro de diálogo, instala
	 * el software necesario para el reconocimiento de voz
	 */
	private void aceptar() {
		try {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + appName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id="
							+ appName)));
		}
	}

	/**
	 * Se ejecuta si se pulsa el botón cancelar en el cuadro de diálogo,
	 * advierte que de este modo el modo voz no puede realizarse y cambia al
	 * modo texto
	 */
	private void cancelar() {
		Toast.makeText(this,
				"Sin el software no está disponible esta funcionalidad",
				Toast.LENGTH_LONG).show();
		textMode();
	}

	/**
	 * Cambia la interfaz al modo texto
	 */
	private void textMode() {
		botonSend.setVisibility(View.VISIBLE);
		editInsert.setVisibility(View.VISIBLE);
		botonRecord.setVisibility(View.GONE);
		currentMode = true;
		editInsert.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		Log.i(LOGTAG, "Se ha pasado al modo texto");
	}

	/**
	 * Código que se ejecuta cuando se hace click en un objeto con detector
	 * 
	 * @param v
	 *            Objeto sobre el cual se ha hecho click
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_voice:
			// Cambia al modo voz si se pulsa el botón de voz
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón de voz");
			voiceMode();
			break;
		case R.id.button_text:
			// Cambia al modo texto si se pulsa el botón de texto
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón de texto");
			textMode();
			break;
		case R.id.button_record:
			// Empieza a grabar si se pulsa el botón de grabación
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón de grabación");
			listenToSpeech();
			break;
		case R.id.button_send:
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón de enviar");
			// Comprueba el estado de la conexión
			cd = new ConnectionStatus(getApplicationContext());
			internetConnection = cd.isConnectingToInternet();
			// Si no está conectado se sale del método y se avisa de ello.
			if (!internetConnection) {
				Toast.makeText(getApplicationContext(),
						"No está conectado a internet.", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			// Convierte a string lo que haya en el cuadro de texto
			String text = editInsert.getText().toString();
			doPost(text);
			break;
		default:
			// Si se pulsa otro botón
			Log.w(LOGTAG + " onclick", "Esa función no está implementada");
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
	private void doPost(String message) {
		String rpi = preferences.getString(PreferencesActivity.PREFS_URL,
				Url.RPI);
		String rpiPort = preferences.getString(PreferencesActivity.PREFS_PORT,
				Url.RPI_PORT);
		String rpiPath = preferences.getString(PreferencesActivity.PREFS_PATH,
				Url.RPI_PATH);
		Log.v(LOGTAG + " Texto a enviar:", message);
		// Se crea un postAsynctask que hará la petición post al servidor
		AsyncTask<String, Void, Boolean> postAsyncTask = new PostAsyncTask(
				getApplicationContext());
		// Se ejecuta el asynctask pasándole los parámetros correspondientes
		postAsyncTask.execute(message, rpi, rpiPort, rpiPath, RPI_PARAM);
	}

	/**
	 * Se ejecuta cada vez que se llama al menú estando en la actividad
	 * 
	 * @param menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_speak, menu);
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
	 * Manda a la aplicación que escuche al usuario
	 */
	private void listenToSpeech() {

		// empieza el reconocimiento de voz
		Intent listenIntent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// indica el paquete
		listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				getClass().getPackage().getName());
		// Mensaje a mostrar mientras está escuchando
		listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Diga la frase a enviar a la cara");
		// Establece el modelo de lenguaje
		listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// especifica el número de resultados esperados
		listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

		// empieza a escuchar
		startActivityForResult(listenIntent, VR_REQUEST);
	}

	/**
	 * Devuelve el resultado del reconocimiento
	 * 
	 * @param requestCode
	 *            Código de la actuvudad
	 * @param resultCode
	 *            Código que indica si la actividad se ha realizado
	 *            correctamente
	 * @param data
	 *            Resultado del reconocimiento
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
			// Guarda los resultados en un array list
			data.getDataString();
			ArrayList<String> suggestedWords = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String mostProbableMessage = suggestedWords.get(0);
			Toast.makeText(getApplicationContext(), mostProbableMessage,
					Toast.LENGTH_SHORT).show();
			doPost(mostProbableMessage);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
