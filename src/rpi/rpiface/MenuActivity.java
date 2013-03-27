package rpi.rpiface;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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
 * Se encarga de mostrar el menu principal de la aplicación
 * 
 * @author Francisco Javier García Gómez y Julio Alberto González Marín
 * @version 1.0
 * @since 2013-03-26
 * 
 */

public class MenuActivity extends Activity implements OnClickListener {
	/**
	 * Botón para para la sección para hablar con la cara
	 */
	Button botonHablar;
	/**
	 * Botón para para la sección para mover la cara
	 */
	Button botonMover;
	/**
	 * Botón para para la sección de votaciones
	 */
	Button botonVotar;

	/**
	 * Crea la actividad
	 * 
	 * @param savedInstanceState
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Pone la vista activity_menu
		setContentView(R.layout.activity_menu);
		// Asigna a cada botón su correspondiente botón gráfico
		botonHablar = (Button) findViewById(R.id.button_hablar);
		botonMover = (Button) findViewById(R.id.button_mover);
		botonVotar = (Button) findViewById(R.id.button_votar);
		// Pone detectores de clicks a cada botón
		botonHablar.setOnClickListener(this);
		botonMover.setOnClickListener(this);
		botonVotar.setOnClickListener(this);

	}

	/**
	 * Código que se ejecuta cuando se hace click en un objeto con detector
	 * 
	 * @param v
	 *            Objeto sobre el cual se ha hecho click
	 */
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.button_hablar:
			// Va a la sección de hablar
			Intent intentHablar = new Intent(this, SpeakFaceActivity.class);
			startActivity(intentHablar);
			break;
		case R.id.button_mover:
			// Va a la sección de mover
			Intent intentMover = new Intent(this, MoveFaceActivity.class);
			startActivity(intentMover);
			break;
		case R.id.button_votar:
			// Va a la sección de votar
			Intent intentVotar = new Intent(this, VoteActivity.class);
			startActivity(intentVotar);
			break;
		default:
			// Si se pulsa otro botón
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
	@Override
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
}
