package wrteam.ekart.shop.model;

public class Notification {

    private String id, name, subtitle, type, type_id, image;

    public Notification() {

    }

    public Notification(String id, String name, String subtitle, String type, String type_id, String image) {
        this.id = id;
        this.name = name;
        this.subtitle = subtitle;
        this.type = type;
        this.type_id = type_id;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
