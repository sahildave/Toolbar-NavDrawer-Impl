A template which I use to quickly setup Toolbar, ActionBarDrawerToggle, Navigation Drawer and its item.

Widgets used - BezelImageView and ScrimInsetsScrollView

NavigationItems - A build pattern file to quickly create navigation item with text and icon. Use `NavigationItemId` to create your own Navigation types.

    LinearLayout navdrawerItemsLayout = (LinearLayout) findViewById(R.id.navdrawer_items_list);
    NavigationItems navigationItems = NavigationItems.build()
            .setRoot(navdrawerItemsLayout).setOnClickListener(navItemClickListener);

    ViewGroup newTaskNavItem = navigationItems.createNewNavItem(this)
            .withTextRes(R.string.nav_drawer_note_add)
            .withDrawableRes(R.drawable.ic_action_note_add)
            .withItemId(NavigationItems.NAVDRAWER_ITEM_NEW_TASKS)
            .build();

    ViewGroup divider = navigationItems.createNewNavItem(this)
            .withItemId(NavigationItems.NAVDRAWER_ITEM_SEPARATOR)
            .build();

    ViewGroup settingsNavItem = navigationItems.createNewNavItem(this)
            .withTextRes(R.string.action_settings)
            .withItemId(NavigationItems.NAVDRAWER_ITEM_SETTINGS)
            .build();

    navigationItems.addNavItemToDrawer(newTaskNavItem);
    navigationItems.addNavItemToDrawer(divider);
    navigationItems.addNavItemToDrawer(settingsNavItem);

    

    OnNavItemClickListener navItemClickListener = new OnNavItemClickListener() {
        @Override
        public void onClick(NavItem clickedNavItem, @NavigationItemId int itemId) {
            closeNavDrawer();
            goToNavDrawerItem(itemId);
        }
    };

