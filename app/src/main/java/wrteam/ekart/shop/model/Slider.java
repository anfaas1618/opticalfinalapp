package wrteam.ekart.shop.model;


public class Slider {
    String type, type_id, name, image;

    public Slider(String type, String type_id, String name, String image) {
        this.type = type;
        this.type_id = type_id;
        this.name = name;
        this.image = image;
    }

    public Slider(String image) {
        this.image = image;
    }

    public String getType_id() {
        return type_id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getType() {
        return type;
    }
}
