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
import android.widget.Toast;

public class MoveFaceActivity extends Activity implements OnClickListener {

	private Button botonHappy;
	private Button botonSad;
	private Button botonAngry;
	private Button botonSurprised;
	private Button botonNeutral;
	private ConnectionStatus cd;
	Boolean internetConnection = false;

	private final String RPI_PARAM = "face";
	private static final String LOGTAG = MoveFaceActivity.class
			.getCanonicalName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_move);
		botonHappy = (Button) findViewById(R.id.button_happy);
		botonSad = (Button) findViewById(R.id.button_sad);
		botonAngry = (Button) findViewById(R.id.button_angry);
		botonSurprised = (Button) findViewById(R.id.button_surprised);
		botonNeutral = (Button) findViewById(R.id.button_neutral);

		botonHappy.setOnClickListener(this);
		botonSad.setOnClickListener(this);
		botonAngry.setOnClickListener(this);
		botonSurprised.setOnClickListener(this);
		botonNeutral.setOnClickListener(this);
		Log.i(LOGTAG, "Actividad creada");

	}



	public void onClick(View v) {
		cd = new ConnectionStatus(getApplicationContext());
		internetConnection = cd.isConnectingToInternet();
		if (!internetConnection) {
			Toast.makeText(getApplicationContext(),
					"No está conectado a internet.", Toast.LENGTH_SHORT).show();
			return;
		}

		switch (v.getId()) {
		case R.id.button_happy:
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón contento");
			doGet(0);
			break;
		case R.id.button_sad:
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón triste");
			doGet(1);
			break;
		case R.id.button_surprised:
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón sorprendido");
			doGet(2);
			break;
		case R.id.button_angry:
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón enfadado");
			doGet(3);
			break;
		case R.id.button_neutral:
			Log.i(LOGTAG + " onclick", "Se ha pulsado el botón aburrido");
			doGet(4);
			break;
		default:
			Toast.makeText(getApplicationContext(),
					"Error, esa opción no está implementada",
					Toast.LENGTH_SHORT).show();
			Log.w(LOGTAG + " onclick", "Opción no contemplada");
			break;
		}

	}

	private void doGet(int index) {
		AsyncTask<String, Float, Boolean> getAsyncTask = new GetAsyncTask()
				.execute(index + "", Url.RPI, Url.RPI_PORT, Url.RPI_PATH, RPI_PARAM);
		try {
			if (!getAsyncTask.get()) {
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
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_move, menu);
		return true;
	}

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
