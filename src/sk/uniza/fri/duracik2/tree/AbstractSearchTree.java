/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.tree;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Unlink
 * @param <E>
 */
public abstract class AbstractSearchTree<E extends Comparable<? super E>> implements Iterable<E> {

	/**
	 * Trieda reprezentujúca uzol stromu
	 */
	protected class Node {
		public Node aLavy;
		public Node aPravy;
		public Node aOtec;
		public E aPrvok;

		public Node(E aPrvok) {
			this.aPrvok = aPrvok;
		}

		/**
		 * Nastaví uzol ako lavého syna a ak syn nieje null, nastaví mu ako
		 * parenta aktualny uzol
		 *
		 * @param paNode
		 */
		public void setLeft(Node paNode) {
			aLavy = paNode;
			if (paNode != null) {
				paNode.aOtec = this;
			}
		}

		/**
		 * Nastaví uzol ako pravého syna a ak syn nieje null, nastaví mu ako
		 * parenta aktualny uzol
		 *
		 * @param paNode
		 */
		public void setRight(Node paNode) {
			aPravy = paNode;
			if (paNode != null) {
				paNode.aOtec = this;
			}
		}

		public Node getBrother() {
			if (aOtec == null) {
				return null;
			}
			else if (aOtec.aLavy == this) {
				return aOtec.aPravy;
			}
			else {
				return aOtec.aLavy;
			}
		}

		public boolean amILeft() {
			if (aOtec == null) {
				return false;
			}
			return aOtec.aLavy == this;
		}

		public boolean amIRight() {
			if (aOtec == null) {
				return false;
			}
			return aOtec.aPravy == this;
		}

		/**
		 * Vykreslenie podstromu daného uzla Zdroj:
		 * http://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram
		 */
		public void print() {
			print("", true);
		}

		private void print(String prefix, boolean isTail) {
			System.out.println(prefix + (isTail ? "└─ " : "├─ ") + nodeToString());
			if (aLavy != null) {
				aLavy.print(prefix + (isTail ? "   " : "│  "), false);
			}
			if (aPravy != null) {
				aPravy.print(prefix + (isTail ? "   " : "│  "), false);
			}
		}

		protected String nodeToString() {
			return ((aOtec != null && amILeft()) ? 'L' : 'R') + aPrvok.toString();
		}
	}

	protected TreeIndexer<E> aIndexer;

	protected Node aVrchol;

	protected int aSize;

	protected Node aLastUsedNode;
	protected int aLastTraversedDirection;

	public AbstractSearchTree() {
		/**
		 * Defaultný indexer usporiadáva prvky podľa metódy compare To
		 */
		this(new TreeIndexer<E>() {

			@Override
			public int compare(E e1, Object... params) {
				if (params.length == 1) {
					return (-1) * e1.compareTo((E) params[0]);
				}
				else {
					throw new IllegalArgumentException();
				}
			}
		});
	}

	public AbstractSearchTree(TreeIndexer<E> aIndexer) {
		this.aIndexer = aIndexer;
		aVrchol = null;
		aSize = 0;
	}

	public void clear() {
		aVrchol = null;
		aSize = 0;
		aLastUsedNode = null;
		aLastTraversedDirection = 0;
	}

	public int size() {
		return aSize;
	}

	/**
	 * Nájde prvok v strome
	 *
	 * @param paPrvok
	 * @return prvok, inak NULL pokiaľ sa uzol v strome nenachádza
	 */
	public E find(E paPrvok) {
		return findByParams(paPrvok);
	}

	/**
	 * Overí či prvok je v strome
	 *
	 * @param paPrvok
	 * @return
	 */
	public boolean contains(E paPrvok) {
		return find(paPrvok) != null;
	}

	/**
	 * Nájde prvok v strome podľa parametrov indexera
	 *
	 * @param params
	 * @return
	 */
	public E findByParams(Object... params) {
		Node uzol = searchTree(params);
		if (uzol == null) {
			return null;
		}
		else {
			return uzol.aPrvok;
		}
	}

