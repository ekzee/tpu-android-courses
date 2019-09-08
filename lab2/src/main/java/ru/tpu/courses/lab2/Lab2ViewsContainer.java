package ru.tpu.courses.lab2;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Px;

/**
 * Простейший пример самописного View. В данном случае мы просто наследуемся от LinearLayout-а и
 * добавляем свою логику, но также есть возможность отнаследоваться от {@link android.view.ViewGroup},
 * если необходимо реализовать контейнер для View полностью с нуля, либо отнаследоваться от {@link android.view.View}.
 * <p/>
 * Здесь можно было бы добавить автоматическое сохранение и восстановление состояния, переопределив методы
 * {@link #onSaveInstanceState()} и {@link #onRestoreInstanceState(Parcelable)}.
 */
public class Lab2ViewsContainer extends LinearLayout {

    private int minViewsCount;
    private int viewsCount;

    /**
     * Этот конструктор используется при создании View в коде.
     */
    public Lab2ViewsContainer(Context context) {
        this(context, null);
    }

    /**
     * Этот конструктор выдывается при создании View из XML.
     */
    public Lab2ViewsContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Конструктор, вызывается при инфлейте View, когда у View указан дополнительный стиль.
     * Почитать про стили можно здесь https://developer.android.com/guide/topics/ui/look-and-feel/themes
     *
     * @param attrs атрибуты, указанные в XML. Стандартные android атрибуты обрабатываются внутри родительского класса.
     *              Здесь необходимо только обработать наши атрибуты.
     */
    public Lab2ViewsContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Свои атрибуты описываются в файле res/values/attrs.xml
        // Эта строчка объединяет возможные применённые к View стили
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Lab2ViewsContainer, defStyleAttr, 0);

        minViewsCount = a.getInt(R.styleable.Lab2ViewsContainer_lab2_minViewsCount, 0);
        if (minViewsCount < 0) {
            throw new IllegalArgumentException("minViewsCount can't be less than 0");
        }

        // Полученный TypedArray необходимо обязательно очистить.
        a.recycle();

        setViewsCount(minViewsCount);
    }

    /**
     * Программно создаём {@link TextView} и задаём его атрибуты, альтернативно можно описать его в
     * xml файле и инфлейтить его через класс LayoutInflater.
     */
    public void incrementViews() {
        TextView textView = new TextView(getContext());
        textView.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        textView.setTextSize(16);
        textView.setText(String.valueOf(viewsCount++));
        // У каждого View, который находится внутри ViewGroup есть LayoutParams,
        // в них содержится информация для лэйаута компонентов.
        // Базовая реализация LayoutParams содержит только определение ширины и высоты
        // (то, что мы указываем в xml в атрибутах layout_widget и layout_height).
        // Получить их можно через метод getLayoutParams у View. Метод addView смотрит, если у View
        // не установлены LayoutParams, то создаёт дефолтные, вызывая метод generateDefaultLayoutParams
        addView(textView);
    }

    public void setViewsCount(int viewsCount) {
        if (this.viewsCount == viewsCount) {
            return;
        }
        viewsCount = viewsCount < minViewsCount ? minViewsCount : viewsCount;

        removeAllViews();
        this.viewsCount = 0;
        for (int i = 0; i < viewsCount; i++) {
            incrementViews();
        }
    }

    public int getViewsCount() {
        return viewsCount;
    }

    /**
     * Метод трансформирует указанные dp в пиксели, используя density экрана.
     */
    @Px
    public int dpToPx(float dp) {
        if (dp == 0) {
            return 0;
        }
        float density = getResources().getDisplayMetrics().density;
        return (int) Math.ceil(density * dp);
    }
}