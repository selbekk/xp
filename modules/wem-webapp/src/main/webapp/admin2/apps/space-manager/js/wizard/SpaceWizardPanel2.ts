module app_wizard {
    export class SpaceWizardPanel2 extends api_wizard.WizardPanel {
        constructor(id:string, title:string) {
            super();
            var context = app_wizard.SpaceWizardContext.createSpaceWizardContext();
            this.setTitle(title);
            this.setSubtitle(id);


            var spacePanel = new api_ui.Panel("spacePanel");
            var h1El = new api_ui.H1El();
            h1El.getEl().setInnerHtml("space");
            spacePanel.appendChild(h1El);

            var schemaPanel = new api_ui.Panel("schemaPanel");
            h1El = new api_ui.H1El();
            h1El.getEl().setInnerHtml("schema");
            schemaPanel.appendChild(h1El);

            var modulesPanel = new api_ui.Panel("modulesPanel");
            h1El = new api_ui.H1El();
            h1El.getEl().setInnerHtml("modules");
            modulesPanel.appendChild(h1El);

            var templatesPanel = new api_ui.Panel("templatesPanel");
            h1El = new api_ui.H1El();
            h1El.getEl().setInnerHtml("templates");
            templatesPanel.appendChild(h1El);

            this.addStep(new api_wizard.WizardStep("Space", spacePanel));
            this.addStep(new api_wizard.WizardStep("Schemas", schemaPanel));
            this.addStep(new api_wizard.WizardStep("Modules", modulesPanel));
            this.addStep(new api_wizard.WizardStep("Templates", templatesPanel));

            this.addToolbar(new SpaceWizardToolbar2(context.getActions()));
        }
    }
}