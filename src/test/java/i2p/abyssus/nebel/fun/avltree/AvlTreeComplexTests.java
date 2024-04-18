package i2p.abyssus.nebel.fun.avltree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.random.RandomGenerator;

/**
 * Тесты дерева, которые сложно отнести к модульным.
 */
public class AvlTreeComplexTests {

// instance fields

	/**
	 * ГПСЧ
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
	 * Изначально пустое дерево для проведения тестов.
	 */
	private AvlTree<Long, Integer> tree;

// instance methods

	/**
	 * Инициализация полей перед тестом.
	 */
	@BeforeEach
	public void initFields (
	) { // method body
		tree = new AvlTree<Long, Integer>(keyExtractor, keyComparator);
	} // initFields()

	/**
	 * Добавление большого числа элементов.
	 * Высота дерева не должна превышать теоретический максимум.
	 * Ожидания: высота дерева не превышает 1.45*log2(n+2).
	 */
	@Test
	public void treeHeight_afterAddingItems (
	) { // method body
		// arrange
		final int MIN_N = 0x2000;
		final int MAX_N = 0x80_0000;
		int n = rng.nextInt(MIN_N, MAX_N + 1);
		// act
		for (int i = n; i > 0; i--) {
			final Long item = rng.nextLong();
			tree.put(item);
		} // for
		n = (int) tree.size();
		// assert
		final double maxHeight = (1.45 / Math.log(2)) * Math.log(n + 2);
		final boolean isValidHeight = (tree.height() <= maxHeight);
		Assertions.assertTrue(isValidHeight, "Добавление большого числа элементов. Высота превысила максимум");
	} // treeHeight_afterAddingItems()

	/**
	 * Удаление большого числа элементов.
	 * Высота дерева не должна превышать теоретический максимум.
	 * Ожидания: высота дерева не превышает 1.45*log2(n+2).
	 */
	@Test
	public void treeHeight_afterRemovingItems (
	) { // method body
		// arrange
		final int REDUCED_SIZE_MIN = 0x80;
		final int FULL_SIZE_MIN = 0x2000;
		final int FULL_SIZE_MAX = 0x80_000;
		final int fullSize = rng.nextInt(FULL_SIZE_MIN, FULL_SIZE_MAX + 1);
		final int reducedSize = rng.nextInt(REDUCED_SIZE_MIN, FULL_SIZE_MIN);
		// act
		while (tree.size() < fullSize) {
			final Long item = rng.nextLong();
			tree.put(item);
		} // while
		final Iterator<Long> iterator = tree.iterator();
		while (iterator.hasNext()) {
			iterator.next();
			final boolean doRemoving = (rng.nextInt(fullSize) >= reducedSize);
			if (doRemoving) {
				iterator.remove();
			} // if
		} // while
		// assert
		final double maxHeight = (1.45 / Math.log(2)) * Math.log(tree.size() + 2);
		final boolean isValidHeight = (tree.height() <= maxHeight);
		Assertions.assertTrue(isValidHeight, "Удаление большого числа элементов. Высота превысила максимум");
	} // treeHeight_afterRemovingItems()

	/**
	 * Удаление случайных элементов итератором.
	 * Ожидания: остались все элементы кроме тех, что были удалены.
	 */
	@Test
	public void iteratorRemove_removingRandomItems_allRemainingItems (
	) { // method body
		// arrange
		final int MIN_N = 128;
		final int MAX_N = 8192;
		final int n = rng.nextInt(MIN_N, MAX_N + 1);
		final int m = rng.nextInt((n / 4), (n / 4 * 3) + 1);
		final NavigableMap<Integer, Long> stdTree = new TreeMap<Integer, Long>(keyComparator);
		int virtualSize = n;
		int virtualPos = 0;
		// act
		while (tree.size() != n) {
			final Long item = rng.nextLong();
			final Integer key = keyExtractor.apply(item);
			stdTree.put(key, item);
			tree.put(item);
		} // while
		ListIterator<Long> iterator = tree.iterator();
		for (int i = m; i > 0; i--) {
			boolean direction = rng.nextBoolean();
			direction = (direction && (virtualPos != virtualSize)) || (!direction && (virtualPos == 0));
			Long item = null;
			if (direction) {
				final int stepCount = rng.nextInt(virtualSize - virtualPos) + 1;
				for (int j = stepCount; j > 0; j--) {
					item = iterator.next();
				} // for
				virtualPos += stepCount - 1;
			} else {
				final int stepCount = rng.nextInt(virtualPos) + 1;
				for (int j = stepCount; j > 0; j--) {
					item = iterator.previous();
				} // for
				virtualPos -= stepCount;
			} // if
			stdTree.remove(keyExtractor.apply(item));
			iterator.remove();
			virtualSize--;
		} // for
		// assert
		iterator = tree.iterator();
		for (final Long expectedItem : stdTree.values()) {
			final Long actualItem = iterator.next();
			Assertions.assertSame(expectedItem, actualItem, "Удаление случайных элементов итератором. Сверка элементов с образцом. Элементы не совпадают");
		} // for
		Assertions.assertFalse(iterator.hasNext(), "Удаление случайных элементов итератором. Сверка количества элементов. В тестируемом дереве лишние элементы");
	} // iteratorRemove_removingRandomItems_allRemainingItems()

	// todo
} // AvlTreeComplexTests
