package com.anarimonov.phonesalebot.utils;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String botToken = "5401421696:AAFariWTwECRqAC8ncjZme85sSepTu3MrYU";
    public static final String botUsername = "phone_sale_bot";


    public static final String menuMessageUz = """
            Bosh menyuga xush kelibsiz!

            Iltimos, kategoriyalardan birini tanlang!""";
    public static final String menuMessageRu = """
            Добро пожаловать в главное меню!

            Пожалуйста, выберите одну из категорий!""";

    public static List<String> menuButtonsUz = Arrays.asList(
            " Yangi narxlar \uD83D\uDCB2",
            "Telefonni narxlash \uD83D\uDCB8",
            "Adminga murojaat qilish",
            "Sozlamalar ⚙️"
    );
    public static List<String> menuButtonsRu = Arrays.asList(
            "Новые цены \uD83D\uDCB2",
            "Ценообразование телефонов\uD83D\uDCB2",
            "Связаться с админом",
            "Опции ⚙️"
    );

}

