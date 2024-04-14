package i2p.abyssus.nebel.fun.avltree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
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

	// todo
} // AvlTreeComplexTests
