module app.login {

    import UserStore = api.security.UserStore;

    export class LoginForm extends api.dom.FormEl {

        private messageContainer: api.dom.DivEl;
        private userIdInput: api.ui.text.TextInput;
        private passwordInput: api.ui.text.PasswordInput;

        private authenticator: Authenticator;
        private userStores: {[userStoreId: string]: UserStore;};
        private onUserAuthenticatedHandler: (user: api.security.User) => void;

        constructor(authenticator: Authenticator) {
            super('login-form');
            this.authenticator = authenticator;
            this.userStores = {};
            this.onUserAuthenticatedHandler = null;

            this.userIdInput = new api.ui.text.TextInput('form-item');
            this.userIdInput.setPlaceholder(_i18n('userid or e-mail'));
            this.passwordInput = new api.ui.text.PasswordInput('form-item');
            this.passwordInput.setPlaceholder(_i18n('password'));
            this.userIdInput.onKeyUp((event: KeyboardEvent) => {
                this.onInputTyped(event);
            });
            this.passwordInput.onKeyUp((event: KeyboardEvent) => {
                this.onInputTyped(event);
            });

            this.messageContainer = new api.dom.DivEl("message-container");

            this.appendChild(this.userIdInput);
            this.appendChild(this.passwordInput);
            this.appendChild(this.messageContainer);

            this.onShown((event) => {
                this.userIdInput.giveFocus();
            })
        }

        onUserAuthenticated(handler: (user: api.security.User) => void) {
            this.onUserAuthenticatedHandler = handler;
        }

        hide() {
            super.hide();
        }

        private loginButtonClick() {
            var userName = this.userIdInput.getValue();
            var password = this.passwordInput.getValue();
            if (userName === '' || password === '') {
                return;
            }

            this.userIdInput.removeClass('invalid');
            this.passwordInput.removeClass('invalid');

            this.authenticator.authenticate(userName, password,
                (loginResult: api.security.auth.LoginResult) => this.handleAuthenticateResponse(loginResult));
        }

        private handleAuthenticateResponse(loginResult: api.security.auth.LoginResult) {
            if (loginResult.isAuthenticated()) {
                if (this.onUserAuthenticatedHandler) {
                    this.onUserAuthenticatedHandler(loginResult.getUser());
                }
                this.passwordInput.setValue('');
                this.messageContainer.setHtml('');
            } else {
                this.messageContainer.setHtml('Login failed!');
                this.passwordInput.giveFocus();
                this.userIdInput.addClass('invalid');
                this.passwordInput.addClass('invalid');
            }
        }

        private onInputTyped(event: KeyboardEvent) {
            var fieldsNotEmpty: boolean = (this.userIdInput.getValue() !== '') && (this.passwordInput.getValue() !== '');
            if (fieldsNotEmpty && event.keyCode == 13) {
                this.loginButtonClick();
            }
        }
    }

}