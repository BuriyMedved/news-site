package org.buriy.medved.frontend.components

import com.vaadin.flow.component.html.Span

class Divider : Span(){
    init {
        style.set("background-color", "lightgray");
        style.set("flex", "0 0 2px");
        style.set("align-self", "stretch");
    }
}