	/**
	 * Nájde najmenší prvok vyhovujúci parametrom
	 *
	 * @param params
	 * @return
	 */
	protected Node findLower(Object... params) {
		Node uzol = searchTree(params);
		if (uzol == null) {
			return null;
		}
		else {
			//Nieje zaručené že sme našli prvý prvok
			Node predchodca = inOrderPredecessor(uzol);
			while (predchodca != null && aIndexer.compare(predchodca.aPrvok, params) == 0) {
				uzol = predchodca;
				predchodca = inOrderPredecessor(uzol);
			}
			return uzol;
		}
	}

	/**
	 * Nájde všetky prvky, ktoré vyhovujú parametrom
	 *
	 * @param params
	 * @return
	 */
	public Iterator<E> findAll(final Object... params) {
		final Node clousure = findLower(params);
		return new Iterator<E>() {
			Node current = clousure;

			@Override
			public boolean hasNext() {
				return current != null && aIndexer.compare(current.aPrvok, params) == 0;
			}

			@Override
			public E next() {
				Node aktual = current;
				current = inOrderSuccessor(current);
				return aktual.aPrvok;
			}

			@Override
			public void remove() {
			}
		};
	}

	/**
	 * Nájde rozsah Parametre sa zadávajú param1od, param1do, param2od,
	 * param2do,...
	 *
	 * @param params
	 * @return
	 */
	public Iterator<E> findRange(final Object... params) {
		if (params.length % 2 != 0) {
			throw new IllegalArgumentException();
		}
		final Object[] pFrom = new Object[params.length / 2];
		final Object[] pTo = new Object[params.length / 2];
		for (int i = 0; i < params.length; i += 2) {
			pFrom[i / 2] = params[i];
			pTo[i / 2] = params[i + 1];
		}

		Node begin = findLower(pFrom);
		begin = (begin == null && aLastTraversedDirection < 0) ? aLastUsedNode : begin;
		begin = (begin == null) ? inOrderSuccessor(aLastUsedNode) : begin;

		final Node clousure = begin;
		return new Iterator<E>() {
			Node current = clousure;

			@Override
			public boolean hasNext() {
				return current != null && aIndexer.compare(current.aPrvok, pTo) >= 0;
			}

			@Override
			public E next() {
				Node aktual = current;
				current = inOrderSuccessor(current);
				return aktual.aPrvok;
			}

			@Override
			public void remove() {
			}
		};

	}

	/**
	 * Metoda prehľadá strom
	 * do atribútu aLastUsedNode nastaví posledný použitý uzol v
	 * prehľadávani
	 * null - ak je strom prázdny rovnaký prvok ako hľadaný ak sa nejaký našiel
	 *
	 * @param params
	 * @return Null, ak je strom prázdny alebo sa uzol v strome
	 * nenachadza<br>hľadaný uzol, ak ho našlo
	 */
	protected Node searchTree(Object... params) {
		Node uzol = aVrchol;
		aLastUsedNode = null;
		while (uzol != null) {
			aLastUsedNode = uzol;
			aLastTraversedDirection = aIndexer.compare(uzol.aPrvok, params);

			if (aLastTraversedDirection == 0) {
				return uzol;
			}
			else if (aLastTraversedDirection < 0) {
				uzol = uzol.aLavy;
			}
			else if (aLastTraversedDirection > 0) {
				uzol = uzol.aPravy;
			}
			else {
				return uzol;
			}
		}
		return null;
	}

	/**
	 * Vloží prvok do stromu na misesto podĺa princípu BVS
	 *
	 * @param paNode
	 * @return
	 */
	protected boolean insertNode(Node paNode) {
		Node dummyNode = searchTree(paNode.aPrvok);
		//Strom je prázdny
		if (aLastUsedNode == null) {
			aVrchol = paNode;
			aSize = 1;
		}
		else {
			if (dummyNode != null) {
				return false;
			}
			else if (aLastTraversedDirection < 0) {
				aLastUsedNode.setLeft(paNode);
				aSize++;
			}
			else if (aLastTraversedDirection > 0) {
				aLastUsedNode.setRight(paNode);
				aSize++;
			}
			else {
				throw new RuntimeException("WUT?");
			}
		}
		return true;
	}

