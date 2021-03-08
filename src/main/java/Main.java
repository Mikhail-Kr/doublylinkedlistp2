import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ListIterator;
import src.main.java.DoublyLinkedList;

public class Main {
  /**
  * Проверяет работу классов, созданных
  * в рамках выполнения задания.
  *
  * @param args аргументы командной строки.
  */
  public static void main(String[] args) {
    DoublyLinkedList<String> list = new DoublyLinkedList<>();
    list.add("a1");
    list.add("b2");
    list.add("c3");
    list.add(1, "d4");
    list.add("e5");
    list.set(4, "a6");
    list.add("f5");
    list.add(3, "t1000");

    //Проверка позитивного использования DoublyLinkedList.size()
    if (list.size() != 7) {
      System.err.println("Некорректная работа позитивного использования DoublyLinkedList.size()");
    }

    //Проверка негативного использования DoublyLinkedList.size()
    boolean thrown = false;
    try {
      if (list.size() != 8) {
        throw new IllegalArgumentException();
      }
    } catch (IllegalArgumentException ex) {
      thrown = true;
    }
    if (!thrown) {
      System.out.println("Некорректная работа метода size()");
    }

    //Проверка позитивного использования DoublyLinkedList.add()
    if ((!list.get(0).equals("a1"))
            || (!list.get(1).equals("b2"))
            || (!list.get(2).equals("d4"))
            || (!list.get(6).equals("f5"))) {
      System.err.println("Некорректная работа позитивного использования DoublyLinkedList.set()");
    }


    //Проверка негативного использования DoublyLinkedList.add()
    thrown = false;
    try {
      if ((!list.get(0).equals("b2"))
              || (!list.get(1).equals("c3"))
              || (!list.get(2).equals("f5"))
              || (!list.get(6).equals("t1000"))) {
        throw new IllegalArgumentException();
      }
    } catch (IllegalArgumentException ex) {
      thrown = true;
    }
    if (!thrown) {
      System.err.println("Некорректная работа метода addFirst");
    }

    //Позитивный работы метода clone.
    DoublyLinkedList<String> list2;
    try {
      list2 = (DoublyLinkedList<String>) list.clone();
      if (!list2.equals(list)) {
        System.out.println("Некорректная работа метода clone");
      }
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }

    //Позитивная проверка работы метода listIterator.
    thrown = false;
    ListIterator<String> listIt;
    try {
      listIt = list.listIterator(0);
    } catch (IndexOutOfBoundsException ex) {
      thrown = true;
    }
    if (thrown) {
      System.out.println("Некорректная работа метода listIterator");
    }

    //Негативная проверка работы метода listIterator.
    thrown = false;
    try {
      listIt = list.listIterator(8);
    } catch (IndexOutOfBoundsException ex) {
      thrown = true;
    }
    if (!thrown) {
      System.out.println("Некорректная работа метода listIterator");
    }

    //Позитивная проверка сериализации/десериализации через GSON
    DoublyLinkedList<String> testGson = new DoublyLinkedList<>();
    testGson.add("a1");
    testGson.add("b2");
    testGson.add("c3");

    try {
      ObjectOutputStream objectOutputStream =
              new ObjectOutputStream(new FileOutputStream("test.json"));
      objectOutputStream.writeObject(testGson.listToJson());
    } catch (IOException e) {
      e.printStackTrace();
    }

    DoublyLinkedList<String> readList = null;
    try {
      ObjectInputStream objectInputStream =
              new ObjectInputStream(new FileInputStream("test.json"));
      readList =
              testGson.listFromJson((String) objectInputStream.readObject());
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    if (!testGson.equals(readList)) {
      System.err.println("Некорректная работа сериализации/десериализации через GSON");
    }

    //Позитивная проверка сериализации/десериализации через Externalize
    try {
      ObjectOutputStream oos =
              new ObjectOutputStream(new FileOutputStream("test.txt"));
      oos.writeObject(testGson);
    } catch (IOException e) {
      e.printStackTrace();
    }

    DoublyLinkedList<String> readList2 = null;
    try {
      ObjectInputStream ois =
              new ObjectInputStream(new FileInputStream("test.txt"));
      readList2 =
              (DoublyLinkedList<String>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    if (!testGson.equals(readList2)) {
      System.err.println("Некорректная работа сериализации/десериализации через Externalize");
    }
  }
}
