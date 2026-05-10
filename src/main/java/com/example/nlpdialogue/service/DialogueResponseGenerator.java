package com.example.nlpdialogue.service;

import org.springframework.stereotype.Service;

@Service
public class DialogueResponseGenerator {

    public String generate(String intent, String userText) {
        if (intent == null) {
            return "Не вдалося визначити намір запиту. Уточніть, будь ласка, що саме потрібно зробити з текстом.";
        }

        return switch (intent) {
            case "GREETING" -> "Вітаю. Я NLP-сервіс, який може обробляти текст, визначати наміри, класифікувати документи та зберігати історію діалогу.";
            case "TEXT_PROCESSING" -> "Запит схожий на обробку тексту. Я можу виконати нормалізацію, токенізацію та базовий аналіз структури повідомлення.";
            case "DOCUMENT_CLASSIFICATION" -> "Запит пов’язаний із класифікацією документів. Для такого сценарію можна використати OpenNLP DocumentCategorizer.";
            case "NER_REQUEST" -> "Запит стосується розпізнавання сутностей. Для цього можна використати OpenNLP NameFinder або rule-based NER.";
            case "SENTIMENT_ANALYSIS" -> "Запит пов’язаний з аналізом тональності. Можна застосувати словниковий підхід або ML-класифікацію.";
            case "SEARCH_REQUEST" -> "Запит схожий на пошук інформації. Для цього можна нормалізувати запит, побудувати індекс і ранжувати результати за релевантністю.";
            case "HELP" -> "Доступні сценарії: обробка тексту, класифікація документів, NER, пошук, аналіз тональності та перегляд історії діалогу.";
            default -> "Я отримав повідомлення і зберіг його в історії діалогу. Намір визначено як: " + intent + ".";
        };
    }
}