	/*    
	 *             X           rotate_left(X)--->            Y
	 *           /   \                                     /   \
	 *          A     Y        <---rotate_right(Y)        X     C
	 *              /   \                               /   \
	 *             B     C                             A     B
	 */
	/**
	 * Pravá rotácia okolo vrcholu X Referenica na X sa nemení!
	 *
	 * @param x
	 */
	protected void rotate_left(Node x) {
		Node y = x.aPravy;
		E data = x.aPrvok;
		x.aPrvok = y.aPrvok;
		y.aPrvok = data;

		x.setRight(y.aPravy);
		y.setRight(y.aLavy);
		y.setLeft(x.aLavy);
		x.setLeft(y);
	}

	/**
	 * Ľavá rotácia okolo vrcholu X Referenica na X sa nemení!
	 *
	 * @param y
	 */
	protected void rotate_right(Node y) {
		Node x = y.aLavy;
		E data = x.aPrvok;
		x.aPrvok = y.aPrvok;
		y.aPrvok = data;

		y.setLeft(x.aLavy);
		x.setLeft(x.aPravy);
		x.setRight(y.aPravy);
		y.setRight(x);
	}

	/**
	 * Vráti nasledovníka v inorder prehliadke
	 *
	 * @param x
	 * @return
	 */
	protected Node inOrderSuccessor(Node x) {
		if (x == null) {
			return null;
		}
        //Ak mám pravého syna nasledovník bude tam
		//Ako jeho najlavejší syn
		else if (x.aPravy != null) {
			Node y = x.aPravy;
			while (y.aLavy != null) {
				y = y.aLavy;
			}
			return y;
        //Ak nemáme, tak nasledovník bude 
		//Bude to otec, ktorý má 
		}
		else {
			Node y = x.aOtec;
			Node z = x;
			while (y != null && z.amIRight()) {
				z = y;
				y = y.aOtec;
			}
			return y;
		}
	}

	/**
	 * Vráti predchodcu v inorder prehliadke
	 *
	 * @param x
	 * @return
	 */
	protected Node inOrderPredecessor(Node x) {
		if (x == null) {
			return null;
		}
		else if (x.aLavy != null) {
			Node y = x.aLavy;
			while (y.aPravy != null) {
				y = y.aPravy;
			}
			return y;
		}
		else {
			Node y = x.aOtec;
			Node z = x;
			while (y != null && z.amILeft()) {
				z = y;
				y = y.aOtec;
			}
			return y;
		}
	}

	/**
	 * In order prehliadka
	 *
	 * @return
	 */
	public Iterator<E> inOrderIterator() {
		Node uzol = aVrchol;
		while (uzol != null && uzol.aLavy != null) {
			uzol = uzol.aLavy;
		}
		final Node clousure = uzol;
		return new Iterator<E>() {
			Node uzol = clousure;

			@Override
			public boolean hasNext() {
				return uzol != null;
			}

			@Override
			public E next() {
				if (hasNext()) {
					E data = uzol.aPrvok;
					uzol = inOrderSuccessor(uzol);
					return data;
				}
				return null;
			}

			@Override
			public void remove() {

			}
		};
	}

	/**
	 * Lever order prehliadka
	 *
	 * @return
	 */
	public Iterator<E> levelOrderIterator() {
		final LinkedList<Node> zasobnik = new LinkedList<>();
		if (aVrchol != null) {
			zasobnik.addLast(aVrchol);
		}
		return new Iterator<E>() {

			@Override
			public boolean hasNext() {
				return zasobnik.size() > 0;
			}

			@Override
			public E next() {
				if (hasNext()) {
					Node uzol = zasobnik.removeFirst();
					if (uzol.aLavy != null) {
						zasobnik.addLast(uzol.aLavy);
					}
					if (uzol.aPravy != null) {
						zasobnik.addLast(uzol.aPravy);
					}
					return uzol.aPrvok;
				}
				return null;
			}

			@Override
			public void remove() {
			}
		};
	}

	@Override
	public Iterator<E> iterator() {
		return inOrderIterator();
	}

	/**
	 * Vykreslí strom
	 *
	 * @see Node.print
	 */
	public void printTree() {
		if (aVrchol != null) {
			aVrchol.print();
		}
	}
}
