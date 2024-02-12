package i2p.abyssus.nebel.fun.avltree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.function.Function;

public class AvlTreeTests {

// instance fields

	private Function<Integer, Integer> keyExtractor;
	private Comparator<Integer> keyComparator;

// instance methods

	@BeforeEach
	public void testInit (
	) { // method body
		keyExtractor = Function.<Integer>identity();
		keyComparator = Comparator.<Integer>naturalOrder();
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

	// todo
} // AvlTreeTests
