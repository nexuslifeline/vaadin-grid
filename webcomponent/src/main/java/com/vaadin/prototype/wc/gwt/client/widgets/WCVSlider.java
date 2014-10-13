package com.vaadin.prototype.wc.gwt.client.widgets;

import static com.google.gwt.query.client.GQuery.$;
import static com.google.gwt.query.client.GQuery.Widgets;
import static com.google.gwt.query.client.GQuery.console;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.js.JsExport;
import com.google.gwt.core.client.js.JsType;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.VSlider;
import com.vaadin.prototype.wc.gwt.client.WC;
import com.vaadin.prototype.wc.gwt.client.html.HTMLElement;
import com.vaadin.prototype.wc.gwt.client.html.HTMLEvents;
import com.vaadin.prototype.wc.gwt.client.html.HTMLShadow;

@JsExport
@JsType
public class WCVSlider extends HTMLElement.Prototype implements
        HTMLElement.LifeCycle.Created,
        HTMLElement.LifeCycle.Attached,
        HTMLElement.LifeCycle.Changed,
        ValueChangeHandler<Double>, Handler {

    public static final String TAG = "v-slider";

    private VSlider slider;
    private HTMLEvents changeEvent;
    private HTMLElement container;
    private HTMLElement style;
    private Panel shadowPanel;
    private boolean initialized = false;
    private String theme = "valo";

    public WCVSlider() {
        // FIXME: If there is no default constructor JsInterop does not export anything
    }

    @Override
    public void createdCallback() {
        style = WC.create("style");
        style.setAttribute("language", "text/css");

        slider = new VSlider();
        slider.addValueChangeHandler(this);

        changeEvent = WC.document.createEvent("HTMLEvents");
        changeEvent.initEvent("change", false, false);

        container = WC.create("div");
        readAttributes();
    }

    /*
     * TODO: common stuff for exporting other widgets
     */
    private void initWidgetSystem() {
        if (!initialized) {
            initialized = true;
            Widget elementWidget = $(this).widget();
            if (elementWidget == null) {
                elementWidget = $(this).as(Widgets).panel().widget();
            }
            elementWidget.addAttachHandler(this);

            HTMLShadow shadow = this.createShadowRoot();
            shadow.appendChild(style);
            shadow.appendChild(container);

            Panel shadowPanel = $(container).as(Widgets).panel().widget();
            shadowPanel.add(slider);
        }
    }

    @Override
    public void attachedCallback() {
        initWidgetSystem();
        slider.buildBase();
    }

    @Override
    public void onValueChange(ValueChangeEvent<Double> ev) {
        String val = ev.getValue().toString();
        console.log("CAMBIA " + val + " " + getAttribute("value"));
        if (!val.equals(getAttribute("value"))) {
            setAttribute("value", val);
            dispatchEvent(changeEvent);
        }
    }

    @Override
    public void attributeChangedCallback() {
        readAttributes();
    }

    private void readAttributes() {
        slider.setMinValue(getAttrDoubleValue("min", 0));
        slider.setMaxValue(getAttrDoubleValue("max", 100));
        slider.setValue(getAttrDoubleValue("value", 0));
        theme = getAttrValue("theme", "valo");
        console.log(GWT.getModuleBaseURL() + "../../themes/" + theme + "/styles.css");
        style.innerText(
                "@import url('" + GWT.getModuleBaseURL() + "../../themes/" + theme + "/styles.css');\n" +
                "@import url('/VAADIN/themes/" + theme + "/styles.css');\n"
                );
        container.setAttribute("class", theme);
    }

    // TODO: Make this part of the API of a utils class.
    private double getAttrDoubleValue(String attr, double def) {
        return Double.valueOf(getAttrValue(attr, String.valueOf(def)));
    }

    // TODO: Make this part of the API of a utils class.
    private String getAttrValue(String attr, String def) {
        String val = getAttribute(attr);
        return val == null || val.isEmpty() ? def : val;
    }

    @Override
    public void onAttachOrDetach(AttachEvent event) {
        // TODO: Do something with shadowPanel, right now
        // gQuery creates a new root-panel so it does not
        // have any parent, but we should maintain the widget
        // hierarchy someway.
    }
}
