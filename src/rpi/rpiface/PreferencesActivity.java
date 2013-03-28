package rpi.rpiface;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class PreferencesActivity extends Activity implements OnClickListener {

	public static final String PREFS_URL = "rpi.rpiface.prefs.url";
	public static final String PREFS_PORT = "rpi.rpiface.prefs.port";
	public static final String PREFS_PATH = "rpi.rpiface.prefs.path";
	public static final String PREFS_MODE = "rpi.rpiface.prefs.mode";

	private SharedPreferences preferences;
	private EditText editUrl;
	private EditText editPort;
	private EditText editPath;
	private RadioGroup radioMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);

		((Button) findViewById(R.id.prefs_button_ok)).setOnClickListener(this);
		((Button) findViewById(R.id.prefs_button_cancel))
				.setOnClickListener(this);

		editUrl = (EditText) findViewById(R.id.prefs_field_url);
		editPort = (EditText) findViewById(R.id.prefs_field_port);
		editPath = (EditText) findViewById(R.id.prefs_field_path);
		radioMode = (RadioGroup) findViewById(R.id.prefs_radio_mode);

		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		loadPreferences(preferences);
	}

	private void loadPreferences(SharedPreferences preferences) {
		editUrl.setText(preferences.getString(PREFS_URL, Url.RPI));
		editPort.setText(preferences.getString(PREFS_PORT, Url.RPI_PORT));
		editPath.setText(preferences.getString(PREFS_PATH, Url.RPI_PATH));
		radioMode
				.check(preferences.getBoolean(PREFS_MODE, true) ? R.id.prefs_radio_textMode
						: R.id.prefs_radio_voiceMode);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_preferences, menu);
		return true;
	}

	private boolean savePreferences(SharedPreferences preferences) {
		String url = editUrl.getText().toString();
		String port = editPort.getText().toString();
		String path = editPath.getText().toString();
		boolean mode = (radioMode.getCheckedRadioButtonId() == R.id.prefs_radio_textMode);
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
		preferences.edit().putString(PREFS_URL, url)
				.putString(PREFS_PORT, port).putString(PREFS_PATH, path)
				.putBoolean(PREFS_MODE, mode).commit();
		return true;

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.prefs_button_ok:
			if (savePreferences(preferences))
				finish();
			break;
		case R.id.prefs_button_cancel:
			finish();
			break;
		}

	}

}
