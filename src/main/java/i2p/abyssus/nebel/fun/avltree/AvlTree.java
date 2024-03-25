package i2p.abyssus.nebel.fun.avltree;

import java.util.*;
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

		/**
		 * Конструктор узла дерева.
		 * <p>Конструктор выполняет инициализацию поля хранимого элемента и ссылок на предшествующий и следующий узлы в цепочке. Ссылки на дочерние узлы остаются пустыми, что логично, так как дочерние узлы могут быть либо добавлены, либо изменены во время ребалансировки дерева. Так как созданный узел не имеет дочерних узлов, то высота поддерева с корнем в этом узле, равна единице.</p>
		 * @param item Хранимый элемент.
		 * @param previousNode Предшествующий узел цепочки.
		 * @param nextNode Следующий узел цепочки.
		 */
		private Node (
			final E item,
			final Node<E> previousNode,
			final Node<E> nextNode
		) { // method body
			this.item = item;
			this.previousNode = previousNode;
			this.nextNode = nextNode;
			this.subtreeHeight = 1;
		} // Node()

		/**
		 * Очистка узла.
		 * <p>Метод очищает узел, подготавливая его к передачи сборщику мусора, или повторному использованию.</p>
		 */
		private void clear (
		) { // method body
			item = null;
			leftChild = null;
			rightChild = null;
			previousNode = null;
			nextNode = null;
			subtreeHeight = 1;
		} // clear()

	} // Node

	/**
	 * Итератор элементов дерева.
	 * <p>Класс реализует интерфейс {@link ListIterator}, за тем исключением, что методы {@link #add(Object) add()}, {@link #nextIndex()}, {@link #previousIndex()} и {@link #set(Object) set()}, при обращении к ним, выбрасывают исключение {@link UnsupportedOperationException}.</p>
	 */
	private class TreeIterator implements ListIterator<E> {

	// static fields

		/**
		 * Константа маркера последнего узла, указывающая на то, что последним был возвращен элемент узла, на который ссылается поле {@link #previousNode}. Устанавливается при прямом продвижении итератора, после обращения к методу {@link #next()}.
		 */
		private static final byte PREVIOUS_NODE = -1;

		/**
		 * Константа маркера последнего узла, указывающая на то, что узел хранивший последний возвращённый элемент был удалён, во время обращения к методу {@link #remove()}, либо его никогда не существовало, так как не было обращений к методам {@link #previous()} и {@link #next()}.
		 */
		private static final byte UNDEFINED = 0;

		/**
		 * Константа маркера последнего узла, указывающая на то, что последним был возвращен элемент узла, на который ссылается поле {@link #nextNode}. Устанавливается при обратном продвижении итератора, после обращения к методу {@link #previous()}.
		 */
		private static final byte NEXT_NODE = 1;

	// instance fields

		/**
		 * Согласованная версия дерева. При отличии от актуальной версии дерева, итератор выбрасывает исключение {@link ConcurrentModificationException}.
		 */
		private long consistentTreeVersion;

		/**
		 * Следующий узел, элемент которого возвращается при обращении к методу {@link #next()}.
		 */
		private Node<E> nextNode;

		/**
		 * Предшествующий узел, элемент которого возвращается при обращении к методу {@link #previous()}.
		 */
		private Node<E> previousNode;

		/**
		 * Маркер переменной содержащей последний возвращённый узел. Необходим для корректной работы метода {@link #remove()}.
		 */
		private byte lastNodeMark;

	// constructors

		/**
		 * Конструктор итератора.
		 * <p>Создаёт экземпляр итератора, связанный с текущей версией дерева, и принимая ссылки на два последовательных, связанных между собой узла. Никаких проверок принадлежности указанных узлов к текущему дереву не производится - конструктор полагается на вызывающий код. Однако, при включенных операторах контроля, если хотя бы один из указанных узлов существует, производится проверка связности узлов между собой.</p>
		 * @param previousNode Ссылка на предшествующий узел.
		 * @param nextNode Ссылка на следующий узел.
		 * @throws AssertionError Если включены операторы контроля, и выполнено любое из условий:<ul>
		 *     <li>Предшествующий узел существует, но следующим для него является не другой указанный узел.</li>
		 *     <li>Следующий узел существует, но предшествующим для него является не другой указанный узел.</li>
		 * </ul>
		 */
		private TreeIterator (
			final Node<E> previousNode,
			final Node<E> nextNode
		) throws AssertionError
		{ // method body
			assert (previousNode == null) || (previousNode.nextNode == nextNode);
			assert (nextNode == null) || (nextNode.previousNode == previousNode);
			consistentTreeVersion = treeVersion;
			this.nextNode = nextNode;
			this.previousNode = previousNode;
			lastNodeMark = UNDEFINED;
		} // TreeIterator()

	// instance methods

		@Override
		public boolean hasNext (
		) { // method body
			// todo
			throw new NoSuchMethodError();
		} // hasNext()

		@Override
		public E next (
		) throws NoSuchElementException
		{ // method body
			// todo
			throw new NoSuchMethodError();
		} // next()

		@Override
		public boolean hasPrevious (
		) { // method body
			// todo
			throw new NoSuchMethodError();
		} // hasPrevious()

		@Override
		public E previous (
		) throws NoSuchElementException
		{ // method body
			// todo
			throw new NoSuchMethodError();
		} // previous()

		@Override
		public int nextIndex (
		) throws UnsupportedOperationException
		{ // method body
			// todo
			throw new NoSuchMethodError();
		} // nextIndex()

		@Override
		public int previousIndex (
		) throws UnsupportedOperationException
		{ // method body
			// todo
			throw new NoSuchMethodError();
		} // previousIndex()

		@Override
		public void remove (
		) throws IllegalStateException
		{ // method body
			// todo
			throw new NoSuchMethodError();
		} // remove()

		@Override
		public void set (
			final E item
		) throws UnsupportedOperationException
		{ // method body
			// todo
			throw new NoSuchMethodError();
		} // set()

		@Override
		public void add (
			final E item
		) throws UnsupportedOperationException
		{ // method body
			// todo
			throw new NoSuchMethodError();
		} // add()

		// todo
	} // TreeIterator

