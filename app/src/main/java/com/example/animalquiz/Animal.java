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
            new Animal("いぬ",     R.drawable.ic_dog,      R.raw.dog),
            new Animal("ねこ",     R.drawable.ic_cat,      R.raw.cat),
            new Animal("うし",     R.drawable.ic_cow,      R.raw.cow),
            new Animal("ぶた",     R.drawable.ic_pig,      R.raw.pig),
            new Animal("うま",     R.drawable.ic_horse,    R.raw.horse),
            new Animal("ひつじ",   R.drawable.ic_sheep,    R.raw.sheep),
            new Animal("にわとり", R.drawable.ic_chicken,  R.raw.chicken),
            new Animal("かえる",   R.drawable.ic_frog,     R.raw.frog),
            new Animal("ぞう",     R.drawable.ic_elephant, R.raw.elephant),
            new Animal("ライオン", R.drawable.ic_lion,     R.raw.lion),
            new Animal("さる",     R.drawable.ic_monkey,   R.raw.monkey),
            new Animal("くま",     R.drawable.ic_bear,     R.raw.bear),
            new Animal("うさぎ",   R.drawable.ic_rabbit,   R.raw.rabbit),
            new Animal("とり",     R.drawable.ic_bird,     R.raw.bird)
        );
    }
}
