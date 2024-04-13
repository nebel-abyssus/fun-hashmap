package i2p.abyssus.nebel.fun.avltree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.random.RandomGenerator;

/**
 * Тесты итератора элементов дерева.
 */
public class AvlTreeIteratorTests {

// instance fields

	/**
	 * ГПСЧ.
	 */
	private final RandomGenerator rng = ThreadLocalRandom.current();

	/**
	 * Экстрактор ключей.
	 */
	private final Function<Long, Integer> keyExtractor = Long::intValue;

	/**
	 * Компаратор ключей.
	 */
	private final Comparator<Integer> keyComparator = Comparator.<Integer>naturalOrder();

	/**
	 * Всегда пустое дерево.
	 */
	private AvlTree<Long, Integer> emptyTree;

	/**
	 * Изначально пустое дерево. Предполагается заполнение тестовым методом под конкретные нужды.
	 */
	private AvlTree<Long, Integer> tree;

	/**
	 * Предзаполненное дерево нечётных десятичных цифр. Перед тестами заполняется элементами 1, 3, 5, 7 и 9.
	 */
	private AvlTree<Long, Integer> oddDigitsTree;

// instance methods

	@BeforeEach
	public void testInit (
	) { // method body
		emptyTree = new AvlTree<Long, Integer>(keyExtractor, keyComparator);
		tree = new AvlTree<Long, Integer>(keyExtractor, keyComparator);
		oddDigitsTree = new AvlTree<Long, Integer>(keyExtractor, keyComparator);
		oddDigitsTree.put(1L);
		oddDigitsTree.put(3L);
		oddDigitsTree.put(5L);
		oddDigitsTree.put(7L);
		oddDigitsTree.put(9L);
	} // testInit()

