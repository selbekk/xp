module app.browse {

    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;
    import UploadItem = api.ui.uploader.UploadItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentSummaryAndCompareStatusFetcher = api.content.ContentSummaryAndCompareStatusFetcher;
    import CompareStatus = api.content.CompareStatus;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import ContentServerEvent = api.content.event.ContentServerEvent;
    import ContentServerChange = api.content.event.ContentServerChange;
    import ContentServerChangeType = api.content.event.ContentServerChangeType;
    import BatchContentRequest = api.content.BatchContentRequest;
    import TreeNodesOfContentPath = api.content.TreeNodesOfContentPath;
    import ContentId = api.content.ContentId;
    import DetailsPanel = app.view.detail.DetailsPanel;
    import ActiveDetailsPanelsManager = app.view.detail.ActiveDetailsPanelManager;
    import NonMobileDetailsPanelsManager = app.view.detail.NonMobileDetailsPanelsManager;
    import NonMobileDetailsPanelsManagerBuilder = app.view.detail.NonMobileDetailsPanelsManagerBuilder;
    import BatchContentServerEvent = api.content.event.BatchContentServerEvent;
    import ContentDeletedEvent = api.content.event.ContentDeletedEvent;

    export class ContentBrowsePanel extends api.app.browse.BrowsePanel<ContentSummaryAndCompareStatus> {

        private browseActions: app.browse.action.ContentTreeGridActions;

        private toolbar: ContentBrowseToolbar;

        private contentTreeGrid: app.browse.ContentTreeGrid;

        private contentFilterPanel: app.browse.filter.ContentBrowseFilterPanel;

        private contentBrowseItemPanel: ContentBrowseItemPanel;

        private mobileContentItemStatisticsPanel: app.view.MobileContentItemStatisticsPanel;

        private mobileBrowseActions: app.browse.action.MobileContentTreeGridActions;

        private floatingDetailsPanel: DetailsPanel;

        private defaultDockedDetailsPanel: DetailsPanel;

        constructor() {

            this.contentTreeGrid = new app.browse.ContentTreeGrid();

            this.contentBrowseItemPanel = components.detailPanel = new ContentBrowseItemPanel();

            this.contentFilterPanel = new app.browse.filter.ContentBrowseFilterPanel();

            this.browseActions = <app.browse.action.ContentTreeGridActions>this.contentTreeGrid.getContextMenu().getActions();

            this.toolbar = new ContentBrowseToolbar(this.browseActions);

            this.defaultDockedDetailsPanel = DetailsPanel.create().setUseSplitter(false).build();

            super({
                browseToolbar: this.toolbar,
                treeGrid: this.contentTreeGrid,
                browseItemPanel: this.contentBrowseItemPanel,
                filterPanel: this.contentFilterPanel,
                hasDetailsPanel: true
            });

            var showMask = () => {
                if (this.isVisible()) {
                    this.contentTreeGrid.mask();
                }
            };
            this.contentFilterPanel.onSearch(showMask);
            this.contentFilterPanel.onReset(showMask);
            this.contentFilterPanel.onRefresh(showMask);

            this.getTreeGrid().onDataChanged((event: api.ui.treegrid.DataChangedEvent<ContentSummaryAndCompareStatus>) => {
                if (event.getType() === 'updated') {
                    var browseItems = this.treeNodesToBrowseItems(event.getTreeNodes());
                    this.getBrowseItemPanel().updateItemViewers(browseItems);
                }
            });

            this.onShown(() => {
                app.Router.setHash("browse");
            });

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                if (item.isInRangeOrSmaller(ResponsiveRanges._360_540)) {
                    this.browseActions.TOGGLE_SEARCH_PANEL.setVisible(true);
                } else if (item.isInRangeOrBigger(ResponsiveRanges._540_720)) {
                    this.browseActions.TOGGLE_SEARCH_PANEL.setVisible(false);
                }
            });

            this.handleGlobalEvents();
        }

        protected initFilterAndContentGridAndBrowseSplitPanel() {

            var nonMobileDetailsPanelsManagerBuilder = NonMobileDetailsPanelsManager.create();
            this.initSplitPanelWithDockedDetails(nonMobileDetailsPanelsManagerBuilder);
            this.initFloatingDetailsPanel(nonMobileDetailsPanelsManagerBuilder);
            this.initItemStatisticsPanelForMobile();

            var nonMobileDetailsPanelsManager = nonMobileDetailsPanelsManagerBuilder.build();
            if (nonMobileDetailsPanelsManager.requiresCollapsedDetailsPanel()) {
                nonMobileDetailsPanelsManager.hideDockedDetailsPanel();
            }
            nonMobileDetailsPanelsManager.ensureButtonHasCorrectState();

            this.setActiveDetailsPanel(nonMobileDetailsPanelsManager);

            this.subscribeDetailsPanelsOnEvents(nonMobileDetailsPanelsManager);

            this.onShown(() => {
                if (!!nonMobileDetailsPanelsManager.getActivePanel().getActiveWidget()) {
                    nonMobileDetailsPanelsManager.getActivePanel().getActiveWidget().slideIn();
                }
            });

            this.toolbar.appendChild(nonMobileDetailsPanelsManager.getToggleButton());
        }

        private subscribeDetailsPanelsOnEvents(nonMobileDetailsPanelsManager: NonMobileDetailsPanelsManager) {

            this.getTreeGrid().onSelectionChanged((currentSelection: TreeNode<Object>[], fullSelection: TreeNode<Object>[]) => {
                var browseItems: api.app.browse.BrowseItem<ContentSummaryAndCompareStatus>[] = this.getBrowseItemPanel().getItems(),
                    item: api.app.browse.BrowseItem<ContentSummaryAndCompareStatus> = null;
                if (browseItems.length > 0) {
                    item = browseItems[browseItems.length - 1];
                }
                this.updateDetailsPanel(item ? item.getModel() : null);
            });

            ResponsiveManager.onAvailableSizeChanged(this.getFilterAndContentGridAndBrowseSplitPanel(), (item: ResponsiveItem) => {
                nonMobileDetailsPanelsManager.handleResizeEvent();
            });

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                if (ResponsiveRanges._540_720.isFitOrBigger(item.getOldRangeValue()) &&
                    item.isInRangeOrSmaller(ResponsiveRanges._360_540)) {
                    nonMobileDetailsPanelsManager.hideActivePanel();
                    ActiveDetailsPanelsManager.setActiveDetailsPanel(this.mobileContentItemStatisticsPanel.getDetailsPanel());
                }
            });

        }

        private initSplitPanelWithDockedDetails(nonMobileDetailsPanelsManagerBuilder: NonMobileDetailsPanelsManagerBuilder) {

            var contentPanelsAndDetailPanel: api.ui.panel.SplitPanel = new api.ui.panel.SplitPanelBuilder(this.getFilterAndContentGridAndBrowseSplitPanel(),
                this.defaultDockedDetailsPanel).
                setAlignment(api.ui.panel.SplitPanelAlignment.VERTICAL).
                setSecondPanelSize(280, api.ui.panel.SplitPanelUnit.PIXEL).
                setSecondPanelMinSize(280, api.ui.panel.SplitPanelUnit.PIXEL).
                setAnimationDelay(600).
                setSecondPanelShouldSlideRight(true).
                build();

            contentPanelsAndDetailPanel.addClass("split-panel-with-details");
            contentPanelsAndDetailPanel.setSecondPanelSize(280, api.ui.panel.SplitPanelUnit.PIXEL);

            this.appendChild(contentPanelsAndDetailPanel);

            nonMobileDetailsPanelsManagerBuilder.setSplitPanelWithGridAndDetails(contentPanelsAndDetailPanel);
            nonMobileDetailsPanelsManagerBuilder.setDefaultDetailsPanel(this.defaultDockedDetailsPanel);
        }

        private initFloatingDetailsPanel(nonMobileDetailsPanelsManagerBuilder: NonMobileDetailsPanelsManagerBuilder) {

            this.floatingDetailsPanel = DetailsPanel.create().build();

            this.floatingDetailsPanel.addClass("floating-details-panel");

            nonMobileDetailsPanelsManagerBuilder.setFloatingDetailsPanel(this.floatingDetailsPanel);

            this.appendChild(this.floatingDetailsPanel);
        }

        private initItemStatisticsPanelForMobile() {
            this.mobileBrowseActions = new app.browse.action.MobileContentTreeGridActions(this.contentTreeGrid);
            this.mobileContentItemStatisticsPanel = new app.view.MobileContentItemStatisticsPanel(this.mobileBrowseActions);

            api.content.TreeGridItemClickedEvent.on((event) => {
                if (ActiveDetailsPanelsManager.getActiveDetailsPanel() == this.mobileContentItemStatisticsPanel.getDetailsPanel()) {
                    var browseItems: api.app.browse.BrowseItem<ContentSummaryAndCompareStatus>[] = this.getBrowseItemPanel().getItems();
                    if (browseItems.length == 1) {
                        new api.content.page.IsRenderableRequest(new api.content.ContentId(browseItems[0].getId())).sendAndParse().
                            then((renderable: boolean) => {
                                var item: api.app.view.ViewItem<ContentSummaryAndCompareStatus> = browseItems[0].toViewItem();
                                item.setRenderable(renderable);
                                this.mobileContentItemStatisticsPanel.setItem(item);
                                this.mobileBrowseActions.updateActionsEnabledState(browseItems);
                            });
                    }
                }
            });

            this.appendChild(this.mobileContentItemStatisticsPanel);
        }

        private setActiveDetailsPanel(nonMobileDetailsPanelsManager: NonMobileDetailsPanelsManager) {
            if (this.mobileContentItemStatisticsPanel.isVisible()) {
                ActiveDetailsPanelsManager.setActiveDetailsPanel(this.mobileContentItemStatisticsPanel.getDetailsPanel());
            } else {
                ActiveDetailsPanelsManager.setActiveDetailsPanel(nonMobileDetailsPanelsManager.getActivePanel());
            }
        }

        treeNodesToBrowseItems(nodes: TreeNode<ContentSummaryAndCompareStatus>[]): BrowseItem<ContentSummaryAndCompareStatus>[] {
            var browseItems: BrowseItem<ContentSummaryAndCompareStatus>[] = [];

            // do not proceed duplicated content. still, it can be selected
            nodes.forEach((node: TreeNode<ContentSummaryAndCompareStatus>, index: number) => {
                for (var i = 0; i <= index; i++) {
                    if (nodes[i].getData().getId() === node.getData().getId()) {
                        break;
                    }
                }
                if (i === index) {
                    var data = node.getData();
                    if (!!data && !!data.getContentSummary()) {
                        var item = new BrowseItem<ContentSummaryAndCompareStatus>(data).
                            setId(data.getId()).
                            setDisplayName(data.getContentSummary().getDisplayName()).
                            setPath(data.getContentSummary().getPath().toString()).
                            setIconUrl(new ContentIconUrlResolver().setContent(data.getContentSummary()).resolve());
                        browseItems.push(item);
                    }
                }
            });

            return browseItems;
        }


        private handleGlobalEvents() {

            api.content.ContentChildOrderUpdatedEvent.on((event) => {
                //this.handleChildOrderUpdated(event);
            });

            ToggleSearchPanelEvent.on(() => {
                this.toggleFilterPanel();
            });

            app.create.NewMediaUploadEvent.on((event) => {
                this.handleNewMediaUpload(event);
            });

            /*
             * ContentServerEvent handlers for the new API.
             * TODO: Replace all of the old events with the new one.
             */
            var handler = this.contentServerEventHandler.bind(this);

            BatchContentServerEvent.on(handler);
            this.onRemoved(() => {
                BatchContentServerEvent.un(handler);
            });
        }

        private contentServerEventHandler(event: BatchContentServerEvent) {

            var changes = [];
            event.getEvents().forEach((event) => {
                changes = changes.concat(event.getContentChange());
            });

            changes.forEach((change: ContentServerChange) => {
                switch (change.getChangeType()) {
                case ContentServerChangeType.CREATE:
                    this.handleContentCreated(change);
                    break;
                case ContentServerChangeType.UPDATE:
                    this.handleContentUpdated(change, changes);
                    break;
                case ContentServerChangeType.RENAME:
                    this.handleContentRenamed(change);
                    break;
                case ContentServerChangeType.DELETE:
                    break;
                case ContentServerChangeType.PENDING:
                    this.handleContentPending(change);
                    break;
                case ContentServerChangeType.DUPLICATE:
                    // same logic as for creation
                    this.handleContentCreated(change);
                    break;
                case ContentServerChangeType.PUBLISH:
                    this.handleContentPublished(change);
                    break;
                case ContentServerChangeType.MOVE:
                    this.handleContentMoved(change, changes);
                    break;
                case ContentServerChangeType.SORT:
                    this.handleContentSorted(change);
                    break;
                case ContentServerChangeType.UNKNOWN:
                    break;
                default:
                    //
                }
            });

            switch (event.getType()) { // move here events who needs in batch handling
            case ContentServerChangeType.DELETE:
                this.handleContentDeleted(changes);
                break;
            }
        }

        private handleContentCreated(change: ContentServerChange, useNewContentPaths: boolean = false, triggeredByRename: boolean = false) {

            var paths: api.content.ContentPath[] = useNewContentPaths ? change.getNewContentPaths() : change.getContentPaths();
            var createResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(paths, true);

            return ContentSummaryAndCompareStatusFetcher.
                fetchByPaths(createResult.map((el) => {
                    return el.getAltPath();
                })).
                then((data: ContentSummaryAndCompareStatus[]) => {
                    var isFiltered = this.contentTreeGrid.getRoot().isFiltered(),
                        nodes: TreeNode<ContentSummaryAndCompareStatus>[] = [];

                    data.forEach((el) => {
                        for (var i = 0; i < createResult.length; i++) {
                            if (el.getContentSummary().getPath().isChildOf(createResult[i].getPath())) {
                                if (triggeredByRename) {
                                    var renameResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(change.getContentPaths());
                                    var premerged = renameResult.map((el) => {
                                        return el.getNodes();
                                    });
                                    // merge array of nodes arrays
                                    nodes = nodes.concat.apply(nodes, premerged);
                                    nodes.forEach((node) => {
                                        if (node.getDataId() === el.getId()) {
                                            node.setData(el);
                                            node.clearViewers();
                                            this.contentTreeGrid.xUpdatePathsInChildren(node);
                                        }
                                    });
                                } else {
                                    this.contentTreeGrid.xAppendContentNodes(
                                        createResult[i].getNodes().map((node) => {
                                            return new api.content.TreeNodeParentOfContent(el, node);
                                        }),
                                        !isFiltered
                                    ).then((results) => {
                                            nodes = nodes.concat(results);
                                        });
                                }
                                break;
                            }
                        }
                    });

                    this.contentTreeGrid.initAndRender();

                    isFiltered = true;
                    if (isFiltered) {
                        this.setFilterPanelRefreshNeeded(true);
                        window.setTimeout(() => {
                            this.refreshFilter();
                        }, 1000);
                    }
                });
        }

        private handleContentUpdated(change: ContentServerChange,
                                     changes: ContentServerChange[]) {

            if (changes.length === 1) {

                var updateResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(change.getContentPaths());

                var ids: ContentId[] = [];
                updateResult.forEach((el) => {
                    ids = ids.concat(el.getNodes().map((node) => {
                        return node.getData().getContentId();
                    }));
                });
                return ContentSummaryAndCompareStatusFetcher.
                    fetchByIds(ids).
                    then((data: ContentSummaryAndCompareStatus[]) => {
                        var results = [];
                        data.forEach((el) => {
                            for (var i = 0; i < updateResult.length; i++) {
                                if (updateResult[i].getId() === el.getId()) {
                                    updateResult[i].updateNodeData(el);

                                    this.updateStatisticsPreview(el); // update preview item

                                    this.updateItemInDetailsPanelIfNeeded(el);
                                    new api.content.event.ContentUpdatedEvent(el.getContentId()).fire();

                                    results.push(updateResult[i]);
                                    break;
                                }
                            }
                        });
                        this.browseActions.updateActionsEnabledState(this.getBrowseItemPanel().getItems()); // update actions state in case of permission changes
                        this.mobileBrowseActions.updateActionsEnabledState(this.getBrowseItemPanel().getItems());

                        return this.contentTreeGrid.xPlaceContentNodes(results);
                    });
            }
        }

        private handleContentRenamed(change: ContentServerChange) {
            this.handleContentCreated(change, true, true)
        }

        private handleContentMoved(change: ContentServerChange, changes: ContentServerChange[]) {
            this.handleContentDeleted(changes);
            this.handleContentCreated(change, true);
        }

        private handleContentDeleted(changes: ContentServerChange[]) {

            var paths = [];
            changes.forEach(change => {
                paths = paths.concat(change.getContentPaths());
            });

            var deleteResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(paths);
            var nodes = deleteResult.map((el) => {
                return el.getNodes();
            });
            var merged = [];
            // merge array of nodes arrays
            merged = merged.concat.apply(merged, nodes);

            var contentDeletedEvent = new ContentDeletedEvent();
            merged.forEach((node: TreeNode<ContentSummaryAndCompareStatus>) => {
                var contentSummary = node.getData().getContentSummary();
                if (node.getData() && !!contentSummary) {

                    this.updateDetailsPanel(null);
                    contentDeletedEvent.addItem(contentSummary.getContentId(), contentSummary.getPath());
                }
            });
            contentDeletedEvent.fire();

            this.contentTreeGrid.xDeleteContentNodes(merged);

            var isFiltered = this.contentTreeGrid.getRoot().isFiltered();
            isFiltered = true;
            if (isFiltered) {
                this.setFilterPanelRefreshNeeded(true);
                window.setTimeout(() => {
                    this.refreshFilter();
                }, 1000);
            }
        }

        private handleContentPending(change: ContentServerChange) {

            var pendingResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(change.getContentPaths());

            return ContentSummaryAndCompareStatusFetcher.fetchByPaths(pendingResult.
                    map((el) => {
                        return (el.getNodes().length > 0 && el.getNodes()[0].getData())
                            ? el.getNodes()[0].getData().getContentSummary().getPath()
                            : null;
                    }).
                    filter((el) => {
                        return el !== null;
                    })
            ).then((data: ContentSummaryAndCompareStatus[]) => {
                    var contentDeletedEvent = new ContentDeletedEvent();
                    data.forEach((el) => {
                        for (var i = 0; i < pendingResult.length; i++) {
                            if (pendingResult[i].getId() === el.getId()) {
                                pendingResult[i].updateNodeData(el);

                                this.updateItemInDetailsPanelIfNeeded(el);

                                contentDeletedEvent.addPendingItem(el.getContentId(), el.getPath());
                                break;
                            }
                        }
                    });
                    contentDeletedEvent.fire();

                    this.contentTreeGrid.invalidate();
                });
        }

        private handleContentPublished(change: ContentServerChange) {
            var publishResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(change.getContentPaths());

            return ContentSummaryAndCompareStatusFetcher.
                fetchByPaths(publishResult.map((el) => {
                    return el.getPath();
                })).
                then((data: ContentSummaryAndCompareStatus[]) => {
                    data.forEach((el) => {
                        for (var i = 0; i < publishResult.length; i++) {
                            if (publishResult[i].getId() === el.getId()) {
                                publishResult[i].updateNodeData(el);

                                this.updateItemInDetailsPanelIfNeeded(el);

                                new api.content.event.ContentPublishedEvent(el.getContentId(), el.getCompareStatus()).fire();
                                break;
                            }
                        }
                    });
                    this.contentTreeGrid.invalidate();
                });
        }

        private handleContentSorted(change: ContentServerChange) {
            var sortResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(change.getContentPaths());

            var nodes = sortResult.map((el) => {
                return el.getNodes();
            });
            var merged = [];
            // merge array of nodes arrays
            merged = merged.concat.apply(merged, nodes);

            this.contentTreeGrid.xSortNodesChildren(merged).then(() => this.contentTreeGrid.invalidate());
        }

        private handleNewMediaUpload(event: app.create.NewMediaUploadEvent) {
            event.getUploadItems().forEach((item: UploadItem<ContentSummary>) => {
                this.contentTreeGrid.appendUploadNode(item);
            });
        }

        private updateStatisticsPreview(el: ContentSummaryAndCompareStatus) {
            var content = el,
                previewItem = this.getBrowseItemPanel().getStatisticsItem();

            if (!!content && !!previewItem && content.getPath().toString() === previewItem.getPath()) {
                new api.content.page.IsRenderableRequest(el.getContentId()).sendAndParse().
                    then((renderable: boolean) => {
                        var item = new BrowseItem<ContentSummaryAndCompareStatus>(content).
                            setId(content.getId()).
                            setDisplayName(content.getDisplayName()).
                            setPath(content.getPath().toString()).
                            setIconUrl(new ContentIconUrlResolver().setContent(content.getContentSummary()).resolve()).
                            setRenderable(renderable);
                        this.getBrowseItemPanel().setStatisticsItem(item);
                    });
            }
        }

        private updateDetailsPanel(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
            var detailsPanel = ActiveDetailsPanelsManager.getActiveDetailsPanel();
            return detailsPanel ? detailsPanel.setItem(item) : wemQ<any>(null);
        }

        private updateItemInDetailsPanelIfNeeded(item: ContentSummaryAndCompareStatus) {
            var detailsPanelItem: ContentSummaryAndCompareStatus = ActiveDetailsPanelsManager.getActiveDetailsPanel().getItem();
            if (detailsPanelItem && (detailsPanelItem.getId() == item.getId())) {
                this.updateDetailsPanel(item);
            }
        }
    }
}