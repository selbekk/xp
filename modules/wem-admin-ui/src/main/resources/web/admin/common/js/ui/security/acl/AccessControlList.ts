module api.ui.security.acl {

    import Permission = api.security.acl.Permission;
    import Principal = api.security.Principal;
    import PrincipalType = api.security.PrincipalType;
    import AccessControlEntry = api.security.acl.AccessControlEntry;


    export class AccessControlList extends api.ui.selector.list.ListBox<AccessControlEntry> {

        private itemsEditable: boolean = true;

        constructor(className?: string) {
            super('access-control-list' + (className ? " " + className : ""));
        }

        createItemView(entry: AccessControlEntry): AccessControlListItem {
            var itemView = new AccessControlListItem(entry);
            itemView.setEditable(this.itemsEditable);
            itemView.onRemoveClicked(() => {
                this.removeItem(entry);
            });
            return itemView;
        }

        getItemId(item: AccessControlEntry): string {
            return item.getPrincipalKey().toString();
        }

        setItemsEditable(editable: boolean): AccessControlList {
            if (this.itemsEditable != editable) {
                this.refreshList();
                this.itemsEditable = editable;
            }
            return this;
        }

        isItemsEditable(): boolean {
            return this.itemsEditable;
        }

    }

}