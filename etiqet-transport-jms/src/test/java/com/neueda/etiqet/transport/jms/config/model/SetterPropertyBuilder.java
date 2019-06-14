package com.neueda.etiqet.transport.jms.config.model;

import com.neueda.etiqet.transport.jms.ArgType;
import com.neueda.etiqet.transport.jms.ConstructorArg;
import com.neueda.etiqet.transport.jms.SetterProperty;

public class SetterPropertyBuilder {
    private SetterProperty property;

    private SetterPropertyBuilder() {
        this.property = new SetterProperty();
    }

    public static SetterPropertyBuilder aSetterProperty() {
        return new SetterPropertyBuilder();
    }

    public SetterPropertyBuilder argType(final ArgType argType) {
        property.setArgType(argType);
        return this;
    }

    public SetterPropertyBuilder name(final String name) {
        property.setArgName(name);
        return this;
    }

    public SetterPropertyBuilder argValue(final String argValue) {
        property.setArgValue(argValue);
        return this;
    }

    public SetterProperty build() {
        return property;
    }
}
