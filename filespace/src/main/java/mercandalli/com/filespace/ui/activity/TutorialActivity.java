package mercandalli.com.filespace.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import mercandalli.com.filespace.R;

/**
 * Created by Jonathan on 03/11/2015.
 */
public class TutorialActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ImageView btnNext;
    private TextView btnFinish;
    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isFirstLogin()) {
            MainActivity.start(this);
            finish();
        }

        setContentView(R.layout.activity_tutorial);

        intro_images = (ViewPager) findViewById(R.id.tutorial_pager_introduction);
        btnNext = (ImageView) findViewById(R.id.btn_next);
        btnFinish = (TextView) findViewById(R.id.btn_finish);

        pager_indicator = (LinearLayout) findViewById(R.id.tutorial_viewPagerCountDots);

        btnNext.setOnClickListener(this);
        btnFinish.setOnClickListener(this);

        //btnNext.setColorFilter(ContextCompat.getColor(this, R.color.tutorial_button), PorterDuff.Mode.SRC_ATOP);

        mAdapter = new ViewPagerAdapter(TutorialActivity.this);
        intro_images.setAdapter(mAdapter);
        intro_images.setCurrentItem(0);
        intro_images.addOnPageChangeListener(this);
        setUiPageViewController();
    }

    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tutorial_nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(10, 0, 10, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tutorial_selecteditem_dot));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (intro_images.getCurrentItem() < dotsCount - 1) {
                    intro_images.setCurrentItem((intro_images.getCurrentItem() < dotsCount)
                            ? intro_images.getCurrentItem() + 1 : 0);
                } else {

                    final SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, MODE_PRIVATE)
                            .edit();
                    editor.putBoolean(KEY_IS_FIRST_LOGIN, false);
                    editor.apply();

                    MainActivity.start(this);
                    finish();
                }
                break;

            case R.id.btn_finish:

                final SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, MODE_PRIVATE)
                        .edit();
                editor.putBoolean(KEY_IS_FIRST_LOGIN, false);
                editor.apply();

                MainActivity.start(this);
                finish();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tutorial_nonselecteditem_dot));
        }

        dots[position].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tutorial_selecteditem_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class ViewPagerAdapter extends PagerAdapter {

        private Context mContext;

        public ViewPagerAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.activity_tutorial_pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.img_pager_item);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private static final String SHARED_PREFERENCES_TUTORIAL = "Login.Permission";
    private static final String KEY_IS_FIRST_LOGIN = "LoginPermission.Key.KEY_IS_FIRST_LOGIN";

    private boolean isFirstLogin() {
        final SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_TUTORIAL, MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_IS_FIRST_LOGIN, true);
    }
}