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

	} // Node

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
	 * <p>Поле содержит число узлов в дереве.</p>
	 */
	private long size;

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
				size++;
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
			size++;
			lastFoundPath = null;
			lastSearchedKey = null;
		} // if
		version++;
		return replacedItem;
	} // put()

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

	private Node<E> zagZig (
		final Node<E> root
	) throws NullPointerException
	{ // method body
		// todo
		throw new NoSuchMethodError();
	} // zagZig()

	// todo
} // AvlTree