	/**
	 * Очистка пустого дерева, к которому присоединён итератор.
	 * Ожидание: обращение к методу {@link ListIterator#hasNext() hasNext()} выбрасывает исключение {@link ConcurrentModificationException}.
	 */
	@Test
	public void hasNext_afterClearEmptyTree_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = emptyTree.iterator();
		// act
		emptyTree.clear();
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::hasNext, "Очистка пустого дерева, к которому присоединён итератор. Метод hasNext() не выбросил исключение");
	} // hasNext_afterClearEmptyTree_throwsCME()

	/**
	 * Добавление нового элемента в дерево, к которому присоединён итератор.
	 * Ожидание: метод {@link ListIterator#hasNext() hasNext()} выбрасывает исключение {@link ConcurrentModificationException}.
	 */
	@Test
	public void hasNext_afterPuttingNewItem_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long newItem = rng.nextLong();
		// act
		oddDigitsTree.put(newItem);
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::hasNext, "Добавление нового элемента в дерево, к которому присоединён итератор. Метод hasNext() не выбросил исключение");
	} // hasNext_afterPuttingNewItem_throwsCME()

	/**
	 * Удаление элемента из дерева, к которому присоединён итератор.
	 * Ожидание: метод {@link ListIterator#hasNext() hasNext()} выбрасывает исключение {@link ConcurrentModificationException}.
	 */
	@Test
	public void hasNext_afterItemRemoving_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long item = 3L;
		final Integer itemKey = keyExtractor.apply(item);
		// act
		oddDigitsTree.remove(itemKey);
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::hasNext, "Удаление элемента из дерева, к которому присоединён итератор. Метод hasNext() не выбросил исключение");
	} // hasNext_afterItemRemoving_throwsCME()

	/**
	 * Модификация дерева, к которому присоединён итератор, другим итератором.
	 * Ожидание: метод {@link ListIterator#hasNext() hasNext()} выбрасывает исключение {@link ConcurrentModificationException}.
	 */
	@Test
	public void hasNext_afterTreeModificationByOtherIterator_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long targetItem = 3L;
		final Integer itemKey = keyExtractor.apply(targetItem);
		final ListIterator<Long> otherIterator = oddDigitsTree.iterator(itemKey);
		// act
		otherIterator.next();
		otherIterator.remove();
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::hasNext, "Модификация дерева, к которому присоединён итератор, другим итератором. Метод hasNext() не выбросил исключение");
	} // hasNext_afterTreeModificationByOtherIterator_throwsCME()

	/**
	 * Модификация дерева, к которому присоединён итератор, тем же итератором.
	 * Ожидания: метод {@link ListIterator#hasNext() hasNext()} не выбрасывает исключений.
	 */
	@Test
	public void hasNext_afterTreeModificationBySameIterator_noThrows (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		// act
		iterator.next();
		iterator.remove();
		// assert
		Assertions.assertDoesNotThrow(iterator::hasNext, "Модификация дерева, к которому присоединён итератор, тем же итератором. Метод hasNext() выбросил исключение");
	} // hasNext_afterTreeModificationBySameIterator_noThrows()

	/**
	 * Итератор перед пустым деревом.
	 * Ожидания: метод {@link ListIterator#hasNext() hasNext()} возвращает {@code false}.
	 */
	@Test
	public void hasNext_preEmptyTree_returnFalse (
	) { // method body
		Assertions.assertFalse(emptyTree.preFirstIterator().hasNext(), "Итератор перед пустым деревом. hasNext() вернул true");
	} // hasNext_preEmptyTree_returnFalse()

	/**
	 * Итератор перед непустым деревом.
	 * Ожидания: метод {@link ListIterator#hasNext() hasNext()} возвращает {@code true}.
	 */
	@Test
	public void hasNext_preNonEmptyTree_returnTrue (
	) { // method body
		Assertions.assertTrue(oddDigitsTree.preFirstIterator().hasNext(), "Итератор перед непустым деревом. hasNext() вернул false");
	} // hasNext_preNonEmptyTree_returnTrue()

	/**
	 * Итератор перед выбранным элементом дерева.
	 * Ожидания: метод {@link ListIterator#hasNext() hasNext()} возвращает {@code true}.
	 */
	@Test
	public void hasNext_preSpecifiedItem_returnTrue (
	) { // method body
		// arrange
		final Long item = 7L;
		final Integer key = keyExtractor.apply(item);
		final ListIterator<Long> iterator = oddDigitsTree.iterator(key);
		// act
		final boolean hasNext = iterator.hasNext();
		// assert
		Assertions.assertTrue(hasNext, "Итератор перед выбранным элементом дерева. hasNext() вернул false");
	} // hasNext_preSpecifiedItem_returnTrue()

	/**
	 * Итератор после непустого дерева.
	 * Ожидания: метод {@link ListIterator#hasNext() hasNext()} возвращает {@code false}.
	 */
	@Test
	public void hasNext_postNonEmptyTree_returnFalse (
	) { // method body
		Assertions.assertFalse(oddDigitsTree.postLastIterator().hasNext(), "Итератор после непустого дерева. hasNext() вернул true");
	} // hasNext_postNonEmptyTree_returnFalse()

	/**
	 * Очистка пустого дерева, к которому присоединён итератор.
	 * Ожидания: обращение к методу {@link ListIterator#next() next()} выбрасывает исключение {@link ConcurrentModificationException}.
	 */
	@Test
	public void next_afterEmptyTreeClearing_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = emptyTree.iterator();
		// act
		emptyTree.clear();
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::next, "Очистка пустого дерева, к которому присоединён итератор. next() не выбросил CME");
	} // next_afterEmptyTreeClearing_throwsCME()

	/**
	 * Добавление нового элемента в дерево, к которому присоединён итератор.
	 * Ожидания: метод {@link ListIterator#next() next()} выбрасывает исключение {@link ConcurrentModificationException}.
	 */
	@Test
	public void next_afterPuttingNewItem_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long newItem = rng.nextLong();
		// act
		oddDigitsTree.put(newItem);
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::next, "Добавление нового элемента в дерево, к которому присоединён итератор. next() не выбросил CME");
	} // next_afterPuttingNewItem_throwsCME()

	/**
	 * Удаление элемента из дерева, к которому присоединён итератор.
	 * Ожидания: метод {@link ListIterator#next() next()} выбрасывает исключение {@link ConcurrentModificationException}.
	 */
	@Test
	public void next_afterItemRemoving_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		// act
		oddDigitsTree.remove(key);
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::next, "Удаление элемента из дерева, к которому присоединён итератор. next() не выбросил CME");
	} // next_afterItemRemoving_throwsCME()

	/**
	 * Модификация дерева, к которому присоединён итератор, другим итератором.
	 * Ожидания: метод {@link ListIterator#next() next()} выбрасывает исключение {@link ConcurrentModificationException}.
	 */
	@Test
	public void next_afterTreeModificationByOtherIterator_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		final ListIterator<Long> otherIterator = oddDigitsTree.iterator(key);
		// act
		otherIterator.next();
		otherIterator.remove();
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::next, "Модификация дерева, к которому присоединён итератор, другим итератором. next() не выбросил CME");
	} // next_afterTreeModificationByOtherIterator_throwsCME()

	/**
	 * Итератор перед пустым деревом.
	 * Ожидания: метод {@link ListIterator#next() next()} выбрасывает исключение {@link NoSuchElementException}.
	 */
	@Test
	public void next_beforeEmptyTree_throwsNSEE (
	) { // method body
		Assertions.assertThrows(NoSuchElementException.class, emptyTree.preFirstIterator()::next, "Итератор перед пустым деревом. next() не выбросил NSEE");
	} // next_beforeEmptyTree_throwsNSEE()

	/**
	 * Итератор после непустого дерева.
	 * Ожидания: метод {@link ListIterator#next() next()} выбрасывает исключение {@link NoSuchElementException}.
	 */
	@Test
	public void next_afterNonEmptyTree_throwsNSEE (
	) { // method body
		Assertions.assertThrows(NoSuchElementException.class, oddDigitsTree.postLastIterator()::next, "Итератор после непустого дерева. next() не выбросил NSEE");
	} // next_afterNonEmptyTree_throwsNSEE()

	/**
	 * Модификация дерева, к которому присоединён итератор, тем же итератором.
	 * Ожидания: метод {@link ListIterator#next() next()} не выбрасывает исключений.
	 */
	@Test
	public void next_afterTreeModificationBySameIterator_noThrows (
	) { // method body
		// arrange
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		final ListIterator<Long> iterator = oddDigitsTree.iterator(key);
		// act
		iterator.next();
		iterator.remove();
		// assert
		Assertions.assertDoesNotThrow(iterator::next, "Модификация дерева, к которому присоединён итератор, тем же итератором. next() выбросил исключение");
	} // next_afterTreeModificationBySameIterator_noThrows()

	/**
	 * Итератор установлен перед деревом нечётных цифр.
	 * Ожидания: метод {@link ListIterator#next() next()} возвращает элемент 1.
	 */
	@Test
	public void next_beforeOddDigitsTree_return1 (
	) { // method body
		Assertions.assertEquals(1, oddDigitsTree.preFirstIterator().next(), "Итератор установлен перед деревом нечётных цифр. next() возвратил неожиданное значение");
	} // next_beforeOddDigitsTree_return1()

	/**
	 * Итератор установлен перед выбранным значением.
	 * Ожидания: метод {@link ListIterator#next() next()} возвращает выбранное значение.
	 */
	@Test
	public void next_beforeSpecifiedItem_returnSpecifiedItem (
	) { // method body
		// arrange
		final Long expectedItem = 7L;
		final Integer key = keyExtractor.apply(expectedItem);
		final ListIterator<Long> iterator = oddDigitsTree.iterator(key);
		// act
		final Long actualItem = iterator.next();
		// assert
		Assertions.assertEquals(expectedItem, actualItem, "Итератор установлен перед выбранным значением. next() возвратил неожидаемое значение");
	} // next_beforeSpecifiedItem_returnSpecifiedItem()

	/**
	 * Итератор всех элементов случайно составленного дерева.
	 * Ожидания: возвращены те же самые элементы, из которых составлено дерево.
	 */
	@Test
	public void next_randomItemsTree_returnSameItems (
	) { // method body
		// arrange
		final int MIN_N = 128;
		final int MAX_N = 2048;
		final int n = rng.nextInt(MIN_N, MAX_N + 1);
		final NavigableMap<Integer, Long> items = new TreeMap<Integer, Long>(keyComparator);
		// act
		for (int i = n; i > 0; i--) {
			final Long item = rng.nextLong();
			final Integer key = keyExtractor.apply(item);
			items.put(key, item);
			tree.put(item);
		} // for
		final Collection<Long> expectedItems = items.values();
		final Iterator<Long> actualItems = tree.iterator();
		// assert
		for (final Long expectedItem : expectedItems) {
			final Long actualItem = actualItems.next();
			Assertions.assertSame(expectedItem, actualItem, "Итератор всех элементов случайно составленного дерева. next() вернул неожидаемый элемент");
		} // for
		Assertions.assertThrows(NoSuchElementException.class, actualItems::next, "Итератор всех элементов случайно составленного дерева. next() не выбросил NSEE, после конца последовательности элементов дерева");
	} // next_randomItemsTree_returnSameItems()

	/**
	 * Итератор элементов случайно составленного дерева.
	 * Ожидание: ключи формируют возрастающую последовательность.
	 */
	@Test
	public void next_randomItemsTree_itemKeysFormAscendingOrder (
	) { // method body
		// arrange
		final int MIN_N = 128;
		final int MAX_N = 2048;
		final int n = rng.nextInt(MIN_N, MAX_N + 1);
		// act
		for (int i = n; i > 0; i--) {
			final Long item = rng.nextLong();
			tree.put(item);
		} // for
		// assert
		final Iterator<Long> iterator = tree.iterator();
		final Long firstItem = iterator.next();
		Integer nextKey = keyExtractor.apply(firstItem);
		while (iterator.hasNext()) {
			final Integer prevKey = nextKey;
			final Long nextItem = iterator.next();
			nextKey = keyExtractor.apply(nextItem);
			Assertions.assertTrue(keyComparator.compare(prevKey, nextKey) < 0, "Итератор элементов случайно составленного дерева. Ключи элементов не образуют возрастающую последовательность");
		} // while
	} // next_randomItemsTree_itemKeysFormAscendingOrder()

	/**
	 * Очистка пустого дерева, к которому присоединён итератор.
	 * Ожидания: метод {@link ListIterator#hasPrevious() hasPrevious()} выбрасывает {@link ConcurrentModificationException}.
	 */
	@Test
	public void hasPrevious_afterClearEmptyTree_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = emptyTree.iterator();
		// act
		emptyTree.clear();
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::hasPrevious, "Очистка пустого дерева, к которому присоединён итератор. hasPrevious() не выбросил CME");
	} // hasPrevious_afterClearEmptyTree_throwsCME()

	/**
	 * Добавление нового элемента в дерево, сопоставленное итератору.
	 * Ожидания: метод {@link ListIterator#hasPrevious() hasPrevious()} выбрасывает {@link ConcurrentModificationException}.
	 */
	@Test
	public void hasPrevious_afterPuttingNewItem_throwsCME (
	) { // method body
		// arrange
		final Long newItem = rng.nextLong();
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		// act
		oddDigitsTree.put(newItem);
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::hasPrevious, "Добавление нового элемента в дерево, сопоставленное итератору. hasPrevious() не выбросил CME");
	} // hasPrevious_afterPuttingNewItem_throwsCME()

	/**
	 * Удаление элемента из дерева, сопоставленного итератору.
	 * Ожидания: метод {@link ListIterator#hasPrevious() hasPrevious()} выбрасывает {@link ConcurrentModificationException}.
	 */
	@Test
	public void hasPrevious_afterItemRemoving_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		// act
		oddDigitsTree.remove(key);
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::hasPrevious, "Удаление элемента из дерева, сопоставленного итератору. hasPrevious() не выбросил CME");
	} // hasPrevious_afterItemRemoving_throwsCME()

	/**
	 * Модификация дерева, сопоставленного итератору, другим итератором.
	 * Ожидания: метод {@link ListIterator#hasPrevious() hasPrevious()} выбрасывает {@link ConcurrentModificationException}.
	 */
	@Test
	public void hasPrevious_afterTreeModificationByAnotherIterator_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		final ListIterator<Long> anotherIterator = oddDigitsTree.iterator(key);
		// act
		anotherIterator.next();
		anotherIterator.remove();
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::hasPrevious, "Модификация дерева, сопоставленного итератору, другим итератором. hasPrevious() не выбросил CME");
	} // hasPrevious_afterTreeModificationByAnotherIterator_throwsCME()

	/**
	 * Модификация дерева, сопоставленного итератору, тем же итератором.
	 * Ожидания: метод {@link ListIterator#hasPrevious() hasPrevious()} не выбрасывает исключений.
	 */
	@Test
	public void hasPrevious_afterTreeModificationBySameIterator_noThrows (
	) { // method body
		// arrange
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		final ListIterator<Long> iterator = oddDigitsTree.iterator(key);
		// act
		iterator.next();
		iterator.remove();
		// assert
		Assertions.assertDoesNotThrow(iterator::hasPrevious, "Модификация дерева, сопоставленного итератору, тем же итератором. hasPrevious() выбросил исключение");
	} // hasPrevious_afterTreeModificationBySameIterator_noThrows()

	/**
	 * Итератор после пустого дерева.
	 * Ожидания: метод {@link ListIterator#hasPrevious() hasPrevious()} возвращает {@code false}.
	 */
	@Test
	public void hasPrevious_postEmptyTree_returnFalse (
	) { // method body
		Assertions.assertFalse(emptyTree.postLastIterator().hasPrevious(), "Итератор после пустого дерева. hasPrevious() не вернул false");
	} // hasPrevious_postEmptyTree_returnFalse()

	/**
	 * Итератор после непустого дерева.
	 * Ожидания: метод {@link ListIterator#hasPrevious() hasPrevious()} возвращает {@code true}.
	 */
	@Test
	public void hasPrevious_postNonEmptyTree_returnTrue (
	) { // method body
		Assertions.assertTrue(oddDigitsTree.postLastIterator().hasPrevious(), "Итератор после непустого дерева. hasPrevious() не вернул true");
	} // hasPrevious_postNonEmptyTree_returnTrue()

	/**
	 * Итератор перед непустым деревом.
	 * Ожидания: метод {@link ListIterator#hasPrevious() hasPrevious()} возвращает {@code false}.
	 */
	@Test
	public void hasPrevious_preNonEmptyTree_returnFalse (
	) { // method body
		Assertions.assertFalse(oddDigitsTree.preFirstIterator().hasPrevious(), "Итератор перед непустым деревом. hasPrevious() не вернул false");
	} // hasPrevious_preNonEmptyTree_returnFalse()

	/**
	 * Итератор расположен после нескольких элементов.
	 * Ожидания: метод {@link ListIterator#hasPrevious() hasPrevious()} возвращает {@code true}.
	 */
	@Test
	public void hasPrevious_postFewItems_returnTrue (
	) { // method body
		// arrange
		final Long item = 7L;
		final Integer key = keyExtractor.apply(item);
		final ListIterator<Long> iterator = oddDigitsTree.iterator(key);
		// act
		final boolean hasPrevious = iterator.hasPrevious();
		// assert
		Assertions.assertTrue(hasPrevious, "Итератор расположен после нескольких элементов. hasPrevious() не возвратил true");
	} // hasPrevious_postFewItems_returnTrue()

	/**
	 * Очистка дерева, сопоставленного итератору.
	 * Ожидания: метод {@link ListIterator#previous() previous()} выбрасывает {@link ConcurrentModificationException}.
	 */
	@Test
	public void previous_afterEmptyTreeClearing_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = emptyTree.postLastIterator();
		// act
		emptyTree.clear();
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::previous, "Очистка дерева, сопоставленного итератору. previous() не выбросил CME");
	} // previous_afterEmptyTreeClearing_throwsCME()

	/**
	 * Добавление нового элемента в дерево, сопоставленное итератору.
	 * Ожидания: метод {@link ListIterator#previous() previous()} выбрасывает {@link ConcurrentModificationException}.
	 */
	@Test
	public void previous_afterPuttingNewItem_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long newItem = rng.nextLong();
		// act
		oddDigitsTree.put(newItem);
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::previous, "Добавление нового элемента в дерево, сопоставленное итератору. previous() не выбросил CME");
	} // previous_afterPuttingNewItem_throwsCME()

	/**
	 * Удаление элемента из дерева, сопоставленного итератору.
	 * Ожидания: метод {@link ListIterator#previous() previous()} выбрасывает {@link ConcurrentModificationException}.
	 */
	@Test
	public void previous_afterItemRemoving_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.postLastIterator();
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		// act
		oddDigitsTree.remove(key);
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::previous, "Удаление элемента из дерева, сопоставленного итератору. previous() не выбросил CME");
	} // previous_afterItemRemoving_throwsCME()

	/**
	 * Модификация дерева, сопоставленного итератору, другим итератором.
	 * Ожидания: метод {@link ListIterator#previous() previous()} выбрасывает {@link ConcurrentModificationException}.
	 */
	@Test
	public void previous_afterTreeModificationByAnotherIterator_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		final ListIterator<Long> anotherIterator = oddDigitsTree.iterator(key);
		// act
		anotherIterator.next();
		anotherIterator.remove();
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::previous, "Модификация дерева, сопоставленного итератору, другим итератором. previous() не выбросил CME");
	} // previous_afterTreeModificationByAnotherIterator_throwsCME()

	/**
	 * Итератор расположен после пустого дерева.
	 * Ожидания: метод {@link ListIterator#previous() previos()} выбрасывает {@link NoSuchElementException}.
	 */
	@Test
	public void previous_postEmptyTree_throwsNSEE (
	) { // method body
		Assertions.assertThrows(NoSuchElementException.class, emptyTree.postLastIterator()::previous, "Итератор расположен после пустого дерева. previous() не выбросил NSEE");
	} // previous_postEmptyTree_throwsNSEE()

	/**
	 * Итератор расположен перед непустым деревом.
	 * Ожидания: метод {@link ListIterator#previous() previos()} выбрасывает {@link NoSuchElementException}.
	 */
	@Test
	public void previous_preNonEmptyTree_throwsNSEE (
	) { // method body
		Assertions.assertThrows(NoSuchElementException.class, oddDigitsTree.preFirstIterator()::previous, "Итератор расположен перед непустым деревом. previous() не выбросил NSEE");
	} // previous_preNonEmptyTree_throwsNSEE()

	/**
	 * Модификация дерева, сопоставленного итератору, тем же итератором.
	 * Ожидания: метод {@link ListIterator#previous() previous()} не выбрасывает исключений.
	 */
	@Test
	public void previous_afterTreeModificationBySameIterator_noThrows (
	) { // method body
		// arrange
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		final ListIterator<Long> iterator = oddDigitsTree.iterator(key);
		// act
		iterator.next();
		iterator.remove();
		// assert
		Assertions.assertDoesNotThrow(iterator::previous, "Модификация дерева, сопоставленного итератору, тем же итератором. previous() выбросил исключение");
	} // previous_afterTreeModificationBySameIterator_noThrows()

	/**
	 * Итератор расположен после дерева нечётных цифр.
	 * Ожидания: метод {@link ListIterator#previous() previous()} возвращает значение 9.
	 */
	@Test
	public void previous_postOddDigitsTree_return9 (
	) { // method body
		// arrange
		final Long expectedValue = 9L;
		final ListIterator<Long> iterator = oddDigitsTree.postLastIterator();
		// act
		final Long actualValue = iterator.previous();
		// assert
		Assertions.assertEquals(expectedValue, actualValue, "Итератор расположен после дерева нечётных цифр. previous() не вернул значение 9");
	} // previous_postOddDigitsTree_return9()

	/**
	 * Итератор расположен после случайной цифры, в дереве нечётных цифр.
	 * Ожидания: метод {@link ListIterator#previous() previous()} возвращает значение цифры.
	 */
	@Test
	public void previous_postRandomDigitInOddDigitsTree_returnDigitValue (
	) { // method body
		// arrange
		final Long digitValue = rng.nextLong(5) * 2 + 1;
		final Integer digitKey = keyExtractor.apply(digitValue);
		final ListIterator<Long> iterator = oddDigitsTree.iterator(digitKey);
		iterator.next();
		// act
		final Long actualValue = iterator.previous();
		// assert
		Assertions.assertEquals(digitValue, actualValue, "Итератор расположен после случайной цифры, в дереве нечётных цифр. previous() не вернул значение цифры");
	} // previous_postRandomDigitInOddDigitsTree_returnDigitValue()

	/**
	 * Итератор расположен после дерева случайных элементов.
	 * Ожидания: метод {@link ListIterator#previous() previous()} последовательно возвращает те же элементы, в том же объёме.
	 */
	@Test
	public void previous_postRandomItemsTree_returnSameItems (
	) { // method body
		// arrange
		final int MIN_N = 128;
		final int MAX_N = 2048;
		final int n = rng.nextInt(MIN_N, MAX_N + 1);
		final NavigableMap<Integer, Long> stdTree = new TreeMap<Integer, Long>(keyComparator);
		for (int i = n; i > 0; i--) {
			final Long item = rng.nextLong();
			final Integer key = keyExtractor.apply(item);
			stdTree.put(key, item);
			tree.put(item);
		} // for
		// act
		final SequencedCollection<Long> expectedItems = stdTree.sequencedValues().reversed();
		final ListIterator<Long> actualItems = tree.postLastIterator();
		// assert
		for (final Long expectedItem : expectedItems) {
			final Long actualItem = actualItems.previous();
			Assertions.assertSame(expectedItem, actualItem, "Итератор расположен после дерева случайных элементов. previous() не вернул ожидаемый элемент");
		} // for
		Assertions.assertFalse(actualItems.hasPrevious(), "Итератор расположен после дерева случайных элементов. Размеры перечислений значений не совпадают");
	} // previous_postRandomItemsTree_returnSameItems()

	/**
	 * Итератор расположен после дерева случайных элементов.
	 * Ожидания: ключи возвращаемых элементов образуют убывающую последовательность.
	 */
	@Test
	public void previous_postRandomItemsTree_descendingItemKeys (
	) { // method body
		// arrange
		final int MIN_N = 128;
		final int MAX_N = 2048;
		final int n = rng.nextInt(MIN_N, MAX_N + 1);
		for (int i = n; i > 0; i--) {
			final Long item = rng.nextLong();
			tree.put(item);
		} // for
		// act
		final ListIterator<Long> iterator = tree.postLastIterator();
		// assert
		Long nextItem = iterator.previous();
		while (iterator.hasPrevious()) {
			final Long previousItem = iterator.previous();
			final Integer nextItemKey = keyExtractor.apply(nextItem);
			final Integer previousItemKey = keyExtractor.apply(previousItem);
			final boolean isDescended = (keyComparator.compare(previousItemKey, nextItemKey) < 0);
			Assertions.assertTrue(isDescended, "Итератор расположен после дерева случайных элементов. Ключи элементов не образуют убывающую последовательность");
			nextItem = previousItem;
		} // while
	} // previous_postRandomItemsTree_descendingItemKeys()

	/**
	 * Метод должен выбрасывать {@link UnsupportedOperationException} при любом обращении.
	 * Ожидания: метод {@link ListIterator#nextIndex() nextIndex()} выбрасывает {@link UnsupportedOperationException}.
	 */
	@Test
	public void nextIndex_throwsUOE (
	) { // method body
		Assertions.assertThrows(UnsupportedOperationException.class, oddDigitsTree.iterator()::nextIndex, "Метод должен выбрасывать UnsupportedOperationException при любом обращении. nextIndex() не выбросил UOE");
	} // nextIndex_throwsUOE()

	/**
	 * Метод должен выбрасывать {@link UnsupportedOperationException} при любом обращении.
	 * Ожидания: метод {@link ListIterator#previousIndex() previousIndex()} выбрасывает {@link UnsupportedOperationException}.
	 */
	@Test
	public void previousIndex_throwsUOE (
	) { // method body
		Assertions.assertThrows(UnsupportedOperationException.class, oddDigitsTree.iterator()::previousIndex, "Метод должен выбрасывать UnsupportedOperationException при любом обращении. previousIndex() не выбросил UOE");
	} // previousIndex_throwsUOE()

	/**
	 * Очистка дерева, сопоставленного итератору.
	 * Ожидания: метод {@link ListIterator#remove() remove()} выбрасывает {@link ConcurrentModificationException}.
	 */
	@Test
	public void remove_treeClearing_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = emptyTree.iterator();
		// act
		emptyTree.clear();
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::remove, "Очистка дерева, сопоставленного итератору. remove() не выбросил CME");
	} // remove_treeClearing_throwsCME()

	/**
	 * Добавление нового элемента в дерево, сопоставленное итератору.
	 * Ожидания: метод {@link ListIterator#remove() remove()} выбрасывает {@link ConcurrentModificationException}.
	 */
	@Test
	public void remove_puttingNewItem_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long newItem = rng.nextLong();
		// act
		oddDigitsTree.put(newItem);
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::remove, "Добавление нового элемента в дерево, сопоставленное итератору. remove() не выбросил CME");
	} // remove_puttingNewItem_throwsCME()

	/**
	 * Удаление элемента из дерева, сопоставленного итератору, средствами самого дерева.
	 * Ожидания: метод {@link ListIterator#remove() remove()} выбрасывает {@link ConcurrentModificationException}.
	 */
	@Test
	public void remove_itemRemovingByTree_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		// act
		oddDigitsTree.remove(key);
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::remove, "Удаление элемента из дерева, сопоставленного итератору, средствами самого дерева. remove() не выбросил CME");
	} // remove_itemRemovingByTree_throwsCME()

	/**
	 * Модификация дерева сопоставленного итератору, другим итератором.
	 * Ожидания: метод {@link ListIterator#remove() remove()} выбрасывает {@link ConcurrentModificationException}.
	 */
	@Test
	public void remove_afterTreeModificationByOtherIterator_throwsCME (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		final ListIterator<Long> otherIterator = oddDigitsTree.iterator(key);
		// act
		otherIterator.next();
		otherIterator.remove();
		// assert
		Assertions.assertThrows(ConcurrentModificationException.class, iterator::remove, "Модификация дерева сопоставленного итератору, другим итератором. remove() не выбросил CME");
	} // remove_afterTreeModificationByOtherIterator_throwsCME()

	/**
	 * Попытка удаления элемента, до его получения.
	 * Ожидания: метод {@link ListIterator#remove() remove()} выбрасывает {@link IllegalStateException}.
	 */
	@Test
	public void remove_beforeItemReceiving_throwsISE (
	) { // method body
		Assertions.assertThrows(IllegalStateException.class, oddDigitsTree.iterator()::remove, "Попытка удаления элемента, до его получения. remove() не выбросил ISE");
	} // remove_beforeItemReceiving_throwsISE()

	/**
	 * Попытка удаления элемента, сразу после модификации дерева тем же итератором.
	 * Ожидания: метод {@link ListIterator#remove() remove()} выбрасывает {@link IllegalStateException}.
	 */
	@Test
	public void remove_afterTreeModificationBySameIterator_throwsISE (
	) { // method body
		// arrange
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		final ListIterator<Long> iterator = oddDigitsTree.iterator(key);
		// act
		iterator.next();
		iterator.remove();
		// assert
		Assertions.assertThrows(IllegalStateException.class, iterator::remove, "Попытка удаления элемента, сразу после модификации дерева тем же итератором. remove() не выбросил ISE");
	} // remove_afterTreeModificationBySameIterator_throwsISE()

	/**
	 * Удаление элемента, после его получения методом {@link ListIterator#next() next()}.
	 * Ожидания: выбранный элемент удалён из дерева.
	 */
	@Test
	public void remove_afterNextCalling_itemRemoved (
	) { // method body
		// arrange
		final Long item = 3L;
		final Integer key = keyExtractor.apply(item);
		final ListIterator<Long> iterator = oddDigitsTree.iterator(key);
		// act
		iterator.next();
		iterator.remove();
		// assert
		Assertions.assertNull(oddDigitsTree.findItemByKey(key), "Удаление элемента, после его получения методом next(). Удалённый элемент найден в дереве");
	} // remove_afterNextCalling_itemRemoved()

	/**
	 * Удаление элемента, после его получения методом {@link ListIterator#previous() previous()}.
	 * Ожидания: выбранный элемент удалён из дерева.
	 */
	@Test
	public void remove_afterPreviousCalling_itemRemoved (
	) { // method body
		// arrange
		final Long nextItem = 5L;
		final Integer nextItemKey = oddDigitsTree.keyExtractor().apply(nextItem);
		final ListIterator<Long> iterator = oddDigitsTree.iterator(nextItemKey);
		// act
		final Long targetItem = iterator.previous();
		iterator.remove();
		// assert
		Assertions.assertNull(oddDigitsTree.findItem(targetItem), "Удаление элемента, после его получения методом previous(). Удалённый элемент найден в дереве");
	} // remove_afterPreviousCalling_itemRemoved()

	/**
	 * Метод должен выбрасывать {@link UnsupportedOperationException} при любом обращении.
	 * Ожидания: метод {@link ListIterator#set(Object) set()} выбрасывает {@link UnsupportedOperationException}.
	 */
	@Test
	public void set_throwsUOE (
	) { // method body
		// arrange
		final Long item = rng.nextLong();
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		// act
		iterator.next();
		// assert
		Assertions.assertThrows(UnsupportedOperationException.class, () -> iterator.set(item), "Метод должен выбрасывать UnsupportedOperationException при любом обращении. set() не выбросил UOE");
	} // set_throwsUOE()

	/**
	 * Метод должен выбрасывать {@link UnsupportedOperationException} при любом обращении.
	 * Ожидания: метод {@link ListIterator#add(Object) add()} выбрасывает {@link UnsupportedOperationException}.
	 */
	@Test
	public void add_throwsUOE (
	) { // method body
		// arrange
		final Long item = rng.nextLong();
		final ListIterator<Long> iterator = tree.iterator();
		// assert
		Assertions.assertThrows(UnsupportedOperationException.class, () -> iterator.add(item), "Метод должен выбрасывать UnsupportedOperationException при любом обращении. add() не выбросил UOE");
	} // add_throwsUOE()

	// todo
} // AvlTreeIteratorTests
