module api.content {

    export class AttachmentItem extends api.dom.DivEl {

        private link: api.dom.AEl;

        private removeEl: api.dom.DivEl;

        private value: string;

        constructor(contentId: string, value: string, removeCallback?: (value) => void) {
            super("attachment-item");

            this.value = value;

            this.link = new api.dom.AEl().
                setUrl(api.util.UriHelper.getRestUri('content/media/' + contentId +'/'+ value));
            this.link.setHtml(value);

            this.initRemoveButton(removeCallback);
        }

        private initRemoveButton(callback?: (value) => void) {
            this.removeEl = new api.dom.DivEl("icon remove");

            this.removeEl.onClicked(() => {
                if (callback) {
                    callback(this.value);
                    this.remove();
                }
            });
        }

        getValue() : string {
            return this.value;
        }

        doRender(): boolean {
            this.removeChildren();

            this.appendChild(this.removeEl);
            this.appendChild(this.link);
            return true;
        }
    }
}