// static methods

	/**
	 * Высота поддерева.
	 * <p>Метод, принимая ссылку на узел, возвращает высоту поддерева с корнем в этом узле. Высота несуществующего поддерева равна нулю. Метод не обходит поддерево, а лишь считывает высоту из поля корневого узла.</p>
	 * @param root Корень поддерева.
	 * @return Высота поддерева.
	 */
	private static int subtreeHeight (
		final Node<?> root
	) { // method body
		return (root != null) ? root.subtreeHeight : 0;
	} // subtreeHeight()

	/**
	 * Коррекция высоты поддерева.
	 * <p>Метод рассчитывает, корректирует и возвращает высоту поддерева в указанном корневом узле, основываясь на высотах его дочерних веток.</p>
	 * @param root Корень поддерева.
	 * @return Новая высота поддерева.
	 * @throws NullPointerException Если указанный корень поддерева не существует.
	 */
	private static int heightCorrection (
		final Node<?> root
	) throws NullPointerException
	{ // method body
		root.subtreeHeight = Math.max(subtreeHeight(root.leftChild), subtreeHeight(root.rightChild)) + 1;
		return root.subtreeHeight;
	} // heightCorrection()

	/**
	 * Поиск самого левого узла в указанном поддереве.
	 * <p>Метод ищет и возвращает самый левый узел поддерева, заданного указанным корнем.</p>
	 * <p>Во время работы, метод дополняет указанный путь пройденными узлами, от указанного корня и до искомого включительно. Соответствие корня поддерева и пути никак не проверяется, и возлагается на вызывающий код.</p>
	 * @param <E> Тип элементов поддерева.
	 * @param root Корень поддерева.
	 * @param path Дополняемый путь.
	 * @return Самый левый узел поддерева.
	 * @throws NullPointerException Если любой из аргументов не существует.
	 */
	private static <E> Node<E> findLeftmostNode (
		final Node<E> root,
		final Deque<Node<E>> path
	) throws NullPointerException
	{ // method body
		Node<E> leftmostNode = root;
		while (leftmostNode.leftChild != null) {
			path.push(leftmostNode);
			leftmostNode = leftmostNode.leftChild;
		} // while
		path.push(leftmostNode);
		return leftmostNode;
	} // findLeftmostNode()

	/**
	 * Поиск самого правого узла в указанном поддереве.
	 * <p>Метод ищет и возвращает самый правый узел поддерева, заданного указанным корнем.</p>
	 * <p>Во время работы, метод дополняет указанный путь пройденными узлами, от указанного корня и до искомого включительно. Соответствие корня поддерева и пути никак не проверяется, и возлагается на вызывающий код.</p>
	 * @param <E> Тип элементов поддерева.
	 * @param root Корень поддерева.
	 * @param path Дополняемый путь.
	 * @return Самый правый узел поддерева.
	 * @throws NullPointerException Если любой из аргументов не существует.
	 */
	private static <E> Node<E> findRightmostNode (
		final Node<E> root,
		final Deque<Node<E>> path
	) throws NullPointerException
	{ // method body
		Node<E> rightmostNode = root;
		while (rightmostNode.rightChild != null) {
			path.push(rightmostNode);
			rightmostNode = rightmostNode.rightChild;
		} // while
		path.push(rightmostNode);
		return rightmostNode;
	} // findRightmostNode()

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
	private long treeVersion;

	/**
	 * Размер дерева.
	 * <p>Поле содержит число узлов в дереве.</p>
	 */
	private long treeSize;

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
		final Deque<Node<E>> path = findPathByKey(key);
		E item = null;
		if (!path.isEmpty()) {
			final E lastNodeItem = path.peek().item;
			if (keyComparator.compare(key, keyExtractor.apply(lastNodeItem)) == 0) {
				item = lastNodeItem;
			} // if
		} // if
		return item;
	} // findItemByKey()

	/**
	 * Поиск элемента.
	 * <p>Метод находит в дереве и возвращает элемент с ключом равным ключу указанного элемента. Если такой элемент отсутствует, возвращается значение {@code null}. Отношения ключей определяются компаратором.</p>
	 * @param item Элемент с искомым ключом.
	 * @return Элемент с ключом равным ключу указанного элемента, или значение {@code null}.
	 */
	public E findItem (
		final E item
	) { // method body
		return findItemByKey(keyExtractor.apply(item));
	} // findItem()

	/**
	 * Помещение элемента в дерево.
	 * <p>Метод помещает элемент в дерево. Если элемент с таким же ключом уже есть в дереве, он заменяется указанным.</p>
	 * <p>Метод возвращает либо заменяемый элемент с тем же ключом, либо, если элемента с таким ключом в дереве нет, значение {@code null}.</p>
	 * @param item Помещаемый в дерево элемент.
	 * @return Заменяемый элемент с тем же ключом, либо значение {@code null}.
	 */
	public E put (
		final E item
	) { // method body
		E replacedItem = null;
		if (rootNode != null) {
			final Deque<Node<E>> path = findPath(item);
			final Node<E> targetNode = path.peek();
			if (keyComparator.compare(keyExtractor.apply(item), keyExtractor.apply(targetNode.item)) != 0) {
				linkChildNode(targetNode, item);
				rebalancing(path);
				treeSize++;
				lastFoundPath = null;
				lastSearchedKey = null;
			} else {
				replacedItem = targetNode.item;
				targetNode.item = item;
			} // if
		} else {
			rootNode = new Node<E>(item, null, null);
			leftmostNode = rootNode;
			rightmostNode = rootNode;
			treeSize++;
			lastFoundPath = null;
			lastSearchedKey = null;
		} // if
		treeVersion++;
		return replacedItem;
	} // put()

	/**
	 * Высота дерева.
	 * <p>Метод возвращает высоту дерева. Эти сведения можно использовать как для отладки, так и для оценки скорости поиска в дереве.</p>
	 * @return Высота дерева.
	 */
	public int height (
	) { // method body
		return subtreeHeight(rootNode);
	} // height()

	/**
	 * Экстрактор ключей.
	 * <p>Метод возвращает экстрактор ключей, используемый деревом.</p>
	 * @return Экстрактор ключей.
	 */
	public Function<? super E, ? extends K> keyExtractor (
	) { // method body
		return keyExtractor;
	} // keyExtractor()

	/**
	 * Компаратор ключей.
	 * <p>Метод возвращает компаратор ключей, используемый деревом.</p>
	 * @return Компаратор ключей.
	 */
	public Comparator<? super K> keyComparator (
	) { // method body
		return keyComparator;
	} // keyComparator()

	/**
	 * Проверка пустоты дерева.
	 * @return <ul>
	 *     <li>{@code true} - Если дерево пусто.</li>
	 *     <li>{@code false} - В противном случае.</li>
	 * </ul>
	 */
	public boolean isEmpty (
	) { // method body
		return rootNode == null;
	} // isEmpty()

	/**
	 * Размер дерева.
	 * <p>Метод возвращает количество элементов в дереве.</p>
	 * @return Количество элементов в дереве.
	 */
	public long size (
	) { // method body
		return treeSize;
	} // size()

	/**
	 * Очистка дерева.
	 * <p>Метод полностью очищает дерево от его содержимого. Метод выполняется за линейное время, так как подготавливает более не нужные внутренние структуры, для передачи сборщику мусора.</p>
	 * <p>Метод возвращает то же дерево, у которого он был вызван.</p>
	 * @return Очищаемое дерево.
	 */
	public AvlTree<E, K> clear (
	) { // method body
		Node<?> next = leftmostNode;
		while (next != null) {
			final Node<?> current = next;
			next = current.nextNode;
			current.clear();
		} // while
		rootNode = null;
		leftmostNode = null;
		rightmostNode = null;
		lastFoundPath = null;
		lastSearchedKey = null;
		treeSize = 0;
		treeVersion++;
		return this;
	} // clear()

	/**
	 * Удаление элемента.
	 * <p>Метод удаляет из дерева элемент с указанным ключом. Возвращает удалённый элемент. Если элемент с указанным ключом отсутствует, то дерево остаётся неизменным, а метод возвращает значение {@code null}.</p>
	 * @param key Ключ удаляемого элемента.
	 * @return Удалённый элемент, или значение {@code null}.
	 */
	public E remove (
		final K key
	) { // method body
		final Deque<Node<E>> path = findPathByKey(key);
		Node<E> target = path.peek();
		E removedItem = null;
		if ((target != null) && (keyComparator.compare(keyExtractor.apply(target.item), key) == 0)) {
			removedItem = target.item;
			while ((target.leftChild != null) || (target.rightChild != null)) {
				final Node<E> newTarget = (subtreeHeight(target.leftChild) >= subtreeHeight(target.rightChild))
					? findRightmostNode(target.leftChild, path)
					: findLeftmostNode(target.rightChild, path);
				target.item = newTarget.item;
				target = newTarget;
			} // while
			removeNode(path);
			rebalancing(path);
			lastFoundPath = null;
			lastSearchedKey = null;
			treeSize--;
			treeVersion++;
		} // if
		return removedItem;
	} // remove()

	/**
	 * Итератор перед первым элементом дерева.
	 * <p>Метод возвращает итератор, установленный перед первым (самым левым) элементом дерева.</p>
	 * @return Итератор, установленный перед первым элементом дерева.
	 */
	public ListIterator<E> preFirstIterator (
	) { // method body
		return iteratorOfNext(leftmostNode);
	} // preFirstIterator()

	/**
	 * Итератор после последнего элемента дерева.
	 * <p>Метод возвращает итератор, установленный после последнего (самого правого) элемента дерева.</p>
	 * @return Итератор, установленный после последнего элемента дерева.
	 */
	public ListIterator<E> postLastIterator (
	) { // method body
		return iteratorOfPrevious(rightmostNode);
	} // postLastIterator()

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

	/**
	 * Присоединение дочернего узла.
	 * <p>Метод создает и присоединяет к указанному родительскому узлу новый дочерний узел, содержащий указанный элемент. Никаких проверок не производится, поэтому вызывающий код должен удостоверится, что указанный родительский узел может выступать родителем дочернего узла с указанным содержимым (элементом).</p>
	 * <p>При присоединении дочернего узла, метод определяет свободную ветку родительского узла, присоединяя к ней новый дочерний узел. Поскольку никаких проверок на допустимость не производится, указанный родительский узел должен содержать свободную дочернюю ветку с подходящей для указанного элемента стороны, иначе произойдёт нарушение логической структуры дерева. Если обе дочерние ветки свободны, нужная определяется сравнением ключей элементов. Кроме прикрепления к родительскому узлу, метод встраивает созданный узел в цепочку узлов, в зависимости от занимаемой дочерней ветки. При необходимости проводится коррекция ссылок на крайние узлы цепочки.</p>
	 * <p>По окончании работы, метод возвращает созданный дочерний узел.</p>
	 * @param parentNode Родительский узел.
	 * @param item Элемент нового узла.
	 * @return Созданный дочерний узел.
	 * @throws NullPointerException Если указанный родительский узел не существует.
	 */
	private Node<E> linkChildNode (
		final Node<E> parentNode,
		final E item
	) throws NullPointerException
	{ // method body
		final Node<E> newNode;
		if ((parentNode.rightChild != null)
			|| ((parentNode.leftChild == null)
				&& (keyComparator.compare(keyExtractor.apply(item), keyExtractor.apply(parentNode.item)) < 0)))
		{ // if block
			// new node is left child node
			newNode = new Node<E>(item, parentNode.previousNode, parentNode);
			parentNode.leftChild = newNode;
			parentNode.previousNode = newNode;
			if (newNode.previousNode != null) {
				newNode.previousNode.nextNode = newNode;
			} else {
				// only the leftmost node does not have a previous node
				leftmostNode = newNode;
			} // if
		} else {
			// new node is right child node
			newNode = new Node<E>(item, parentNode, parentNode.nextNode);
			parentNode.rightChild = newNode;
			parentNode.nextNode = newNode;
			if (newNode.nextNode != null) {
				newNode.nextNode.previousNode = newNode;
			} else {
				// only the rightmost node does not have a next node
				rightmostNode = newNode;
			} // if
		} // if
		return newNode;
	} // linkChildNode()

	/**
	 * Замена узла.
	 * <p>Метод заменяет ссылку на один из дочерних узлов, в указанном родительском узле. Родительский узел должен содержать ссылку на заменяемый узел, а заменяемый узел должен существовать, так как в противном случае, если родительский узел ещё не имеет ни одного дочернего узла, возникнет неоднозначность, которую метод будет не в силах правильно разрешить.</p>
	 * <p>Если указанный родительский узел не существует, то заменяется корень дерева.</p>
	 * <p>Если ссылки на заменяемый и заменяющий узлы совпадают, метод не совершает никаких действий.</p>
	 * @param parentNode Родительский узел.
	 * @param replaceableNode Заменяемый узел.
	 * @param replacementNode Заменяющий узел.
	 * @throws AssertionError Если включены операторы контроля и верно одно из условий:<ul>
	 *     <li>Заменяемый узел не существует.</li>
	 *     <li>Родительский узел не содержит ссылку на заменяемый дочерний узел.</li>
	 *     <li>Родительский узел не существует и корень дерева не является заменяемым узлом.</li>
	 * </ul>
	 */
	private void replaceNode (
		final Node<E> parentNode,
		final Node<E> replaceableNode,
		final Node<E> replacementNode
	) throws AssertionError
	{ // method body
		assert replaceableNode != null;
		assert (parentNode == null) || ((parentNode.leftChild == replaceableNode) || (parentNode.rightChild == replaceableNode));
		assert (parentNode != null) || (rootNode == replaceableNode);
		if (replaceableNode != replacementNode) {
			if (parentNode != null) {
				if (parentNode.leftChild == replaceableNode) {
					parentNode.leftChild = replacementNode;
				} else {
					parentNode.rightChild = replacementNode;
				} // if
			} else {
				rootNode = replacementNode;
			} // if
		} // if
	} // replaceNode()

	/**
	 * Ребалансировка пути в дереве.
	 * <p>Метод принимает путь в дереве, и производит ребалансировку узлов на этом пути. Во время ребалансировки путь разрушается. Во время ребалансировки может произойти смена корня дерева.</p>
	 * <p>Метод не производит проверок корректности пути, возлагая эту обязанность на вызывающий код.</p>
	 * @param path Путь ребалансировки.
	 * @throws NullPointerException Если указанный путь не существует.
	 */
	private void rebalancing (
		final Deque<Node<E>> path
	) throws NullPointerException
	{ // method body
		if (!path.isEmpty()) {
			int oldSubtreeHeight;
			int newSubtreeHeight;
			do {
				final Node<E> oldRoot = path.pop();
				oldSubtreeHeight = oldRoot.subtreeHeight;
				final Node<E> newRoot = balance(oldRoot);
				newSubtreeHeight = newRoot.subtreeHeight;
				replaceNode(path.peek(), oldRoot, newRoot);
			} while ((!path.isEmpty()) && (newSubtreeHeight != oldSubtreeHeight));
		} // if
	} // rebalancing()

	/**
	 * Метод балансировки поддерева.
	 * <p>Принимая ссылку на корень поддерева, метод производит балансировку, возвращая новый корень поддерева.</p>
	 * <p>Высота заявленная в указанном корне, может не соответствовать фактической высоте поддерева, однако во время выполнения метода, высоты будут скорректированы.</p>
	 * @param root Корень поддерева.
	 * @return Новый корень поддерева.
	 * @throws NullPointerException Если указанный корень не существует.
	 */
	private Node<E> balance (
		final Node<E> root
	) throws NullPointerException
	{ // method body
		final Node<E> newRoot;
		final int leftChildHeight = subtreeHeight(root.leftChild);
		final int rightChildHeight = subtreeHeight(root.rightChild);
		if (Math.abs(leftChildHeight - rightChildHeight) > 1) {
			if (leftChildHeight < rightChildHeight) {
				if (subtreeHeight(root.rightChild.leftChild) <= subtreeHeight(root.rightChild.rightChild)) {
					newRoot = zig(root);
				} else {
					newRoot = zigZag(root);
				} // if
			} else {
				if (subtreeHeight(root.leftChild.rightChild) <= subtreeHeight(root.leftChild.leftChild)) {
					newRoot = zag(root);
				} else {
					newRoot = zagZig(root);
				} // if
			} // if
		} else {
			root.subtreeHeight = Math.max(leftChildHeight, rightChildHeight) + 1;
			newRoot = root;
		} // if
		return newRoot;
	} // balance()

	/**
	 * Малое левое вращение.
	 * <p>Метод осуществляет малое левое вращение указанного поддерева. Заявленные высоты старого и нового корней обновляются. Расчёт новых высот не принимает во внимание заявленную в корне высоту поддерева. Возвращается новый корень поддерева.</p>
	 * @param root Корень вращаемого поддерева.
	 * @return Новый корень поддерева.
	 * @throws NullPointerException Если указанный корень, либо его правый дочерний узел не существует.
	 */
	private Node<E> zig (
		final Node<E> root
	) throws NullPointerException
	{ // method body
		final Node<E> a = root;
		final Node<E> b = a.rightChild;
		final Node<E> c = b.leftChild;
		a.rightChild = c;
		b.leftChild = a;
		a.subtreeHeight = subtreeHeight(c) + 1;
		b.subtreeHeight = a.subtreeHeight + 1;
		return b;
	} // zig()

	/**
	 * Большое левое вращение.
	 * <p>Метод осуществляет большое левое вращение указанного поддерева. Заявленные высоты изменяемых узлов обновляются. Расчёт новых высот не принимает во внимание заявленную в корне высоту поддерева. Возвращается новый корень поддерева.</p>
	 * @param root Корень вращаемого поддерева.
	 * @return Новый корень поддерева.
	 * @throws NullPointerException Если указанный корень, либо его правый дочерний узел, либо левый дочерний узел правого дочернего узла корня не существует.
	 */
	private Node<E> zigZag (
		final Node<E> root
	) throws NullPointerException
	{ // method body
		final Node<E> a = root;
		final Node<E> b = a.rightChild;
		final Node<E> c = b.leftChild;
		a.rightChild = c.leftChild;
		b.leftChild = c.rightChild;
		c.leftChild = a;
		c.rightChild = b;
		a.subtreeHeight = subtreeHeight(a.leftChild) + 1;
		b.subtreeHeight = a.subtreeHeight;
		c.subtreeHeight = a.subtreeHeight + 1;
		return c;
	} // zigZag()

	/**
	 * Малое правое вращение.
	 * <p>Метод осуществляет малое правое вращение указанного поддерева. Заявленные высоты старого и нового корней обновляются. Расчёт новых высот не принимает во внимание заявленную в корне высоту поддерева. Возвращается новый корень поддерева.</p>
	 * @param root Корень вращаемого поддерева.
	 * @return Новый корень поддерева.
	 * @throws NullPointerException Если указанный корень, либо его левый дочерний узел не существует.
	 */
	private Node<E> zag (
		final Node<E> root
	) throws NullPointerException
	{ // method body
		final Node<E> a = root;
		final Node<E> b = a.leftChild;
		final Node<E> c = b.rightChild;
		a.leftChild = c;
		b.rightChild = a;
		a.subtreeHeight = subtreeHeight(c) + 1;
		b.subtreeHeight = a.subtreeHeight + 1;
		return b;
	} // zag()

	/**
	 * Большое правое вращение.
	 * <p>Метод осуществляет большое правое вращение указанного поддерева. Заявленные высоты изменяемых узлов обновляются. Расчёт новых высот не принимает во внимание заявленную в корне высоту поддерева. Возвращается новый корень поддерева.</p>
	 * @param root Корень вращаемого поддерева.
	 * @return Новый корень поддерева.
	 * @throws NullPointerException Если указанный корень, либо его левый дочерний узел, либо правый дочерний узел левого дочернего узла корня не существует.
	 */
	private Node<E> zagZig (
		final Node<E> root
	) throws NullPointerException
	{ // method body
		final Node<E> a = root;
		final Node<E> b = a.leftChild;
		final Node<E> c = b.rightChild;
		a.leftChild = c.rightChild;
		b.rightChild = c.leftChild;
		c.rightChild = a;
		c.leftChild = b;
		a.subtreeHeight = subtreeHeight(a.rightChild) + 1;
		b.subtreeHeight = a.subtreeHeight;
		c.subtreeHeight = a.subtreeHeight + 1;
		return c;
	} // zagZig()

	/**
	 * Удаление узла.
	 * <p>Метод удаляет из дерева и очищает листовой узел, находящийся на вершине указанного пути. Кроме того, удаляемый узел снимается с вершины пути.</p>
	 * @param path Путь к удаляемому узлу.
	 * @throws NoSuchElementException Если указанный путь пуст.
	 * @throws NullPointerException Если на вершине пути находится значение {@code null}.
	 * @throws AssertionError Если разрешены операторы контроля, а узел находящийся на вершине пути не является листом.
	 */
	private void removeNode (
		final Deque<Node<E>> path
	) throws NoSuchElementException,
		NullPointerException,
		AssertionError
	{ // method body
		final Node<E> target = path.pop();
		assert target.leftChild == null;
		assert target.rightChild == null;
		replaceNode(path.peek(), target, null);
		if (target.previousNode != null) {
			target.previousNode.nextNode = target.nextNode;
		} else {
			leftmostNode = target.nextNode;
		} // if
		if (target.nextNode != null) {
			target.nextNode.previousNode = target.previousNode;
		} else {
			rightmostNode = target.previousNode;
		} // if
		target.clear();
	} // removeNode()

	/**
	 * Итератор от указанного следующего узла.
	 * <p>Метод возвращает итератор, установленный перед указанным узлом. Если указанный узел не существует, то возвращается "пустой" итератор - то есть итератор, хоть и привязанный к текущему дереву, однако не позволяющий получить хотя бы один элемент.</p>
	 * @param nextNode Следующий узел.
	 * @return Итератор установленный перед указанным узлом.
	 */
	private TreeIterator iteratorOfNext (
		final Node<E> nextNode
	) { // method body
		final Node<E> previousNode = (nextNode != null) ? nextNode.previousNode : null;
		return new TreeIterator(previousNode, nextNode);
	} // iteratorOfNext()

	/**
	 * Итератор от указанного предшествующего узла.
	 * <p>Метод возвращает итератор, установленный после указанного узла. Если указанный узел не существует, то возвращается "пустой" итератор - то есть итератор, хоть и привязанный к текущему дереву, однако не позволяющий получить хотя бы один элемент.</p>
	 * @param previousNode Предшествующий узел.
	 * @return Итератор установленный после указанного узла.
	 */
	private TreeIterator iteratorOfPrevious (
		final Node<E> previousNode
	) { // method body
		final Node<E> nextNode = (previousNode != null) ? previousNode.nextNode : null;
		return new TreeIterator(previousNode, nextNode);
	} // iteratorOfPrevious()

	// todo
} // AvlTree
