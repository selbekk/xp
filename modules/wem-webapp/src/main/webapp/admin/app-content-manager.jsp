<%@ taglib prefix="w" uri="uri:enonic.wem.taglib" %>
<!DOCTYPE html>
<w:helper var="helper"/>
<html>
<head>
  <meta charset="utf-8"/>
  <title>Enonic WEM Admin</title>
  <link rel="stylesheet" type="text/css" href="resources/css/icons.css">
  <link rel="stylesheet" type="text/css" href="resources/css/icons-metro.css">
  <link rel="stylesheet" type="text/css" href="resources/css/admin-preview-panel.css">

  <link rel="stylesheet" type="text/css" href="resources/css/admin-top-bar.css">
  <link rel="stylesheet" type="text/css" href="resources/css/admin-start-menu.css">

  <link rel="stylesheet" type="text/css" href="resources/lib/ext/resources/css/admin.css"/>

  <!-- ExtJS -->

  <script type="text/javascript" src="resources/lib/ext/ext-all-debug.js"></script>

  <!-- Configuration -->

  <script type="text/javascript" src="global.config.js"></script>
  <script type="text/javascript" charset="utf-8">

    window.CONFIG = {
      baseUrl: '<%= helper.getBaseUrl() %>'
    };

    Ext.Loader.setConfig({
      paths: {
        'Common': 'common/js',
        'Admin': 'resources/app'
      },
      disableCaching: false
    });

  </script>

  <!-- Templates -->

  <script type="text/javascript" src="resources/app/view/XTemplates.js"></script>

  <!-- Third party plugins -->

  <script type="text/javascript" src="resources/lib/jit/jit-yc.js"></script>

  <!-- Application -->

  <script type="text/javascript">
    Ext.application({
      name: 'App',

      controllers: [
        'Admin.controller.Controller',
        'Admin.controller.TopBarController',
        'Admin.controller.contentManager.GridPanelController',
        'Admin.controller.contentManager.DetailPanelController',
        'Admin.controller.contentManager.FilterPanelController',
        'Admin.controller.contentManager.BrowseToolbarController',
        'Admin.controller.contentManager.ContentWizardController',
        'Admin.controller.contentManager.ContentPreviewController',
        'Admin.controller.contentManager.DialogWindowController'
      ],

      requires: [
        'Admin.MessageBus',
        'Admin.view.FeedbackBox',
        'Admin.view.TabPanel'
      ],

      launch: function () {

        Ext.create('Ext.container.Viewport', {
          layout: 'fit',
          cls: 'admin-viewport',

          items: [
            {
              xtype: 'cmsTabPanel',
              appName: 'Content Manager',
              appIconCls: 'icon-metro-content-manager-24',
              items: [
                {
                  id: 'tab-browse',
                  title: 'Browse',
                  closable: false,
                  xtype: 'panel',
                  layout: 'border',
                  items: [
                    {
                      region: 'west',
                      xtype: 'contentFilter',
                      width: 200
                    },
                    {
                      region: 'center',
                      xtype: 'contentShow'
                    }
                  ]
                }
              ]
            }
          ]
        });

        Ext.create('widget.feedbackBox');

      }
    });

  </script>

</head>
<body>
</body>
</html>
