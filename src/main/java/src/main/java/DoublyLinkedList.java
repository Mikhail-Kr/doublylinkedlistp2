package src.main.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.AbstractSequentialList;
import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Класс двусвязного списка.
 *
 * @param <T> тип элементов в списке.
 */
public class DoublyLinkedList<T> extends AbstractSequentialList<T>
        implements Cloneable, Externalizable {
  private int size = 0;
  private Node<T> first;
  private Node<T> last;

  /**
   * Пустой конструктор.
   */
  public DoublyLinkedList() {
  }

  /**
   * Возвращает количество элементов в двусвязном списке.
   *
   * @return количество элементов в двусвязном списке.
   */
  @Override
  public int size() {
    return size;
  }

  @Override
  public ListIterator<T> listIterator(int i) {
    if (!(i >= 0 && i <= size)) {
      throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);
    }
    return new ListItr(i);
  }

  /**
   * Превращает строковое представление объекта
   * в виде GSON в объект класса DoublyLinkedList.
   *
   * @param readJson строковое представление объекта в виде JSON.
   * @return объект класса DoublyLinkedList.
   */
  public DoublyLinkedList<T> listFromJson(String readJson) {
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    return gson.fromJson(readJson, DoublyLinkedList.class);
  }


  /**
   * Класс, описывающий итератор по двусвязному списку.
   */
  private class ListItr implements ListIterator<T> {
    private Node<T> lastReturned;
    private Node<T> next;
    private int nextIndex;
    private int expectedModCount = modCount;

    ListItr(int i) {
      next = (i == size) ? null : node(i);
      nextIndex = i;
    }

    @Override
    public boolean hasNext() {
      return nextIndex < size;
    }

    @Override
    public T next() {
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      if (!hasNext()) {
        throw new NoSuchElementException();
      }

      lastReturned = next;
      next = next.next;
      nextIndex++;
      return lastReturned.item;
    }


    @Override
    public boolean hasPrevious() {
      return nextIndex > 0;
    }

    @Override
    public T previous() {
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      if (!hasPrevious()) {
        throw new NoSuchElementException();
      }

      lastReturned = next = (next == null) ? last : next.prev;
      nextIndex--;
      return lastReturned.item;
    }

    @Override
    public int nextIndex() {
      return nextIndex;
    }

    @Override
    public int previousIndex() {
      return nextIndex - 1;
    }

    @Override
    public void remove() {
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      if (lastReturned == null) {
        throw new IllegalStateException();
      }

      Node<T> lastNext = lastReturned.next;
      final Node<T> n = lastReturned.next;
      final Node<T> p = lastReturned.prev;

      if (p == null) {
        first = n;
      } else {
        p.next = n;
        lastReturned.prev = null;
      }

      if (n == null) {
        last = p;
      } else {
        n.prev = p;
        lastReturned.next = null;
      }

      lastReturned.item = null;
      size--;
      modCount++;
      if (next == lastReturned) {
        next = lastNext;
      } else {
        nextIndex--;
        lastReturned = null;
        expectedModCount++;
      }
    }

    @Override
    public void set(T element) {
      if (lastReturned == null) {
        throw new IllegalStateException();
      }
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      lastReturned.item = element;
    }

    @Override
    public void add(T element) {
      if (modCount != expectedModCount) {
        throw new ArrayIndexOutOfBoundsException();
      } else if (modCount == 0) {
        //Добавление нулевого элемента
        Node<T> newNode = new Node<>(element, null, null);
        first = newNode;
        last = newNode;
      } else if (first != null & nextIndex == size) {
        //Добавление элемента в конец коллекции
        Node<T> newNode = new Node<>(element, last, null);
        last.prev = last;
        last = newNode;
        newNode.prev.next = newNode;
      } else if (size == 1) {
        //Добавление второго элемента list[1]
        Node<T> newNode = new Node<>(element, first, null);
        first.next = newNode;
        last = newNode;
      } else if (nextIndex > 0 && nextIndex < size) {
        //Добавление элемента в центр коллекции
        Node<T> newNode = new Node<>(element, null, null);
        Node<T> x = first;
        for (int j = 0; j < nextIndex; j++) {
          x = x.next;
        }
        newNode.next = x.next;
        newNode.prev = x.prev;
        x.next = newNode;
      }
      modCount++;
      size++;
    }

    /**
     * Возвращает ненулевой узел по указанному индексу элемента.
     *
     * @param index индекс элемента.
     * @return ненулевой узел.
     */
    Node<T> node(int index) {
      Node<T> x;
      if (index < (size >> 1)) {
        x = first;
        for (int i = 0; i < index; i++) {
          x = x.next;
        }
      } else {
        x = last;
        for (int i = size - 1; i > index; i--) {
          x = x.prev;
        }
      }
      return x;
    }
  }

  /**
   * Класс узла списка.
   *
   * @param <T> тип элемента списка.
   */
  public static class Node<T> implements Serializable {
    T item;
    Node<T> next;
    Node<T> prev;

    Node(T item, Node<T> prev, Node<T> next) {
      this.item = item;
      this.next = next;
      this.prev = prev;
    }
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(size);
    out.writeObject(first);
    out.writeObject(last);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    size = (int) in.readObject();
    first = (Node<T>) in.readObject();
    last = (Node<T>) in.readObject();
  }

  /**
   * Возвращает строковое представление объекта
   * в виде GSON.
   *
   * @return строковое представление объекта.
   */
  public String listToJson() {
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    return gson.toJson(this);
  }

  /**
   * Клонирует объект.
   *
   * @return склонированный объект.
   * @throws CloneNotSupportedException исключение при клонировании.
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    DoublyLinkedList<T> clone = (DoublyLinkedList<T>) super.clone();
    clone.first = null;
    clone.last = null;
    clone.size = 0;
    clone.modCount = 0;
    for (Node<T> x = first; x != null; x = x.next) {
      clone.add(x.item);
    }
    return clone;
  }
}