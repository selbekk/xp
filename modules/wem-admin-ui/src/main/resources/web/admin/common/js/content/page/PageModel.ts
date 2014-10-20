module api.content.page {

    import RootDataSet = api.data.RootDataSet;

    export class PageModel {

        private content: api.content.Content;

        private defaultTemplate: PageTemplate;

        private initialized: boolean = false;

        private controller: PageDescriptor;

        private template: PageTemplate;

        private usingDefaultTemplate: boolean = false;

        private regions: PageRegions;

        private config: RootDataSet;

        private propertyChangedListeners: {(event: api.PropertyChangedEvent):void}[] = [];

        constructor(content: api.content.Content, defaultPageTemplate: PageTemplate) {
            this.content = content;
            this.defaultTemplate = defaultPageTemplate;
        }

        isInitialized(): boolean {
            return this.initialized;
        }

        setController(pageDescriptor: PageDescriptor, eventSource?: any): PageModel {

            var oldControllerKey = this.controller ? this.controller.getKey() : null;
            this.controller = pageDescriptor;
            this.usingDefaultTemplate = false;

            if (!this.isInitialized()) {
                this.regions = this.content.isPage() ? this.content.getPage().getRegions() : new PageRegionsBuilder().build();
                this.config = this.content.isPage() ? this.content.getPage().getConfig() : new RootDataSet();
                this.initialized = true;
            }
            else {
                var regionDescriptors = pageDescriptor.getRegions();
                this.regions.changeRegionsTo(regionDescriptors);
            }

            var newControllerKey = this.controller ? this.controller.getKey() : null;
            if (!api.ObjectHelper.equals(oldControllerKey, newControllerKey)) {
                this.notifyPropertyChanged("controller", oldControllerKey, newControllerKey, eventSource);
            }

            return this;
        }

        setDefaultTemplate(eventSource?: any): PageModel {

            var oldTemplateKey = this.template ? this.template.getKey() : null;
            this.template = null;
            this.usingDefaultTemplate = true;

            // Need to clone config objects from template, otherwise the template gets changed while editing, since DataSet's are mutable
            this.regions = this.defaultTemplate.getRegions().clone();
            this.config = this.defaultTemplate.getConfig().clone();

            if (!api.ObjectHelper.equals(oldTemplateKey, null)) {
                this.notifyPropertyChanged("template", oldTemplateKey, null, eventSource);
            }
            this.initialized = true;

            return this;
        }

        setTemplate(template: PageTemplate, page: Page, eventSource?: any): PageModel {
            api.util.assertNotNull(template, "template cannot be null");
            var oldTemplateKey = this.template ? this.template.getKey() : null;
            this.template = template;
            this.usingDefaultTemplate = false;

            if (!this.isInitialized()) {
                if (page) {
                    this.regions = page.getRegions();
                    this.config = page.getConfig();
                }
                else {
                    // Need to clone config objects from template, otherwise the template gets changed while editing, since DataSet's are mutable
                    this.regions = template.getRegions().clone();
                    this.config = template.getConfig().clone();
                }
            }
            else {


            }
            var newTemplateKey = this.template ? this.template.getKey() : null;
            if (!api.ObjectHelper.equals(oldTemplateKey, newTemplateKey)) {
                this.notifyPropertyChanged("template", oldTemplateKey, newTemplateKey, eventSource);
            }
            this.initialized = true;
            return this;
        }

        setConfig(value: RootDataSet, eventOrigin?: any): PageModel {
            var oldValue = this.config;
            this.config = value;
            if (!api.ObjectHelper.equals(oldValue, value)) {
                this.notifyPropertyChanged("config", oldValue, value, eventOrigin);
            }
            return this;
        }

        getPage(): Page {

            if (this.isUsingDefaultTemplate()) {
                var defaultPage = this.defaultTemplate.getPage();
                var regionsChanges = !this.regions.equals(defaultPage.getRegions());
                var configChanges = !this.config.equals(defaultPage.getConfig());

                if (!regionsChanges && !configChanges) {
                    return null;
                }
                else {
                    var oldTemplateKey = this.template ? this.template.getKey() : null;
                    this.template = this.defaultTemplate;
                    var newTemplateKey = this.template ? this.template.getKey() : null;
                    if (!api.ObjectHelper.equals(oldTemplateKey, newTemplateKey)) {
                        this.notifyPropertyChanged("template", oldTemplateKey, newTemplateKey, this);
                    }
                }
            }
            else {
                if (!this.hasTemplate() && !this.hasController()) {
                    return null;
                }
            }

            return new PageBuilder().
                setController(this.getControllerKey()).
                setTemplate(this.getTemplateKey()).
                setRegions(this.regions).
                setConfig(this.config).
                build();
        }

        isPageTemplate(): boolean {
            return this.content.isPageTemplate();
        }

        hasController(): boolean {
            return !!this.controller;
        }

        getControllerKey(): DescriptorKey {
            return this.controller ? this.controller.getKey() : null;
        }

        getController(): PageDescriptor {
            return this.controller;
        }

        hasTemplate(): boolean {
            return !!this.template;
        }

        isUsingDefaultTemplate(): boolean {
            return this.usingDefaultTemplate;
        }

        getTemplateKey(): PageTemplateKey {
            return this.template ? this.template.getKey() : null;
        }

        getTemplate(): PageTemplate {
            return this.template;
        }

        getRegions(): PageRegions {
            return this.regions;
        }

        getConfig(): RootDataSet {
            return this.config;
        }

        onPropertyChanged(listener: (event: api.PropertyChangedEvent)=>void) {
            this.propertyChangedListeners.push(listener);
        }

        unPropertyChanged(listener: (event: api.PropertyChangedEvent)=>void) {
            this.propertyChangedListeners =
            this.propertyChangedListeners.filter((curr: (event: api.PropertyChangedEvent)=>void) => {
                return listener != curr;
            });
        }

        private notifyPropertyChanged(property: string, oldValue: any, newValue: any, origin: any) {
            var event = new api.PropertyChangedEvent(property, oldValue, newValue, origin);
            this.propertyChangedListeners.forEach((listener: (event: api.PropertyChangedEvent)=>void) => {
                listener(event);
            })
        }
    }
}