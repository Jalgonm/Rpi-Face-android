package rpi.rpiface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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
 * Esta clase se encarga de mostrar una pantalla de bienvenida
 * 
 * @author Francisco Javier García Gómez y Julio Alberto González Marín
 * @version 1.0
 * @since 2013-03-26
 * 
 */

public class WelcomeActivity extends Activity {
	/**
	 * Etiqueta usada en el log
	 */
	private static final String LOGTAG = WelcomeActivity.class
			.getCanonicalName();
	/**
	 * Duración de la pantalla de bienvenida
	 */
	private static final long SPLASH_DISPLAY_LENGTH = 3500;

	/**
	 * Crea la actividad
	 * 
	 * @param savedInstanceState
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Pone la vista activity_welcome
		setContentView(R.layout.activity_welcome);
		Handler handler = new Handler();
		handler.postDelayed(getRunnableStartApp(), SPLASH_DISPLAY_LENGTH);
		Log.i(LOGTAG, "Actividad creada");

	}

	/**
	 * Clase runnable que se ejecutará el terminar el tiempo establacido
	 * 
	 * @return Runnable que inicia la actividad principal.
	 */
	private Runnable getRunnableStartApp() {
		Runnable runnable = new Runnable() {
			/**
			 * Inicia la actividad principal
			 */
			public void run() {

				Intent intent = new Intent(WelcomeActivity.this,
						MenuActivity.class);
				startActivity(intent);
				finish();
			}
		};
		return runnable;
	}
}
