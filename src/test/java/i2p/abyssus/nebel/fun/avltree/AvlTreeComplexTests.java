package i2p.abyssus.nebel.fun.avltree;

import org.junit.jupiter.api.BeforeEach;

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

	// todo
} // AvlTreeComplexTests
