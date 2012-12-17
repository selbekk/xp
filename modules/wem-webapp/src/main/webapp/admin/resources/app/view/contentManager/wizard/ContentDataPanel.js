Ext.define('Admin.view.contentManager.wizard.ContentDataPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.contentDataPanel',

    requires: [
        'Admin.lib.formitem.Layout',
        'Admin.lib.formitem.FormItemSet',
        'Admin.lib.formitem.HtmlArea',
        'Admin.lib.formitem.Relation',
        'Admin.lib.formitem.TextArea',
        'Admin.lib.formitem.TextLine'
    ],

    mixins: {
        formGenerator: 'Admin.lib.formitem.FormGenerator'
    },

    contentType: undefined,

    content: null, // content to be edited

    jsonSubmit: true,

    autoDestroy: true,

    initComponent: function () {
        var me = this;
        me.items = [];

        console.log('Content data',  me.content.data);

        if (me.content) {
            me.mixins.formGenerator.addComponentsBasedOnContentData(me.content.data, me.contentType.form, me);
        } else {
            me.mixins.formGenerator.addComponentsBasedOnContentType(me.contentType.form, me);
        }

        me.callParent(arguments);
    },


    getData: function () {
        return this.getContentData();
    },


    getContentData: function () {
        return this.buildContentData();
    },



    /**
     * TODO: Refactor to a new class, ContentDataBuilder
     */
    buildContentData: function () {
        var me = this;
        var contentData = {};
        var formItems = me.items.items;

        Ext.Array.each(formItems, function (formItem) {
            if (formItem.getXType() === 'FormItemSet') {
                me.addFormItemSetContentData(formItem, contentData, '');
            } else if (formItem.getXType() === 'Layout') {
                me.addLayoutData(formItem, contentData);
            } else {
                contentData[formItem.name] = formItem.getValue();
            }
        });

        return contentData;
    },


    addLayoutData: function (layoutComponent, contentData) {
        var items = layoutComponent.items.items;
        var layoutName = layoutComponent.name;

        console.log(layoutName);

        Ext.Array.each(items, function (item, index) {
            contentData[layoutName.concat('[0].', item.name)] = item.getValue();
        });
    },


    addFormItemSetContentData: function (formItemSetItem, contentData, parentName) {
        var me = this;
        var blocks = me.getFormItemSetBlocks(formItemSetItem);

        Ext.Array.each(blocks, function (block, index) {

            var blockItems = block.items.items;

            Ext.Array.each(blockItems, function (item) {
                var formItemSetName = '';
                if (parentName !== '') {
                    formItemSetName = parentName + '.'; // Eg. contact_info[0]
                }
                formItemSetName = formItemSetName.concat(formItemSetItem.name, '[', index, ']');

                if (item.getXType() === 'FormItemSet') {
                    // Recursive
                    me.addFormItemSetContentData(item, contentData, formItemSetName);
                } else {
                    if (item.cls !== 'header') {
                        contentData[formItemSetName.concat('.', item.name)] = item.getValue();
                    }
                }
            });
        });
    },


    getFormItemSetBlocks: function (formItemSetComponent) {
        var blocks = [];
        Ext.Array.each(formItemSetComponent.items.items, function (item, index) {
            if (item.cls && item.cls.indexOf('admin-formitemset-block') > -1) {
                blocks.push(item);
            }
        });

        return blocks;
    }


});