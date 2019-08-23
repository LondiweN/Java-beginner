//dummy console.log to prevent errrors
(function() {
    if (!window.console) {
        window.console = {
            log: function() {
            }
        };
    }
}());

(function($) {

    $.fn.textEntryStopped = function(callback) {
        var $this = $(this);
        $this.keydown(function() {
            countDown($this, callback);
        });
        $this.keyup(function() {
            countDown($this, callback);
        });
        $this.keypress(function() {
            countDown($this, callback);
        });
        $this.on("paste", function() {
            countDown($this, callback);
        });

        function countDown($this, callback) {
            if ($this.data('textEntryTimeout')) {
                clearTimeout($this.data('textEntryTimeout'));
            }
            $this.data('textEntryTimeout', setTimeout(callback, 500));
        }
    };

    var ideInstanceIdSequence = 0;

    var newFileTypes = [{
            category: "Java",
            types: [{
                    type: "Java Class",
                    description: "A plain Java class.",
                    template: "{{package}}public class {{name}} {\n  \n}",
                    mode: "text/x-java"
                }, {
                    type: "Java Interface",
                    description: "A Java interface.",
                    template: "{{package}}public interface {{name}} {\n  \n}",
                    mode: "text/x-java"
                }, {
                    type: "Java Enum",
                    description: "A Java enumeration.",
                    template: "{{package}}public enum {{name}} {\n  \n}",
                    mode: "text/x-java"
                }, {
                    type: "Java Annotation",
                    description: "Create a new Java annotation type.",
                    template: "{{package}}public @interface {{name}} {\n  \n}",
                    mode: "text/x-java"
                }, {
                    type: "Package Info",
                    description: "Create a new package-info.java file.",
                    template: "{{package}}",
                    mode: "text/x-java"
                }]
        }];

//$( "<div />" ).appendTo( "body" ).progressbar({ value: 20 });
    $.widget("stcurr.ide", {
        version: "0.3.1",
        _ideInstanceId: null,
        // Default options.
        options: {
            theme: "eclipse",
            mode: "basic",
            disableStorage: false,
            selectedTab: null,
            tabs: [],
            args: "",
            URL_PREFIX: "../"
        },
        _editor: null,
        _create: function() {
            this._ideInstanceId = "ide-" + ideInstanceIdSequence++;
            // this.element
            // this.options
            $.extend(this.options, this.element.data("options"));
            this.element.wrapInner("<div class='instructions'/>");
            this._createToolbar();
            var tabId = "tabs-" + location.pathname + "-" + this._ideInstanceId;
            console.log("tabId:" + tabId);
            if (!this.options.disableStorage && sessionStorage && sessionStorage.getItem(tabId) && JSON.parse(sessionStorage.getItem(tabId)).length > 0) {
                console.log("Restoring " + JSON.parse(sessionStorage.getItem(tabId)).length + " tab(s) from session storage");
                console.log(JSON.parse(sessionStorage.getItem(tabId)));
                for (var x in JSON.parse(sessionStorage.getItem(tabId))) {
                    console.log(x);
                }
                this.options.tabs = JSON.parse(sessionStorage.getItem(tabId));
            } 
            
            if (this.options.tabs.length > 0) {
                this.element.find(".instructions").remove();
                console.log("Populating tabs with " + this.options.tabs.length + " files.");
                this._createTabs();
                this._createEditor();
                this._createOutput();
                this._handleStoreTabsInSession();
            } else {
                console.log("No prepopulated tabs.");
            }
            this._createNewFileDialog();
            this._createArgsDialog();
            this._createAboutDialog();
        },
        _destroyTabs: function() {
            this.element.find(".fileNames").buttonset("destroy");
            this.element.find(".fileNames").remove();
        },
        _destroyEditor: function() {
            this.element.find(".editor").remove();
            this._editor = undefined;
        },
        _destroyOutput: function() {
            this.element.find(".outputTab").remove();
            this.element.find(".output").remove();
        },
        _createToolbar: function() {
            var that = this;
            if (this.options.mode === "basic") {
                var toolbarHtml =
                        '<div class="toolbar ui-widget-header ui-corner-all">' +
                        '<button class="newButton">New</button>' +
                        '<button class="runButton">Run</button>' +
                        '<button class="configButton">Configure</button>' +
                        // '<button class="findButton">Find/Replace</button>' +
                        '<button class="undoButton">Undo</button>' +
                        '<button class="redoButton">Redo</button>' +
                        // '<button class="gotoButton">Goto Line</button>' +
                        '<button class="reformatButton">Indent Lines</button>' +
                        '<button class="deleteButton">Delete</button>' +
                        '<button class="restartButton">Discard All Work</button>' +
                        '<button class="aboutButton">About</button>' +
                        '</div>';
                this.element.prepend(toolbarHtml);
                this.element.find(".newButton").button({
                    text: false,
                    icons: {
                        primary: "ui-icon-document"
                    }
                }).click(function() {
                    $(this).blur();
                    $('.ide-new-file-type form > fieldset').not('.page1').hide();
                    $('.ide-new-file-type form > fieldset.page1').show();
                    $("#ide-new-file-dialog").dialog("open");
                });
                this.element.find(".runButton").button({
                    text: false,
                    disabled: true,
                    icons: {
                        primary: "ui-icon-play"
                    }
                }).click(function() {
                    $(this).blur();
                    that.element.find(".output").empty();
                    that.element.find(".outputTab:hidden").show();
                    that.element.find(".output:hidden").show();
                    var project = {
                        files: []
                    };
                    project.selectedFile = parseInt(that.element.find('.fileNames input[type="radio"][name="' + that._ideInstanceId + '-file-radio"]:checked').val(), 10);
                    project.args = that.options.args;
                    var fileNameRadios = that.element.find('.fileNames input[type="radio"][name="' + that._ideInstanceId + '-file-radio"]');
                    $(fileNameRadios).each(function() {
                        project.files.push({name: $(this).data("name"), text: $(this).data("doc").getValue()});
                    });

                    console.log("project: " + JSON.stringify(project));
                    $.ajax({
                        type: 'POST',
                        contentType: 'application/json',
                        processData: false,
                        url: that.options.URL_PREFIX + 'resources/javac/project',
                        data: JSON.stringify(project),
                        success: function(data, textStatus, jqXHR) {
                            //TODO Validate that .text() escapes everything
                            that.element.find(".output").text(data);
                        },
                        error: function(jqXHR, textStatus, errorThrown) {
                            that.element.find(".output").text('POST error: ' + textStatus + ',' + errorThrown);
                        }
                    });
                }
                );
                this.element.find(".configButton").button({
                    text: false,
                    icons: {
                        primary: "ui-icon-gear"
                    }
                }).click(function() {
                    //TODO set main args
                    $(this).blur();
                    $("#ide-args-dialog").dialog("open");
                });
                this.element.find(".findButton").button({
                    text: false,
                    disabled: true,
                    icons: {
                        primary: "ui-icon-search"
                    }
                });
                this.element.find(".undoButton").button({
                    text: false,
                    disabled: true,
                    icons: {
                        primary: "ui-icon-arrowreturnthick-1-w"
                    }
                }).click(function() {
                    that._editor.getDoc().undo();
                    $(this).blur();
                });
                this.element.find(".redoButton").button({
                    text: false,
                    disabled: true,
                    icons: {
                        primary: "ui-icon-arrowreturnthick-1-e"
                    }
                }).click(function() {
                    that._editor.getDoc().redo();
                    $(this).blur();
                });
                this.element.find(".gotoButton").button({
                    text: false,
                    disabled: true,
                    icons: {
                        primary: "ui-icon-arrowthickstop-1-e"
                    }
                });
                this.element.find(".reformatButton").button({
                    text: false,
                    disabled: true,
                    icons: {
                        primary: "ui-icon-grip-dotted-horizontal"
                    }
                }).click(function() {
                    var lineCount = that._editor.getDoc().lineCount();
                    for (var i = 0; i < lineCount; i++) {
                        that._editor.indentLine(i, "smart");
                    }
                    $(this).blur();
                });
                this.element.find(".deleteButton").button({
                    text: false,
                    disabled: true,
                    icons: {
                        primary: "ui-icon-trash"
                    }
                }).click(function() {
                    $(this).blur();
                    var fileNameSet = that.element.find(".fileNames");
                    var fileNameRadios = fileNameSet.find('input[type="radio"][name="' + that._ideInstanceId + '-file-radio"]');
                    var fileNameRadio = fileNameRadios.filter(':checked');
                    var selectedFile = parseInt(fileNameRadio.val(), 10);
                    if (fileNameRadios.length > 1) {
                        that.options.tabs.splice(fileNameRadio.val(), 1);
                        fileNameSet.find("label[for='" + fileNameRadio.attr("id") + "']").remove();
                        fileNameRadio.remove();
                        fileNameRadios = fileNameSet.find('input[type="radio"][name="' + that._ideInstanceId + '-file-radio"]');
                        fileNameSet.buttonset("refresh");
                        fileNameRadios.each(function() {
                            var label = fileNameSet.find("label[for='" + that._ideInstanceId + "-file-" + $(this).val() + "']");
                            //adjust all tabs to the right of the deleted one
                            if ($(this).val() > selectedFile) {
                                $(this).val($(this).val() - 1);
                                $(this).attr("id", that._ideInstanceId + "-file-" + $(this).val());
                                label.attr("for", that._ideInstanceId + "-file-" + $(this).val());
                            }
                        });
                        console.log("selected:" + selectedFile);
                        if (selectedFile > 0) {
                            fileNameRadio = fileNameRadios.filter("[value='" + (selectedFile - 1) + "']");
                            fileNameRadio.change().parent().find("label[for=" + fileNameRadio.attr("id") + "]").click();
                        } else {
                            fileNameRadio = fileNameRadios.first();
                            console.log("id:" + fileNameRadio.attr("id"));
                            fileNameRadio.change().parent().find("label[for=" + fileNameRadio.attr("id") + "]").click();
                        }
                    } else {
                        that.options.tabs = [];
                        that._destroyTabs();
                        that._destroyEditor();
                        that._destroyOutput();
                        that.element.find(".runButton").button("option", "disabled", true);
                        that.element.find(".reformatButton").button("option", "disabled", true);
                        that.element.find(".deleteButton").button("option", "disabled", true);
                    }
                });
                this.element.find(".restartButton").button({
                    text: false,
                    icons: {
                        primary: "ui-icon-seek-first"
                    }
                }).click(function() {
                    if (sessionStorage) {
                        sessionStorage.clear();
                    }
                    location.reload(false);
                });
                this.element.find(".aboutButton").button({
                    text: false,
                    icons: {
                        primary: "ui-icon-info"
                    }
                }).click(function() {
                    $(this).blur();
                    $("#ide-about-dialog").dialog("open");
                });
            } else {
                return;
            }
        },
// var bar = $( "<div />" ).appendTo( "body").progressbar({ value: 20 });
// Get the current value.
// alert( bar.progressbar( "value" ) ); 
// Update the value.
// bar.progressbar( "value", 50 );
// Get the current value again.
// alert( bar.progressbar( "value" ) );

        _addFile: function(mode, template, name, pkg) {
            var text;
            if (mode === "text/x-java") {
                if (pkg) {
                    text = template.replace("{{package}}", "package " + pkg + ";\n\n");
                } else {
                    text = template.replace("{{package}}", "");
                }
                text = text.replace("{{name}}", name);
                if (name.indexOf(".java", name.length - ".java".length) === -1) {
                    name += ".java";
                }
            } else {
                text = template;
            }

            var that = this;
            if (this.options.tabs.length > 0) {
                this.options.tabs.push({name: name, text: text});
                var fileNameSet = this.element.find(".fileNames");
                var fileNameInput =
                        $('<input type="radio" id="' + this._ideInstanceId + '-file-' + (this.options.tabs.length - 1) + '" name="' + this._ideInstanceId + '-file-radio" value="' + (this.options.tabs.length - 1) + '" />' +
                        '<label for="' + this._ideInstanceId + '-file-' + (this.options.tabs.length - 1) + '">' + name + '</label>');
                fileNameSet.append(fileNameInput);
                //http://bugs.jqueryui.com/ticket/8975
                fileNameSet.find("#" + this._ideInstanceId + '-file-' + (this.options.tabs.length - 1)).button();
                fileNameSet.buttonset("refresh");

                var doc = new CodeMirror.Doc(text, mode);
                fileNameInput.data("name", name);
                fileNameInput.data("doc", doc);
                fileNameInput.change(function() {
                    fileNameSet.find("input").blur();
                    that._editor.swapDoc($(this).data("doc"));
                    that._handleUndoRedoButtonState();
                });

                var fileNameRadio = fileNameSet.find('#' + this._ideInstanceId + '-file-' + (this.options.tabs.length - 1));
                fileNameRadio.change().parent().find("label[for='" + fileNameRadio.attr("id") + "']").click();
            } else {
                this.element.find(".instructions").remove();
                this.options.tabs.push({name: name, text: text});
                this._createTabs();
                this._createEditor();
                this._createOutput();
            }
            this._handleStoreTabsInSession();
        },
        _deleteFile: function() {

        },
        _createTabs: function() {
            var fileNameSet = $('<div class="fileNames"></div>').appendTo(this.element);
            for (var i = 0; i < this.options.tabs.length; i++) {
                var fileNameInput =
                        '<input type="radio" id="' + this._ideInstanceId + '-file-' + i + '" name="' + this._ideInstanceId + '-file-radio" value="' + i + '" />' +
                        '<label for="' + this._ideInstanceId + '-file-' + i + '">' + this.options.tabs[i].name + '</label>';
                fileNameSet.append(fileNameInput);
            }
            fileNameSet.buttonset();
            this.element.find(".runButton").button("option", "disabled", false);
            this.element.find(".reformatButton").button("option", "disabled", false);
            this.element.find(".deleteButton").button("option", "disabled", false);
        },
        _createEditor: function() {
            var that = this;
            var editorDiv = $('<div class="editor"></div>').appendTo(this.element);
            //$(editorDiv).resizable({minHeight: 100, minWidth: 100, handles: "s, e"});
            this._editor = CodeMirror(function(elt) {
                editorDiv.append(elt);
            }, {
                lineNumbers: true,
                matchBrackets: true,
                theme: this.options.theme,
                mode: "text/x-java"
                        //viewportMargin: Infinity
            });

            this._editor.on("change", function() {
                that._handleUndoRedoButtonState();
                //TODO update options.tabs
                that._handleStoreTabsInSession();
            });

            var fileNameSet = this.element.find('.fileNames');
            var fileNameRadios = fileNameSet.find('input[type="radio"][name="' + this._ideInstanceId + '-file-radio"]');
            fileNameRadios.each(function() {
                var name = that.options.tabs[$(this).val()].name;
                var mode = name.length > 4 && name.indexOf(".java") === name.length - 5 ? "text/x-java" : "text/plain";
                var doc = new CodeMirror.Doc(that.options.tabs[$(this).val()].text, mode);
                $(this).data("name", name);
                $(this).data("doc", doc);
            });
            fileNameRadios.change(function() {
                fileNameSet.find('input').blur();
                that._editor.swapDoc($(this).data("doc"));
                that._handleUndoRedoButtonState();
            });
            var fileNameRadio = fileNameRadios.first();
            fileNameRadio.change().parent().find("label[for='" + fileNameRadio.attr("id") + "']").click();
            this.element.find(".fileNames").buttonset("refresh");
        },
        _createOutput: function() {
            var that = this;
            var outputBar = $('<div class="outputTab"></div>').appendTo(this.element);
            $('<input type="radio" id="' + this._ideInstanceId + '-output-text" name="' + this._ideInstanceId + '-output-radio" value="Output" />' +
                    '<label for="' + this._ideInstanceId + '-output-text">Output</label>').appendTo(outputBar);
            $('<input type="radio" id="' + this._ideInstanceId + '-output-button" name="' + this._ideInstanceId + '-output-radio" value="Close" />' +
                    '<label for="' + this._ideInstanceId + '-output-button">Close</label>').appendTo(outputBar);

            outputBar.buttonset();
            var outputText = this.element.find('#' + this._ideInstanceId + '-output-text');
            var outputCloseButton = this.element.find('#' + this._ideInstanceId + '-output-button');

            //outputText.attr("checked", "checked").change().click();
            outputText.change().parent().find("label[for='" + outputText.attr("id") + "']").click();

            //outputText.trigger("change");
            //outputText.attr("checked", "checked");
            outputCloseButton.button("option", "icons", {primary: 'ui-icon-closethick'});
            outputCloseButton.button("option", "text", false);
            outputCloseButton.click(function() {
                //outputCloseButton.blur();
                //outputText.attr("checked", "checked").change().click();
                outputText.change().parent().find("label[for='" + outputText.attr("id") + "']").click();
                //outputText.change().click();
                that.element.find(".outputTab").hide();
                that.element.find(".output").hide();
            });
            outputText.button("refresh");
            this.element.append("<div class='output'></div>");
            this.element.find(".outputTab").hide();
            this.element.find(".output").hide();
        },
        _createArgsDialog: function() {
            var that = this;
            var argsDialog = $("#ide-args-dialog");
            if (argsDialog.length === 0) {
                argsDialog = $('<div id="ide-args-dialog" title="Project Properties">' +
                    '<form>' +
                    '<fieldset>' +
                    '<label>Arguments:<input type="text" name="args" size="60"/></label>' +
                    '</fieldset>' +
                    '</form>' +
                    '</div>');
                argsDialog.appendTo("body");
                var argsField = argsDialog.find('input[name="args"][type="text"]');
                argsDialog.dialog({
                    autoOpen: false,
                    height: "auto",
                    width: "auto",
                    resizable: false,
                    modal: true,
                    buttons: [{
                            text: "Save",
                            click: function() {
                                var args = argsField.val();
                                that.options.args = args;
                                $(this).dialog("close");
                            }
                        }, {
                            text: "Cancel",
                            click: function() {
                                $(this).dialog("close");
                            }
                        }],
                    open: function() {
                        argsField.val(that.options.args);
                    },
                    close: function() {

                    }
                });
            }
        },
        _createNewFileDialog: function() {
            var that = this;
            var newFileDialog = $("#ide-new-file-dialog");
            if (newFileDialog.length === 0) {
                newFileDialog = $('<div id="ide-new-file-dialog" title="Create New File">' +
                        '<div class="ide-new-file-type">' +
                        '<form>' +
                        '<fieldset class="page1">' +
                        '<legend>New File Type</legend>' +
                        '<div>' +
                        '<div class="left">' +
                        '<fieldset>' +
                        '<legend>Category</legend>' +
                        '<select name="category" size="10">' +
                        '</select>' +
                        '</fieldset>' +
                        '</div>' +
                        '<div class="right">' +
                        '<fieldset>' +
                        '<legend>File Type</legend>' +
                        '<select name="type" size="10">' +
                        '</select>' +
                        '</fieldset>' +
                        '</div>' +
                        '</div>' +
                        '<fieldset>' +
                        '<legend>Description</legend>' +
                        '<p></p>' +
                        '</fieldset>' +
                        '</fieldset>' +
                        '<fieldset class="page2">' +
                        '<legend></legend>' +
                        '<div><label>Name:<input name="name" type="text" autocomplete="off"></input></label></div>' +
                        '<div><label>Package:<input name="package" type="text" autocomplete="off"></input></label></div>' +
                        '<p><span class="icon" style="display:inline-block"></span><span class="text-content"></span></p>' +
                        '</fieldset>' +
                        '</form>' +
                        '</div>' +
                        '</div>');
                newFileDialog.appendTo("body");
                var newDialogPage1 = newFileDialog.find("fieldset.page1");
                var newDialogPage2 = newFileDialog.find("fieldset.page2");
                var categorySelect = newDialogPage1.find("select[name='category']");
                var typeSelect = newDialogPage1.find("select[name='type']");
                var nameField = newDialogPage2.find('input[name="name"][type="text"]');
                var packageField = newDialogPage2.find('input[name="package"][type="text"]');

                categorySelect.change(function() {
                    var selected = $(this).find(":selected");
                    typeSelect.empty();
                    newFileDialog.find("fieldset.page1 p").empty();
                    if (selected.length > 0) {
                        var types = selected.data("types");
                        for (var t = 0; t < types.length; t++) {
                            $("<option>" + types[t].type + "</option>")
                                    .appendTo(typeSelect)
                                    .data("description", types[t].description)
                                    .data("template", types[t].template)
                                    .data("mode", types[t].mode);
                        }
                    }
                });
                typeSelect.change(function() {
                    var selected = $(this).find(":selected");
                    if (selected.length > 0) {
                        newFileDialog.find("fieldset.page1 p").text(selected.data("description"));
                        newFileDialog.closest(".ui-dialog").find(".ui-dialog-buttonset button span:contains('Next')").parent().button("option", "disabled", false);
                    }
                });
                var validateNames = function() {
                    newDialogPage2.find('p > span.icon').removeClass().addClass('icon');
                    newFileDialog.closest(".ui-dialog").find(".ui-dialog-buttonset button span:contains('Finish')").parent().button("option", "disabled", true).removeClass("ui-state-focus");
                    var selected = typeSelect.find(":selected");
                    if (selected.val() === "Package Info") {
                        if (!(packageField.val()) || (packageField.val().length === 0)) {
                            newDialogPage2.find("p > span.text-content").text("A package name is required.");
                        } else {
                            newDialogPage2.find("p > span.text-content").empty();
                            $.ajax({
                                type: 'POST',
                                contentType: 'text/plain',
                                processData: false,
                                url: that.options.URL_PREFIX + 'resources/javac/qualified-name-validator',
                                data: packageField.val(),
                                success: function(data, textStatus, jqXHR) {
                                    if (data === 'true') {
                                        newFileDialog.closest(".ui-dialog").find(".ui-dialog-buttonset button span:contains('Finish')").parent().button("option", "disabled", false);
                                    } else {
                                        newDialogPage2.find("p > span.text-content").text('A valid package name is required.');
                                        newDialogPage2.find('p > span.icon').removeClass().addClass('icon').addClass("ui-icon ui-icon-alert");
                                    }
                                },
                                error: function(jqXHR, textStatus, errorThrown) {
                                    newDialogPage2.find("p > span.text-content").text('POST error: ' + textStatus + ',' + errorThrown);
                                    newDialogPage2.find('p > span.icon').removeClass().addClass('icon').addClass("ui-icon ui-icon-alert");
                                }
                            });
                        }
                    } else {
                        if ((!(nameField.val()) && !(packageField.val())) || (nameField.val().length + packageField.val().length === 0)) {
                            newDialogPage2.find("p > span.text-content").text("A package name is recommended but not required.");
                        } else {
                            newDialogPage2.find("p > span.text-content").empty();
                            if (!(packageField.val()) || packageField.val().length === 0) {
                                $.ajax({
                                    type: 'POST',
                                    contentType: 'text/plain',
                                    processData: false,
                                    url: that.options.URL_PREFIX + 'resources/javac/short-name-validator',
                                    data: nameField.val(),
                                    success: function(data, textStatus, jqXHR) {
                                        if (data === 'true') {
                                            newFileDialog.closest(".ui-dialog").find(".ui-dialog-buttonset button span:contains('Finish')").parent().button("option", "disabled", false);
                                        } else {
                                            newDialogPage2.find("p > span.text-content").text('A valid ' + typeSelect.find(":selected").val() + ' name is required.');
                                            newDialogPage2.find('p > span.icon').removeClass().addClass('icon').addClass("ui-icon ui-icon-alert");
                                        }
                                    },
                                    error: function(jqXHR, textStatus, errorThrown) {
                                        newDialogPage2.find("p > span.text-content").text('POST error: ' + textStatus + ',' + errorThrown);
                                        newDialogPage2.find('p > span.icon').removeClass().addClass('icon').addClass("ui-icon ui-icon-alert");
                                    }
                                });
                            } else {
                                $.ajax({
                                    type: 'POST',
                                    contentType: 'application/json',
                                    processData: false,
                                    url: that.options.URL_PREFIX + 'resources/javac/name-validator',
                                    data: JSON.stringify({name: nameField.val(), pkg: packageField.val()}),
                                    success: function(data, textStatus, jqXHR) {
                                        if (data === 'true') {
                                            newFileDialog.closest(".ui-dialog").find(".ui-dialog-buttonset button span:contains('Finish')").parent().button("option", "disabled", false);
                                        } else {
                                            if (data === 'name') {
                                                newDialogPage2.find("p > span.text-content").text('A valid ' + typeSelect.find(":selected").val() + ' name is required.');
                                            } else {
                                                newDialogPage2.find("p > span.text-content").text('A valid package name is required.');
                                            }
                                            newDialogPage2.find('p > span.icon').removeClass().addClass('icon').addClass("ui-icon ui-icon-alert");
                                        }
                                    },
                                    error: function(jqXHR, textStatus, errorThrown) {
                                        newDialogPage2.find("p > span.text-content").text('POST error: ' + textStatus + ',' + errorThrown);
                                        newDialogPage2.find('p > span.icon').removeClass().addClass('icon').addClass("ui-icon ui-icon-alert");
                                    }
                                });
                            }
                        }
                    }
                };
                nameField.keydown(function() {
                    newFileDialog.closest(".ui-dialog").find(".ui-dialog-buttonset button span:contains('Finish')").parent().button("option", "disabled", true).removeClass("ui-state-focus");
                });
                nameField.textEntryStopped(function() {
                    validateNames();
                });
                packageField.keydown(function() {
                    newFileDialog.closest(".ui-dialog").find(".ui-dialog-buttonset button span:contains('Finish')").parent().button("option", "disabled", true).removeClass("ui-state-focus");
                });
                packageField.textEntryStopped(function() {
                    validateNames();
                });
                for (var i = 0; i < newFileTypes.length; i++) {
                    $("<option>" + newFileTypes[i].category + "</option>")
                            .appendTo(categorySelect)
                            .data("types", newFileTypes[i].types);
                }
                newFileDialog.dialog({
                    autoOpen: false,
                    height: "auto",
                    width: "auto",
                    resizable: false,
                    modal: true,
                    buttons: [{
                            text: "Back",
                            //disabled: true,
                            icons: {primary: "ui-icon-carat-1-w"},
                            click: function() {
                                newDialogPage1.show();
                                newDialogPage2.hide();
                                $(this).closest(".ui-dialog").find(".ui-dialog-buttonset button span:contains('Back')").parent().button("option", "disabled", true).removeClass("ui-state-focus");
                                $(this).closest(".ui-dialog").find(".ui-dialog-buttonset button span:contains('Next')").parent().button("option", "disabled", false);
                                $(this).closest(".ui-dialog").find(".ui-dialog-buttonset button span:contains('Finish')").parent().button("option", "disabled", true).removeClass("ui-state-focus");
                            }
                        }, {
                            text: "Next",
                            //disabled: true,
                            icons: {secondary: "ui-icon-carat-1-e"},
                            click: function() {
                                newDialogPage1.hide();
                                var selected = typeSelect.find(":selected");
                                newDialogPage2.find("> legend").text("New " + selected.val());
                                packageField.val("");
                                if (selected.val() === "Package Info") {
                                    nameField.val("package-info.java");
                                    nameField.prop('disabled', true);
                                } else {
                                    nameField.val("");
                                    nameField.prop('disabled', false);
                                }
                                validateNames(); // if user went back and then next again
                                newDialogPage2.show();
                                $(this).closest(".ui-dialog").find(".ui-dialog-buttonset button span:contains('Back')").parent().button("option", "disabled", false);
                                $(this).closest(".ui-dialog").find(".ui-dialog-buttonset button span:contains('Next')").parent().button("option", "disabled", true).removeClass("ui-state-focus");

                            }
                        }, {
                            text: "Finish",
                            click: function() {
                                var typeSelected = typeSelect.find(":selected");
                                var template = typeSelected.data("template");
                                var mode = typeSelected.data("mode");
                                var name = nameField.val();
                                var pkg = packageField.val();
                                that._addFile(mode, template, name, pkg);
                                $(this).dialog("close");
                            }
                        }, {
                            text: "Cancel",
                            click: function() {
                                $(this).dialog("close");
                            }
                        }],
                    open: function() {
                        newFileDialog.closest(".ui-dialog").find(".ui-dialog-buttonset button span").not(":contains('Cancel')").parent().button("option", "disabled", true).removeClass("ui-state-focus");
                        categorySelect.find("option:selected").prop("selected", false);
                        categorySelect.val([]);
                        typeSelect.empty();
                        newDialogPage1.find("p").empty();
                        newDialogPage2.find('input[type="text"]').val('');
                        newDialogPage2.find("p span.text-content").text("A package name is recommended but not required.");
                    },
                    close: function() {

                    }
                });
            }
        },
        _createAboutDialog: function() {
//            var that = this;
            var aboutDialog = $("#ide-about-dialog");
            if (aboutDialog.length === 0) {
                aboutDialog = $('<div id="ide-about-dialog" title="About">' +
                        '<p>Java Code Console v' + this.version + ' ' +
                        '<p>Please contact <a href="mailto:matt.heimer@oracle.com">matt.heimer@oracle.com</a> with any feedback.</p>' +
                        '<p>Copyright © 2014, Oracle and/or its affiliates. All rights reserved.</p>' +
                        '<p>Powered By: Java SE 7, jQuery v' + $.fn.jquery + ', jQuery UI v' + $.ui.version + ' ' +
                        'jQuery Cookie Plugin v1.3.1, CodeMirror v' + CodeMirror.version + '</p>' +
                        '<p></p>' +
                        '</div>');
                aboutDialog.appendTo("body");
                //var newDialogPage1 = newFileDialog.find("fieldset.page1");
                aboutDialog.dialog({
                    autoOpen: false,
                    height: "auto",
                    width: "auto",
                    resizable: false,
                    modal: false,
                    open: function() {
//                        $(".ui-dialog .ui-dialog-buttonset button span").not(":contains('Cancel')").parent().button("option", "disabled", true).removeClass("ui-state-focus");
//                        categorySelect.find("option:selected").prop("selected", false);
//                        categorySelect.val([]);
//                        typeSelect.empty();
//                        newDialogPage1.find("p").empty();
//                        newDialogPage2.find('input[type="text"]').val('');
//                        newDialogPage2.find("p span.text-content").text("A package name is recommended but not required.");
                    }
                });
            }
        },
        _handleUndoRedoButtonState: function() {
            var histSize = this._editor.getDoc().historySize();
            if (histSize.undo > 0) {
                this.element.find(".undoButton").button("option", "disabled", false);
            } else {
                this.element.find(".undoButton").button("option", "disabled", true);
            }
            if (histSize.redo > 0) {
                this.element.find(".redoButton").button("option", "disabled", false);
            } else {
                this.element.find(".redoButton").button("option", "disabled", true);
            }
        },
        _handleStoreTabsInSession: function() {
            if(!this.options.disableStorage) {
                var project = {
                    files: []
                };
                //project.selectedFile = parseInt(this.element.find('.fileNames input[type="radio"][name="' + this._ideInstanceId + '-file-radio"]:checked').val(), 10);
                var fileNameRadios = this.element.find('.fileNames input[type="radio"][name="' + this._ideInstanceId + '-file-radio"]');
                $(fileNameRadios).each(function() {
                    project.files.push({name: $(this).data("name"), text: $(this).data("doc").getValue()});
                });
                var tabId = "tabs-" + location.pathname + "-" + this._ideInstanceId;
                sessionStorage.setItem(tabId, JSON.stringify(project.files));
                console.log("Storing tabs in session");
                console.log(project.files);
            }
        },
        /*
         * enable
         * Helper method that just calls option('disabled', false). 
         * Note that you'll want to handle this by having an if (key === "disabled") block in your _setOption
         * 
         * disable
         * Helper method that just calls option('disabled', true). 
         * Note that you'll want to handle this by having an if (key === "disabled") block in your _setOption
         */
        _setOption: function(key, value) {
            if (key === "theme") {
                _editor.setOption("theme", value);
            }
            this._super(key, value);
        },
        destroy: function() {
            //TODO implement method
        }

    });

})(jQuery);


