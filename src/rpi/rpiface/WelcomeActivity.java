package rpi.rpiface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;

public class WelcomeActivity extends Activity {

	private static final String LOGTAG = WelcomeActivity.class
			.getCanonicalName();
	private static final long SPLASH_DISPLAY_LENGTH = 5000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		Handler handler = new Handler();
		handler.postDelayed(getRunnableStartApp(), SPLASH_DISPLAY_LENGTH);
		Log.i(LOGTAG, "Actividad creada");

	}

	private Runnable getRunnableStartApp() {
		Runnable runnable = new Runnable() {
			public void run() {

				Intent intent = new Intent(WelcomeActivity.this,
						MenuActivity.class);
				startActivity(intent);
				finish();
			}
		};
		return runnable;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_move, menu);
		Log.i(LOGTAG, "Se ha creado el menú con éxito");
		return true;
	}

}
