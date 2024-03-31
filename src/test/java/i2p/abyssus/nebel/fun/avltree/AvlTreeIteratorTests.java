package i2p.abyssus.nebel.fun.avltree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.ListIterator;
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
	public void hasNext_afterModificationBySameIterator_noThrows (
	) { // method body
		// arrange
		final ListIterator<Long> iterator = oddDigitsTree.iterator();
		// act
		iterator.next();
		iterator.remove();
		// assert
		Assertions.assertDoesNotThrow(iterator::hasNext, "Модификация дерева, к которому присоединён итератор, тем же итератором. Метод hasNext() выбросил исключение");
	} // hasNext_afterModificationBySameIterator_noThrows()

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

	// todo
} // AvlTreeIteratorTests
