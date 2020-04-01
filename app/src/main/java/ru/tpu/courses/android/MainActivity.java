package ru.tpu.courses.android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.tpu.courses.lab1.Lab1Activity;
import ru.tpu.courses.lab2.Lab2Activity;
import ru.tpu.courses.lab3.Lab3Activity;
import ru.tpu.courses.lab4.Lab4Activity;
import ru.tpu.courses.lab5.Lab5Activity;

/**
 * {@link android.app.Activity} - объект, который представляет отдельный экран, видимый пользователю.
 * Все Activity должны быть зарегистрированы в манифесте приложения (файл AndroidManifest.xml).
 * Когда пользователь нажимает на иконку приложения, андроид ОС смотрит в манифест приложения и находит Activity,
 * указанную как основную (см. содержание тэга activity в манифесте приложения). После этого создаёт класс
 * {@link android.app.Application} (который можно переопределить, см. {@link App} и атрибут android:name в тэге application),
 * если он не был создан до этого и потом сам создаёт объект класса указанной Activity.
 * <p/>
 * Мы наследуемся от {@link AppCompatActivity}, т.к. в ней содержатся логика,
 * необходимая для обратной совместимости с более старыми версиями андроид.
 * <p/>
 * Темы лабораторных работ:
 * <ul>
 * <li>Знакомство с Android Studio и Git</li>
 * <li>Вёрстка UI. Сохранение состояния</li>
 * <li>RecyclerView, взаимодействие между экранами. Memory Cache</li>
 * <li>Взаимодействие с файловой системой, SQLite</li>
 * <li>Запросы к сети, многопоточность</li>
 * </ul>
 * P.S. Читать JavaDoc в студии удобнее, нажимая Ctrl+Q
 */
public class MainActivity extends AppCompatActivity {

    /**
     * У Activity нельзя создавать не пустой конструктор, иначе система не сможет создать объект этого класса
     * (т.к. у неё не будет необходимых параметров, принимаемых в конструкторе).
     * Вместо этого переопределяется метод {@link #onCreate(Bundle)},
     * который будет вызван сразу после создания объекта и подготовки его к выводу UI.
     *
     * @param savedInstanceState - здесь нам приходит сохраненное состояние Activity, подробнее об этом во 2ой лабораторной.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Переопределяя методы жизненного цикла Activity
        super.onCreate(savedInstanceState);

        // Метод setContentView задаёт корневую View у Activity.
        // У него есть 2 перегрузки, которые отражают 2 способа верстки UI:
        // 1. Программно, аллоцируя и конфигурируя в коде все необходимые View. Это будет эффективнее второго способа, но весьма неудобно.
        // Впрочем, есть отдельные библиотеки, которые упрощают процесс верстки UI в коде (например https://github.com/Kotlin/anko),
        // но в лабораторных они рассматриваться не будут.
        //
        // 2. Принимает идентификатор ресурса на иерархию View в формате XML. В этом случае
        // андроид будет парсить указанный файл и за нас создавать всю иерархию View.
        // Этот процесс называется инфлейтом и за него отвечает класс LayoutInflater.

        // Ресурсы приложения должны добавляться в папку res, где уже распределяются по типам в разные папки.
        // Файлы вёрстки находятся в папке layout. Для всех ресурсов генерируются уникальные идентификаторы
        // в классе R. Найти его можно в папке build/generated/r_class_sources/debug/r.
        // Если проект сбилдился без ошибок, то по ресурсу можно перейти в соответствующий XML файл,
        // нажав по нему левой кнопкой мыши с зажатой клавишей Ctrl.
        setContentView(R.layout.activity_main);

        // После инфлейта View, нам необходимо найти кнопки по их идентификатору, используя метод findViewById.
        // Далее вызываем метод setOnClickListener, в который передаём лямбду, содержащую то, что мы хотим сделать при нажатии на кнопку.
        // Каждая из лабораторных работ выполняется в отдельном изолированном модуле.
        // В основной модуль (app) подключаются модули с лабораторными работами.
        // Для сборки проекта используется билд система Gradle. Его конфигурация находится в файлах с расширением *.gradle.
        findViewById(R.id.lab1).setOnClickListener((v) -> {
            // Для того, чтобы перейти на другой экран (Activity), используется метод startActivity.
            // Ему в параметры приходит объект класса Intent, содержащий информацию о том,
            // какую Activity необходимо запустить и с какими параметрами.
            // В случае, если приложение было выгржено из памяти системой
            // (например, это может произойти, когда мы свернули приложение), система сохранит
            // в файловую систему все объекты Intent, использованные для переходов между Activity.
            // Тогда, когда мы развернём приложение, используя эту сохраненную информацию, система
            // сможет восстановить приложение в той точке, на которой был пользователь.
            // Подробнее о системе Intent-ов - https://developer.android.com/guide/components/intents-filters
            startActivity(Lab1Activity.newIntent(this));
        });
        findViewById(R.id.lab2).setOnClickListener((v) -> startActivity(Lab2Activity.newIntent(this)));
        findViewById(R.id.lab3).setOnClickListener((v) -> startActivity(Lab3Activity.newIntent(this)));
        findViewById(R.id.lab4).setOnClickListener((v) -> startActivity(Lab4Activity.newIntent(this)));
        findViewById(R.id.lab5).setOnClickListener((v) -> startActivity(Lab5Activity.newIntent(this)));
    }
}
