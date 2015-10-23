package mercandalli.com.filespace.config;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

/**
 * The {@link MyApp} module
 */
@Module
public class MyAppModule {

    private MyApp mMyApp;

    public MyAppModule(MyApp myApp) {
        this.mMyApp = myApp;
    }

    @Provides
    public Application provideApp() {
        return mMyApp;
    }

}
