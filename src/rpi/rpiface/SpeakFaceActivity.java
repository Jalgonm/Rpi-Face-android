package rpi.rpiface;

import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
	 * Botón para cambiar al modo de voz
	 */
	Button botonVoice;
	/**
	 * Botón para cambiar al modo texto
	 */
	Button botonText;
	/**
	 * Botón para grabar voz
	 */
	Button botonRecord;
	/**
	 * Botón para enviar el texto
	 */
	Button botonSend;
	/**
	 * Cuadro de texto para insertar el mensaje
	 */
	EditText editInsert;
	/**
	 * Parámetro usado en la petición postS
	 */
	private final String RPI_PARAM = "message";
	/**
	 * Objeto para comprobar el estado de le conexión
	 */
	private ConnectionStatus cd;
	/**
	 * Booleano que guardará el estado de la conexión
	 */
	Boolean internetConnection = false;

	/**
	 * Crea la actividad
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Pone la vista activity_speak
		setContentView(R.layout.activity_speak);
		Log.v(LOGTAG, "Se ha cargado la interfaz de SpeakFaceActivity");
		// Asigna a cada botón su correspondiente botón gráfico
		botonVoice = (Button) findViewById(R.id.button_voice);
		botonText = (Button) findViewById(R.id.button_text);
		botonRecord = (Button) findViewById(R.id.button_record);
		botonSend = (Button) findViewById(R.id.button_send);
		editInsert = (EditText) findViewById(R.id.editText_text);
		voiceMode();
		// Pone detectores de clicks a cada botón
		botonVoice.setOnClickListener(this);
		botonText.setOnClickListener(this);
		botonRecord.setOnClickListener(this);
		botonSend.setOnClickListener(this);
		Log.v(LOGTAG, "Actividad creada");
	}

	/**
	 * Switches the interface to the voice mode.
	 */
	private void voiceMode() {
		botonSend.setVisibility(View.GONE);
		editInsert.setVisibility(View.GONE);
		botonRecord.setVisibility(View.VISIBLE);
		Log.i(LOGTAG, "Se ha pasado al modo voz");
	}

	/**
	 * Switches the interface to the text mode.
	 */
	private void textMode() {
		botonSend.setVisibility(View.VISIBLE);
		editInsert.setVisibility(View.VISIBLE);
		botonRecord.setVisibility(View.GONE);
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
			// TODO hacer que reconozca voz
			// Empieza a grabar si se pulsa el botón de grabación
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón de grabación");

			break;
		case R.id.button_send:
			// Si no está conectado se sale del método y se avisa de ello.
			if (!internetConnection) {
				Toast.makeText(getApplicationContext(),
						"No está conectado a internet.", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			// Convierte a string lo que haya en el cuadro de texto
			String text = editInsert.getText().toString();
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón de enviar");
			Log.v(LOGTAG + " Texto a enviar:", text);
			// Comprueba el estado de la conexión
			cd = new ConnectionStatus(getApplicationContext());
			internetConnection = cd.isConnectingToInternet();
			// Se crea un postAsynctask que hará la petición post al servidor
			AsyncTask<String, Void, Boolean> postAsyncTask = new PostAsyncTask();
			// Se ejecuta el asynctask pasándole los parámetros correspondientes
			postAsyncTask.execute(text, Url.RPI, Url.RPI_PORT, Url.RPI_PATH,
					RPI_PARAM);
			try {
				// Si no hay conexión con el servidor se avisa de ello
				if (!postAsyncTask.get()) {
					Toast.makeText(
							getApplicationContext(),
							"Hubo problemas con el servidor. Reinténtelo más tarde.",
							Toast.LENGTH_SHORT).show();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			Toast.makeText(getApplicationContext(),
					"Opción aún no implementada", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
