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

	private Function<Integer, Integer> keyExtractor;
	private Comparator<Integer> keyComparator;
	private RandomGenerator rng;
	private AvlTree<Integer, Integer> tree;

// instance methods

	@BeforeEach
	public void testInit (
	) { // method body
		keyExtractor = Function.<Integer>identity();
		keyComparator = Comparator.<Integer>naturalOrder();
		rng = ThreadLocalRandom.current();
		tree = new AvlTree<Integer, Integer>(keyExtractor, keyComparator);
	} // testInit()

	@Test
	public void constructor_nullKeyExtractor_throwsNPE (
	) { // method body
		Assertions.assertThrows(NullPointerException.class, () -> new AvlTree<Integer, Integer>(null, keyComparator));
	} // constructor_nullKeyExtractor_throwsNPE()

	@Test
	public void constructor_nullKeyComparator_throwsNPE (
	) { // method body
		Assertions.assertThrows(NullPointerException.class, () -> new AvlTree<Integer, Integer>(keyExtractor, null));
	} // constructor_nullKeyComparator_throwsNPE()

	@Test
	public void constructor_validArguments_notThrows (
	) { // method body
		Assertions.assertDoesNotThrow(() -> new AvlTree<Integer, Integer>(keyExtractor, keyComparator));
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
		final Integer foundItem = tree.findItemByKey(key);
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
		tree.put(1);
		tree.put(3);
		tree.put(5);
		tree.put(7);
		tree.put(9);
		final Integer seven = 7;
		final Integer key = keyExtractor.apply(seven);
		// act
		final Integer foundItem = tree.findItemByKey(key);
		// assert
		Assertions.assertNotNull(foundItem, "In the tree containing 7, 7 was not found");
		Assertions.assertEquals(seven, foundItem, "In the tree containing 7, for key of 7, was found not 7");
	} // findItemByKey_treeContains357_found7()

	/**
	 * Дерево содержит элементы 1, 3, 5, 7, 9. Элемент 2 не должен быть найден.
	 */
	@Test
	public void findItemByKey_treeContains13579_notFound2 (
	) { // method body
		// arrange
		tree.put(1);
		tree.put(3);
		tree.put(5);
		tree.put(7);
		tree.put(9);
		final Integer two = 2;
		final Integer key = keyExtractor.apply(two);
		// act
		final Integer foundItem = tree.findItemByKey(key);
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
		final Integer item = rng.nextInt();
		// act
		final Integer foundItem = tree.findItem(item);
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
		tree.put(1);
		tree.put(3);
		tree.put(5);
		tree.put(7);
		tree.put(9);
		final Integer seven = 7;
		// act
		final Integer foundItem = tree.findItem(seven);
		// assert
		Assertions.assertNotNull(foundItem, "In the tree containing 7, 7 was not found");
		Assertions.assertEquals(seven, foundItem, "In the tree containing 7, for search request of 7, was found not 7");
	} // findItem_treeContains357_found7()

	/**
	 * Дерево содержит элементы 1, 3, 5, 7, 9. Элемент 2 не должен быть найден.
	 */
	@Test
	public void findItem_treeContains13579_notFound2 (
	) { // method body
		// arrange
		tree.put(1);
		tree.put(3);
		tree.put(5);
		tree.put(7);
		tree.put(9);
		final Integer two = 2;
		// act
		final Integer foundItem = tree.findItem(two);
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
		tree.put(1);
		final Integer item = 2;
		// act
		final Integer replacedItem = tree.put(item);
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
		final Integer someItem = rng.nextInt();
		final Integer newItem = Integer.valueOf(someItem);
		tree.put(someItem);
		// act
		final Integer replacedItem = tree.put(newItem);
		// assert
		Assertions.assertSame(someItem, replacedItem, "Placed item replace an item with different key");
	} // put_treeContains1_put1_replace1()

	// todo
} // AvlTreeTests
