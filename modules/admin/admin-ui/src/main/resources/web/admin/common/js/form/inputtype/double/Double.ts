module api.content.form.inputtype.double {

    import BaseInputTypeNotManagingAdd = api.form.inputtype.support.BaseInputTypeNotManagingAdd;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class Double extends BaseInputTypeNotManagingAdd<number> {

        constructor(config: api.form.inputtype.InputTypeViewContext) {
            super(config);
        }

        getValueType(): ValueType {
            return ValueTypes.DOUBLE;
        }

        newInitialValue(): Value {
            return ValueTypes.DOUBLE.newNullValue();
        }

        createInputOccurrenceElement(index: number, property: Property): api.dom.Element {
            var inputEl = api.ui.text.TextInput.middle(undefined, this.getPropertyValue(property));
            inputEl.setName(this.getInput().getName() + "-" + property.getIndex());

            inputEl.onValueChanged((event: api.ValueChangedEvent) => {
                var isValid = this.isValid(event.getNewValue());
                if (isValid) {
                    this.onValueChanged(property, parseFloat(event.getNewValue()), ValueTypes.DOUBLE);
                }
                inputEl.updateValidationStatusOnUserInput(this.isValid(event.getNewValue()));
            });

            property.onPropertyValueChanged((event: api.data.PropertyValueChangedEvent) => {
                if (!this.ignorePropertyChange) {
                    this.updateInputOccurrenceElement(inputEl, property, true);
                }
            });

            return inputEl;
        }

        updateInputOccurrenceElement(occurrence: api.dom.Element, property: api.data.Property, unchangedOnly?: boolean) {
            var input = <api.ui.text.TextInput> occurrence;

            if (!unchangedOnly || !input.isDirty()) {
                input.setValue(this.getPropertyValue(property));
            }
        }

        availableSizeChanged() {
        }

        valueBreaksRequiredContract(value: Value): boolean {
            return value.isNull() || !value.getType().equals(ValueTypes.DOUBLE);
        }

        hasInputElementValidUserInput(inputElement: api.dom.Element) {
            var value = <api.ui.text.TextInput>inputElement;

            return this.isValid(value.getValue());
        }

        private isValid(value: string): boolean {
            var validUserInput = true;

            if (api.util.StringHelper.isEmpty(value)) {
                validUserInput = true;
            } else {

                if (api.util.NumberHelper.isNumber(+value)) {
                    validUserInput = true;
                } else {
                    validUserInput = false;
                }
            }

            return validUserInput;
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("Double", Double));
}