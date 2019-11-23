package ru.tpu.courses.lab4.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * <p>
 * Класс, который является входной точкой для описания БД для Room. В Аннотации к классу
 * указывается список сущностей-таблиц (в нашем случае это только класс Student), версия БД
 * (необходима для вызова миграции). Флаг exportSchema отключает выгрузку в json файл схемы БД (в
 * обычном случае его оставляют включённым и этот файл добавляют в репозиторий, для сохранения
 * истории изменения схемы БД).
 * </p>
 * <p>
 * Для всех Data access objects (DAO) (см. {@link StudentDao}) должны быть написаны абстрактные
 * методы, возвращающие их {@link #studentDao()}
 * </p>
 * <p>
 * Библиотека Room использует эти классы за основу для кодогенарации методов работы с БД,
 * используя информацию, которую мы указываем в аннотациях. Когда мы создаём инстанс нашей БД
 * через {@link Room#databaseBuilder(Context, Class, String)} (см. метод {@link #getInstance(Context)})
 * Room возвращает нам инстанс уже сгенерированной реализации БД.
 * </p>
 */
@Database(entities = Student.class, version = 1, exportSchema = false)
public abstract class Lab4Database extends RoomDatabase {

    private static Lab4Database db;

    /**
     * Синглетон, в котором происходит инициализация и настройка самой БД. Используем синглетон,
     * т.к. инстанс класса БД должен быть только один (т.ё. в нём происходит управление
     * соединением к БД).
     */
    @NonNull
    public static Lab4Database getInstance(@NonNull Context context) {
        if (db == null) {
            synchronized (Lab4Database.class) {
                if (db == null) {
                    db = Room.databaseBuilder(
                            context.getApplicationContext(),
                            Lab4Database.class,
                            "lab4_database"
                    )
                            // Запросы к БД могут быть весьма медленными и вообще практически любое
                            // обращение к файловой системе рекомендуется выполнять отдельно от
                            // основного потока. По умолчанию, если выполнить запросы к БД через
                            // Room, то приложение будет крашится (это некоторый способ сказать
                            // программисту, что он делает неоптимальную вещь). Пока что мы
                            // отключаем эту проверку этим методом. В следующей лабораторной работе
                            // подробнее будет рассмотрено взаимодействие с БД не через основной поток.
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return db;
    }

    public abstract StudentDao studentDao();
}
