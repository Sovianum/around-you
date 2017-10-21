package ru.mail.park.aroundyou;

import java.util.Random;

class NeighbourMock {
    public static final Random RANDOM = new Random();

    public static NeighbourItem getRandomNeighbourItem() {
        return new NeighbourItem(
                loginList[RANDOM.nextInt(loginList.length)],
                "none",
                aboutList[RANDOM.nextInt(aboutList.length)],
                20
        );
    }

    public static final String[] loginList = {
            "Вася", "Петя", "Миша", "Даша"
    };

    public static final String[] aboutList = {
            "Ничего интересного",
            "Никому ничего не скажу",
            "Узнаешь - закачаешься",
            "Умереть - не встать"
    };
}
