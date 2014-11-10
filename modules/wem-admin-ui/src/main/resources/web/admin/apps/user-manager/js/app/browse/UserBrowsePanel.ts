module app.browse {
    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;
    import UserTreeGridItem = app.browse.UserTreeGridItem;
    import UserTreeGridItemType = app.browse.UserTreeGridItemType;

    export class UserBrowsePanel extends api.app.browse.BrowsePanel<app.browse.UserTreeGridItem> {

        private browseActions: app.browse.UserBrowseActions;

        private userTreeGrid: UserItemsTreeGrid;

        private userFilterPanel: app.browse.filter.PrincipalBrowseFilterPanel;

        private toolbar: UserBrowseToolbar;

        constructor() {
            var treeGridContextMenu = new app.browse.UserTreeGridContextMenu();
            this.userTreeGrid = new UserItemsTreeGrid();

            this.browseActions = UserBrowseActions.init(this.userTreeGrid);
            treeGridContextMenu.setActions(this.browseActions);
            this.userFilterPanel = new app.browse.filter.PrincipalBrowseFilterPanel();
            this.toolbar = new UserBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new UserBrowseItemPanel();

            super({
                browseToolbar: this.toolbar,
                treeGrid: this.userTreeGrid,
                browseItemPanel: browseItemPanel,
                filterPanel: this.userFilterPanel
            });

            this.userTreeGrid.onSelectionChanged((selectedRows: TreeNode<UserTreeGridItem>[]) => {
                // this.browseActions.updateActionsEnabledState(<any[]>selectedRows.map((elem) => {
                //     return elem.getData();
                // }));
            });
        }

        treeNodesToBrowseItems(nodes: TreeNode<UserTreeGridItem>[]): BrowseItem<UserTreeGridItem>[] {
            var browseItems: BrowseItem<UserTreeGridItem>[] = [];

            // do not proceed duplicated content. still, it can be selected
            nodes.forEach((node: TreeNode<UserTreeGridItem>) => {
                var userGridItem = node.getData();

                var item = new BrowseItem<UserTreeGridItem>(userGridItem).
                    setId(userGridItem.getDataId()).
                    setDisplayName(userGridItem.getItemDisplayName()).
                    //TODO set a less icon-class
                    setIconUrl(this.selectIconUrl(userGridItem));

                browseItems.push(item);

            });
            return browseItems;
        }

        private selectIconUrl(item: app.browse.UserTreeGridItem): string {
            var type: UserTreeGridItemType = item.getType();
            switch (type) {
            case UserTreeGridItemType.USER_STORE:
            {
                return  api.util.UriHelper.getAdminUri('common/images/icons/128x128/userstore.png');
            }
            case UserTreeGridItemType.PRINCIPAL:
            {
                if (item.getPrincipal().isRole()) {
                    //TODO instead of URI need to set icon-class
                    return  api.util.UriHelper.getAdminUri('common/images/icons/128x128/userstore.png');
                }
                if (item.getPrincipal().isUser()) {
                    //TODO instead of URI need to set icon-class
                    return  api.util.UriHelper.getAdminUri('common/images/icons/128x128/businessman.png');
                }
                if (item.getPrincipal().isGroup()) {
                    return  api.util.UriHelper.getAdminUri('common/images/icons/128x128/group.png');
                }

            }
            case UserTreeGridItemType.GROUPS:
            {            //TODO instead of URI need to set icon-class
                return  api.util.UriHelper.getAdminUri('common/images/icons/128x128/group.png');
            }
            case UserTreeGridItemType.ROLES:

            {
                //TODO instead of URI need to set icon-class
                return  api.util.UriHelper.getAdminUri('common/images/icons/icoMoon/128x128/puzzle.png');
            }
            case UserTreeGridItemType.USERS:
            {     //TODO instead of URI need to set icon-class
                return  api.util.UriHelper.getAdminUri('common/images/icons/128x128/businessmen.png');
            }
            }

        }
    }

}