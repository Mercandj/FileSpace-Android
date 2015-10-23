package mercandalli.com.filespace.config;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

public class MyApp extends Application {

    private MyAppComponent mMyAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        //Dagger - Object graph creation
        setupGraph();
    }

    private void setupGraph() {
        mMyAppComponent = DaggerMyAppComponent.builder()
                .myAppModule(new MyAppModule(this))
                .build();

        mMyAppComponent.inject(this);
    }

    public MyAppComponent getAppComponent() {
        return mMyAppComponent;
    }

    public static MyApp get(Context context) {
        return (MyApp) context.getApplicationContext();
    }
}
