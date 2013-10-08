module app_browse {

    export class ContentBrowsePanel extends api_app_browse.BrowsePanel {

        private browseActions:app_browse.ContentBrowseActions;

        private toolbar:ContentBrowseToolbar;

        private contentTreeGridPanel:app_browse.ContentTreeGridPanel;

        private contentFilterPanel:app_browse_filter.ContentBrowseFilterPanel;

        private contentBrowseItemPanel:ContentBrowseItemPanel;

        constructor() {
            var treeGridContextMenu = new ContentTreeGridContextMenu();
            this.contentTreeGridPanel = components.gridPanel = new app_browse.ContentTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.browseActions = ContentBrowseActions.init(this.contentTreeGridPanel);
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new ContentBrowseToolbar(this.browseActions);
            this.contentBrowseItemPanel =
            components.detailPanel = new ContentBrowseItemPanel({actionMenuActions: [
                this.browseActions.SHOW_NEW_CONTENT_DIALOG_ACTION,
                this.browseActions.EDIT_CONTENT,
                this.browseActions.OPEN_CONTENT,
                this.browseActions.DELETE_CONTENT,
                this.browseActions.DUPLICATE_CONTENT,
                this.browseActions.MOVE_CONTENT]});

            this.contentFilterPanel = new app_browse_filter.ContentBrowseFilterPanel();

            new api_content.FindContentRequest().send().done(
                (jsonResponse:api_rest.JsonResponse) => {
                    var termsFacets:api_facet.Facet[] = api_facet.FacetFactory.createFacets(jsonResponse.getJson().facets);
                    this.contentFilterPanel.updateFacets(termsFacets);
                }
            );

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: this.contentTreeGridPanel,
                browseItemPanel: this.contentBrowseItemPanel,
                filterPanel: this.contentFilterPanel});

            ShowPreviewEvent.on((event) => {
                this.contentBrowseItemPanel.setPreviewMode(true);
            });

            ShowDetailsEvent.on((event) => {
                this.contentBrowseItemPanel.setPreviewMode(false);
            });

            this.contentTreeGridPanel.addListener(<api_app_browse_grid.TreeGridPanelListener>{
                onSelectionChanged: (event:api_app_browse_grid.TreeGridSelectionChangedEvent) => {
                    var contentSummaries = this.extModelsToContentSummaries(event.selectedModels);
                    this.browseActions.updateActionsEnabledState(contentSummaries);
                }
            });
        }

        extModelsToContentSummaries(models:Ext_data_Model[]):api_content.ContentSummary[] {

            var summaries:api_content.ContentSummary[] = [];
            for (var i = 0; i < models.length; i++) {
                summaries.push(new api_content.ContentSummary(<api_content_json.ContentSummaryJson>models[i].data))
            }
            return summaries;
        }

        extModelsToBrowseItems(models:Ext_data_Model[]):api_app_browse.BrowseItem[] {

            var browseItems:api_app_browse.BrowseItem[] = [];
            models.forEach((model:Ext_data_Model) => {
                var item = new api_app_browse.BrowseItem(model).
                    setDisplayName(model.data['displayName']).
                    setPath(model.data['path']).
                    setIconUrl(model.data['iconUrl']);
                browseItems.push(item);
            });
            return browseItems;
        }
    }

}
