package edu.uci.collabevent;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Prateek on 06/06/16.
 */
public class NavUtils {

    private static boolean mUserLearnedDrawer;
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    public static void setUpNavDrawer(Toolbar mToolbar, ActionBar actionBar, final DrawerLayout mDrawerLayout, NavigationView mNavigationView, final Context mContext) {
        if (mToolbar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationIcon(R.drawable.ic_dehaze_white_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(mContext, PREF_USER_LEARNED_DRAWER, "false"));

        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            mUserLearnedDrawer = true;
            saveSharedSetting(mContext, PREF_USER_LEARNED_DRAWER, "true");
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String name = sharedPref.getString(mContext.getString(R.string.user_name), "Jon Doe");
        String email = sharedPref.getString(mContext.getString(R.string.user_email), "JonDoe@example.com");

        View header = mNavigationView.getHeaderView(0);

        TextView mName = (TextView) header.findViewById(R.id.username);
        mName.setText(name);
        TextView mEmail = (TextView) header.findViewById(R.id.email);
        mEmail.setText(email);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_1:
                        Toast.makeText(mContext, "Profile", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_item_2:
                        Toast.makeText(mContext, "Sign Out", Toast.LENGTH_SHORT).show();
                        SignOutTask signOutTask = new SignOutTask(mContext);
                        signOutTask.execute();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPref.getString(settingName, defaultValue);
    }
}
