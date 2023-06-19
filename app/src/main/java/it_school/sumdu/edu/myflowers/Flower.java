package it_school.sumdu.edu.myflowers;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Flower implements Parcelable {
    private int id;
    private String name;
    private String description;
    private String image;
    private float width;
    private float height;
    private int count;
    private String dateOfPurchase;

    public Flower(int id, String name, String description, String image, float width, float height, int count, String dateOfPurchase) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.width = width;
        this.height = height;
        this.count = count;
        this.dateOfPurchase = dateOfPurchase;
    }

    protected Flower(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        image = in.readString();
        width = in.readFloat();
        height = in.readFloat();
        count = in.readInt();
        dateOfPurchase = in.readString();
    }

    public static final Creator<Flower> CREATOR = new Creator<Flower>() {
        @Override
        public Flower createFromParcel(Parcel in) {
            return new Flower(in);
        }

        @Override
        public Flower[] newArray(int size) {
            return new Flower[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getCount() {
        return count;
    }

    public String getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setDateOfPurchase(String dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeFloat(width);
        dest.writeFloat(height);
        dest.writeInt(count);
        dest.writeString(dateOfPurchase);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Uri getImageUri() {
        // Convert the image string to a Uri if needed
        if (image != null && !image.isEmpty()) {
            return Uri.parse(image);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flower flower = (Flower) o;
        return Integer.toString(id).equals(Integer.toString(flower.id));
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

