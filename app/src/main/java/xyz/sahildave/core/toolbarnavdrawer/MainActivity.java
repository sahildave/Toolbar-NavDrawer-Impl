package xyz.sahildave.core.toolbarnavdrawer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import xyz.sahildave.core.toolbarnavdrawer.NavigationItems.NavItem;
import xyz.sahildave.core.toolbarnavdrawer.NavigationItems.NavigationItemId;
import xyz.sahildave.core.toolbarnavdrawer.NavigationItems.OnNavItemClickListener;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    @NavigationItemId private int mNavDrawerSelectedItem;
    private NavigationItems mNavigationItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbarAndNavDrawer();
        populateNavDrawer();

        goToNavDrawerItem(NavigationItems.NAVDRAWER_ITEM_NEW_TASKS);
    }

    private void setupToolbarAndNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) return;

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.app_name,
                R.string.app_name) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                syncState();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setSupportActionBar(toolbar);
    }

    private void populateNavDrawer() {
        // Nav Items
        LinearLayout navdrawerItemsLayout = (LinearLayout) findViewById(R.id.navdrawer_items_list);
        mNavigationItems = NavigationItems.build()
                .setRoot(navdrawerItemsLayout).setOnClickListener(navItemClickListener);

        ViewGroup newTaskNavItem = mNavigationItems.createNewNavItem(this)
                .withTextRes(R.string.nav_drawer_note_add)
                .withDrawableRes(R.drawable.ic_action_note_add)
                .withItemId(NavigationItems.NAVDRAWER_ITEM_NEW_TASKS)
                .build();

        ViewGroup myTaskNavItem = mNavigationItems.createNewNavItem(this)
                .withTextRes(R.string.nav_drawer_note_list)
                .withDrawableRes(R.drawable.ic_action_view_list)
                .withItemId(NavigationItems.NAVDRAWER_ITEM_MY_TASKS)
                .build();

        ViewGroup divider = mNavigationItems.createNewNavItem(this)
                .withItemId(NavigationItems.NAVDRAWER_ITEM_SEPARATOR)
                .build();

        ViewGroup settingsNavItem = mNavigationItems.createNewNavItem(this)
                .withTextRes(R.string.action_settings)
                .withItemId(NavigationItems.NAVDRAWER_ITEM_SETTINGS)
                .build();

        mNavigationItems.addNavItemToDrawer(newTaskNavItem);
        mNavigationItems.addNavItemToDrawer(myTaskNavItem);
        mNavigationItems.addNavItemToDrawer(divider);
        mNavigationItems.addNavItemToDrawer(settingsNavItem);

        // User Account
        ImageView header = (ImageView) findViewById(R.id.account_header_drawer_background);
        ImageView profile = (ImageView) findViewById(R.id.account_header_drawer_current);
        TextView displayName = (TextView) findViewById(R.id.account_header_drawer_name);
        TextView emailId = (TextView) findViewById(R.id.account_header_drawer_email);

        header.setImageResource(R.drawable.placeholder_cover);
        profile.setImageResource(R.drawable.ic_action_person);
        displayName.setText("User Name");
        emailId.setText("User Email");
    }

    OnNavItemClickListener navItemClickListener = new OnNavItemClickListener() {
        @Override
        public void onClick(NavItem clickedNavItem, @NavigationItemId int itemId) {
            closeNavDrawer();
            goToNavDrawerItem(itemId);
        }
    };

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void goToNavDrawerItem(@NavigationItemId int itemId) {
        Log.d(LOG_TAG, "Selecting item - " + itemId);

        if (mNavDrawerSelectedItem == itemId) {
            Log.d(LOG_TAG, "Reselected the item.");
            return;
        }

        mNavigationItems.setSelectedNavDrawerItem(itemId);
        this.mNavDrawerSelectedItem = itemId;

        switch (itemId) {
            case NavigationItems.NAVDRAWER_ITEM_NEW_TASKS:
                Log.d(LOG_TAG, "Switched to NAVDRAWER_ITEM_NEW_TASKS");
                break;
            case NavigationItems.NAVDRAWER_ITEM_MY_TASKS:
                Log.d(LOG_TAG, "Switched to NAVDRAWER_ITEM_MY_TASKS");
                break;
        }
        mDrawerToggle.syncState();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
