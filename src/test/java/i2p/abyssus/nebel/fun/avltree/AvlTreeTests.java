package i2p.abyssus.nebel.fun.avltree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.random.RandomGenerator;

public class AvlTreeTests {

// instance fields

	private Function<Long, Integer> keyExtractor;
	private Comparator<Integer> keyComparator;
	private RandomGenerator rng;

	/**
	 * Всегда пустое дерево.
	 */
	private AvlTree<Long, Integer> emptyTree;

	/**
	 * Изначально пустое дерево, заполняемое в тестовых методах.
	 */
	private AvlTree<Long, Integer> tree;

	/**
	 * Заполненное дерево. Содержит нечётные натуральные числа меньшие 10.
	 */
	private AvlTree<Long, Integer> filledTree;

// instance methods

	@BeforeEach
	public void testInit (
	) { // method body
		keyExtractor = Long::intValue;
		keyComparator = Comparator.<Integer>naturalOrder();
		rng = ThreadLocalRandom.current();
		emptyTree = new AvlTree<Long, Integer>(keyExtractor, keyComparator);
		tree = new AvlTree<Long, Integer>(keyExtractor, keyComparator);
		filledTree = new AvlTree<Long, Integer>(keyExtractor, keyComparator);
		filledTree.put(1L);
		filledTree.put(3L);
		filledTree.put(5L);
		filledTree.put(7L);
		filledTree.put(9L);
	} // testInit()

	@Test
	public void constructor_nullKeyExtractor_throwsNPE (
	) { // method body
		Assertions.assertThrows(NullPointerException.class, () -> new AvlTree<Long, Integer>(null, keyComparator));
	} // constructor_nullKeyExtractor_throwsNPE()

	@Test
	public void constructor_nullKeyComparator_throwsNPE (
	) { // method body
		Assertions.assertThrows(NullPointerException.class, () -> new AvlTree<Long, Integer>(keyExtractor, null));
	} // constructor_nullKeyComparator_throwsNPE()

	@Test
	public void constructor_validArguments_notThrows (
	) { // method body
		Assertions.assertDoesNotThrow(() -> new AvlTree<Long, Integer>(keyExtractor, keyComparator));
	} // constructor_validArguments_notThrows()

	/**
	 * В пустом дереве не должно найтись никакого элемента.
	 */
	@Test
	public void findItemByKey_emptyTree_nothingFound (
	) { // method body
		// arrange
		final Integer key = rng.nextInt();
		// act
		final Long foundItem = emptyTree.findItemByKey(key);
		// assert
		Assertions.assertNull(foundItem, "In the empty tree was found an item");
	} // findItemByKey_emptyTree_nothingFound()

	/**
	 * Дерево содержит элементы 1, 3, 5, 7, 9. Элемент 7 должен быть найден.
	 */
	@Test
	public void findItemByKey_treeContains13579_found7 (
	) { // method body
		// arrange
		final Long seven = 7L;
		final Integer key = keyExtractor.apply(seven);
		// act
		final Long foundItem = filledTree.findItemByKey(key);
		// assert
		Assertions.assertNotNull(foundItem, "In the tree containing 7, 7 was not found");
		Assertions.assertSame(seven, foundItem, "In the tree containing 7, for key of 7, was found not 7");
	} // findItemByKey_treeContains357_found7()

	/**
	 * Дерево содержит элементы 1, 3, 5, 7, 9. Элемент 2 не должен быть найден.
	 */
	@Test
	public void findItemByKey_treeContains13579_notFound2 (
	) { // method body
		// arrange
		final Long two = 2L;
		final Integer key = keyExtractor.apply(two);
		// act
		final Long foundItem = filledTree.findItemByKey(key);
		// assert
		Assertions.assertNull(foundItem, "In the tree not containing 2, an item with key of 2 was found");
	} // findItemByKey_treeContains13579_notFound2()

	/**
	 * В пустом дереве не должно найтись никакого элемента.
	 */
	@Test
	public void findItem_emptyTree_nothingFound (
	) { // method body
		// arrange
		final Long item = rng.nextLong();
		// act
		final Long foundItem = emptyTree.findItem(item);
		// assert
		Assertions.assertNull(foundItem, "In the empty tree was found an item");
	} // findItem_emptyTree_nothingFound()

