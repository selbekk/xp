module api.ui.form {

    export class FormItem extends api.dom.DivEl {

        private label: api.dom.LabelEl;
        private input: api.dom.FormInputEl;
        private error: api.dom.SpanEl;
        private validator: (input: api.dom.FormInputEl) => string;
        private invalidClass: string = "invalid";

        private focusListeners: {(event: FocusEvent):void}[] = [];

        private blurListeners: {(event: FocusEvent):void}[] = [];

        constructor(builder: FormItemBuilder) {
            super("form-item");
            this.error = new api.dom.SpanEl("error");
            this.appendChild(this.error);

            this.input = builder.getInput();
            this.input.onFocus((event: FocusEvent) => {
                this.notifyFocused(event);
            });

            this.input.onBlur((event: FocusEvent) => {
                this.notifyBlurred(event);
            });
            if (builder.getLabel()) {
                this.label = new api.dom.LabelEl(builder.getLabel(), this.input);
                if(Validators.required == builder.getValidator()) {
                    this.label.setClass("required");
                }
                this.appendChild(this.label);
            }
            this.appendChild(this.input);

            if(builder.getValidator()) {
                this.validator = builder.getValidator();
            }
        }

        getLabel(): api.dom.LabelEl {
            return this.label;
        }

        getInput(): api.dom.FormInputEl {
            return this.input;
        }

        getValidator(): (input: api.dom.FormInputEl) => string {
            return this.validator;
        }

        validate(validationResult:ValidationResult, markInvalid?: boolean) {
            if (this.validator) {
                var validationMessage = this.validator(this.input);
                if(validationMessage) {
                    validationResult.addError(new ValidationError(this, validationMessage));
                }
                if (markInvalid) {
                    if (validationMessage) {
                        this.addClass(this.invalidClass);
                    } else {
                        this.removeClass(this.invalidClass);
                    }
                    this.error.setHtml(validationMessage || "");
                }
            }
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners.push(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners = this.focusListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners.push(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners = this.blurListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocused(event: FocusEvent) {
            this.focusListeners.forEach((listener) => {
                listener(event);
            })
        }

        private notifyBlurred(event: FocusEvent) {
            this.blurListeners.forEach((listener) => {
                listener(event);
            })
        }

    }

    export class FormItemBuilder {

        private label: string;
        private validator: (el: api.dom.FormInputEl) => string;
        private input: api.dom.FormInputEl;

        constructor(input: api.dom.FormInputEl) {
            if(!input) {
                throw new Error("Input can't be null.");
            }
            this.input = input;
        }

        build() {
            return new FormItem(this);
        }

        getInput(): api.dom.FormInputEl {
            return this.input;
        }

        setLabel(label: string):FormItemBuilder {
            this.label = label;
            return this;
        }

        getLabel(): string {
            return this.label;
        }

        setValidator(validator: (input: api.dom.FormInputEl) => string):FormItemBuilder {
            this.validator = validator;
            return this;
        }

        getValidator(): (input: api.dom.FormInputEl) => string {
            return this.validator;
        }

    }

}