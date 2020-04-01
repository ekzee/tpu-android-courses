package ru.tpu.courses.lab5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * <b>Запросы к сети, многопоточность</b>
 * <p>В Андроиде весь рендер UI и обработка пользовательских нажатий выполняется на основном (UI) потоке.
 * Таким образом, если у нас есть какая-то потенциально длительная операция (запросы к БД, в сеть),
 * то их необходимо выполнять на отдельном потоке. Реализация многопоточного кода зачастую бывает
 * нетривиальной, например из-за проблемы гонки потоков - https://ru.wikipedia.org/wiki/%D0%A1%D0%BE%D1%81%D1%82%D0%BE%D1%8F%D0%BD%D0%B8%D0%B5_%D0%B3%D0%BE%D0%BD%D0%BA%D0%B8
 * <p/>
 * <p>
 * Есть много способов писать многопоточный код (как средствами фреймворка, так и многопоточный код),
 * но в лабораторной работе будет рассмотрен самый стандартный подход, через потоки. В предлагаемой
 * реализации много минусов, она создана только для ознакомления с основами многопоточного взаимодействия
 * в андроид.
 * </p>ё
 * <p>
 * Офф документация - https://developer.android.com/training/multiple-threads/index.html
 * </p>
 */
public class Lab5Activity extends AppCompatActivity {

    private static final String TAG = Lab5Activity.class.getSimpleName();

    /**
     * Создаём пул потоков. Он необходим, чтобы переиспользовать уже созданные {@link Thread}, а не
     * создавать каждый раз новый поток при необходимости выполнения асинхронной операции.
     * Переменная статическая, т.к. должна жить вне жизненного цикла активити. Обычно такие вещи
     * инициализируются где-то в {@link android.app.Application}.
     */
    private static Executor threadExecutor = Executors.newCachedThreadPool();

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, Lab5Activity.class);
    }

    private SearchTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.lab5_title, getClass().getSimpleName()));

        // Создаём задачку на поиск по репозиториям гитхаба. Процесс выполнения задачи будет приходить
        // в searchObserver
        task = new SearchTask(searchObserver);

        // Выполняем задачу на экзекьюторе. Он внутри проверит, есть ли в пуле свободный поток. Если
        // есть, то выполнит задачу на нём. Если нет, то создаст новый (new Thread(task).start())
        threadExecutor.execute(task);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Обязательно необходимо отписать обсервер до уничтожения Activity, иначе мы создадим утечку
        // памяти
        task.unregisterObserver();
    }

    private Observer<List<Repo>> searchObserver = new Observer<List<Repo>>() {
        @Override
        public void onLoading(@NonNull Task<List<Repo>> task) {
            Log.d(TAG, "onLoading");
        }

        @Override
        public void onSuccess(@NonNull Task<List<Repo>> task, @Nullable List<Repo> data) {
            Log.d(TAG, "onSuccess");
            for (Repo repo : data) {
                Log.d(TAG, repo.toString());
            }
        }

        @Override
        public void onError(@NonNull Task<List<Repo>> task, @NonNull Exception e) {
            Log.e(TAG, "onError", e);
        }
    };
}
