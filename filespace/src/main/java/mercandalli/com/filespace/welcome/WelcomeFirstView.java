package mercandalli.com.filespace.welcome;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import mercandalli.com.filespace.R;

/**
 * Created by Jonathan on 03/11/2015.
 */
public class WelcomeFirstView extends FrameLayout {

    public WelcomeFirstView(Context context) {
        super(context);
        setContentView(context);
    }

    public WelcomeFirstView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setContentView(context);
    }

    public WelcomeFirstView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setContentView(context);
    }

    private void setContentView(Context context) {
        final View view = inflate(context, R.layout.activity_welcome_first, this);

        ((ImageView) view.findViewById(R.id.activity_welcome_first_img)).setImageResource(R.drawable.welcome_first);
    }
}
