/* 
 * Copyright 2010-2013 Eric Kok et al.
 * 
 * Transdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Transdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Transdroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.transdroid.core.seedbox;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.transdroid.R;
import org.transdroid.core.app.settings.ServerSetting;
import org.transdroid.daemon.Daemon;
import org.transdroid.daemon.OS;
import org.transdroid.daemon.util.HttpHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.io.InputStream;

/**
 * Implementation of {@link SeedboxSettings} for a Xirvik shared seedbox.
 * @author Eric Kok
 */
public class XirvikSharedSettings extends SeedboxSettingsImpl implements SeedboxSettings {

	public static final String TAG = "XirvikSharedSettings";
	
	@Override
	public String getName() {
		return "Xirvik shared";
	}

	@Override
	public ServerSetting getServerSetting(SharedPreferences prefs, int orderOffset, int order) {
		// @formatter:off
		String server = prefs.getString("seedbox_xirvikshared_server_" + order, null);
		if (server == null) {
			return null;
		}
		Daemon type = Daemon.fromCode(prefs.getString("seedbox_xirvikshared_client_" + order, null));
		String user = prefs.getString("seedbox_xirvikshared_user_" + order, null);
		String pass = prefs.getString("seedbox_xirvikshared_pass_" + order, null);
		String rpc = prefs.getString("seedbox_xirvikshared_rpc_" + order, null);
		String authToken = prefs.getString("seedbox_xirvikshared_token_" + order, null);
		return new ServerSetting(
				orderOffset + order,
				prefs.getString("seedbox_xirvikshared_name_" + order, null), 
				type,
				server,
				null,
				0,
				null,
				443, 
				true, 
				false,
				null,
				rpc,
				true, 
				user,
				pass, 
				null,
				OS.Linux, 
				null, 
				"ftp://" + user + "@" + server + "/downloads", 
				pass, 
				6, 
				prefs.getBoolean("seedbox_xirvikshared_alarmfinished_" + order, true),
				prefs.getBoolean("seedbox_xirvikshared_alarmnew_" + order, false),
				prefs.getString("seedbox_xirvikshared_alarmexclude_" + order, null),
				prefs.getString("seedbox_xirvikshared_alarminclude_" + order, null),
				true,
				authToken);
		// @formatter:on
	}

	@Override
	public Intent getSettingsActivityIntent(Context context) {
		return XirvikSharedSettingsActivity_.intent(context).get();
	}

	@Override
	public int getMaxSeedboxOrder(SharedPreferences prefs) {
		return getMaxSeedboxOrder(prefs, "seedbox_xirvikshared_server_");
	}

	@Override
	public void removeServerSetting(SharedPreferences prefs, int order) {
		removeServerSetting(prefs, "seedbox_xirvikshared_server_", new String[] { "seedbox_xirvikshared_name_",
				"seedbox_xirvikshared_server_", "seedbox_xirvikshared_user_", "seedbox_xirvikshared_pass_",
				"seedbox_xirvikshared_rpc_","seedbox_xirvikshared_token_" }, order);
	}

	public void saveServerSetting(final Context context, String server, String token, String rcp) {
		// Get server order
		int key = SeedboxProvider.XirvikShared.getSettings().getMaxSeedboxOrder(PreferenceManager.getDefaultSharedPreferences(context)) + 1;

		// Shared preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		// Check server already exists to replace token
		for(int i = 0 ; i <= SeedboxProvider.XirvikShared.getSettings().getMaxSeedboxOrder(PreferenceManager.getDefaultSharedPreferences(context)) ; i++) {
			Log.e(TAG, prefs.getString("seedbox_xirvikshared_server_" + i, ""));
			if(prefs.getString("seedbox_xirvikshared_server_" + i, "").equals(server)) {
				Log.d(TAG, "Server found, updating token!");
				key = i;
			}
		}

		// Preferences Editor
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString("seedbox_xirvikshared_client_" + key, Daemon.toCode(Daemon.rTorrent));
		prefsEditor.putString("seedbox_xirvikshared_name" + key, "QR Server " + key);
		prefsEditor.putString("seedbox_xirvikshared_server_" + key, server);
		prefsEditor.putString("seedbox_xirvikshared_user_" + key, "");
		prefsEditor.putString("seedbox_xirvikshared_pass_" + key, "");
		prefsEditor.putString("seedbox_xirvikshared_token_" + key, token);
		prefsEditor.putString("seedbox_xirvikshared_rpc_" + key, rcp);
		prefsEditor.commit();
	}

}