	/**
	 * Дерево содержит элементы 1, 3, 5, 7, 9. Элемент 7 должен быть найден.
	 */
	@Test
	public void findItem_treeContains13579_found7 (
	) { // method body
		// arrange
		final Long seven = 7L;
		// act
		final Long foundItem = filledTree.findItem(seven);
		// assert
		Assertions.assertNotNull(foundItem, "In the tree containing 7, 7 was not found");
		Assertions.assertSame(seven, foundItem, "In the tree containing 7, for search request of 7, was found not 7");
	} // findItem_treeContains357_found7()

	/**
	 * Дерево содержит элементы 1, 3, 5, 7, 9. Элемент 2 не должен быть найден.
	 */
	@Test
	public void findItem_treeContains13579_notFound2 (
	) { // method body
		// arrange
		final Long two = 2L;
		// act
		final Long foundItem = filledTree.findItem(two);
		// assert
		Assertions.assertNull(foundItem, "In the tree not containing 2, 2 was found");
	} // findItem_treeContains13579_notFound2()

	/**
	 * Дерево содержит элемент 1. Помещаем элемент 2. Замены элементов быть не должно.
	 */
	@Test
	public void put_treeContains1_put2_returnNull (
	) { // method body
		// arrange
		tree.put(1L);
		final Long item = 2L;
		// act
		final Long replacedItem = tree.put(item);
		// assert
		Assertions.assertNull(replacedItem, "In tree not containing 2, item 2 replace an item");
	} // put_treeContains1_put2_returnNull()

	/**
	 * Дерево содержит некоторый элемент. Помещаем элемент с тем же ключом. Заменяем уже существующий элемент.
	 */
	@Test
	public void put_treeContains1_put1_replace1 (
	) { // method body
		// arrange
		final Long someItem = rng.nextLong();
		final Long newItem = Long.valueOf(someItem);
		tree.put(someItem);
		// act
		final Long replacedItem = tree.put(newItem);
		// assert
		Assertions.assertSame(someItem, replacedItem, "Placed item replace an item with different key");
	} // put_treeContains1_put1_replace1()

	/**
	 * Высота пустого дерева должна равняться нулю.
	 */
	@Test
	public void height_emptyTree_returnZero (
	) { // method body
		Assertions.assertEquals(0, emptyTree.height(), "The height of an empty tree is non-zero");
	} // height_emptyTree_returnZero()

	/**
	 * Высота АВЛ-дерева содержащего пять элементов должна равняться 3.
	 */
	@Test
	public void height_treeContains13579_return3 (
	) { // method body
		Assertions.assertEquals(3, filledTree.height(), "The height of the avl-tree containing 5 items is different from 3");
	} // height_treeContains13579_return3()

	/**
	 * Высота АВЛ-дерева содержащего 1 элемент должна равняться 1.
	 */
	@Test
	public void height_treeOfOneItem_return1 (
	) { // method body
		// arrange
		final Long item = rng.nextLong();
		tree.put(item);
		// act
		final int height = tree.height();
		// assert
		Assertions.assertEquals(1, height, "The height of an avl-tree containing 1 item is different from 1");
	} // height_treeOfOneItem_return1()

	/**
	 * Высота АВЛ-дерева содержащего 2 элемента должна равняться 2.
	 */
	@Test
	public void height_treeOfTwoItems_return2 (
	) { // method body
		// arrange
		final Long firstItem = rng.nextLong();
		final Long secondItem = rng.nextLong();
		tree.put(firstItem);
		tree.put(secondItem);
		// act
		final int height = tree.height();
		// assert
		Assertions.assertEquals(2, height, "The height of an avl-tree containing 2 items is different from 2");
	} // height_treeOfTwoItems_return2()

	@Test
	public void height_treeOfNItems_heightBelowMaximum (
	) { // method body
		// arrange
		final int MIN_N = 128;
		final int MAX_N = 2048;
		final int n = rng.nextInt(MIN_N, MAX_N + 1);
		final double maxHeight = (1.45 / Math.log(2)) * Math.log(n + 2);
		for (int i = n; i > 0; i--) {
			final Long item = rng.nextLong();
			tree.put(item);
		} // for
		// act
		final int height = tree.height();
		// assert
		Assertions.assertTrue(height <= maxHeight, "The tree height exceeds the maximum possible height");
	} // height_treeOfNItems_heightBelowMaximum()

	// todo
} // AvlTreeTests
