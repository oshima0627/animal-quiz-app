package com.example.animalquiz;

import java.util.Arrays;
import java.util.List;

public class Animal {

    private final String name;
    private final int imageResId;
    private final int soundResId;

    public Animal(String name, int imageResId, int soundResId) {
        this.name = name;
        this.imageResId = imageResId;
        this.soundResId = soundResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getSoundResId() {
        return soundResId;
    }

    public static List<Animal> getAllAnimals() {
        return Arrays.asList(
            new Animal("いのしし", R.drawable.ic_boar,     R.raw.boar),
            new Animal("ねこ",     R.drawable.ic_cat,      R.raw.cat),
            new Animal("ひよこ",   R.drawable.ic_chick,    R.raw.chick),
            new Animal("にわとり", R.drawable.ic_chicken,  R.raw.chicken),
            new Animal("うし",     R.drawable.ic_cow,      R.raw.cow),
            new Animal("からす",   R.drawable.ic_crow,     R.raw.crow),
            new Animal("いぬ",     R.drawable.ic_dog,      R.raw.dog),
            new Animal("ぞう",     R.drawable.ic_elephant, R.raw.elephant),
            new Animal("やぎ",     R.drawable.ic_goat,     R.raw.goat),
            new Animal("うま",     R.drawable.ic_horse,    R.raw.horse),
            new Animal("ライオン", R.drawable.ic_lion,     R.raw.lion),
            new Animal("ひつじ",   R.drawable.ic_sheep,    R.raw.sheep),
            new Animal("すずめ",   R.drawable.ic_sparrow,  R.raw.sparrow),
            new Animal("おおかみ", R.drawable.ic_wolf,     R.raw.wolf)
        );
    }
}
