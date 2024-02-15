package i2p.abyssus.nebel.fun.avltree;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Objects;
import java.util.function.Function;

/**
 * Реализация <a href="https://ru.wikipedia.org/wiki/%D0%90%D0%92%D0%9B-%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE">АВЛ-дерева</a>.
 * <p>Ключи элементов должны попарно отличаться, с точки зрения компаратора ключей. Дерево может принимать в качестве элементов значения {@code null}, если указанный при создании дерева экстрактор ключей принимает значения {@code null}. Все значения ключей возвращаемые экстрактором должны приниматься компаратором.</p>
 * @param <E> Тип элементов.
 * @param <K> Тип ключей элементов.
 */
/* Детали реализации:
 * Дерево представлено узлами. Узлы дополнительно образуют двусвязный список. Ключи элементов в списке узлов, упорядочены от меньших к большим.
 */
public class AvlTree <E, K> {

// nested classes

	/**
	 * Узел АВЛ-дерева.
	 * <p>Простой класс для непосредственного хранения данных. Класс не содержит никаких методов, а доступ к его полям осуществляется напрямую.</p>
	 * <p>Узлы не только хранят элементы, но и составляют структуру дерева. Кроме того, узлы образуют двусвязный список, с возрастающими ключами элементов.</p>
	 * @param <E> Тип элементов.
	 */
	private static class Node <E> {

	// instance fields

		/**
		 * Хранимый элемент.
		 */
		private E item;

		/**
		 * Левый дочерний узел.
		 */
		private Node<E> leftChild;

		/**
		 * Правый дочерний узел.
		 */
		private Node<E> rightChild;

		/**
		 * Предшествующий узел списка.
		 */
		private Node<E> previousNode;

		/**
		 * Следующий узел списка.
		 */
		private Node<E> nextNode;

		/**
		 * Высота поддерева, с корнем в данном узле.
		 */
		private int subtreeHeight;

	// constructors

		/**
		 * Конструктор узла дерева.
		 * <p>Конструктор не выполняет никаких действий. Поля необходимо инициализировать &quot;вручную&quot; после обращения к конструктору.</p>
		 */
		private Node (
		) { // method body
			// do nothing
		} // Node()

	} // Node

// instance fields

	/**
	 * Экстрактор ключей.
	 */
	private final Function<? super E, ? extends K> keyExtractor;

	/**
	 * Компаратор ключей.
	 */
	private final Comparator<? super K> keyComparator;

	/**
	 * Корень дерева. Существует, если дерево не пусто.
	 */
	private Node<E> rootNode;

	/**
	 * Самый левый узел. Существует, если дерево не пусто.
	 */
	private Node<E> leftmostNode;

	/**
	 * Самый правый узел. Существует, если дерево не пусто.
	 */
	private Node<E> rightmostNode;

	/**
	 * Версия дерева.
	 * <p>Необходима для правильной работы итераторов. Увеличивается при каждой модификации.</p>
	 */
	private long version;

	/**
	 * Размер дерева.
	 * <p>Поле содержит число узлов в дереве. Значение поля не должно превышать {@link Integer#MAX_VALUE}.</p>
	 */
	private int size;

	/**
	 * Последний найденный путь.
	 * <p>Кеш пути к узлу c последним искомым ключом. Обновляется при поиске узла с любым ключом отличным от запрошенного в последний раз. Сбрасывается при любой модификации дерева.</p>
	 */
	private Deque<Node<E>> lastFoundPath;

	/**
	 * Последний искомый ключ.
	 * <p>Значение последнего искомого ключа. Необходимо для нормальной работы кеша пути к ключу. Используется только в случае, когда сам кеш не сброшен. Обновляется при поиске узла с любым отличным ключом. Сбрасывается при любой модификации дерева.</p>
	 */
	private K lastSearchedKey;

// constructors

	/**
	 * Конструктор АВЛ-дерева.
	 * @param keyExtractor Экстрактор ключей.
	 * @param keyComparator Компаратор ключей.
	 * @throws NullPointerException Если любой из аргументов не существует.
	 */
	public AvlTree (
		final Function<? super E, ? extends K> keyExtractor,
		final Comparator<? super K> keyComparator
	) throws NullPointerException
	{ // method body
		this.keyExtractor = Objects.<Function<? super E, ? extends K>>requireNonNull(keyExtractor);
		this.keyComparator = Objects.<Comparator<? super K>>requireNonNull(keyComparator);
	} // AvlTree()

// instance methods

	/**
	 * Поиск элемента по ключу.
	 * <p>Метод находит в дереве и возвращает элемент с ключом равным указанному. Если такой элемент отсутствует, возвращается значение {@code null}. Отношения ключей определяются компаратором.</p>
	 * @param key Ключ искомого элемента.
	 * @return Элемент с ключом равным заданному, или значение {@code null}.
	 */
	public E findItemByKey (
		final K key
	) { // method body
		// todo
		throw new NoSuchMethodError();
	} // findItemByKey()

	public E add (
		final E item
	) { // method body
		// todo
		throw new NoSuchMethodError();
	} // add()

	/**
	 * Поиск пути к узлу.
	 * <p>Метод находит и возвращает путь к узлу ключ элемента которого, равен указанному. Отношения ключей определяются компаратором.</p>
	 * <p>Путь укладывается на стек начиная с корня дерева, и заканчивая самим искомым узлом, либо его возможным родителем, если узла с указанным ключом не существует.</p>
	 * @param key Ключ элемента искомого узла.
	 * @return Путь к узлу с искомым ключом.
	 */
	private Deque<Node<E>> findPathByKey (
		final K key
	) { // method body
		final Deque<Node<E>> path;
		if ((lastFoundPath != null) && (keyComparator.compare(lastSearchedKey, key) == 0)) {
			path = lastFoundPath;
		} else {
			path = new ArrayDeque<Node<E>>();
			if (rootNode != null) {
				int keyComparingResult;
				Node<E> nextNode = rootNode;
				do {
					path.push(nextNode);
					keyComparingResult = keyComparator.compare(key, keyExtractor.apply(nextNode.item));
					nextNode = (keyComparingResult < 0) ? nextNode.leftChild : nextNode.rightChild;
				} while ((nextNode != null) && (keyComparingResult != 0));
			} // if
			lastFoundPath = path;
			lastSearchedKey = key;
		} // if
		return path;
	} // findPathByKey()

	/**
	 * Поиск пути к узлу.
	 * <p>Метод находит и возвращает путь к узлу с равным по ключу элементом. Отношения ключей определяются компаратором.</p>
	 * <p>Путь укладывается на стек начиная с корня дерева, и заканчивая искомым узлом, либо его возможным родителем, если узла содержащего элемент с равным ключом в дереве нет.</p>
	 * @param item Элемент с искомым ключом.
	 * @return Путь к узлу содержащему элемент с искомым ключом.
	 */
	private Deque<Node<E>> findPath (
		final E item
	) { // method body
		return findPathByKey(keyExtractor.apply(item));
	} // findPath()

	// todo
} // AvlTree