package com.metalheart.service.impl;

import com.metalheart.service.UsernameService;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class UsernameServiceImpl implements UsernameService {

    private static String[] adjectives = new String[] {
        "Хитрый", "Коварный",
        "Майор", "Капитан",
        "Лейтенант", "Рядовой",
        "Ефрейтор", "Прапорщик",
        "Полковник", "Подполковник",
        "Генерал", "Сержант"
    };

    private static String[] nouns = new String[] {
        "Жук", "Лис",
        "Волк", "Слон",
        "Бобер", "Свин",
        "Таракан", "Клещ"
    };

    @Override
    public String generateUsername() {
        Random random = new Random();
        return adjectives[random.nextInt(adjectives.length)] + " " + nouns[random.nextInt(nouns.length)];
    }
}
