package pegorov.lesson1;

import java.util.ArrayList;
import java.util.Arrays;

public class Lesson1 {
    public static void main(String[] args) {
        Integer arr1[] = {1, 2, 3, 4, 5, 6, 7};
        String arr2[] = {"A", "B", "C"};
        metod1(arr1, 1, 4);
        metod1(arr2, 0, 2);

        String[] arrayOfStrings = {"A", "B", "C", "D"};
        toList(arrayOfStrings);
        Box<Orange> or = new Box<>();
        Box<Orange> or1 = new Box<>();
        Box<Apple> ap = new Box<>();
        Box<Apple> ap1 = new Box<>();

        or.addFruit(new Orange(), 10);
        or1.addFruit(new Orange(), 12);
        ap.addFruit(new Apple(), 8);
        ap1.addFruit(new Apple(), 4);

        or1.showBox();
        or.pourTo(or1);
        or1.showBox();

        ap.showBox();
        ap.pourTo(ap1);
        ap.showBox();
    }

    public static void metod1(Object[] obj, int i, int j) {
        Object sw = obj[i];
        obj[i] = obj[j];
        obj[j] = sw;

        System.out.println(Arrays.toString(obj));
    }

    public static <T> void toList(T[] arr) {
        ArrayList<T> list = new ArrayList<>(Arrays.asList(arr));

        System.out.println(list);
    }
}
