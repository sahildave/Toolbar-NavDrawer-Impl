package xyz.sahildave.core.toolbarnavdrawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;


public class NavigationItems {
    private static final String LOG_TAG = NavigationItems.class.getName();
    static volatile NavigationItems singleton = null;

    // symbols for navdrawer items (indices must correspond to array below). This is
    // not a list of items that are necessarily *present* in the Nav Drawer; rather,
    // it's a list of all possible items.
    @IntDef({NAVDRAWER_ITEM_NEW_TASKS,
            NAVDRAWER_ITEM_MY_TASKS,
            NAVDRAWER_ITEM_SETTINGS,
            NAVDRAWER_ITEM_INVALID,
            NAVDRAWER_ITEM_SEPARATOR,
            NAVDRAWER_ITEM_SEPARATOR_SPECIAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationItemId {}
    public static final int NAVDRAWER_ITEM_NEW_TASKS = 10;
    public static final int NAVDRAWER_ITEM_MY_TASKS = 11;
    public static final int NAVDRAWER_ITEM_SETTINGS = 12;
    public static final int NAVDRAWER_ITEM_INVALID = -11;
    public static final int NAVDRAWER_ITEM_SEPARATOR = -12;
    public static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -13;

    private ViewGroup mViewGroupRoot;
    private Map<Integer, NavItem> navdrawerItemsMap = new HashMap<>();
    private OnNavItemClickListener mOnClickListener;

    public static NavigationItems build() {
        if (singleton == null) {
            synchronized (NavigationItems.class) {
                if (singleton == null) {
                    singleton = new Builder().build();
                }
            }
        }
        return singleton;
    }

    public static void nullify() {
        singleton = null;
    }

    public NavItem createNewNavItem(Context context) {
        return new NavItem(context);
    }


    public NavigationItems setRoot(ViewGroup root){
        mViewGroupRoot = root;
        return this;
    }

    public NavigationItems setOnClickListener(OnNavItemClickListener onClickListener){
        mOnClickListener = onClickListener;
        return this;
    }

    public void addNavItemToDrawer(ViewGroup mNavDrawerItem) {
        if (mViewGroupRoot == null) {
            throw new RuntimeException("You must set rootViewGroup. Use #setRoot");
        }

        mViewGroupRoot.addView(mNavDrawerItem);
    }

    public NavItem getNavItemOfView(ViewGroup navItemViewGroup) {
        Object viewTag = navItemViewGroup.getTag();
        if(viewTag !=null && viewTag instanceof Integer) {
            return navdrawerItemsMap.get((Integer) navItemViewGroup.getTag());
        }
        return null;
    }

    public NavItem findItemByID(@NavigationItemId int itemId) {
        return navdrawerItemsMap.get(itemId);
    }

    private void addToMap(NavItem navItem){
        navdrawerItemsMap.put(navItem.getItemId(), navItem);
    }

    public void selectItem(@NavigationItemId int mNavDrawerItem){
        NavItem navItem = findItemByID(mNavDrawerItem);
        navItem.toggleSelection(true);
        mOnClickListener.onClick(navItem, navItem.getItemId());
    }

    public void setSelectedNavDrawerItem(int itemId) {
        if (navdrawerItemsMap != null) {
            for (Map.Entry<Integer, NavItem> entry : navdrawerItemsMap.entrySet()) {
                NavItem entryItem = entry.getValue();
                entryItem.setSelected(itemId == entry.getKey());
                entryItem.reformatNavDrawerItem(entryItem.getView());
            }
        }
    }

    public void deselectItem(@NavigationItemId int mNavDrawerItem){
        NavItem navItem = findItemByID(mNavDrawerItem);
        navItem.toggleSelection(false);
    }

    public interface OnNavItemClickListener {
        void onClick(NavItem clickedNavItem, @NavigationItemId int itemId);
    }

    public class NavItem {
        private final Context context;
        private String mText;
        private Drawable mDrawable = null;
        @NavigationItemId
        private int itemId = NAVDRAWER_ITEM_INVALID;
        private ViewGroup mNavDrawerItem;
        private boolean selected;

        private NavItem(Context context) {
            this.context = context;
        }

        public NavItem withTextRes(@StringRes int stringRes){
            this.mText = context.getResources().getString(stringRes);
            return this;
        }

        public NavItem withDrawableRes(@DrawableRes int drawableRes){
            this.mDrawable = ContextCompat.getDrawable(context, drawableRes);
            return this;
        }

        public NavItem withItemId(@NavigationItemId int itemId){
            this.itemId = itemId;
            return this;
        }

        public ViewGroup build(){
            if(context == null) {
                throw new RuntimeException("No context found.");
            }

            if(itemId == NAVDRAWER_ITEM_INVALID){
                throw new RuntimeException("You must set itemId. Use #withItemId");
            }
            if (mText == null && itemId != NAVDRAWER_ITEM_SEPARATOR && itemId != NAVDRAWER_ITEM_SEPARATOR_SPECIAL) {
                throw new RuntimeException("You must set text. Use #withTextRes");
            }

            return buildView();
        }

        public ViewGroup getView() {
            if(mNavDrawerItem == null) {
                throw new RuntimeException("Please Use #build before doing #getView");
            }
            return mNavDrawerItem;
        }

        @NavigationItemId
        public Integer getItemId() {
            return itemId;
        }


        public void toggleSelection(boolean selected) {
            formatNavDrawerItem(getView(), selected);
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        private ViewGroup buildView() {
            Log.d(LOG_TAG, "Building Navigation Item - " + this.toString());
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            if (itemId != NAVDRAWER_ITEM_SEPARATOR && itemId != NAVDRAWER_ITEM_SEPARATOR_SPECIAL) {
                mNavDrawerItem = (ViewGroup) inflater.inflate(R.layout.navigation_drawer_item_row, mViewGroupRoot, false);
                setText(mNavDrawerItem);
                setDrawable(mNavDrawerItem);
                formatNavDrawerItem(mNavDrawerItem, false);
                mNavDrawerItem.setClickable(true);
                mNavDrawerItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getTag() != null && v.getTag() instanceof Integer) {
                            mOnClickListener.onClick(NavItem.this, getItemId());
                        }
                    }
                });

            } else {
                mNavDrawerItem = (ViewGroup) inflater.inflate(R.layout.navigation_drawer_separator, mViewGroupRoot, false);
            }
            addToMap(this);
            mNavDrawerItem.setTag(itemId);
            return mNavDrawerItem;
        }


