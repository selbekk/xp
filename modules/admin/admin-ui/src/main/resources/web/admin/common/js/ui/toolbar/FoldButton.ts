module api.ui.toolbar {

    export class FoldButton extends api.dom.DivEl {

        private span: api.dom.SpanEl;
        private dropdown: api.dom.DivEl;
        private widthCache: number[] = [];

        constructor() {
            super("button", api.StyleHelper.COMMON_PREFIX);

            this.addClass("fold-button");

            this.dropdown = new api.dom.DivEl("dropdown", api.StyleHelper.COMMON_PREFIX);
            this.appendChild(this.dropdown);

            this.span = new api.dom.SpanEl('fold-label');
            this.span.setHtml("More");
            this.appendChild(this.span);
        }

        push(element: api.dom.Element, width: number) {
            this.dropdown.prependChild(element);
            this.widthCache.unshift(width);
        }

        pop(): api.dom.Element {
            var top = this.dropdown.getFirstChild();
            this.dropdown.removeChild(top);
            this.widthCache.shift();
            return top;
        }

        setLabel(label: string) {
            this.span.setHtml(label);
        }

        getDropdown(): api.dom.DivEl {
            return this.dropdown;
        }

        getNextButtonWidth(): number {
            return this.widthCache[0];
        }

        getButtonsCount(): number {
            return this.dropdown.getChildren().length;
        }

        isEmpty(): boolean {
            return this.dropdown.getChildren().length == 0;
        }

    }

}