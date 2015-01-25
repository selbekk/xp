module api.content.page.inputtype.pagecontroller {

    import support = api.form.inputtype.support;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import PageDescriptorDropdown = api.content.page.PageDescriptorDropdown;
    import PageDescriptor = api.content.page.PageDescriptor;
    import PageDescriptorsJson = api.content.page.PageDescriptorsJson;
    import GetPageDescriptorsByModulesRequest = api.content.page.GetPageDescriptorsByModulesRequest;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import ValueChangedEvent = api.form.inputtype.support.ValueChangedEvent;
    import Element = api.dom.Element;
    import ContentInputTypeViewContext = api.content.form.inputtype.ContentInputTypeViewContext;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class PageController extends support.BaseInputTypeNotManagingAdd<any, string> {

        constructor(context: ContentInputTypeViewContext<any>) {
            super(context);
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return ValueTypes.STRING.newNullValue();
        }

        createInputOccurrenceElement(index: number, property: Property): Element {
            var context = <ContentInputTypeViewContext<any>>this.getContext(),
                moduleKeys = context.site.getModuleKeys(),
                request = new GetPageDescriptorsByModulesRequest(moduleKeys),
                loader = new api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>(request);

            var dropdown = new PageDescriptorDropdown('page-controller[' + index + ']', {
                loader: loader
            });

            loader.onLoadedData((event: LoadedDataEvent<PageDescriptor>) => {
                dropdown.setValue(property.getString());
            });

            dropdown.onOptionSelected((event: OptionSelectedEvent<PageDescriptor>) => {
                var newValue = new Value(event.getOption().value, ValueTypes.STRING);
                property.setValue(newValue);
            });

            return dropdown;
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.STRING);
        }

        static getName(): api.form.InputTypeName {
            return new api.form.InputTypeName("PageController", false);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class(PageController.getName().getName(), PageController));

}