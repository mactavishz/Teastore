package tools.descartes.teastore.entities;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

public class Icon {

    @JsonbProperty("icon")
    public String icon;

    public Icon() {
    }

    @JsonbCreator
    public Icon(@JsonbProperty("icon") String icon) {
        this.icon = icon;
    }
}