        private void setText(View navDrawerItem) {
            TextView title = (TextView) navDrawerItem.findViewById(R.id.nav_item_title);
            title.setText(mText);
        }

        private void setDrawable(View navDrawerItem) {
            ImageView icon = (ImageView) navDrawerItem.findViewById(R.id.nav_item_icon);
            if(mDrawable == null) {
                icon.setVisibility(View.GONE);
                TextView title = (TextView) navDrawerItem.findViewById(R.id.nav_item_title);
                title.setPadding(CoreViewUtils.dpToPx(context, 16), 0, 0, 0);
            } else {
                icon.setImageDrawable(mDrawable);
            }
        }

        private void reformatNavDrawerItem(ViewGroup mNavDrawerItem) {
            formatNavDrawerItem(mNavDrawerItem, isSelected());
        }

        private void formatNavDrawerItem(ViewGroup mNavDrawerItem, boolean selected) {
            ImageView iconView = (ImageView) mNavDrawerItem.findViewById(R.id.nav_item_icon);
            TextView titleView = (TextView) mNavDrawerItem.findViewById(R.id.nav_item_title);

            mNavDrawerItem.setBackgroundResource(selected ?
                    R.drawable.selected_navdrawer_item_background :
                    R.color.navdrawer_background);

            // configure its appearance according to whether or not it's selected
            if(titleView!=null) titleView.setTextColor(selected ?
                    ContextCompat.getColor(context, R.color.navdrawer_text_color_selected) :
                    ContextCompat.getColor(context, R.color.navdrawer_text_color));
            if(iconView!=null) iconView.setColorFilter(selected ?
                    ContextCompat.getColor(context, R.color.navdrawer_icon_tint_selected) :
                    ContextCompat.getColor(context, R.color.navdrawer_icon_tint));
            mNavDrawerItem.setTag(itemId);
        }

        @Override
        public String toString() {
            return "NavItem{" +
                    "mText='" + mText + '\'' +
                    ", itemId=" + itemId +
                    ", selected=" + selected +
                    '}';
        }
    }

    public static class Builder{
        /** Create the {@link NavigationItems} instance. */
        public NavigationItems build() {
            return new NavigationItems();
        }
    }
}