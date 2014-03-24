module app.wizard.page.contextwindow.inspect {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import DescriptorKey = api.content.page.DescriptorKey;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import GetLayoutDescriptorsByModulesRequest = api.content.page.layout.GetLayoutDescriptorsByModulesRequest;

    export class LayoutInspectionPanel extends PageComponentInspectionPanel<LayoutComponent, LayoutDescriptor> {

        private layoutDescriptors: {
            [key: string]: LayoutDescriptor;
        };

        constructor(liveFormPanel: app.wizard.page.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-layout", liveFormPanel, siteTemplate);

            this.layoutDescriptors = {};
            var getLayoutDescriptorsRequest = new GetLayoutDescriptorsByModulesRequest(this.getSiteTemplate().getModules());
            getLayoutDescriptorsRequest.sendAndParse().done((results: LayoutDescriptor[]) => {
                results.forEach((layoutDescriptor: LayoutDescriptor) => {
                    this.layoutDescriptors[layoutDescriptor.getKey().toString()] = layoutDescriptor;
                });
            });
        }

        getDescriptor(): LayoutDescriptor {
            if (!this.getComponent().hasDescriptor()) {
                return null;
            }
            return this.layoutDescriptors[this.getComponent().getDescriptor().toString()];
        }

        setLayoutComponent(component: LayoutComponent) {
            this.setComponent(component);

            var layoutDescriptor = this.getDescriptor();
            if (!layoutDescriptor) {
                return;
            }
            this.setupComponentForm(component, layoutDescriptor);
        }

    }
}