
package com.tmser;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author tmser
 * @version $Id: Gtest.java, v 1.0 2019年11月14日 下午8:13:07 tmser Exp $
 */
public class Gtest {
  static class Fruit {

  }

  static class Apple extends Fruit {

  }

  static class Orange extends Fruit {

  }

  static class Tapple extends Apple {

  }

  static void add(List<? extends Apple> list) {
    // list.add(new Apple());
    // list.add(new Fruit());
    // list.add(new Tapple());
    Fruit f = list.get(0);
    // Tapple t = list.get(1);
  }

  static void addSuper(List<? super Apple> list) {
    list.add(new Apple());
    // list.add(new Fruit());]
    list.add(new Tapple());
  }

  static interface G<E> {
  }

  static class ChildG<E> implements G<List<E>> {
  }

  static <T> List<T> getList() {
    List<T> list = get(new ChildG<T>());
    return list;
  }

  static <U> U get(G<U> tf) {
    return null;
  }

  public static void main(String[] args) {
    List<Fruit> fruits = new ArrayList<>();
    List<Apple> apps = new ArrayList<>();
    List<Orange> oranges = new ArrayList<>();
    List<Tapple> tapples = new ArrayList<>();

    // add(fruits);
    add(apps);
    // add(oranges);
    add(tapples);

    addSuper(fruits);
    addSuper(apps);
    // addSuper(oranges);
    // addSuper(tapples);

  }

}
