package i2p.abyssus.nebel.fun.avltree;

import org.junit.jupiter.api.BeforeEach;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Тесты итератора элементов дерева.
 */
public class AvlTreeIteratorTests {

// instance fields

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

	// todo
} // AvlTreeIteratorTests
