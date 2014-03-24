module api.content.page {

    export class PageComponent {

        private name: ComponentName;

        private region: RegionPath;

        private path: ComponentPath;

        private descriptorKey: DescriptorKey;

        private config: api.data.RootDataSet;

        constructor(builder?: PageComponentBuilder<any>) {
            if (builder != undefined) {
                this.name = builder.name;
                this.descriptorKey = builder.descriptor;
                this.config = builder.config;
                this.region = builder.region;
                if (this.region && this.name) {
                    this.path = ComponentPath.fromRegionPathAndComponentName(this.region, this.name);
                }
            }
        }

        setPath(path: ComponentPath) {
            this.path = path;
            if (path != null) {
                this.region = path.getRegionPath();
            }
        }

        getPath(): ComponentPath {
            return this.path;
        }

        getName(): api.content.page.ComponentName {
            return this.name;
        }

        hasDescriptor(): boolean {
            if (this.descriptorKey) {
                return true;
            }
            else {
                return false;
            }
        }

        getDescriptor(): DescriptorKey {
            return this.descriptorKey;
        }

        setDescriptor(key: DescriptorKey) {
            this.descriptorKey = key;
        }

        setConfig(value: api.data.RootDataSet) {
            this.config = value;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }

        toJson(): api.content.page.PageComponentTypeWrapperJson {
            throw new Error("Must be implemented by inheritor: " + api.util.getClassName(this));
        }

        toPageComponentJson(): PageComponentJson {

            return {
                "name": this.name.toString(),
                "descriptor": this.descriptorKey != null ? this.descriptorKey.toString() : null,
                "config": this.config != null ? this.config.toJson() : null
            };
        }
    }

    export class PageComponentBuilder<COMPONENT extends PageComponent> {

        name: api.content.page.ComponentName;

        descriptor: DescriptorKey;

        config: api.data.RootDataSet;

        region: RegionPath;

        public setName(value: api.content.page.ComponentName): PageComponentBuilder<COMPONENT> {
            this.name = value;
            return this;
        }

        public setDescriptor(value: DescriptorKey): PageComponentBuilder<COMPONENT> {
            this.descriptor = value;
            return this;
        }

        public setConfig(value: api.data.RootDataSet): PageComponentBuilder<COMPONENT> {
            this.config = value;
            return this;
        }

        public setRegion(value: api.content.page.RegionPath): PageComponentBuilder<COMPONENT> {
            this.region = value;
            return this;
        }

        public build(): COMPONENT {
            throw new Error("Must be implemented by inheritor");
        }
    }
}