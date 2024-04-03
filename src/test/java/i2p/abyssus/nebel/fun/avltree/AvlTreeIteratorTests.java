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

	// todo
} // AvlTreeIteratorTests
