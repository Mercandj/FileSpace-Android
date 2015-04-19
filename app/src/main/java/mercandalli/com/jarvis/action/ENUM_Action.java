package mercandalli.com.jarvis.action;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.provider.Settings;

import mercandalli.com.jarvis.activity.Application;

public enum ENUM_Action {	
	
	BATTERIE(new Action() {
			@Override
			public String action(Application app) {
				IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
				Intent batteryStatus = app.registerReceiver(null, ifilter);
				int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	
				float batteryPct = level / (float)scale;
				batteryPct*=100.0;
				
				return "Tu as "+batteryPct+"% de batterie.";
			}
		}
	),
	WIFI(new Action() {
			@Override
			public String action(Application app) {
				WifiManager wifiManager = (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
				if(wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED)
					return "Le wifi est activ�.";
				else
					return "Le wifi n'est pas activ�.";
			}
		}
	),
	WIFI_ON(new Action() {
			@Override
			public String action(Application app) {
				WifiManager wifiManager = (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
				wifiManager.setWifiEnabled(true);
				return "Le wifi est activ�.";
			}
		}
	),
	WIFI_OFF(new Action() {
			@Override
			public String action(Application app) {
				WifiManager wifiManager = (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
				wifiManager.setWifiEnabled(false);
				return "Le wifi est �teint.";
			}
		}
	),
	MODE_AVION(new Action() {
			@Override
			public String action(Application app) {
				if(Settings.System.getInt(app.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1)
					return "Tu es en mode avion.";
				else
					return "Le mode avion n'est pas activ�.";
			}
		}
	),
	LOCALISATION(new Action() {
			@Override
			public String action(Application app) {
				LocationManager myLocationManager;
				String PROVIDER = LocationManager.NETWORK_PROVIDER;				
				myLocationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
				   
				//get last known location, if available
				Location location = myLocationManager.getLastKnownLocation(PROVIDER);
				
				if(location!=null)
					return "Votre longitude est : "+location.getLongitude()+".\nVotre latitude est : "+location.getLatitude()+".";
				else		
					return "Pas de localisation.";
			}
		}
	),	
	VERSION_DROID(new Action() {
			@Override
			public String action(Application app) {
                try {
                    return app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                return "";
			}
		}
	),
    QUIT(new Action() {
        @Override
        public String action(Application app) {
            app.finish();
            return "";
        }
    }
    ),
	;
	
	public Action action;
	
	private ENUM_Action(Action action) {
		this.action = action;
	}
}
